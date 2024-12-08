package vsukharew.multiple.data.sources.data.repository

import kotlinx.coroutines.flow.Flow
import vsukharew.multiple.data.sources.domain.model.LoadStrategy
import vsukharew.multiple.data.sources.domain.model.Source
import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.domain.type.AppError
import vsukharew.multiple.data.sources.domain.type.Either

interface TweetsRepo {
    suspend fun getTweet(
        tweetId: String,
        loadStrategy: LoadStrategy
    ): Either<AppError<Any>, Tweet>
    suspend fun getTweets(loadStrategy: LoadStrategy): Either<AppError<Any>, Flow<Pair<List<Tweet>, Source>>>
}