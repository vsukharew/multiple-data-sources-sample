package vsukharew.multiple.data.sources.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import vsukharew.multiple.data.sources.App
import vsukharew.multiple.data.sources.SingleTweetState
import vsukharew.multiple.data.sources.SingleTweetViewModel
import vsukharew.multiple.data.sources.SingleTweetViewModelFactory
import vsukharew.multiple.data.sources.domain.model.Tweet

@Composable
fun SingleTweetLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun SingleTweet(tweet: Tweet, modifier: Modifier = Modifier) {
    with(tweet) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp,
            modifier = modifier.padding(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "$author: $message"
            )
        }
    }
}

@Composable
fun SingleTweetScreen(
    app: App,
    tweetId: String,
    viewModel: SingleTweetViewModel = viewModel(factory = SingleTweetViewModelFactory(app, tweetId))
) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is SingleTweetState.MainProgress -> SingleTweetLoading()
        is SingleTweetState.Success -> SingleTweet(state.tweet)
    }
}
