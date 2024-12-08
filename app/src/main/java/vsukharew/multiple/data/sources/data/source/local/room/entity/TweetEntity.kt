package vsukharew.multiple.data.sources.data.source.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tweets",
    foreignKeys = [
        ForeignKey(
            entity = AuthorEntity::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
        ),
        ForeignKey(
            entity = PlatformEntity::class,
            parentColumns = ["id"],
            childColumns = ["platformId"],
        )
    ]
)
data class TweetEntity(
    @PrimaryKey val id: String,
    val message: String,
    val authorId: Long,
    val platformId: Long,
    val avatarUrl: String
)