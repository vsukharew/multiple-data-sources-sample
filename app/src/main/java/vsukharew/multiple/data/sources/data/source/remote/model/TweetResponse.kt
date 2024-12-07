package vsukharew.multiple.data.sources.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class TweetResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("avatarUrl")
    val avatarUrl: String,
)