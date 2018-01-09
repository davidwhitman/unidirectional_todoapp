package com.davidwhitman.unidirtodo.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import com.davidwhitman.unidirtodo.R
import com.davidwhitman.unidirtodo.databinding.HomeBinding
import com.davidwhitman.unidirtodo.home.business.HomeViewModel
import com.nextfaze.poweradapters.binder
import com.nextfaze.poweradapters.data.Data
import com.nextfaze.poweradapters.data.DataBindingAdapter
import com.nextfaze.poweradapters.recyclerview.toRecyclerAdapter

/**
 * @author Thundercloud Dev on 12/8/2017.
 */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeBinding
    private lateinit var adapter: DataBindingAdapter<TodoItem>
    val data = mutableListOf<TodoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val binder = binder<TodoItem, TodoItemView>(R.layout.home_todo_binder_item) { _, todoItem, _ ->
            this.title.text = todoItem.name
        }

        adapter = DataBindingAdapter(binder, Data.fromList { data })
        binding.homeTodoList.adapter = adapter.toRecyclerAdapter()
        binding.homeTodoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        subscribeUiToData(viewModel)

        binding.homeNewItem.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                UiTodoActionEmitter.dispatch(HomeViewModel.TodoUiAction.UpdateTodoItem(name = editText.text.toString()))
                editText.text = ""
                true
            } else {
                false
            }
        }

        binding.homeLoading.setOnRefreshListener { UiTodoActionEmitter.dispatch(HomeViewModel.TodoUiAction.OnRefresh()) }

        UiTodoActionEmitter.dispatch(HomeViewModel.TodoUiAction.OnLoad())
    }

    private fun subscribeUiToData(viewModel: HomeViewModel) {
        viewModel.state.observe(this, Observer<HomeState> { newState ->
            renderUi(newState!!)
        })
    }

    private fun renderUi(newState: HomeState) {
        when (newState) {
            is HomeState.Loading -> binding.homeLoading.isRefreshing = true
            is HomeState.Loaded -> {
                val diffResult = DiffUtil.calculateDiff(createDiffCallback(data, newState.items))
                data.clear()
                data.addAll(newState.items)
                diffResult.dispatchUpdatesTo(adapter.toRecyclerAdapter())
                binding.homeLoading.isRefreshing = false
            }
            is HomeState.Empty -> {
                data.clear()
                binding.homeLoading.isRefreshing = false
            }
        }
    }

    private fun createDiffCallback(old: List<TodoItem>, new: List<TodoItem>) = object : DiffUtil.Callback() {
        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = old[oldItemPosition] == new[newItemPosition]

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = old[oldItemPosition].key == new[newItemPosition].key

    }
}