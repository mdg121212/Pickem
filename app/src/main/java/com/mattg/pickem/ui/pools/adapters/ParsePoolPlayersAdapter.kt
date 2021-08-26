package com.mattg.pickem.ui.pools.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.PoolPlayerRecyclerItemBinding
import com.mattg.pickem.parsebackend.models.ParsePoolPlayer

class ParsePoolPlayersAdapter(
    val context: Context,
    private val players: ArrayList<ParsePoolPlayer>
) :
    RecyclerView.Adapter<ParsePoolPlayerListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParsePoolPlayerListViewHolder {
        return ParsePoolPlayerListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ParsePoolPlayerListViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int {
        return players.size
    }
}

class ParsePoolPlayerListViewHolder private constructor(private val binding: PoolPlayerRecyclerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): ParsePoolPlayerListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PoolPlayerRecyclerItemBinding.inflate(layoutInflater, parent, false)
            return ParsePoolPlayerListViewHolder(binding)
        }
    }

    fun bind(item: ParsePoolPlayer) {
        binding.parsePoolPlayer = item
        // val picks = item.picks
//        if(item.picksIn == true){
//            binding.ivPicksIn.visibility = View.VISIBLE
//        }
        binding.executePendingBindings()
    }
}