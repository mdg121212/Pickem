package com.mattg.pickem.ui.home.adapters

import com.mattg.pickem.db.Pick
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.User

class RecyclerClickListener (val clickListener: (user: User, position: Int, uid: String) -> Unit){
    fun onClickInvite(user: User, position: Int, uid: String) = clickListener(user, position, uid)
}
class InvitesClickListener (val clickListener: (invite: Invite, position: Int, delete: Int) -> Unit){
    fun onClickInviteItem(invite: Invite, position: Int, delete: Int) =
        clickListener(invite, position, delete)
}
class UserPoolClickListener(val clickListener: (poolId: String, poolOwner: String, position: Int, buttonInt: Int, poolName: String) -> Unit) {
    fun onClickPoolItem(poolId: String, poolOwner: String, position: Int, buttonInt: Int, poolName: String)
            = clickListener(poolId, poolOwner, position, buttonInt, poolName)
}
class PicksClickListener(val clickListener: (pick: Pick, position: Int, option: Int) -> Unit) {
    fun onClickPick(pick: Pick, position: Int, option: Int) = clickListener(pick, position, option)
}
class PoolPlayerListClickListener(val clickListener: (user: User, position: Int, action: Int) -> Unit) {
    fun onClick(user: User, position: Int, action: Int) = clickListener(user, position, action)
}