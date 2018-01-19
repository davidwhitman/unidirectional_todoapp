package com.davidwhitman.unidirtodo.home.business

import com.davidwhitman.unidirtodo.home.TodoItem
import com.davidwhitman.unidirtodo.home.database.TodoItemDatabase
import com.davidwhitman.unidirtodo.home.mapFromDb
import com.davidwhitman.unidirtodo.home.mapToDb
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author David Whitman on 1/8/2018.
 */
internal object TodoBusiness {
    fun doAction(action: TodoAction) {
        val actionResults: Observable<Result> = when (action) {
            is TodoAction.GetTodoList -> {
                Single.timer(2, TimeUnit.SECONDS)
                        .toObservable()
                        .map {
                            Result.GotTodoList(todoList = TodoItemDatabase.getInstance().access
                                    .getItems()
                                    .mapFromDb()) as Result
                        }
                        .startWith(Result.InFlight())
                        .subscribeOn(Schedulers.io())
            }
            is TodoAction.UpdateTodoItem -> {
                Single.create<Result> {
                    TodoItemDatabase.getInstance().access.insertItem(TodoItem(key = action.key, name = action.name).mapToDb())
                    it.onSuccess(Result.ModifiedTodoList())
                }
                        .toObservable()
                        .subscribeOn(Schedulers.io())
            }
        }

        actionResults.subscribe { result -> BusinessTodoActionEmitter.relay.accept(result) }
    }

    sealed class TodoAction : Action {
        class GetTodoList(override val description: String = "GetTodoList") : TodoAction()
        data class UpdateTodoItem(override val description: String = "UpdateTodoItem",
                                  val key: Long = Random().nextLong(),
                                  val name: String) : TodoAction()
    }

    sealed class Result : Action {
        data class GotTodoList(override val description: String = "GotTodoList", val todoList: List<TodoItem>) : Result()
        data class ModifiedTodoList(override val description: String = "ModifiedTodoList") : Result()
        data class InFlight(override val description: String = "InFlight") : Result()
    }
}