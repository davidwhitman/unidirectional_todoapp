package com.davidwhitman.unidirtodo.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.davidwhitman.unidirtodo.home.business.BusinessTodoActionEmitter
import com.davidwhitman.unidirtodo.home.business.TodoBusiness
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * @author David Whitman on 12/8/2017.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var currentState: HomeState = HomeState.Empty()
    private val stateHistory = mutableMapOf(Date() to currentState)

    fun bind(intentions: Observable<Intention>): LiveData<HomeState> {
        val liveData = MutableLiveData<HomeState>()

        intentions.subscribe { intention ->
            val actionToTake = when (intention) {
                is HomeViewModel.Intention.Load,
                is HomeViewModel.Intention.Refresh -> TodoBusiness.TodoAction.GetTodoList()
                is HomeViewModel.Intention.UpdateTodoItem -> TodoBusiness.TodoAction.UpdateTodoItem(key = intention.key, name = intention.name)
            }

            TodoBusiness.doAction(actionToTake)
        }

        BusinessTodoActionEmitter.relay
                .subscribeOn(AndroidSchedulers.mainThread())
                .map { result -> homeStateReducer(result, currentState) }
                .distinctUntilChanged()
                .subscribe { newState ->
                    currentState = newState
                    stateHistory[Date()] = newState
                    liveData.postValue(newState)
                }

        return liveData
    }

    private fun homeStateReducer(result: TodoBusiness.Result, currentState: HomeState): HomeState =
            when (result) {
                is TodoBusiness.Result.InFlight -> HomeState.Loading()
                is TodoBusiness.Result.GotTodoList -> HomeState.Loaded(result.todoList)
                is TodoBusiness.Result.ModifiedTodoList -> {
                    TodoBusiness.doAction(TodoBusiness.TodoAction.GetTodoList())
                    HomeState.Loading()
                }
            }

    sealed class Intention {
        abstract val description: String

        data class Load(override val description: String = "Load") : Intention()
        data class Refresh(override val description: String = "Refresh") : Intention()
        data class UpdateTodoItem(override val description: String = "UpdateTodoItem",
                                  val key: Long = Random().nextLong(),
                                  val name: String) : Intention()
    }
}