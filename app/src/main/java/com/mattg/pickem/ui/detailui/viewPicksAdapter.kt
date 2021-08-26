package com.mattg.pickem.ui.detailui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.PicksItemLayoutBinding
import com.mattg.pickem.db.Pick
import com.mattg.pickem.utils.PicksClickListener

class ViewPicksAdapter(
    val context: Context,
    val picks: ArrayList<Pick>,
    val clickListener: PicksClickListener
) : RecyclerView.Adapter<PicksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicksViewHolder {
        return PicksViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PicksViewHolder, position: Int) {
        holder.bind(picks[position], clickListener)
    }

    override fun getItemCount(): Int {
        return picks.size
    }
}

class PicksViewHolder private constructor(private val binding: PicksItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): PicksViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PicksItemLayoutBinding.inflate(layoutInflater, parent, false)
            return PicksViewHolder(binding)
        }
    }

    fun bind(item: Pick, clickListener: PicksClickListener) {
        binding.pick = item
        binding.btnDeletePick.setOnClickListener {
            clickListener.onClickPick(item, adapterPosition, 1)
        }
        binding.btnDuplicatePick.setOnClickListener {
            clickListener.onClickPick(item, adapterPosition, 2)
        }
    }
}