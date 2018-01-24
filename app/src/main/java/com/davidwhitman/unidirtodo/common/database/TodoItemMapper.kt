package com.davidwhitman.unidirtodo.common.database

import com.davidwhitman.unidirtodo.common.TodoItem

/**
 * @author David Whitman on 1/18/2018.
 */
object TodoItemMapper {
    fun toDb(item: TodoItem) = DbTodoItem(item.key, item.name)

    fun fromDb(item: DbTodoItem) = TodoItem(item.key, item.name)
}

fun List<DbTodoItem>.mapFromDb() = this.map { TodoItemMapper.fromDb(it) }

fun List<TodoItem>.mapToDb() = this.map { TodoItemMapper.toDb(it) }

fun DbTodoItem.mapFromDb() = TodoItemMapper.fromDb(this)

fun TodoItem.mapToDb() = TodoItemMapper.toDb(this)