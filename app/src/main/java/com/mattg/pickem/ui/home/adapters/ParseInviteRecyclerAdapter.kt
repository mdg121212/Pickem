package com.mattg.pickem.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.InviteRecyclerItemBinding
import com.mattg.pickem.parsebackend.models.ParseInvite
import com.mattg.pickem.utils.ParseInvitesClickListener

class ParseInviteRecyclerAdapter(
    val context: Context,
    private val invites: ArrayList<ParseInvite>,
    private val clickListener: ParseInvitesClickListener
) :
    RecyclerView.Adapter<ParseInviteListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParseInviteListViewHolder {
        return ParseInviteListViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return invites.size
    }


    override fun onBindViewHolder(holder: ParseInviteListViewHolder, position: Int) {
        holder.bind(invites[position], clickListener)
    }
}

class ParseInviteListViewHolder private constructor(private val binding: InviteRecyclerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): ParseInviteListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = InviteRecyclerItemBinding.inflate(layoutInflater, parent, false)
            return ParseInviteListViewHolder(binding)
        }
    }

    fun bind(item: ParseInvite, clickListener: ParseInvitesClickListener) {
        binding.parseinvitation = item
        binding.tvInvitePoolName.text = item.poolName
        binding.tvInviteSenderName.text = item.sender
        binding.btnAccept.setOnClickListener {
            clickListener.onClickInviteItem(item, adapterPosition, 1)
        }
        binding.btnDecline.setOnClickListener {
            clickListener.onClickInviteItem(item, adapterPosition, 2)
        }
        binding.executePendingBindings()
    }
}