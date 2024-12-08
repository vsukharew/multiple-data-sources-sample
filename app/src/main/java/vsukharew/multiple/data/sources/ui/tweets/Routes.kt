package vsukharew.multiple.data.sources.ui.tweets

import kotlinx.serialization.Serializable

@Serializable
data object TweetsListRoute

@Serializable
data class SingleTweetRoute(val tweetId: String)