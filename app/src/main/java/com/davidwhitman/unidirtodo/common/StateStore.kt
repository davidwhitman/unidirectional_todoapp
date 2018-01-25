package com.davidwhitman.unidirtodo.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import java.util.*

/**
 * @author David Whitman on 1/23/2018.
 */
object StateStore {
    private val innerState = MutableLiveData<AppState>()

    private val stateHistoryBackingField = mutableMapOf<Date, AppState>()

    val state = innerState as LiveData<AppState>

    val stateHistory = stateHistoryBackingField as Map<Date, AppState>

    fun dispatch(transformer: Function1<AppState, AppState>) {
        val newState = transformer(state.value!!)
        Timber.d { "State: $newState" }
        innerState.value = newState
        stateHistoryBackingField[Date()] = newState
    }

    init {
        innerState.value = AppState()
    }
}