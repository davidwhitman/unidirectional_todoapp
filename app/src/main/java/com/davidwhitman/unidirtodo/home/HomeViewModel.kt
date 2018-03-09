package com.davidwhitman.unidirtodo.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.davidwhitman.unidirtodo.common.AppState
import com.davidwhitman.unidirtodo.common.StateStore
import com.davidwhitman.unidirtodo.common.TodoItem
import com.davidwhitman.unidirtodo.home.business.Action
import com.davidwhitman.unidirtodo.home.business.TodoBusiness
import com.davidwhitman.unidirtodo.home.business.TodoResult
import com.github.ajalt.timberkt.Timber
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import java.util.*

/**
 * @author David Whitman on 12/8/2017.
 */
class HomeViewModel : ViewModel() {
    /**
     * Takes the previous [HomeState] and applies a [TodoResult] to it, which results in a new [HomeState].
     * This is a pure function; absolutely no side effects!
     */
    private val reducer: BiFunction<HomeState, TodoResult, HomeState> = BiFunction { oldState, result ->
        when (result) {
            is TodoResult.InFlight,
            is TodoResult.ModifiedTodoList ->
                oldState.copy(refreshing = true)
            is TodoResult.GotTodoList ->
                oldState.copy(items = result.todoList, refreshing = false)
            is TodoResult.ErrorGettingList ->
                oldState.copy(error = result.exception.message)
            is TodoResult.DismissedError ->
                oldState.copy(error = null, refreshing = false)
        }
    }

    /**
     * Takes in a stream of [Intention]s (events from the UI) and returns a [LiveData] that emits state changes.
     * In general, an intention from the UI (eg user clicks on "Refresh") will result in one or more new states
     * (eg "Loading" and then "Loaded")
     */
    internal fun bind(incomingIntentions: Observable<Intention>): LiveData<AppState> {
        incomingIntentions
                .doOnNext { Timber.d { "Intention: $it" } }
                .map { intention -> mapIntentionToAction(intention) }
                .doOnNext { Timber.d { "Action: $it" } }
                .flatMap { action -> mapActionToResult(action) }
                .doOnNext { Timber.d { "Result: $it" } }
                .scan(HomeState(), reducer)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { newState -> StateStore.dispatch({ oldState -> oldState.copy(homeState = newState) }) }

        return StateStore.state
    }

    private fun mapActionToResult(action: TodoAction): Observable<TodoResult>? {
        return when (action) {
            is TodoAction.GetTodoList -> TodoBusiness.getTodoList()
            is TodoAction.UpdateTodoItem ->
                TodoBusiness.updateTodoItem(TodoItem(action.key, action.name))
                        .flatMap { TodoBusiness.getTodoList() }
            is TodoAction.DeleteItem -> TodoBusiness.deleteItem(action.item)
                    .flatMap { TodoBusiness.getTodoList() }
            is TodoAction.DismissError -> Observable.just(TodoResult.DismissedError())
        }
    }

    private fun mapIntentionToAction(intention: Intention): TodoAction {
        return when (intention) {
            Intention.Load,
            Intention.Refresh -> TodoAction.GetTodoList()
            is Intention.UpdateTodoItem -> TodoAction.UpdateTodoItem(key = intention.key, name = intention.name)
            is Intention.DeleteItem -> TodoAction.DeleteItem(item = intention.item)
            Intention.DismissError -> TodoAction.DismissError()
        }
    }

    /**
     * The possible things the UI can do.
     */
    internal sealed class Intention {
        object Load : Intention()
        object Refresh : Intention()
        data class UpdateTodoItem(val key: Long = Random().nextLong(),
                                  val name: String) : Intention()

        data class DeleteItem(val item: TodoItem) : Intention()

        object DismissError : Intention()
    }

    /**
     * The possible [Action]s that may be taken as a result of the UI intentions.
     */
    sealed class TodoAction : Action {
        data class GetTodoList(override val description: String = "GetTodoList") : TodoAction()
        data class UpdateTodoItem(override val description: String = "UpdateTodoItem",
                                  val key: Long = Random().nextLong(),
                                  val name: String) : TodoAction()

        data class DeleteItem(override val description: String = "DeleteItem",
                              val item: TodoItem) : TodoAction()

        data class DismissError(override val description: String = "DismissError") : TodoAction()
    }
}