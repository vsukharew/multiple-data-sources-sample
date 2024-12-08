package vsukharew.multiple.data.sources

import vsukharew.multiple.data.sources.domain.model.Tweet

sealed class SingleTweetState {
    data object MainProgress : SingleTweetState()
    data class Success(val tweet: Tweet) : SingleTweetState()
}