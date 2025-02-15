package vsukharew.multiple.data.sources.ui.tweets.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import vsukharew.multiple.data.sources.R
import vsukharew.multiple.data.sources.data.repository.TweetsRepo
import vsukharew.multiple.data.sources.domain.model.LoadStrategy
import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.domain.type.Either.Left
import vsukharew.multiple.data.sources.domain.type.Either.Right
import vsukharew.multiple.data.sources.ui.base.BaseViewModel

class TweetsViewModel(
    private val tweetsRepo: TweetsRepo,
    tweetsReducer: TweetsReducer,
) : BaseViewModel<TweetsState, TweetsEvent, TweetsEffect>(tweetsReducer, TweetsState.Initial) {

    fun startLoading() {
        setEvent(TweetsEvent.FirstLoad)
        viewModelScope.launch {
            getTweets(LoadStrategy.CACHE_FIRST)
        }
    }

    fun onRetryClick() {
        setEvent(TweetsEvent.Refresh)
        viewModelScope.launch {
            getTweets(LoadStrategy.REMOTE_ONLY)
        }
    }

    fun onTweetClick(tweet: Tweet) {
        setEvent(TweetsEvent.Click(tweet))
    }

    private suspend fun getTweets(loadStrategy: LoadStrategy) {
        tweetsRepo.getTweets(loadStrategy).onEach {
            when (it) {
                is Left -> {
                    setEvent(TweetsEvent.TweetsLoadingError(R.string.error_text))
                }
                is Right -> {
                    val (tweets, source) = it.data
                    setEvent(TweetsEvent.TweetsLoaded(tweets, source))
                }
            }
        }.launchIn(viewModelScope)
    }
}