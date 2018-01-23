package com.davidwhitman.unidirtodo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import java.util.*

/**
 * @author David Whitman on 1/23/2018.
 */
object StateStore {
    private val innerState = MutableLiveData<State>()

    private val stateHistoryBackingField = mutableMapOf<Date, State>()

    val state = innerState as LiveData<State>

    val stateHistory = stateHistoryBackingField as Map<Date, State>

    fun dispatch(transformer: Function1<State, State>) {
        val newState = transformer(state.value!!)
        Timber.d { "State: $newState" }
        innerState.value = newState
        stateHistoryBackingField[Date()] = newState
    }

    init {
        innerState.value = State()
    }
}