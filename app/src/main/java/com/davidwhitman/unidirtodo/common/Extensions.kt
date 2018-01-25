package com.davidwhitman.unidirtodo.common

import android.arch.lifecycle.LiveData

/**
 * @author David Whitman on 1/25/2018.
 */
val <T> LiveData<T>.valueNN
    get() = this.value!!