package vsukharew.multiple.data.sources.ui.tweets.list

import vsukharew.multiple.data.sources.R
import vsukharew.multiple.data.sources.domain.model.Source
import vsukharew.multiple.data.sources.ui.base.Reducer

object TweetsReducer : Reducer<TweetsState, TweetsEvent, TweetsEffect> {
    override fun reduce(
        currentState: TweetsState,
        event: TweetsEvent
    ): Pair<TweetsState, TweetsEffect?> {
        return when (event) {
            TweetsEvent.FirstLoad -> TweetsState.MainProgress to null
            TweetsEvent.Refresh -> TweetsState.MainProgress to null
            is TweetsEvent.TweetsLoadingError -> reduceTweetsLoadingError(currentState)
            is TweetsEvent.TweetsLoaded -> {
                when (event.source) {
                    Source.CACHE -> TweetsState.LastCachedTweets(event.tweets)
                    Source.REMOTE -> TweetsState.Success(event.tweets)
                } to null
            }
            is TweetsEvent.Click -> currentState to TweetsEffect.NavigateToSingleTweetScreen(event.tweet)
        }
    }

    private fun reduceTweetsLoadingError(
        state: TweetsState
    ): Pair<TweetsState, TweetsEffect?> {
        return when (state) {
            is TweetsState.MainProgress -> {
                TweetsState.Error(R.string.error_text) to null
            }
            is TweetsState.LastCachedTweets -> {
                TweetsState.Success(state.tweets) to TweetsEffect.ShowLoadingErrorSnackBar
            }
            is TweetsState.Error, is TweetsState.Success, is TweetsState.Initial -> {
                state to null
            }
        }
    }
}