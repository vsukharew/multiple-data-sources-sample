package vsukharew.multiple.data.sources.ui.tweets.single

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vsukharew.multiple.data.sources.data.repository.TweetsRepo
import vsukharew.multiple.data.sources.domain.model.LoadStrategy
import vsukharew.multiple.data.sources.domain.type.Either
import vsukharew.multiple.data.sources.ui.base.BaseViewModel

class SingleTweetViewModel(
    private val tweetsRepository: TweetsRepo,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<SingleTweetState, SingleTweetEvent, Nothing>(
    SingleTweetReducer,
    SingleTweetState.Initial
) {

    fun startLoading() {
        savedStateHandle.get<String>(KEY_TWEET_ID)?.let(::getTweet)
    }

    fun getTweet(tweetId: String) {
        setEvent(SingleTweetEvent.FirstLoad)
        viewModelScope.launch {
            when (val getTweet = tweetsRepository.getTweet(tweetId, LoadStrategy.CACHE_ONLY)) {
                is Either.Left -> {
                    // don't expect error when loading from cache
                }
                is Either.Right -> {
                    setEvent(SingleTweetEvent.TweetLoaded(getTweet.data))
                }
            }
        }
    }

    companion object {
        const val KEY_TWEET_ID = "tweet_id"
    }
}