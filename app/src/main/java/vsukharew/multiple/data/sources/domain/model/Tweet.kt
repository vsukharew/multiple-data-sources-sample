package vsukharew.multiple.data.sources.domain.model

data class Tweet(
    val id: String,
    val message: String,
    val platform: Platform,
    val author: String,
    val avatarUrl: String,
)