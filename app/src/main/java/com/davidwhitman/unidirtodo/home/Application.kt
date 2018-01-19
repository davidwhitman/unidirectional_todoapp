package com.davidwhitman.unidirtodo.home

import android.app.Application
import com.davidwhitman.unidirtodo.home.database.TodoItemDatabase

/**
 * @author David Whitman on 1/18/2018.
 */
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        TodoItemDatabase.createInstance(this)
    }
}