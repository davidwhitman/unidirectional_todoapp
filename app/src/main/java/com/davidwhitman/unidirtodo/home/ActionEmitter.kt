package com.davidwhitman.unidirtodo.home

import android.arch.lifecycle.MutableLiveData
import java.util.*

/**
 * @author Thundercloud Dev on 12/8/2017.
 */
object ActionEmitter : MutableLiveData<Action>() {
    fun dispatch(action: Action) {
        value = action
    }
}

sealed class Actions : Action {
    sealed class TodoList : Actions() {
        class GetTodoList : TodoList()
        data class GotTodoList(val todoList: List<TodoItem>) : TodoList()
        data class UpdateTodoItem(val key: Long = Random().nextLong(), val name: String) : TodoList()
    }
}

interface Action