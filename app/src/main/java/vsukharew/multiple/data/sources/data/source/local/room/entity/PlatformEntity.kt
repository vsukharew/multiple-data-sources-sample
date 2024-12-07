package vsukharew.multiple.data.sources.data.source.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platforms")
data class PlatformEntity(
    val name: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)