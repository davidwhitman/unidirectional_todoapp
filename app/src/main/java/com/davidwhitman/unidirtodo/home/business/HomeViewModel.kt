package com.davidwhitman.unidirtodo.home.business

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import com.davidwhitman.unidirtodo.home.HomeState
import com.davidwhitman.unidirtodo.home.UiTodoActionEmitter
import java.util.*

/**
 * @author Thundercloud Dev on 12/8/2017.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var currentState: HomeState = HomeState.Empty()
    private val stateHistory = mutableMapOf(Date() to currentState)

    val state: LiveData<HomeState> = Transformations.switchMap(UiTodoActionEmitter, { action ->

        val actionToTake = when (action) {
            is HomeViewModel.TodoUiAction.OnLoad,
            is HomeViewModel.TodoUiAction.OnRefresh -> TodoBusiness.TodoAction.GetTodoList()
            is HomeViewModel.TodoUiAction.UpdateTodoItem -> TodoBusiness.TodoAction.UpdateTodoItem(key = action.key, name = action.name)
        }

        MediatorLiveData<HomeState>().apply {
            addSource(TodoBusiness.doAction(actionToTake), {
                action.let {
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

    private fun homeStateReducer(action: Action, currentState: HomeState): HomeState =
            when (action) {
                is TodoBusiness.TodoAction.GetTodoList -> HomeState.Loading()
                is TodoBusiness.Result.GotTodoList -> HomeState.Loaded(action.todoList)
                else -> currentState
            }

    sealed class TodoUiAction : Action {
        data class OnLoad(override val description: String = "OnLoad") : TodoUiAction()
        data class OnRefresh(override val description: String = "OnRefresh") : TodoUiAction()
        data class UpdateTodoItem(override val description: String = "UpdateTodoItem",
                                  val key: Long = Random().nextLong(),
                                  val name: String) : TodoUiAction()
    }
}