package vsukharew.multiple.data.sources

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import vsukharew.multiple.data.TweetsViewModelFactory
import vsukharew.multiple.data.sources.ui.composables.TweetsScreen
import vsukharew.multiple.data.sources.ui.theme.MultipledatasourcesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultipledatasourcesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    TweetsScreen(
                        modifier = Modifier.padding(it),
                        viewModel = viewModel(factory = TweetsViewModelFactory(application as App))
                    )
                }
            }
        }
    }
}