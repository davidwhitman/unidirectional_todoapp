package com.davidwhitman.unidirtodo.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import com.davidwhitman.unidirtodo.R
import com.davidwhitman.unidirtodo.databinding.HomeBinding
import com.nextfaze.poweradapters.binder
import com.nextfaze.poweradapters.binding.ListBindingAdapter
import com.nextfaze.poweradapters.recyclerview.toRecyclerAdapter

/**
 * @author Thundercloud Dev on 12/8/2017.
 */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeBinding
    private lateinit var adapter: ListBindingAdapter<TodoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val binder = binder<TodoItem, TodoItemView>(R.layout.home_todo_binder_item) { _, todoItem, _ ->
            this.title.text = todoItem.name
        }

        adapter = ListBindingAdapter(binder)
        binding.homeTodoList.adapter = adapter.toRecyclerAdapter()
        binding.homeTodoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        subscribeUiToData(viewModel)

        binding.homeNewItem.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                ActionCreator.dispatch(Actions.TodoList.UpdateTodoItem(name = editText.text.toString()))
                editText.text = ""
                true
            } else {
                false
            }
        }

        binding.homeLoading.setOnRefreshListener { ActionCreator.dispatch(Actions.TodoList.GetTodoList()) }

        ActionCreator.dispatch(Actions.TodoList.GetTodoList())
    }

    private fun subscribeUiToData(viewModel: HomeViewModel) {
        viewModel.state.observe(this, Observer<HomeState> { newState ->
            renderUi(newState)
        })
    }

    private fun renderUi(newState: HomeState?) {
        when (newState) {
            is HomeState.Loading -> binding.homeLoading.isRefreshing = true
            is HomeState.Loaded -> {
                adapter.clear()
                adapter.addAll(newState.items)
                binding.homeLoading.isRefreshing = false
            }
            is HomeState.Empty -> {
                adapter.clear()
                binding.homeLoading.isRefreshing = false
            }
        }
    }
}