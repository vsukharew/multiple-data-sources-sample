package vsukharew.multiple.data.sources.ui.base

interface Reducer<State : Reducer.ViewState, Event : Reducer.Event, Effect : Reducer.Effect> {
    interface ViewState
    interface Event
    interface Effect

    fun reduce(currentState: State, event: Event): Pair<State, Effect?>
}