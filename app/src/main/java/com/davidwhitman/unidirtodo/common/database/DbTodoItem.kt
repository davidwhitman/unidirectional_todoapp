package com.davidwhitman.unidirtodo.common.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * @author David Whitman on 1/18/2018.
 */
@Entity(tableName = "TodoItems")
data class DbTodoItem(
        @PrimaryKey
        val key: Long,
        val name: String
)