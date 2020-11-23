package com.mattg.pickem.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.InviteRecyclerItemBinding
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.utils.InvitesClickListener

class InviteRecyclerAdapter (val context: Context, private val invites: ArrayList<Invite>, private val clickListener: InvitesClickListener) :
    RecyclerView.Adapter<InviteListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteListViewHolder {
        return InviteListViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return invites.size
    }


    fun removeItem(item: Invite, position: Int){
        invites.remove(item)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: InviteListViewHolder, position: Int) {
        holder.bind(invites[position], clickListener)
    }
}

class InviteListViewHolder private constructor(private val binding: InviteRecyclerItemBinding)
    : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): InviteListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = InviteRecyclerItemBinding.inflate(layoutInflater, parent, false)
            return InviteListViewHolder(binding)
        }
    }

    fun bind(item: Invite, clickListener: InvitesClickListener){
        binding.invitation = item
        binding.tvInviteSenderEmail.text = item.senderEmail
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