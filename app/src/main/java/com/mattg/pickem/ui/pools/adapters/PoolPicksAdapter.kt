package com.mattg.pickem.ui.pools.adapters

//class PoolPicksAdapter(
//        val context: Context,
//        private var picks: ArrayList<PickForDisplay>,
//        val clickListener: DisplayPicksClickListener,
//        val user: FirebaseUser,
//        private val isOwner: Boolean) :
//        RecyclerView.Adapter<PoolPicksViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoolPicksViewHolder {
//        return PoolPicksViewHolder.from(parent)
//    }
//
//    override fun onBindViewHolder(holder: PoolPicksViewHolder, position: Int) {
//       holder.bind(picks[position], clickListener, user, isOwner)
//    }
//
//    override fun getItemCount(): Int {
//        return picks.size
//    }
//
//
//}
//
//class PoolPicksViewHolder private constructor(private val binding: RvPicksListItemBinding)
//    : RecyclerView.ViewHolder(binding.root) {
//    companion object {
//        fun from(parent: ViewGroup): PoolPicksViewHolder{
//            val layoutInflater = LayoutInflater.from(parent.context)
//            val binding = RvPicksListItemBinding.inflate(layoutInflater, parent, false)
//            return PoolPicksViewHolder(binding)
//        }
//
//    }
//    fun bind(item: PickForDisplay, clickListener: DisplayPicksClickListener, user: FirebaseUser, isOwner: Boolean){
//        binding.picksForDisplay = item
//        if(isOwner){
//            binding.btnDeletePicks.apply {
//                visibility = View.VISIBLE
//                setOnClickListener {
//              //      clickListener.onClick(item, adapterPosition, 1)
//                }
//            }
//        } else
//        if(item.playerName == user.displayName){
//            binding.btnDeletePicks.apply {
//                visibility = View.VISIBLE
//                setOnClickListener {
//               //     clickListener.onClick(item, adapterPosition, 1)
//
//                }
//            }
//        }
//        binding.executePendingBindings()
//    }
//
//}
