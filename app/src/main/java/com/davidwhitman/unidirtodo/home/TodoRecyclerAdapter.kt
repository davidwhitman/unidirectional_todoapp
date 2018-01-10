package com.davidwhitman.unidirtodo.home

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.davidwhitman.unidirtodo.databinding.HomeTodoItemBinding

/**
* @author David Whitman on 1/10/2018.
*/
internal class TodoRecyclerAdapter(private val context: Context,
                                   initialItems: List<TodoItem> = emptyList()) : RecyclerView.Adapter<TodoRecyclerAdapter.TodoItemViewHolder>() {
    private val items: MutableList<TodoItem> = initialItems.toMutableList()

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.binding.todoItemName.text = items[position].name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TodoItemViewHolder(HomeTodoItemBinding.inflate(LayoutInflater.from(context), parent, true))

    override fun getItemCount() = items.size

    fun swapItems(newItems: List<TodoItem>) {
        val diffResult = DiffUtil.calculateDiff(createDiffCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun createDiffCallback(old: List<TodoItem>, new: List<TodoItem>) = object : DiffUtil.Callback() {
        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = old[oldItemPosition] == new[newItemPosition]

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = old[oldItemPosition].key == new[newItemPosition].key
    }

    internal data class TodoItemViewHolder(val binding: HomeTodoItemBinding) : RecyclerView.ViewHolder(binding.root)
}
