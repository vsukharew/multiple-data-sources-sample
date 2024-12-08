package vsukharew.multiple.data.sources

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import vsukharew.multiple.data.sources.ui.theme.MultipledatasourcesTheme
import vsukharew.multiple.data.sources.ui.tweets.SingleTweetRoute
import vsukharew.multiple.data.sources.ui.tweets.TweetsListRoute
import vsukharew.multiple.data.sources.ui.tweets.composables.SingleTweetScreen
import vsukharew.multiple.data.sources.ui.tweets.composables.TweetsScreen

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
                        startDestination = TweetsListRoute
                    ) {
                        val app = application as App
                        composable<TweetsListRoute> {
                            TweetsScreen(
                                app = app,
                                onTweetClick = { navController.navigate(SingleTweetRoute(it.id)) },
                                modifier = Modifier.padding(padding)
                            )
                        }
                        composable<SingleTweetRoute> {
                            val route = it.toRoute<SingleTweetRoute>()
                            SingleTweetScreen(app = app, tweetId = route.tweetId)
                        }
                    }
                }
            }
        }
    }
}