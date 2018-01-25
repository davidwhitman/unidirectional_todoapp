package com.davidwhitman.unidirtodo.home.business

import com.davidwhitman.unidirtodo.common.TodoItem

/**
 * @author David Whitman on 1/24/2018.
 */
sealed class TodoResult : com.davidwhitman.unidirtodo.home.business.Result {
    data class GotTodoList(override val description: String = "GotTodoList", val todoList: List<TodoItem>) : TodoResult()
    data class ModifiedTodoList(override val description: String = "ModifiedTodoList") : TodoResult()
    data class InFlight(override val description: String = "InFlight") : TodoResult()
    data class ErrorGettingList(val exception: Exception, override val description: String = exception.message
            ?: "") : TodoResult()
    data class DismissedError(override val description: String = "DismissedError") : TodoResult()
}