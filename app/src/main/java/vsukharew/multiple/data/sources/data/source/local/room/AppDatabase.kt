package vsukharew.multiple.data.sources.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import vsukharew.multiple.data.sources.data.source.local.room.dao.PlatformDao
import vsukharew.multiple.data.sources.data.source.local.room.dao.AuthorDao
import vsukharew.multiple.data.sources.data.source.local.room.dao.TweetDao
import vsukharew.multiple.data.sources.data.source.local.room.entity.PlatformEntity
import vsukharew.multiple.data.sources.data.source.local.room.entity.AuthorEntity
import vsukharew.multiple.data.sources.data.source.local.room.entity.TweetEntity

@Database(
    entities = [
        PlatformEntity::class,
        AuthorEntity::class,
        TweetEntity::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun platformDao(): PlatformDao
    abstract fun authorDao(): AuthorDao
    abstract fun tweetDao(): TweetDao
}