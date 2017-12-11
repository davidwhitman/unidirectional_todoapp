package com.davidwhitman.unidirtodo.home

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.davidwhitman.unidirtodo.databinding.HomeTodoItemBinding

/**
 * @author Thundercloud Dev on 12/10/2017.
 */
class TodoItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {
    private val binding: HomeTodoItemBinding = HomeTodoItemBinding.inflate(LayoutInflater.from(context), this, true)

    val title: TextView = binding.todoItemName
}