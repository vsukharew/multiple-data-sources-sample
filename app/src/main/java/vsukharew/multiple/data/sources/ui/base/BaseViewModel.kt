package vsukharew.multiple.data.sources.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class BaseViewModel<State : Reducer.ViewState, Event : Reducer.Event, Effect : Reducer.Effect>(
    private val reducer: Reducer<State, Event, Effect>,
    defaultUiState: State
) : ViewModel() {
    private val mutableUiState: MutableStateFlow<State> = MutableStateFlow(defaultUiState)
    private val mutableSideEffectsFlow: MutableSharedFlow<Effect> = MutableSharedFlow()
    val sideEffectsFlow = mutableSideEffectsFlow.asSharedFlow()
    val uiState = mutableUiState.asStateFlow()

    fun setEvent(event: Event) {
        val (state, effect) = reducer.reduce(uiState.value, event)
        Log.d("STATE", "$state")
        mutableUiState.update { state }
        effect?.let {
            viewModelScope.launch {
                mutableSideEffectsFlow.emit(it)
            }
        }
    }
}