package com.mattg.pickem.ui.pools.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.WinnerRvItemBinding
import com.mattg.pickem.parsebackend.models.WinnerItem

class WinnersAdapter(val context: Context, private val winners: ArrayList<WinnerItem>) :
    RecyclerView.Adapter<WinnerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinnerViewHolder {
        return WinnerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: WinnerViewHolder, position: Int) {
        holder.bind(winners[position])
    }

    override fun getItemCount(): Int {
        return winners.size
    }


}

class WinnerViewHolder private constructor(private val binding: WinnerRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): WinnerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = WinnerRvItemBinding.inflate(layoutInflater, parent, false)
            return WinnerViewHolder(binding)
        }
    }

    fun bind(item: WinnerItem) {
        binding.winner = item
        binding.executePendingBindings()
    }

}
