package com.davidwhitman.unidirtodo.common

import com.davidwhitman.unidirtodo.home.HomeState
import com.davidwhitman.unidirtodo.signin.ProfileState

/**
 * @author David Whitman on 1/23/2018.
 */
data class AppState(val homeState: HomeState = HomeState(), val profileState: ProfileState = ProfileState())