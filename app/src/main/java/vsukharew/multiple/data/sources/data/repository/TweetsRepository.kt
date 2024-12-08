package vsukharew.multiple.data.sources.data.repository

import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import vsukharew.multiple.data.sources.App
import vsukharew.multiple.data.sources.data.source.local.room.dao.AuthorDao
import vsukharew.multiple.data.sources.data.source.local.room.dao.PlatformDao
import vsukharew.multiple.data.sources.data.source.local.room.dao.TweetDao
import vsukharew.multiple.data.sources.data.source.local.room.entity.AuthorEntity
import vsukharew.multiple.data.sources.data.source.local.room.entity.PlatformEntity
import vsukharew.multiple.data.sources.data.source.local.room.entity.TweetCombined
import vsukharew.multiple.data.sources.data.source.local.room.entity.TweetEntity
import vsukharew.multiple.data.sources.data.source.remote.model.TweetResponse
import vsukharew.multiple.data.sources.domain.mapper.Mapper
import vsukharew.multiple.data.sources.domain.model.LoadStrategy
import vsukharew.multiple.data.sources.domain.model.Platform
import vsukharew.multiple.data.sources.domain.model.Source
import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.domain.type.AppError
import vsukharew.multiple.data.sources.domain.type.Either
import vsukharew.multiple.data.sources.domain.type.Either.Right
import vsukharew.multiple.data.sources.domain.type.EitherScope
import vsukharew.multiple.data.sources.domain.type.sideEffect
import kotlin.random.Random

class TweetsRepository(
    private val tweetDao: TweetDao,
    private val authorDao: AuthorDao,
    private val platformDao: PlatformDao,
    private val gson: Gson,
    private val assets: AssetManager,
) : TweetsRepo {
    private val mapTweetCombinedToTweet = Mapper<TweetCombined, Tweet> { source ->
        source.run {
            Tweet(
                id,
                message,
                platform.let(Platform::fromString),
                author,
                avatarUrl
            )
        }
    }

    private val mapTweetResponseToTweet = Mapper<TweetResponse, Tweet> { source ->
        source.run {
            Tweet(
                id,
                message,
                platform.let(Platform::fromString),
                author,
                avatarUrl
            )
        }
    }

    private val mapTweetResponseToTweetEntity: suspend (TweetResponse, AuthorDao, PlatformDao) -> TweetEntity =
        { tweet, authorDao, platformDao ->
            tweet.run {
                val platformId = platformDao.insert(PlatformEntity(platform))
                val authorId = authorDao.insert(AuthorEntity(author))
                TweetEntity(id, message, authorId, platformId, avatarUrl)
            }
        }

    override suspend fun getTweet(
        tweetId: String,
        loadStrategy: LoadStrategy
    ): Either<AppError<Any>, Tweet> {
        return sideEffect {
            when (loadStrategy) {
                LoadStrategy.CACHE_ONLY -> {
                    tweetDao.getTweetCombined(tweetId)
                        ?.let(mapTweetCombinedToTweet::map)
                        ?: AppError.OtherError(Exception()).left()
                }
                else -> TODO()
            }
        }
    }

    override suspend fun getTweets(loadStrategy: LoadStrategy): Either<AppError<Any>, Flow<Pair<List<Tweet>, Source>>> {
        return sideEffect {
            when (loadStrategy) {
                LoadStrategy.REMOTE_ONLY -> {
                    (getAndSaveRemoteTweets(
                        assets,
                        gson,
                        tweetDao,
                        platformDao,
                        authorDao,
                        mapTweetResponseToTweetEntity
                    ) to Source.REMOTE).let(::flowOf)
                }
                LoadStrategy.CACHE_FIRST -> getTweetsCacheFirst()
                else -> TODO()
            }
        }
    }

    private suspend fun EitherScope<AppError<Any>>.getTweetsCacheFirst(): Flow<Pair<List<Tweet>, Source>> {
        val areNoTweetsInDb = tweetDao.count() == 0L
        return if (areNoTweetsInDb) {
            val remoteTweets = getAndSaveRemoteTweets(
                assets,
                gson,
                tweetDao,
                platformDao,
                authorDao,
                mapTweetResponseToTweetEntity
            )
            flowOf(remoteTweets to Source.REMOTE)
        } else {
            val localTweets = getLocalTweets(tweetDao, mapTweetCombinedToTweet).right()
            flow {
                emit(localTweets to Source.CACHE)
                val remoteTweets = getAndSaveRemoteTweets(
                    assets,
                    gson,
                    tweetDao,
                    platformDao,
                    authorDao,
                    mapTweetResponseToTweetEntity
                )
                emit(remoteTweets to Source.REMOTE)
            }.catch { left }
        }
    }

    private suspend fun EitherScope<AppError<Any>>.getAndSaveRemoteTweets(
        assets: AssetManager,
        gson: Gson,
        tweetDao: TweetDao,
        platformDao: PlatformDao,
        authorDao: AuthorDao,
        mapper: suspend (TweetResponse, AuthorDao, PlatformDao) -> TweetEntity
    ): List<Tweet> {
        delay(3000L)
        return getRemoteTweets(assets, gson).right()
            .also { remoteTweets ->
                putTweetsIntoDb(
                    remoteTweets,
                    tweetDao,
                    platformDao,
                    authorDao,
                    mapper
                )
            }
            .map(mapTweetResponseToTweet::map)
    }

    private suspend fun getLocalTweets(
        tweetDao: TweetDao,
        mapper: Mapper<TweetCombined, Tweet>
    ): Either<AppError<Any>, List<Tweet>> {
        return tweetDao.getAllCombined()
            .map(mapper::map)
            .let(::Right)
    }

    private fun getRemoteTweets(
        assets: AssetManager,
        gson: Gson,
    ): Either<AppError<Any>, List<TweetResponse>> {
        val nextInt = Random(System.currentTimeMillis()).nextInt(10)
        return sideEffect {
            if (nextInt % 5 != 0) {
                assets.open("tweets.json")
                    .bufferedReader()
                    .use { it.readText() }
                    .let {
                        gson.fromJson<List<TweetResponse>>(
                            it,
                            object : TypeToken<List<TweetResponse>>() {}.type
                        )
                    }
            } else {
                AppError.HttpError(500, Any()).left()
            }
        }
    }

    private suspend fun putTweetsIntoDb(
        tweets: List<TweetResponse>,
        tweetDao: TweetDao,
        platformDao: PlatformDao,
        authorDao: AuthorDao,
        mapper: suspend (TweetResponse, AuthorDao, PlatformDao) -> TweetEntity
    ): List<Long> {
        return tweets.map {
            coroutineScope {
                async {
                    val tweetEntity = mapper.invoke(it, authorDao, platformDao)
                    tweetDao.insert(tweetEntity)
                }
            }
        }.awaitAll()
    }
}