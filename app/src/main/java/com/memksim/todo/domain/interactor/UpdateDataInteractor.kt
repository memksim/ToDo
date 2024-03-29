package com.memksim.todo.domain.interactor

import android.util.Log
import com.memksim.todo.utils.consts.INTERACTOR_MESSAGE
import com.memksim.todo.utils.consts.TAG
import com.memksim.todo.utils.exceptions.AddTaskException
import com.memksim.todo.utils.exceptions.DatabaseException
import com.memksim.todo.utils.exceptions.RemoveTaskException
import com.memksim.todo.utils.exceptions.UpdateTaskException
import com.memksim.todo.domain.utils.enums.TaskState.*
import com.memksim.todo.domain.model.TaskDto
import com.memksim.todo.domain.use_case.AddTaskUseCase
import com.memksim.todo.domain.use_case.RemoveTaskUseCase
import com.memksim.todo.domain.use_case.UpdateTaskUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class UpdateDataInteractor @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val removeTaskUseCase: RemoveTaskUseCase
) {

    suspend operator fun invoke(task: TaskDto): Flow<Unit> = flow {
        try {
            when(task.state){
                NEW -> {
                    addTaskUseCase.invoke(task = task)
                }
                UPDATED -> {
                    updateTaskUseCase.invoke(task = task)
                }
                COMPLETED -> {
                    removeTaskUseCase.invoke(task = task)
                }
                SAME -> {
                    updateTaskUseCase.invoke(task = task)
                }
            }
        } catch (e: DatabaseException){
            Log.e(TAG, INTERACTOR_MESSAGE, e)
            throw when(e){
                is AddTaskException -> AddTaskException()
                is UpdateTaskException -> UpdateTaskException()
                is RemoveTaskException -> RemoveTaskException()
                else -> Exception()
            }
        }
        emit(Unit)
    }

}