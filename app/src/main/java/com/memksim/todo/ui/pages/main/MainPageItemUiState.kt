package com.memksim.todo.ui.pages.main

import com.memksim.todo.domain.utils.enums.TaskState
import com.memksim.todo.ui.base.ItemUiState

data class MainPageItemUiState(
    val id: Int,
    val title: String,
    val note: String,
    val date: String,
    val time: String,
    val isCompleted: Boolean,
    val itemState: TaskState
): ItemUiState