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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import vsukharew.multiple.data.TweetsViewModelFactory
import vsukharew.multiple.data.sources.ui.composables.SingleTweetScreen
import vsukharew.multiple.data.sources.ui.composables.TweetsScreen
import vsukharew.multiple.data.sources.ui.theme.MultipledatasourcesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultipledatasourcesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = TweetsList
                    ) {
                        composable<TweetsList> {
                            TweetsScreen(
                                viewModel = viewModel(
                                    factory = TweetsViewModelFactory(
                                        application as App
                                    )
                                ),
                                onTweetClick = { navController.navigate(SingleTweet(it.id)) },
                                modifier = Modifier.padding(padding)
                            )
                        }
                        composable<SingleTweet> {
                            val route: SingleTweet = it.toRoute()
                            SingleTweetScreen(application as App, route.tweetId)
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data object TweetsList

@Serializable
data class SingleTweet(val tweetId: String)