package com.rchugunov.autofilltestapp

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class SuggestionsAdapter(private val onSuggestionClicked: (Suggestion) -> Unit) : ListAdapter<Suggestion, SuggestionsAdapter.ViewHolder>(DIFF_CALLBACK) {

    init {
        submitList(
            listOf(
                Suggestion("Suggestion1"),
                Suggestion("Suggestion2"),
                Suggestion("Suggestion3"),
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(Chip(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chipView.text = getItem(position).value
        holder.chipView.setOnClickListener {
            this.onSuggestionClicked(getItem(position))
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Suggestion>() {
            override fun areItemsTheSame(oldItem: Suggestion, newItem: Suggestion): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Suggestion, newItem: Suggestion): Boolean {
                return oldItem.value == newItem.value
            }
        }
    }

    class ViewHolder(val chipView: Chip) : RecyclerView.ViewHolder(chipView)
}

data class Suggestion(
    val value: String
)