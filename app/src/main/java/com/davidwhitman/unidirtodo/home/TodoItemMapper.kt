package com.davidwhitman.unidirtodo.home

import com.davidwhitman.unidirtodo.home.database.DbTodoItem

/**
 * @author David Whitman on 1/18/2018.
 */
object TodoItemMapper {
    fun toDb(item: TodoItem) = DbTodoItem(item.key, item.name)

    fun fromDb(item: DbTodoItem) = TodoItem(item.key, item.name)
}

fun List<DbTodoItem>.mapFromDb() = this.map { com.davidwhitman.unidirtodo.home.TodoItemMapper.fromDb(it) }

fun List<TodoItem>.mapToDb() = this.map { com.davidwhitman.unidirtodo.home.TodoItemMapper.toDb(it) }

fun DbTodoItem.mapFromDb() = com.davidwhitman.unidirtodo.home.TodoItemMapper.fromDb(this)

fun TodoItem.mapToDb() = com.davidwhitman.unidirtodo.home.TodoItemMapper.toDb(this)