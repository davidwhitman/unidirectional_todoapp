package com.davidwhitman.unidirtodo.home.business

import com.jakewharton.rxrelay2.PublishRelay

/**
 * @author David Whitman on 1/8/2018.
 */
internal object BusinessTodoActionEmitter {
    val relay = PublishRelay.create<TodoBusiness.Result>()
}

interface Action {
    val description: String
}