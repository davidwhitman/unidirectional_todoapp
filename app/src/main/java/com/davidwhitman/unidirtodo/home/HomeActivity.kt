package com.davidwhitman.unidirtodo.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.davidwhitman.unidirtodo.R
import com.davidwhitman.unidirtodo.common.AppState
import com.davidwhitman.unidirtodo.common.TodoItem
import com.davidwhitman.unidirtodo.signin.ProfileActivity
import com.jakewharton.rxrelay2.PublishRelay
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.reactivex.Observable
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home_todo_item.view.*

/**
 * @author David Whitman on 12/8/2017.
 */
class HomeActivity : AppCompatActivity() {
    private val intentions = PublishRelay.create<HomeViewModel.Intention>()

    private val itemsSection = Section()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Create the adapter and add a single section to it (the section holds the items and has no title)
        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(itemsSection)

        home_todoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        home_todoList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        home_todoList.adapter = adapter

        // Create a ViewModel and start sending UI intentions to it while watching for state changes
        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        bindUiToStateChanges(viewModel, intentions)

        // If the user creates a new item, send an intention to the viewmodel
        home_newItem.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && editText.text.isNotBlank()) {
                intentions.accept(HomeViewModel.Intention.UpdateTodoItem(name = editText.text.toString()))
                editText.text = ""
                true
            } else {
                false
            }
        }

        // If the user wants to refresh the list, send an intention to the viewmodel
        home_loading.setOnRefreshListener { intentions.accept(HomeViewModel.Intention.Refresh) }

        // Send an intention that the screen wants to load data
        intentions.accept(HomeViewModel.Intention.Load)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            when (item?.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }

    /**
     * Starts sending UI intentions to the [HomeViewModel] and watches for [HomeState] changes.
     */
    private fun bindUiToStateChanges(viewModel: HomeViewModel, intentions: Observable<HomeViewModel.Intention>) {
        viewModel.bind(intentions).observe(this, Observer { newState ->
            renderUi(newState!!)
        })
    }

    /**
     * Takes in a new [HomeState] and rearranges the UI based on it (and it alone).
     */
    private fun renderUi(newState: AppState) {
        val homeState = newState.homeState

        home_loading.isRefreshing = homeState.refreshing

        itemsSection.update(homeState.items.map { TodoItemBinder(it) })

        homeState.error?.let {
            Toast.makeText(this, homeState.error, Toast.LENGTH_LONG).show()
            intentions.accept(HomeViewModel.Intention.DismissError)
        }
    }

    /**
     * Binds a [TodoItem] to a [ViewHolder] for the RecyclerView.
     */
    private inner class TodoItemBinder(private val item: TodoItem) : Item<ViewHolder>() {
        override fun getLayout() = R.layout.home_todo_item
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.todo_item_name.text = item.name
            viewHolder.itemView.todo_item_delete.setOnClickListener { intentions.accept(HomeViewModel.Intention.DeleteItem(item = item)) }
        }

        override fun isSameAs(other: Item<*>) = other is TodoItemBinder && other.item.key == this.item.key
    }
}