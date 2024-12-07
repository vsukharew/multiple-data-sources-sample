package vsukharew.multiple.data.sources.data.source.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authors")
data class AuthorEntity(
    val name: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)