package com.davidwhitman.unidirtodo.common

/**
 * @author David Whitman on 1/24/2018.
 */
data class User(val id: Long, val name: String, val lists: List<TodoList>)