package vsukharew.multiple.data.sources.ui.tweets.list

import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.ui.base.Reducer

sealed class TweetsEffect : Reducer.Effect {
    data class NavigateToSingleTweetScreen(val tweet: Tweet) : TweetsEffect()
    data object ShowLoadingErrorSnackBar : TweetsEffect()
}