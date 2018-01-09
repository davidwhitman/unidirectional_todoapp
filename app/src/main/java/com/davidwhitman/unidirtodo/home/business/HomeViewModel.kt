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
        MediatorLiveData<HomeState>()
                .apply {
                    val actionToTake = when (action) {
                        is HomeViewModel.TodoUiAction.OnLoad,
                        is HomeViewModel.TodoUiAction.OnRefresh -> TodoBusiness.TodoAction.GetTodoList()
                        is HomeViewModel.TodoUiAction.UpdateTodoItem -> TodoBusiness.TodoAction.UpdateTodoItem(key = action.key, name = action.name)
                    }

                    this.addSource(TodoBusiness.doAction(actionToTake), { result ->
                        val newState = homeStateReducer(result!!, currentState)

                        if (newState != currentState) {
                            currentState = newState
                            stateHistory.put(Date(), newState)
                            value = newState
                        }
                    })
                }
    })

    private fun homeStateReducer(result: TodoBusiness.Result, currentState: HomeState): HomeState =
            when (result) {
                is TodoBusiness.Result.InFlight -> HomeState.Loading()
                is TodoBusiness.Result.GotTodoList -> HomeState.Loaded(result.todoList)
                is TodoBusiness.Result.ModifiedTodoList -> {
                    TodoBusiness.doAction(TodoBusiness.TodoAction.GetTodoList())
                    HomeState.Loading()
                }
            }

    sealed class TodoUiAction : Action {
        data class OnLoad(override val description: String = "OnLoad") : TodoUiAction()
        data class OnRefresh(override val description: String = "OnRefresh") : TodoUiAction()
        data class UpdateTodoItem(override val description: String = "UpdateTodoItem",
                                  val key: Long = Random().nextLong(),
                                  val name: String) : TodoUiAction()
    }
}