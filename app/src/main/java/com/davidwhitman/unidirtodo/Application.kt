package com.davidwhitman.unidirtodo

import android.app.Application
import com.davidwhitman.unidirtodo.common.database.AppDatabase
import com.github.ajalt.timberkt.Timber

/**
 * @author David Whitman on 1/18/2018.
 */
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.uprootAll()
        Timber.plant(timber.log.Timber.DebugTree())
        AppDatabase.createInstance(this)
    }
}