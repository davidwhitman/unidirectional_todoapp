package com.davidwhitman.unidirtodo.home.business

import com.davidwhitman.unidirtodo.common.TodoItem
import com.davidwhitman.unidirtodo.common.database.AppDatabase
import com.davidwhitman.unidirtodo.common.database.mapFromDb
import com.davidwhitman.unidirtodo.common.database.mapToDb
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author David Whitman on 1/8/2018.
 */
internal object TodoBusiness {
    /**
     * Gets a list of items from the database (with artificial delay).
     * Starts with a [TodoResult.InFlight] result and concludes with a [TodoResult.GotTodoList].
     */
    fun getTodoList(): Observable<TodoResult> = Single.timer(Random().nextInt(2).toLong(), TimeUnit.SECONDS)
            .toObservable()
            .map {
                if (Random().nextInt(100) < 5) { // Throw an error 5% of the time
                    TodoResult.ErrorGettingList(exception = RuntimeException("Fake error!"))
                } else {
                    TodoResult.GotTodoList(todoList = AppDatabase.getInstance().access
                            .getItems()
                            .mapFromDb())
                }
            }
            .startWith(TodoResult.InFlight())
            .subscribeOn(Schedulers.io())

    fun updateTodoItem(item: TodoItem): Observable<TodoResult> =
            Single.create<TodoResult> {
                AppDatabase.getInstance().access.insertItem(TodoItem(key = item.key, name = item.name).mapToDb())
                it.onSuccess(TodoResult.ModifiedTodoList())
            }
                    .toObservable()
                    .subscribeOn(Schedulers.io())

    fun deleteItem(item: TodoItem): Observable<TodoResult> =
            Single.create<TodoResult> {
                AppDatabase.getInstance().access.deleteItem(item.mapToDb())
                it.onSuccess(TodoResult.ModifiedTodoList())
            }
                    .toObservable()
                    .subscribeOn(Schedulers.io())
}