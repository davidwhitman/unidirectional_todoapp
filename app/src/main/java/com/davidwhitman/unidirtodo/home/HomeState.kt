package com.davidwhitman.unidirtodo.home

import com.davidwhitman.unidirtodo.common.TodoItem

/**
 * @author David Whitman on 12/8/2017.
 */
sealed class HomeState(open val description: String) {
    data class Empty(override val description: String = "Empty") : HomeState(description)

    data class Loading(override val description: String = "Loading") : HomeState(description)

    data class Loaded(override val description: String = "Loaded", val items: List<TodoItem>) : HomeState(description)
}