package com.mattg.pickem.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.PlayerRecyclerItemBinding
import com.mattg.pickem.utils.RecyclerClickListener
import com.parse.ParseUser

class ParseInviteAdapter(
    val context: Context,
    private val users: ArrayList<ParseUser>,
    private val clickListener: RecyclerClickListener
) :
    RecyclerView.Adapter<ParsePoolUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParsePoolUserViewHolder {
        return ParsePoolUserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ParsePoolUserViewHolder, position: Int) {
        holder.bind(users[position], clickListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }
}

class ParsePoolUserViewHolder private constructor(private val binding: PlayerRecyclerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): ParsePoolUserViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PlayerRecyclerItemBinding.inflate(layoutInflater, parent, false)
            return ParsePoolUserViewHolder(binding)
        }
    }

    fun bind(item: ParseUser, clickListener: RecyclerClickListener) {
        binding.parseuser = item
        binding.btnPlayerItemInvite.setOnClickListener {
            clickListener.onClickInvite(item, adapterPosition, item.objectId)
        }
    }
}