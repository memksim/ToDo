package com.memksim.todo.ui.pages.main

import android.util.Log
import androidx.lifecycle.*
import com.memksim.todo.base.consts.*
import com.memksim.todo.base.exceptions.AddTaskException
import com.memksim.todo.base.exceptions.LoadDataException
import com.memksim.todo.base.exceptions.RemoveTaskException
import com.memksim.todo.base.exceptions.UpdateTaskException
import com.memksim.todo.domain.utils.enums.TaskDtoKey.*
import com.memksim.todo.domain.utils.enums.TaskState.*
import com.memksim.todo.domain.interactor.LoadDataInteractor
import com.memksim.todo.domain.interactor.UpdateDataInteractor
import com.memksim.todo.ui.base.BaseViewModel
import com.memksim.todo.ui.base.UiEvent
import com.memksim.todo.ui.converters.toDto
import com.memksim.todo.ui.converters.toItemUiState
import com.memksim.todo.ui.utils.enums.SearchAppBarState.*
import com.memksim.todo.ui.utils.enums.SortCondition.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainPageViewModel @Inject constructor(
    private val loadDataInteractor: LoadDataInteractor,
    private val updateDataInteractor: UpdateDataInteractor
) : BaseViewModel<MainPageUiState>() {

    private var _viewState: MutableStateFlow<MainPageUiState> = MutableStateFlow(MainPageUiState())
    val viewState: StateFlow<MainPageUiState> = _viewState

    init {
        loadData()
    }

    override fun handleEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            is MainPageEvent.SaveNewTask -> saveTask(uiEvent.task)
            is MainPageEvent.UpdateNewTaskInfo -> {
                updateNewTaskInfo(
                    title = uiEvent.title,
                    note = uiEvent.note,
                    date = uiEvent.date,
                    time = uiEvent.time
                )
            }
        }
    }

    private fun updateNewTaskInfo(
        title: String,
        note: String,
        date: String,
        time: String
    ) {
        val task = _viewState.value.newTask
        _viewState.value = _viewState.value.copy(
            newTask = task.copy(
                title = title.ifEmpty { task.title },
                note = note.ifEmpty { task.note },
                date = date.ifEmpty { task.date },
                time = time.ifEmpty { task.time }
            )
        )
    }

    private fun saveTask(task: MainPageItemUiState) {
        Log.d(TAG, "saveTask: $task")
        viewModelScope.launch {
            updateDataInteractor(task = task.toDto())
                .catch {
                    handleException(it)
                }
                .collect {
                    loadData()
                }
        }
    }

    private fun handleException(exception: Throwable) {
        _viewState.value = _viewState.value.copy(
            isLoading = false,
            toast = when (exception) {
                is LoadDataException -> {
                    LOAD_TASK_ERROR
                }
                is AddTaskException -> {
                    ADD_TASK_ERROR
                }
                is UpdateTaskException -> {
                    UPDATE_TASK_ERROR
                }
                is RemoveTaskException -> {
                    REMOVE_TASK_ERROR
                }
                else -> {
                    UNKNOWN_ERROR
                }
            }
        )
    }

    private fun loadData() {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(
                isLoading = true
            )
            loadDataInteractor()
                .catch {
                    handleException(it)
                }
                .collect {
                    Log.d(TAG, "loadData: $it")
                    _viewState.value = _viewState.value.copy(
                        tasks = it.map { item ->
                            item.toItemUiState()
                        },
                        isLoading = false,
                        newTask = MainPageItemUiState()
                    )
                }
        }
    }

    sealed class MainPageEvent : UiEvent {

        class UpdateNewTaskInfo(
            val title: String = "",
            val note: String = "",
            val date: String = "",
            val time: String = ""
        ) : MainPageEvent()

        class SaveNewTask(val task: MainPageItemUiState) : MainPageEvent()

    }

}