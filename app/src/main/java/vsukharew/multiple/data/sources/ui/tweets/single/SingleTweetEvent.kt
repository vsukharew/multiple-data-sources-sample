package vsukharew.multiple.data.sources.ui.tweets.single

import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.ui.base.Reducer

sealed class SingleTweetEvent : Reducer.Event {
    data object FirstLoad : SingleTweetEvent()
    data class TweetLoaded(val tweet: Tweet) : SingleTweetEvent()
}