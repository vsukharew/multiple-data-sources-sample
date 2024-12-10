package vsukharew.multiple.data.sources.ui.tweets.single

import vsukharew.multiple.data.sources.ui.base.Reducer

object SingleTweetReducer : Reducer<SingleTweetState, SingleTweetEvent, Nothing> {
    override fun reduce(
        currentState: SingleTweetState,
        event: SingleTweetEvent
    ): Pair<SingleTweetState, Nothing?> {
        return when (event) {
            SingleTweetEvent.FirstLoad -> SingleTweetState.MainProgress
            is SingleTweetEvent.TweetLoaded -> SingleTweetState.Success(event.tweet)
        } to null
    }
}