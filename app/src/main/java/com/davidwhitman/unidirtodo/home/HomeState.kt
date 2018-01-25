package com.davidwhitman.unidirtodo.home

import com.davidwhitman.unidirtodo.common.TodoItem

/**
 * @author David Whitman on 12/8/2017.
 */
data class HomeState(val items: List<TodoItem> = emptyList(),
                     val error: String? = null,
                     val refreshing: Boolean = false)