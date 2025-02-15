package vsukharew.multiple.data.sources.ui.tweets.list

import androidx.annotation.StringRes
import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.ui.base.Reducer

sealed class TweetsState : Reducer.ViewState {
    data object Initial : TweetsState()
    data object MainProgress : TweetsState()
    data class LastCachedTweets(val tweets: List<Tweet>) : TweetsState()
    data class Error(@StringRes val errorMessage: Int) : TweetsState()
    data class Success(val tweets: List<Tweet>) : TweetsState()
}