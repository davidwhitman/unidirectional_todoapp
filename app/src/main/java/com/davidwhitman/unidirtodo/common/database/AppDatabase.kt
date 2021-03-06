package com.davidwhitman.unidirtodo.common.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * @author David Whitman on 1/18/2018.
 */
@Database(entities = [DbTodoItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private lateinit var INSTANCE: AppDatabase

        fun getInstance() = INSTANCE

        fun createInstance(context: Context) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "TodoItemsDatabase.db")
                    .build()
        }
    }

    abstract val access: TodoItemDAO
}