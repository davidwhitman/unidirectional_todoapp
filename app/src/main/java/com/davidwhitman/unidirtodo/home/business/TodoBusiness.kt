package com.davidwhitman.unidirtodo.home.business

import android.arch.lifecycle.LiveData
import com.davidwhitman.unidirtodo.home.UiTodoActionEmitter
import com.davidwhitman.unidirtodo.home.TodoItem
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by dwhitman on 1/8/2018.
 */

internal object TodoBusiness {

    private val hardcodedItems = arrayListOf(
            TodoItem(Random().nextLong(), name = "Almond Milk"),
            TodoItem(Random().nextLong(), name = "Matar Paneer"),
            TodoItem(Random().nextLong(), name = "Greek Yogurt"))

    private val items: MutableMap<Long, TodoItem>

    init {
        items = hardcodedItems.map { it.key to it }.toMap().toMutableMap()
    }

    fun doAction(action: TodoAction): LiveData<Action> {
        when (action) {
            is TodoAction.GetTodoList -> {
                Completable.timer(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            UiTodoActionEmitter.dispatch(Result.GotTodoList(todoList = items.values.toList()))
                        }
            }
            is TodoAction.UpdateTodoItem -> {
                items.put(action.key, TodoItem(key = action.key, name = action.name))
                UiTodoActionEmitter.dispatch(TodoAction.GetTodoList())
            }
        }

        return UiTodoActionEmitter
    }

    sealed class TodoAction : Action {
        class GetTodoList(override val description: String = "GetTodoList") : TodoAction()
        data class UpdateTodoItem(override val description: String = "UpdateTodoItem",
                                  val key: Long = Random().nextLong(),
                                  val name: String) : TodoAction()
    }

    sealed class Result : Action {
        data class GotTodoList(override val description: String = "GotTodoList", val todoList: List<TodoItem>) : Result()
    }
}