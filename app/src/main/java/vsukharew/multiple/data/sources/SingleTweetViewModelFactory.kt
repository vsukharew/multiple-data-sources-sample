package vsukharew.multiple.data.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import vsukharew.multiple.data.sources.SingleTweetViewModel.Companion.KEY_TWEET_ID

class SingleTweetViewModelFactory(
    private val app: App,
    private val tweetId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle().apply {
            set(KEY_TWEET_ID, tweetId)
        }
        return SingleTweetViewModel(app.serviceLocator.tweetsRepo, savedStateHandle) as T
    }
}