package vsukharew.multiple.data.sources.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vsukharew.multiple.data.sources.R
import vsukharew.multiple.data.sources.TweetsState
import vsukharew.multiple.data.sources.TweetsViewModel
import vsukharew.multiple.data.sources.domain.model.Platform
import vsukharew.multiple.data.sources.domain.model.Tweet
import vsukharew.multiple.data.sources.ui.theme.MultipledatasourcesTheme

@Composable
fun TweetsScreen(
    modifier: Modifier = Modifier,
    viewModel: TweetsViewModel
) {
    when (val uiState = viewModel.uiState.collectAsState().value) {
        is TweetsState.Initial -> {}
        is TweetsState.MainProgress -> MainProgress(modifier)
        is TweetsState.Error -> ErrorState(uiState, viewModel::retry, modifier)
        is TweetsState.LastCachedTweets -> LastCachedTweets(uiState.tweets)
        is TweetsState.Success -> ActualTweets(uiState.tweets, viewModel::retry)
    }
}

@Composable
fun MainProgress(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastCachedTweets(
    tweets: List<Tweet>
) {
    PullToRefreshBox(isRefreshing = true, onRefresh = {}) {
        Tweets(tweets)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualTweets(
    tweets: List<Tweet>,
    onRefresh: () -> Unit,
) {
    PullToRefreshBox(isRefreshing = false, onRefresh = onRefresh) {
        Tweets(tweets)
    }
}

@Composable
fun Tweets(
    tweets: List<Tweet>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(tweets) { Tweet(it) }
    }
}

@Composable
fun Tweet(
    tweet: Tweet,
    modifier: Modifier = Modifier
) {
    with(tweet) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp,
            modifier = modifier.padding(8.dp).clickable{}
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "$author: $message"
            )
        }
    }
}

@Composable
fun ErrorState(
    state: TweetsState.Error,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.twotone_error_outline_24),
            modifier = Modifier.size(120.dp),
            contentDescription = null
        )
        Text(stringResource(R.string.error_text))
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = onClick
        ) { Text(stringResource(R.string.retry_btn)) }
    }
}

@Composable
@Preview(showBackground = true)
fun MainProgressPreview() {
    MultipledatasourcesTheme {
        MainProgress()
    }
}

@Composable
@Preview(showBackground = true)
fun LastCachedTweetsPreview() {
    LastCachedTweets(
        listOf(
            Tweet(
                id = "1",
                message = "Have you tried writing gradle.kts scripts?",
                platform = Platform.ANDROID,
                author = "Elon Musk",
                "hero.png"
            ),
            Tweet(
                id = "1",
                message = "Have you tried writing gradle.kts scripts?",
                platform = Platform.ANDROID,
                author = "Elon Musk",
                "hero.png"
            ),
            Tweet(
                id = "1",
                message = "Have you tried writing gradle.kts scripts?",
                platform = Platform.ANDROID,
                author = "Elon Musk",
                "hero.png"
            )
        )
    )
}

@Composable
@Preview(showBackground = true)
fun ActualTweetsPreview() {
    ActualTweets(
        tweets = listOf(
            Tweet(
                id = "1",
                message = "Have you tried writing gradle.kts scripts?",
                platform = Platform.ANDROID,
                author = "Elon Musk",
                "hero.png"
            ),
            Tweet(
                id = "1",
                message = "Have you tried writing gradle.kts scripts?",
                platform = Platform.ANDROID,
                author = "Elon Musk",
                "hero.png"
            ),
            Tweet(
                id = "1",
                message = "Have you tried writing gradle.kts scripts?",
                platform = Platform.ANDROID,
                author = "Elon Musk",
                "hero.png"
            )
        )
    ) {}
}

@Composable
@Preview(showBackground = true)
fun TweetPreview() {
    Tweet(
        Tweet(
            id = "1",
            message = "Have you tried writing gradle.kts scripts?",
            platform = Platform.ANDROID,
            author = "Elon Musk",
            "hero.png"
        )
    )
}

@Composable
@Preview(showBackground = true, widthDp = 320, heightDp = 480)
fun ErrorStatePreview() {
    MultipledatasourcesTheme {
        ErrorState(
            state = TweetsState.Error(R.string.error_text),
            onClick = {}
        )
    }
}