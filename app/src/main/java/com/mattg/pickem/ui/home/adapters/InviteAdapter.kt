package com.mattg.pickem.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.PlayerRecyclerItemBinding
import com.mattg.pickem.models.firebase.User
import com.mattg.pickem.utils.RecyclerClickListener


class InviteAdapter(val context: Context, private val users: ArrayList<User>, private val clickListener: RecyclerClickListener) :
      RecyclerView.Adapter<PoolUserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoolUserViewHolder {
        return PoolUserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PoolUserViewHolder, position: Int) {
        holder.bind(users[position], clickListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }
}

class PoolUserViewHolder private constructor(private val binding: PlayerRecyclerItemBinding)
    : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): PoolUserViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PlayerRecyclerItemBinding.inflate(layoutInflater, parent, false)
            return PoolUserViewHolder(binding)
        }
    }

    fun bind(item: User, clickListener: RecyclerClickListener){
        binding.user = item
        binding.btnPlayerItemInvite.setOnClickListener {
            clickListener.onClickInvite(item, adapterPosition, item.userId)
        }
    }
}