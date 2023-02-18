package com.rchugunov.autofilltestapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rchugunov.autofilltestapp.databinding.LayoutAutofillItemBinding

class SuggestionsAdapter(private val onSuggestionClicked: (Suggestion) -> Unit) : ListAdapter<Suggestion, SuggestionsAdapter.ViewHolder>(DIFF_CALLBACK) {

    init {
        submitList(
            listOf(
                Suggestion("PROMO1!"),
                Suggestion("VALENTINE14"),
                Suggestion("NY23"),
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutAutofillItemBinding.inflate(LayoutInflater.from(parent.context)))
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

    class ViewHolder(binding: LayoutAutofillItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val chipView = binding.chip
    }
}

data class Suggestion(
    val value: String,
    val description: String = ""
)