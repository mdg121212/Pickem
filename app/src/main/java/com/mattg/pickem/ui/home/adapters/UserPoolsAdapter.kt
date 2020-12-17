package com.mattg.pickem.ui.home.adapters

//class UserPoolsAdapter(val context: Context, private val pools: ArrayList<Pool>, private val clickListener: UserPoolClickListener) :
//RecyclerView.Adapter<UserPoolListViewHolder>(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPoolListViewHolder {
//        return UserPoolListViewHolder.from(parent)
//    }
//
//    override fun onBindViewHolder(holder: UserPoolListViewHolder, position: Int) {
//       holder.bind(pools[position], clickListener)
//    }
//
//    override fun getItemCount(): Int {
//        return pools.size
//    }
//
//
//}
//
//class UserPoolListViewHolder private constructor(private val binding: PoolRecyclerItemBinding)
//    : RecyclerView.ViewHolder(binding.root){
//    companion object {
//        fun from(parent: ViewGroup): UserPoolListViewHolder {
//            val layoutInflater = LayoutInflater.from(parent.context)
//            val binding = PoolRecyclerItemBinding.inflate(layoutInflater, parent, false)
//            return UserPoolListViewHolder(binding)
//        }
//    }
//    fun bind(item: ParsePool, clickListener: UserPoolClickListener){
//        binding.pool = item
//       // binding.tvActivePlayers.text = item.playerCount.toString()
//        binding.btnSelectPool.setOnClickListener {
//            clickListener.onClickPoolItem(item.documentId, item.ownerId, adapterPosition, 1, item.poolName, item.ownerName)
//        }
//        binding.btnDeletePool.setOnClickListener {
//            clickListener.onClickPoolItem(item.documentId, item.ownerId, adapterPosition, 2, item.poolName, item.ownerName)
//        }
//    }
//
//
//
//}
