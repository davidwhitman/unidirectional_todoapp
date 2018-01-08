package com.davidwhitman.unidirtodo.home.business

import android.arch.lifecycle.MutableLiveData

/**
 * @author Thundercloud Dev on 12/8/2017.
 */
internal abstract class ActionEmitter<A : Action> : MutableLiveData<A>() {
    fun dispatch(action: A) {
        value = action
    }
}

interface Action {
    val description: String
}