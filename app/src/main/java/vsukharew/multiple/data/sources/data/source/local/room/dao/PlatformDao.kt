package vsukharew.multiple.data.sources.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vsukharew.multiple.data.sources.data.source.local.room.entity.PlatformEntity

@Dao
interface PlatformDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(platform: PlatformEntity): Long

    @Query("DELETE FROM platforms")
    suspend fun deleteAll()
}