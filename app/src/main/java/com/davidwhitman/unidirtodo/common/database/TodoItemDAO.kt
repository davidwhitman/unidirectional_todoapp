package com.davidwhitman.unidirtodo.common.database

import android.arch.persistence.room.*

/**
 * @author David Whitman on 1/18/2018.
 */
@Dao
interface TodoItemDAO {
    @Query("SELECT * FROM TodoItems")
    fun getItems(): List<DbTodoItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: DbTodoItem)

    @Delete()
    fun deleteItem(item: DbTodoItem)
}