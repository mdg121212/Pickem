package com.mattg.pickem.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.PoolRecyclerItemBinding
import com.mattg.pickem.models.firebase.Pool
import com.mattg.pickem.utils.UserPoolClickListener
import java.util.*

class UserPoolsAdapter(val context: Context, private val pools: ArrayList<Pool>, private val clickListener: UserPoolClickListener) :
RecyclerView.Adapter<UserPoolListViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPoolListViewHolder {
        return UserPoolListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UserPoolListViewHolder, position: Int) {
       holder.bind(pools[position], clickListener)
    }

    override fun getItemCount(): Int {
        return pools.size
    }


}

class UserPoolListViewHolder private constructor(private val binding: PoolRecyclerItemBinding)
    : RecyclerView.ViewHolder(binding.root){
    companion object {
        fun from(parent: ViewGroup): UserPoolListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PoolRecyclerItemBinding.inflate(layoutInflater, parent, false)
            return UserPoolListViewHolder(binding)
        }
    }
    fun bind(item: Pool, clickListener: UserPoolClickListener){
        binding.pool = item
       // binding.tvActivePlayers.text = item.playerCount.toString()
        binding.btnSelectPool.setOnClickListener {
            clickListener.onClickPoolItem(item.documentId, item.ownerId, adapterPosition, 1, item.poolName, item.ownerName)
        }
        binding.btnDeletePool.setOnClickListener {
            clickListener.onClickPoolItem(item.documentId, item.ownerId, adapterPosition, 2, item.poolName, item.ownerName)
        }
    }



}
