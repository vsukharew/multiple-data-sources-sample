package vsukharew.multiple.data.sources.ui.tweets.single

import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.ui.base.Reducer

sealed class SingleTweetState : Reducer.ViewState {
    data object Initial : SingleTweetState()
    data object MainProgress : SingleTweetState()
    data class Success(val tweet: Tweet) : SingleTweetState()
}