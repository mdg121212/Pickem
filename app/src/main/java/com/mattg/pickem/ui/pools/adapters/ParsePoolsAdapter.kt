package com.mattg.pickem.ui.pools.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.PoolRecyclerItemBinding
import com.mattg.pickem.parsebackend.models.ParsePool
import com.mattg.pickem.utils.UserPoolClickListener
import java.util.*

class ParsePoolsAdapter(
    val context: Context,
    private val pools: ArrayList<ParsePool>,
    private val clickListener: UserPoolClickListener
) :
    RecyclerView.Adapter<ParsePoolListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParsePoolListViewHolder {
        return ParsePoolListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ParsePoolListViewHolder, position: Int) {
        holder.bind(pools[position], clickListener)
    }

    override fun getItemCount(): Int {
        return pools.size
    }


}

class ParsePoolListViewHolder private constructor(private val binding: PoolRecyclerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): ParsePoolListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PoolRecyclerItemBinding.inflate(layoutInflater, parent, false)
            return ParsePoolListViewHolder(binding)
        }
    }

    fun bind(item: ParsePool, clickListener: UserPoolClickListener) {
        binding.parsepool = item
        // binding.tvActivePlayers.text = item.playerCount.toString()
        binding.btnSelectPool.setOnClickListener {
            clickListener.onClickPoolItem(
                item.objectId,
                item.ownerId,
                adapterPosition,
                1,
                item.poolName,
                item.ownerName
            )
        }
        binding.btnDeletePool.setOnClickListener {
            clickListener.onClickPoolItem(
                item.objectId,
                item.ownerId,
                adapterPosition,
                2,
                item.poolName,
                item.ownerName
            )
        }
    }


}
