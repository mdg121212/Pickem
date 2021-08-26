package com.mattg.pickem.ui.pools.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattg.pickem.databinding.RvPicksListItemBinding
import com.mattg.pickem.parsebackend.models.ParsePick
import com.mattg.pickem.utils.DisplayPicksClickListener
import com.parse.ParseUser

class ParsePoolPicksAdapter(
    val context: Context,
    private var picks: ArrayList<ParsePick>,
    val clickListener: DisplayPicksClickListener,
    val user: ParseUser,
    private val isOwner: Boolean
) :
    RecyclerView.Adapter<ParsePoolPicksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParsePoolPicksViewHolder {
        return ParsePoolPicksViewHolder.from(parent)
    }

    override fun onBindViewHolder(holderParse: ParsePoolPicksViewHolder, position: Int) {
        holderParse.bind(picks[position], clickListener, user, isOwner)
    }

    override fun getItemCount(): Int {
        return picks.size
    }


}

class ParsePoolPicksViewHolder private constructor(private val binding: RvPicksListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): ParsePoolPicksViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RvPicksListItemBinding.inflate(layoutInflater, parent, false)
            return ParsePoolPicksViewHolder(binding)
        }

    }

    fun bind(
        item: ParsePick,
        clickListener: DisplayPicksClickListener,
        user: ParseUser,
        isOwner: Boolean
    ) {
        binding.parsePick = item
        if (isOwner) {
            binding.btnDeletePicks.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    clickListener.onClick(item, adapterPosition, 1)
                }
            }
        } else
            if (item.ownerName == user.username) {
                binding.btnDeletePicks.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        clickListener.onClick(item, adapterPosition, 1)

                    }
                }
            }
        binding.executePendingBindings()
    }

}
