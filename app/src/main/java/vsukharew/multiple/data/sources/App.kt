package vsukharew.multiple.data.sources

import android.app.Application
import vsukharew.multiple.data.sources.di.ServiceLocator

class App : Application() {
    val serviceLocator by lazy {
        ServiceLocator(this)
    }
}