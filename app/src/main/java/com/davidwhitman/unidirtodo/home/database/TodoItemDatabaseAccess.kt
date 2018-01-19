package com.davidwhitman.unidirtodo.home.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

/**
 * @author David Whitman on 1/18/2018.
 */
@Dao
interface TodoItemDatabaseAccess {
    @Query("SELECT * FROM todoItems")
    fun getItems(): List<DbTodoItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: DbTodoItem)
}