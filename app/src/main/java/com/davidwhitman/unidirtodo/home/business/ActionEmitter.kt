package com.davidwhitman.unidirtodo.home.business

import android.arch.lifecycle.MutableLiveData

/**
 * @author David Whitman on 12/8/2017.
 */
internal abstract class ActionEmitter<A> : MutableLiveData<A>() {
    fun dispatch(action: A) {
        value = action
    }
}