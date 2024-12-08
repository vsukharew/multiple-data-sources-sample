package vsukharew.multiple.data.sources.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vsukharew.multiple.data.sources.data.source.local.room.entity.TweetCombined
import vsukharew.multiple.data.sources.data.source.local.room.entity.TweetEntity

@Dao
interface TweetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tweet: TweetEntity): Long

    @Query("SELECT COUNT(id) FROM tweets")
    suspend fun count(): Long

    @Query("SELECT " +
            "tweets.id AS id," +
            "tweets.message AS message," +
            "tweets.avatarUrl AS avatarUrl," +
            "authors.name AS author," +
            "platforms.name AS platform " +
            "FROM tweets, authors, platforms " +
            "WHERE authors.id = tweets.authorId " +
            "AND platforms.id = tweets.platformId " +
            "AND tweets.id = :tweetId"
    )
    suspend fun getTweetCombined(tweetId: String): TweetCombined?

    @Query("SELECT " +
            "tweets.id AS id," +
            "tweets.message AS message," +
            "tweets.avatarUrl AS avatarUrl," +
            "authors.name AS author," +
            "platforms.name AS platform " +
            "FROM tweets, authors, platforms " +
            "WHERE authors.id = tweets.authorId " +
            "AND platforms.id = tweets.platformId"
    )
    suspend fun getAllCombined(): List<TweetCombined>

    @Query("DELETE FROM tweets")
    suspend fun deleteAll()
}