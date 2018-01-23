package com.davidwhitman.unidirtodo.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.davidwhitman.unidirtodo.home.business.Action
import com.davidwhitman.unidirtodo.home.business.TodoBusiness
import com.github.ajalt.timberkt.Timber
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.*

/**
 * @author David Whitman on 12/8/2017.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var currentState: HomeState = HomeState.Empty()
    private val stateHistory = mutableMapOf(Date() to currentState)

    /**
     * Takes the previous [HomeState] and applies a [TodoBusiness.TodoResult] to it, which results in a new [HomeState].
     * This is a pure function; absolutely no side effects!
     */
    private val homeStateReducer: BiFunction<HomeState, TodoBusiness.TodoResult, HomeState> = BiFunction { _, result ->
        when (result) {
            is TodoBusiness.TodoResult.InFlight -> HomeState.Loading()
            is TodoBusiness.TodoResult.GotTodoList -> HomeState.Loaded(items = result.todoList)
            is TodoBusiness.TodoResult.ModifiedTodoList -> HomeState.Loading()
        }
    }

    /**
     * Takes in a stream of [Intention]s (events from the UI) and returns a [LiveData] that emits state changes.
     * In general, an intention from the UI (eg user clicks on "Refresh") will result in one or more new states
     * (eg "Loading" and then "Loaded")
     */
    internal fun bind(incomingIntentions: Observable<Intention>): LiveData<HomeState> =
            MutableLiveData<HomeState>()
                    .apply {
                        incomingIntentions
                                .doOnNext { Timber.d { "Intention: $it" } }
                                .map { intention -> mapIntentionToAction(intention) }
                                .doOnNext { Timber.d { "Action: $it" } }
                                .flatMap { action -> mapActionToResult(action) }
                                .doOnNext { Timber.d { "Result: $it" } }
                                .scan(HomeState.Empty(), homeStateReducer)
                                .distinctUntilChanged()
                                .doOnNext { Timber.d { "State: $it" } }
                                .subscribe { newState ->
                                    val liveData = this
                                    currentState = newState
                                    stateHistory[Date()] = newState
                                    liveData.postValue(newState)
                                }
                    }

    private fun mapActionToResult(action: TodoAction): Observable<TodoBusiness.TodoResult>? {
        return when (action) {
            is TodoAction.GetTodoList -> TodoBusiness.getTodoList()
            is TodoAction.UpdateTodoItem ->
                TodoBusiness.updateTodoItem(TodoItem(action.key, action.name))
                        .flatMap { TodoBusiness.getTodoList() }
            is TodoAction.DeleteItem -> TodoBusiness.deleteItem(action.item)
                    .flatMap { TodoBusiness.getTodoList() }
        }
    }

    private fun mapIntentionToAction(intention: Intention): TodoAction {
        return when (intention) {
            is Intention.Load,
            is Intention.Refresh -> TodoAction.GetTodoList()
            is Intention.UpdateTodoItem -> TodoAction.UpdateTodoItem(key = intention.key, name = intention.name)
            is Intention.DeleteItem -> TodoAction.DeleteItem(item = intention.item)
        }
    }

    /**
     * The possible things the UI can do.
     */
    internal sealed class Intention {
        abstract val description: String

        data class Load(override val description: String = "Load") : Intention()
        data class Refresh(override val description: String = "Refresh") : Intention()
        data class UpdateTodoItem(override val description: String = "UpdateTodoItem",
                                  val key: Long = Random().nextLong(),
                                  val name: String) : Intention()

        data class DeleteItem(override val description: String = "DeleteItem",
                              val item: TodoItem) : Intention()
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
    }
}