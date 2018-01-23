package com.davidwhitman.unidirtodo

import com.davidwhitman.unidirtodo.home.HomeState

/**
 * @author David Whitman on 1/23/2018.
 */
data class State(val homeState: HomeState = HomeState.Empty())