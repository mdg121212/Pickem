package com.mattg.pickem.utils

import com.mattg.pickem.db.Pick
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.User
import com.mattg.pickem.parsebackend.models.ParseInvite
import com.mattg.pickem.parsebackend.models.ParsePick
import com.parse.ParseUser

class RecyclerClickListener(val clickListener: (user: ParseUser, position: Int, uid: String) -> Unit) {
    fun onClickInvite(user: ParseUser, position: Int, uid: String) =
        clickListener(user, position, uid)
}

class InvitesClickListener(val clickListener: (invite: Invite, position: Int, delete: Int) -> Unit) {
    fun onClickInviteItem(invite: Invite, position: Int, delete: Int) =
        clickListener(invite, position, delete)
}
class UserPoolClickListener(val clickListener: (poolId: String, poolOwner: String, position: Int, buttonInt: Int, poolName: String, poolOwnerName: String) -> Unit) {
    fun onClickPoolItem(poolId: String, poolOwner: String, position: Int, buttonInt: Int, poolName: String, poolOwnerName: String)
            = clickListener(poolId, poolOwner, position, buttonInt, poolName, poolOwnerName)
}
class PicksClickListener(val clickListener: (pick: Pick, position: Int, option: Int) -> Unit) {
    fun onClickPick(pick: Pick, position: Int, option: Int) = clickListener(pick, position, option)
}

class PoolPlayerListClickListener(val clickListener: (user: User, position: Int, action: Int) -> Unit) {
    fun onClick(user: User, position: Int, action: Int) = clickListener(user, position, action)
}

//changed from pickfordisplay to accomodate parse
class DisplayPicksClickListener(val clickListener: (pick: ParsePick, position: Int, action: Int) -> Unit) {
    fun onClick(pick: ParsePick, position: Int, action: Int) = clickListener(pick, position, action)
}

class ParseInvitesClickListener(val clickListener: (invite: ParseInvite, position: Int, delete: Int) -> Unit) {
    fun onClickInviteItem(invite: ParseInvite, position: Int, delete: Int) =
        clickListener(invite, position, delete)
}
