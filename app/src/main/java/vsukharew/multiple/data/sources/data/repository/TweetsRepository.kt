package vsukharew.multiple.data.sources.data.repository

import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override suspend fun getTweets(loadStrategy: LoadStrategy): Flow<Either<AppError<Any>, Pair<List<Tweet>, Source>>> {
        return flow {
            when (loadStrategy) {
                LoadStrategy.REMOTE_ONLY -> {
                    emit(
                        sideEffect {
                            getTweetsRemoteFirst(
                                assets,
                                gson,
                                tweetDao,
                                platformDao,
                                authorDao,
                                mapTweetResponseToTweetEntity,
                                mapTweetResponseToTweet
                            )
                        }
                    )
                }
                LoadStrategy.CACHE_FIRST -> {
                    val areNoTweetsInDb = tweetDao.count() == 0L
                    if (areNoTweetsInDb) {
                        emit(
                            getRemoteTweets(
                                assets,
                                gson,
                                tweetDao,
                                platformDao,
                                authorDao,
                                mapTweetResponseToTweetEntity,
                                mapTweetResponseToTweet,
                            )
                        )
                    } else {
                        emit(getLocalTweets(tweetDao, mapTweetCombinedToTweet))
                        emit(
                            getRemoteTweets(
                                assets,
                                gson,
                                tweetDao,
                                platformDao,
                                authorDao,
                                mapTweetResponseToTweetEntity,
                                mapTweetResponseToTweet,
                            )
                        )
                    }
                }
                else -> TODO()
            }
        }
    }

    private suspend fun EitherScope<AppError<Any>>.getTweetsRemoteFirst(
        assets: AssetManager,
        gson: Gson,
        tweetDao: TweetDao,
        platformDao: PlatformDao,
        authorDao: AuthorDao,
        mapTweetResponseToTweetEntity: suspend (TweetResponse, AuthorDao, PlatformDao) -> TweetEntity,
        mapTweetResponseToTweet: Mapper<TweetResponse, Tweet>
    ): Pair<List<Tweet>, Source> {
        return getRemoteTweets(
            assets,
            gson,
            tweetDao,
            platformDao,
            authorDao,
            mapTweetResponseToTweetEntity,
            mapTweetResponseToTweet
        ).right()
    }

    private suspend fun getLocalTweets(
        tweetDao: TweetDao,
        mapTweetCombinedToTweet: Mapper<TweetCombined, Tweet>
    ): Either<AppError<Any>, Pair<List<Tweet>, Source>> {
        return tweetDao.getAllCombined()
            .map(mapTweetCombinedToTweet::map)
            .let { it to Source.CACHE }
            .let(::Right)
    }

    private suspend fun getRemoteTweets(
        assets: AssetManager,
        gson: Gson,
        tweetDao: TweetDao,
        platformDao: PlatformDao,
        authorDao: AuthorDao,
        mapTweetResponseToTweetEntity: suspend (TweetResponse, AuthorDao, PlatformDao) -> TweetEntity,
        mapTweetResponseToTweet: Mapper<TweetResponse, Tweet>,
    ): Either<AppError<Any>, Pair<List<Tweet>, Source>> {
        return sideEffect {
            getAndSaveRemoteTweets(
                assets,
                gson,
                tweetDao,
                platformDao,
                authorDao,
                mapTweetResponseToTweetEntity,
                mapTweetResponseToTweet
            ) to Source.REMOTE
        }
    }

    private suspend fun EitherScope<AppError<Any>>.getAndSaveRemoteTweets(
        assets: AssetManager,
        gson: Gson,
        tweetDao: TweetDao,
        platformDao: PlatformDao,
        authorDao: AuthorDao,
        mapTweetResponseToTweetEntity: suspend (TweetResponse, AuthorDao, PlatformDao) -> TweetEntity,
        mapTweetResponseToTweet: Mapper<TweetResponse, Tweet>
    ): List<Tweet> {
        delay(3000L)
        return getRemoteTweets(assets, gson).right()
            .also { remoteTweets ->
                putTweetsIntoDb(
                    remoteTweets,
                    tweetDao,
                    platformDao,
                    authorDao,
                    mapTweetResponseToTweetEntity
                )
            }
            .map(mapTweetResponseToTweet::map)
    }

    private fun EitherScope<AppError<Any>>.getRemoteTweets(
        assets: AssetManager,
        gson: Gson,
    ): Either<AppError<Any>, List<TweetResponse>> {
        val nextInt = Random(System.currentTimeMillis()).nextInt(10)
        return if (nextInt % 5 != 0) {
            assets.open("tweets.json")
                .bufferedReader()
                .use { it.readText() }
                .let {
                    gson.fromJson<List<TweetResponse>>(
                        it,
                        object : TypeToken<List<TweetResponse>>() {}.type
                    )
                }
                .let(::Right)
        } else {
            AppError.HttpError(500, Any()).left()
        }
    }

    private suspend fun putTweetsIntoDb(
        tweets: List<TweetResponse>,
        tweetDao: TweetDao,
        platformDao: PlatformDao,
        authorDao: AuthorDao,
        mapper: suspend (TweetResponse, AuthorDao, PlatformDao) -> TweetEntity
    ): List<Long> {
         return coroutineScope {
            tweets.map {
                async {
                    val tweetEntity = mapper.invoke(it, authorDao, platformDao)
                    tweetDao.insert(tweetEntity)
                }
            }.awaitAll()
        }
    }
}