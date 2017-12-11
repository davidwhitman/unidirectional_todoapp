package com.davidwhitman.unidirtodo.home

/**
 * @author Thundercloud Dev on 12/8/2017.
 */
sealed class HomeState {
    class Empty : HomeState()

    class Loading : HomeState()

    class Loaded(val items: List<TodoItem>) : HomeState()
}