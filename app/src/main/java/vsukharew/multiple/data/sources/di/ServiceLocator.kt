package vsukharew.multiple.data.sources.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import vsukharew.multiple.data.sources.data.repository.TweetsRepo
import vsukharew.multiple.data.sources.data.repository.TweetsRepository
import vsukharew.multiple.data.sources.data.source.local.room.AppDatabase

class ServiceLocator(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
    ).build()
    private val gson = Gson()

    val tweetsRepo: TweetsRepo = TweetsRepository(
        authorDao = database.authorDao(),
        tweetDao = database.tweetDao(),
        platformDao = database.platformDao(),
        gson = gson,
        assets = context.assets
    )
}