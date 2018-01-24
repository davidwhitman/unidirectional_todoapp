package com.davidwhitman.unidirtodo.signin

import android.arch.lifecycle.ViewModel
import com.davidwhitman.unidirtodo.common.TodoList
import com.davidwhitman.unidirtodo.common.User
import java.util.*

/**
 * @author David Whitman on 1/23/2018.
 */
class AuthViewModel : ViewModel() {
    val user = User(Random().nextLong(), "Test User", listOf(TodoList(emptyList())))


}