package com.mattg.pickem.ui.pools.adapters

//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.mattg.pickem.databinding.PoolPlayerRecyclerItemBinding
//import com.mattg.pickem.models.firebase.User
//import com.mattg.pickem.utils.PoolPlayerListClickListener
//
//class PoolPlayerListAdapter(val context: Context, private val players: ArrayList<User>, private val clickListener: PoolPlayerListClickListener):
//RecyclerView.Adapter<PoolPlayerListViewHolder>(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoolPlayerListViewHolder {
//        return PoolPlayerListViewHolder.from(parent)
//    }
//
//    override fun onBindViewHolder(holder: PoolPlayerListViewHolder, position: Int) {
//        holder.bind(players[position], clickListener)
//    }
//
//    override fun getItemCount(): Int {
//        return players.size
//    }
//}
//
//class PoolPlayerListViewHolder private constructor(private val binding: PoolPlayerRecyclerItemBinding)
//    :RecyclerView.ViewHolder(binding.root){
//    companion object{
//        fun from(parent: ViewGroup): PoolPlayerListViewHolder{
//            val layoutInflater = LayoutInflater.from(parent.context)
//            val binding = PoolPlayerRecyclerItemBinding.inflate(layoutInflater, parent, false)
//           return PoolPlayerListViewHolder(binding)
//        }
//    }
//    fun bind(item: User, clickListener: PoolPlayerListClickListener){
//        binding.user = item
//        val picks = item.picks
//        if(item.picksIn == true){
//            binding.ivPicksIn.visibility = View.VISIBLE
//        }
//
//        binding.executePendingBindings()
//    }
//}