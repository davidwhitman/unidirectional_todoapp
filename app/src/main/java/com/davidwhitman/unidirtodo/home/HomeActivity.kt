package com.davidwhitman.unidirtodo.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import com.davidwhitman.unidirtodo.databinding.HomeBinding
import com.jakewharton.rxrelay2.PublishRelay

/**
 * @author David Whitman on 12/8/2017.
 */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeBinding
    private lateinit var adapter: TodoRecyclerAdapter
    private val intentions = PublishRelay.create<HomeViewModel.Intention>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TodoRecyclerAdapter(this)
        binding.homeTodoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.homeTodoList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding.homeTodoList.adapter = adapter

        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        subscribeUiToData(viewModel)

        binding.homeNewItem.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                intentions.accept(HomeViewModel.Intention.UpdateTodoItem(name = editText.text.toString()))
                editText.text = ""
                true
            } else {
                false
            }
        }

        binding.homeLoading.setOnRefreshListener { intentions.accept(HomeViewModel.Intention.Refresh()) }

        intentions.accept(HomeViewModel.Intention.Load())
    }

    private fun subscribeUiToData(viewModel: HomeViewModel) {
        viewModel.bind(intentions).observe(this, Observer { newState ->
            renderUi(newState!!)
        })
    }

    private fun renderUi(newState: HomeState) {
        when (newState) {
            is HomeState.Loading -> binding.homeLoading.isRefreshing = true
            is HomeState.Loaded -> {
                adapter.swapItems(newState.items)
                binding.homeLoading.isRefreshing = false
            }
            is HomeState.Empty -> {
                adapter.swapItems(emptyList())
                binding.homeLoading.isRefreshing = false
            }
        }
    }
}