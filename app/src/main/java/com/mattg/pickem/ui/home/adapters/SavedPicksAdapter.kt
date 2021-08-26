package com.mattg.pickem.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.SavedPicksRvItemBinding
import com.mattg.pickem.db.Pick
import com.mattg.pickem.utils.PicksClickListener


class SavedPicksAdapter(
    val context: Context,
    private val picks: List<Pick>,
    private val clickListener: PicksClickListener
) :
    RecyclerView.Adapter<PicksListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicksListViewHolder {
        return PicksListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PicksListViewHolder, position: Int) {
        holder.bind(picks[position], clickListener)
    }

    override fun getItemCount(): Int {
        return picks.size
    }
}

class PicksListViewHolder private constructor(private val binding: SavedPicksRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): PicksListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SavedPicksRvItemBinding.inflate(layoutInflater, parent, false)
            return PicksListViewHolder(binding)
        }
    }

    fun bind(item: Pick, clickListener: PicksClickListener) {
        binding.picks = item
        binding.root.setOnClickListener {
            clickListener.onClickPick(item, adapterPosition, 1)
        }
        binding.executePendingBindings()

    }


}