package com.davidwhitman.unidirtodo.signin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.davidwhitman.unidirtodo.R
import com.davidwhitman.unidirtodo.common.AppState
import com.jakewharton.rxrelay2.PublishRelay
import kotlinx.android.synthetic.main.profile.*

/**
 * @author David Whitman on 1/25/2018.
 */
class ProfileActivity : AppCompatActivity() {
    private val intentions = PublishRelay.create<ProfileViewModel.Intention>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.bind(intentions)
                .observe(this, Observer { newState ->
                    render(newState!!)
                })

        intentions.accept(ProfileViewModel.Intention.Load)
    }

    private fun render(newState: AppState) {
        profile_lists_number.text = newState.profileState.numberOfItems.toString()
        profile_name.text = newState.profileState.username
    }
}