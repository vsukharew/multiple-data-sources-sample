package vsukharew.multiple.data.sources.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vsukharew.multiple.data.sources.data.source.local.room.entity.AuthorEntity

@Dao
interface AuthorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(author: AuthorEntity): Long

    @Query("DELETE FROM authors")
    suspend fun deleteAll()
}