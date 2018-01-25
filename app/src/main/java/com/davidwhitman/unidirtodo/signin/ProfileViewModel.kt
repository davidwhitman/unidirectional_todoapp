package com.davidwhitman.unidirtodo.signin

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.davidwhitman.unidirtodo.common.*
import com.davidwhitman.unidirtodo.home.business.TodoBusiness
import com.davidwhitman.unidirtodo.home.business.TodoResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import java.util.*

/**
 * @author David Whitman on 1/23/2018.
 */
class ProfileViewModel : ViewModel() {
    val user = User(Random().nextLong(), "Test User", listOf(TodoList(emptyList())))

    private val reducer: BiFunction<ProfileState, in TodoResult, ProfileState> = BiFunction { oldState, result ->
        when (result) {
            is TodoResult.GotTodoList -> oldState.copy(username = user.name, numberOfItems = result.todoList.count())
            else -> oldState.copy(username = user.name)
        }
    }

    internal fun bind(intentions: Observable<Intention>): LiveData<AppState> {
        intentions
                .flatMap {
                    when (it) {
                        is Intention.Load -> {
                            if (StateStore.state.valueNN.homeState.items.isNotEmpty()) {
                                Observable.just(TodoResult.GotTodoList(todoList = StateStore.state.valueNN.homeState.items))
                            } else {
                                TodoBusiness.getTodoList()
                            }
                        }
                    }
                }
                .scan(ProfileState(), reducer)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { newState -> StateStore.dispatch({ oldState -> oldState.copy(profileState = newState) }) }

        return StateStore.state
    }

    internal sealed class Intention {
        object Load : Intention()
    }
}