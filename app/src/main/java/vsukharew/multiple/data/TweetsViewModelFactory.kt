package vsukharew.multiple.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import vsukharew.multiple.data.sources.App
import vsukharew.multiple.data.sources.TweetsViewModel
import kotlin.reflect.KClass

class TweetsViewModelFactory(private val application: App) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return TweetsViewModel(application.serviceLocator.tweetsRepo) as T
    }
}