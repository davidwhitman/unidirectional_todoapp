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

    private val items = hardcodedItems.map { it.key to it }.toMap().toMutableMap()

    val state: LiveData<HomeState> = Transformations.switchMap(ActionCreator) {
        MediatorLiveData<HomeState>().apply {
            addSource(middleware(it)) {
                it?.let {
                    val newState = homeStateReducer(it, currentState)

                    if (newState != currentState) {
                        currentState = newState
                        stateHistory.put(Date(), newState)
                        value = newState
                    }
                }
            }
        }
    }

    private fun middleware(action: Action): LiveData<Action> {
        return when (action) {
            is Actions.TodoList.GetTodoList -> ActionCreator
                    .apply {
                        Completable.timer(1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    value = Actions.TodoList.GotTodoList(items.values.toList())
                                }
                    }
            is Actions.TodoList.UpdateTodoItem -> ActionCreator
                    .apply {
                        items.put(action.key, TodoItem(key = action.key, name = action.name))
                        ActionCreator.dispatch(Actions.TodoList.GetTodoList())
                    }
            else -> ActionCreator
        }
    }

    private fun homeStateReducer(action: Action, currentState: HomeState) =
            when (action) {
                is Actions.TodoList.GetTodoList -> HomeState.Loading()
                is Actions.TodoList.GotTodoList -> HomeState.Loaded(action.todoList)
                else -> currentState
            }
}