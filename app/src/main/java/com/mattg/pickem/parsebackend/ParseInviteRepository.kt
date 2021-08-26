package com.mattg.pickem.parsebackend

import com.mattg.pickem.parsebackend.models.ParseInvite
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import timber.log.Timber

class ParseInviteRepository {

    fun createInvite(
        receiverName: String,
        receiverId: String,
        senderName: String,
        senderId: String,
        poolId: String,
        poolName: String
    )
            : String? {

        /**
         * NEED TO MAKE THIS CREATE A POINTER TO THE SENDER AND RECEIVER
         */
        val invite = ParseObject("Invitation")
        invite.apply {
            put("senderName", senderName)
            put("senderId", senderId)
            put("receiverName", receiverName)
            put("receiverId", receiverId)
            put("poolId", poolId)
            put("poolName", poolName)

        }
        try {
            invite.save()
            Timber.i("******created invitation with id ${invite.objectId}")
            return invite.objectId
        } catch (e: ParseException) {
            Timber.i("*******failed to create invite for these reasons $e ${e.code} ${e.cause}")
            return null
        }
    }

    suspend fun checkForInvites(user: ParseUser): Boolean? = coroutineScope {
        val deferred = async(Dispatchers.IO) {
            Timber.i("****checking invites, user id looked for is ${user.objectId}")
            val query = ParseQuery<ParseObject>("Invitation")
                .whereEqualTo("receiverId", user.objectId)

            try {
                val results = query.find()
                if (results.isNotEmpty()) {
                    Timber.i("*******found invites, not empty")

                    for (result in results) {
                        Timber.i(
                            "*******invite found $result, id ${result.objectId} receiver ${
                                result.get(
                                    "receiverId"
                                ).toString()
                            }"
                        )
                    }

                    return@async !results.isNullOrEmpty()
                } else return@async false

            } catch (e: ParseException) {
                Timber.i("*****tried to get user invites and it failed $e ${e.message}")
                return@async false
            }
        }
        return@coroutineScope deferred.await()


    }

    fun getInvites(user: ParseUser): ArrayList<ParseInvite>? {
        val query = ParseQuery<ParseObject>("Invitation").whereEqualTo("receiverId", user.objectId)

        try {
            val results = query.find()
            Timber.i("********getting parse invites, results are $results")
            if (results.isNotEmpty()) {

                val returnList = ArrayList<ParseInvite>()
                for (result in results) {
                    val newInvite = ParseInvite(
                        result.get("senderName").toString(),
                        result.get("senderId").toString(),
                        result.get("poolId").toString(),
                        result.get("poolName").toString(),
                        result.objectId,
                        result.get("receiverId").toString(),
                        result.get("receiverName").toString()
                    )
                    Timber.i("******FOR LOOP CREATED INVITE----$newInvite")
                    returnList.add(newInvite)
                }


                Timber.i("******RETURNLIST will be returned as $returnList")

//                val invite = results[0]
//                Timber.i("*********first result from invites is $invite ${invite.get("senderName").toString()}")
//                val invites = invite.getList<String>("poolName")
//                Timber.i("*********invites list is $invites")
//               // val returnList = ArrayList<ParseInvite>()
//                invites?.forEach { inviteString ->
//                    Timber.i("*****in for each loop, inviteString =$inviteString")
//                    val inviteToAdd = getInviteById(inviteString)
//                    if (inviteToAdd != null) {
//                        returnList.add(inviteToAdd)
//                    }
//                }

                return returnList

            } else
                return null

        } catch (e: ParseException) {
            Timber.i("******tried to get invites for ${user.username} it failed, $e ${e.message}")
            return null
        }
    }

    fun getInviteById(objectId: String): ParseInvite? {
        val query = ParseQuery<ParseObject>("Invitations")
            .whereEqualTo("objectId", objectId)
        try {
            val results = query.find()
            if (results.isNotEmpty()) {
                val invite = results[0]
                val returnInvite = ParseInvite(
                    invite.get("senderName").toString(),
                    invite.get("senderId").toString(),
                    invite.get("poolId").toString(),
                    invite.get("poolName").toString(),
                    invite.objectId,
                    invite.get("receiverId").toString(),
                    invite.get("receiverName").toString()
                )

                return returnInvite

            } else
                return null
        } catch (e: ParseException) {
            Timber.i("tried to get Invite by id, failed with $e ${e.message}")
            return null
        }
    }

    fun acceptInvite(user: ParseUser, inviteId: String, isAccepted: Boolean) {
        Timber.i("******calling accept invite from repo, isaccepted = $isAccepted looking for id: $inviteId")
        val query = ParseQuery<ParseObject>("Invitation")
            .whereEqualTo("objectId", inviteId)
        try {
            val results = query.find()
            Timber.i("******calling accept invite from repo, results = $results")
            if (results.isNotEmpty()) {
                val invitation = results[0]
                val poolToJoin = invitation.get("poolId").toString()
                val id = invitation.objectId
                if (isAccepted) {
                    ParsePoolRepository().addPlayerToPool(user, poolToJoin)
                    deleteInvite(id, user)
                    return
                } else
                    deleteInvite(id, user)
            } else
                Timber.i("******trying to accept invitation failed, results were empty")

        } catch (e: ParseException) {
            Timber.i("*****tried to accept/delete invite, failed $e ${e.message}")
        }
    }

    fun deleteInvite(inviteId: String, user: ParseUser) {
        val query = ParseQuery<ParseObject>("Invitation")
            .whereEqualTo("objectId", inviteId)

        try {
            val results = query.find()
            if (results.isNotEmpty()) {
                val invite = results[0]
                invite.delete()
            }
//            val userResults = userQuery.find()
//            if(userResults.isNotEmpty()){
//                val userToRemoveFrom = userResults[0]
//                val invites = userToRemoveFrom.getList<String>("invitations")
//                val returnList = ArrayList<String>()
//                invites?.forEach { item ->
//                    if(item != inviteId){
//                        returnList.add(item)
//                    }
//                }
//                val returnArray = returnList.toArray()
//                userToRemoveFrom.put("invitations", returnArray)
//                userToRemoveFrom.save()
        } catch (e: ParseException) {
            Timber.i("*****tried to delete invitation, failed $e ${e.message}")
        }

    }


}