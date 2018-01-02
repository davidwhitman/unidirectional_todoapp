package com.davidwhitman.unidirtodo.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Thundercloud Dev on 12/8/2017.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var currentState: HomeState = HomeState.Empty()
    private val stateHistory = mutableMapOf(Date() to currentState)

    private val hardcodedItems = arrayListOf(
            TodoItem(Random().nextLong(), name = "Almond Milk"),
            TodoItem(Random().nextLong(), name = "Matar Paneer"),
            TodoItem(Random().nextLong(), name = "Greek Yogurt"))

    private val items: MutableMap<Long, TodoItem>

    val state: LiveData<HomeState> = Transformations.switchMap(ActionEmitter, { action ->
        MediatorLiveData<HomeState>().apply {
            addSource(middleware(action), {
                action?.let {
                    val newState = homeStateReducer(action, currentState)

                    if (newState != currentState) {
                        currentState = newState
                        stateHistory.put(Date(), newState)
                        value = newState
                    }
                }
            })
        }
    })

    init {
        items = hardcodedItems.map { it.key to it }.toMap().toMutableMap()
    }

    private fun middleware(action: Action): LiveData<Action> {
        when (action) {
            is Actions.TodoList.GetTodoList -> {
                Completable.timer(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            ActionEmitter.dispatch(Actions.TodoList.GotTodoList(items.values.toList()))
                        }
            }
            is Actions.TodoList.UpdateTodoItem -> {
                items.put(action.key, TodoItem(key = action.key, name = action.name))
                ActionEmitter.dispatch(Actions.TodoList.GetTodoList())
            }
        }

        return ActionEmitter
    }

    private fun homeStateReducer(action: Action, currentState: HomeState): HomeState =
            when (action) {
                is Actions.TodoList.GetTodoList -> HomeState.Loading()
                is Actions.TodoList.GotTodoList -> HomeState.Loaded(action.todoList)
                else -> currentState
            }
}