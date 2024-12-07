package vsukharew.multiple.data.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vsukharew.multiple.data.sources.data.repository.TweetsRepo
import vsukharew.multiple.data.sources.domain.model.LoadStrategy
import vsukharew.multiple.data.sources.domain.model.Source
import vsukharew.multiple.data.sources.domain.type.Either.Left
import vsukharew.multiple.data.sources.domain.type.Either.Right

class TweetsViewModel(
    private val tweetsRepo: TweetsRepo
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<TweetsState>(TweetsState.Initial)
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTweets(LoadStrategy.CACHE_FIRST)
        }
    }

    fun retry() {
        mutableUiState.value = TweetsState.MainProgress
        viewModelScope.launch {
            getTweets(LoadStrategy.REMOTE_ONLY)
        }
    }

    private suspend fun getTweets(loadStrategy: LoadStrategy) {
        mutableUiState.value = TweetsState.MainProgress
        when (val tweets = tweetsRepo.getTweets(loadStrategy)) {
            is Left -> {
                val state = when (val currentState = uiState.value) {
                    is TweetsState.MainProgress -> TweetsState.Error(R.string.error_text)
                    is TweetsState.LastCachedTweets -> TweetsState.Success(currentState.tweets)
                    is TweetsState.Error, is TweetsState.Success, is TweetsState.Initial -> currentState
                }
                mutableUiState.value = state
            }
            is Right -> tweets.data.collect { (tweets, source) ->
                val state = when (source) {
                    Source.CACHE -> if (tweets.isNotEmpty()) {
                        TweetsState.LastCachedTweets(tweets)
                    } else {
                        TweetsState.MainProgress
                    }
                    Source.REMOTE -> TweetsState.Success(tweets)
                }
                mutableUiState.value = state
            }
        }
    }
}