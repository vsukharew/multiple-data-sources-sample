package vsukharew.multiple.data.sources.ui.tweets.single

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vsukharew.multiple.data.sources.data.repository.TweetsRepo
import vsukharew.multiple.data.sources.domain.model.LoadStrategy
import vsukharew.multiple.data.sources.domain.type.Either

class SingleTweetViewModel(
    private val tweetsRepository: TweetsRepo,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<SingleTweetState>(SingleTweetState.MainProgress)
    val uiState = mutableUiState.asStateFlow()

    init {
        savedStateHandle.get<String>(KEY_TWEET_ID)?.let(::getTweet)
    }

    fun getTweet(tweetId: String) {
        viewModelScope.launch {
            when (val tweet = tweetsRepository.getTweet(tweetId, LoadStrategy.CACHE_ONLY)) {
                is Either.Left -> {}
                is Either.Right -> {
                    mutableUiState.value = SingleTweetState.Success(tweet.data)
                }
            }
        }
    }

    companion object {
        const val KEY_TWEET_ID = "tweet_id"
    }
}