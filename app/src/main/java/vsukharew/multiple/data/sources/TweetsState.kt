package vsukharew.multiple.data.sources

import androidx.annotation.StringRes
import vsukharew.multiple.data.sources.domain.model.Tweet

sealed class TweetsState {
    data object Initial : TweetsState()
    data object MainProgress : TweetsState()
    data class LastCachedTweets(val tweets: List<Tweet>) : TweetsState()
    data class Error(@StringRes val errorMessage: Int) : TweetsState()
    data class Success(val tweets: List<Tweet>) : TweetsState()
}