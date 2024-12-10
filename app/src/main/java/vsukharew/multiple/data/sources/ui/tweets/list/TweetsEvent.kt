package vsukharew.multiple.data.sources.ui.tweets.list

import vsukharew.multiple.data.sources.domain.model.Source
import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.ui.base.Reducer

sealed class TweetsEvent : Reducer.Event {
    data object FirstLoad : TweetsEvent()
    data object Refresh : TweetsEvent()
    data class TweetsLoadingError(val errorRes: Int) : TweetsEvent()
    data class TweetsLoaded(val tweets: List<Tweet>, val source: Source): TweetsEvent()
    data class Click(val tweet: Tweet) : TweetsEvent()
}