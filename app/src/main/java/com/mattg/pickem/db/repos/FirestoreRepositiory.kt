package com.mattg.pickem.db.repos


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.Pool
import com.mattg.pickem.models.firebase.User
import timber.log.Timber

class FirestoreRepositiory {

    var mFirebaseDatabaseInstance = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser

    val _listOfInvitations = MutableLiveData<ArrayList<Invite>>()
    val listOfInvitations: LiveData<ArrayList<Invite>> = _listOfInvitations

    private val _returnListOfPoolPlayers = MutableLiveData<ArrayList<User>>()


    fun listenForInvitations(): DocumentReference {
        return mFirebaseDatabaseInstance.collection("users").document(currentUser!!.uid)
    }

    fun resetUserInvitesField() {
        val usersInviteReceivedCollection = getUserBaseCollection(currentUser?.uid!!).collection("invitesReceived")
        usersInviteReceivedCollection.addSnapshotListener { value, error ->
            if (value!!.isEmpty) {
                getUserBaseCollection(currentUser?.uid!!).update("invites", false)
            }
        }
    }

    fun checkAreStillInvites(): Boolean {
        var returnBoolean = false
        val invites = getUserBaseCollection(currentUser?.uid!!).collection("invitesReceived")
                .get().addOnSuccessListener { documents ->
                    returnBoolean = !documents.isEmpty

                }

        return returnBoolean

    }

    fun setInvitesToNone(){
        getUserBaseCollection(currentUser?.uid!!).update("invites", false)
    }

    fun checkForInvitations(snapshot: DocumentSnapshot): Boolean {
        var returnBoolean = false
        if (snapshot.exists()) {
            val userRef = mFirebaseDatabaseInstance.collection("users")
                    .document(currentUser!!.uid)
            if (snapshot.data?.get("invites") != null) {
                returnBoolean = snapshot.data?.get("invites")!! == true
            }
        }
        return returnBoolean
    }

    fun searchForUsers(input: String): Task<QuerySnapshot> {
        return mFirebaseDatabaseInstance.collection("users").whereEqualTo("email", input).get()
    }

    fun getPoolPlayers(poolId: String): CollectionReference {

       return  getUserBaseCollection(currentUser?.uid!!).collection("pools").document(poolId).collection("players")


    }

    fun createPool(userId: String, poolData: HashMap<String, Any>): Pair<Boolean, String?> {
        var bool = false
        var idToPass = ""
        val userRef = getUserBaseCollection(userId).collection("pools")

        userRef.add(poolData).addOnSuccessListener {
            bool = true
            idToPass = it.id
            //add reference to the document inside the document after it is created
            idToPass.let { it1 -> userRef.document(it1).update("documentId", it1) }

        }
                .addOnFailureListener {
                    bool = false
                }
        return Pair(bool, idToPass)
    }

    fun deletePool(poolId: String): Boolean {
        val poolToDelete = mFirebaseDatabaseInstance.collection("users/${currentUser?.uid!!}/pools").document(poolId)
        val docList = ArrayList<String>()
        val children = poolToDelete.collection("players")
                .addSnapshotListener { snapshot, error ->
                    if (error == null) {
                        if (snapshot != null) {
                            //create a list of items in the player collection
                            for (item in snapshot) {
                                //add to list
                                docList.add(item.id)
                            }

                        }
                    }
                }.remove()
        for (item in docList) {
            //for each doc id in list, delete it
            poolToDelete.collection("players").document(item).delete()
        }
        poolToDelete.delete()

        return true
    }



    fun createPoolsOnAccept(poolId: String, senderId: String, poolToCopy: Pool, playerToAdd: HashMap<String, Any>) {

        val userRef = getUserBaseCollection(currentUser?.uid!!)
        val senderRef = getUserBaseCollection(senderId)
        var idHolder = ""
        //add the pool to the current users collection
        val newPoolForCurrentUser = userRef.collection("pools").add(poolToCopy)
        newPoolForCurrentUser.addOnCompleteListener { it ->
            val id = it.result!!.id
            idHolder = id
            //add the document id to the document in a field
            userRef.collection("pools").document(idHolder).collection("players").add(playerToAdd)
            userRef.collection("pools").document(id).update("documentId", id)

        }

        val senderPoolRef = senderRef.collection("pools").document(poolId)
        senderPoolRef.collection("players").add(playerToAdd)

        resetUserInvitesField()
        checkAreStillInvites()
    }



    fun addPlayersToPool(poolId: String, userId: String) {
        val poolRef = mFirebaseDatabaseInstance.collection("users").document(userId).collection("pools")
                .document(poolId).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val oldPlayers = snapshot.data
                        for (entry in oldPlayers?.entries!!) {
                            if (entry.key == "players") {
                                Timber.i("Players data = ${entry.value}")
                            }

                        }
                    }
                }

    }


    fun deleteInvitation(inviteId: String) {
        mFirebaseDatabaseInstance.collection("users").document(currentUser?.uid!!)
                .collection("invitesReceived").document(inviteId).delete()

        resetUserInvitesField()
        checkAreStillInvites()


    }


    fun getUserBaseCollection(userId: String): DocumentReference {
        val userReference = mFirebaseDatabaseInstance.collection("users").document(userId)
        return userReference
    }

    fun getUserPoolsBasePath(userId: String): CollectionReference {
        //do the business logic in the viewmodel
        return mFirebaseDatabaseInstance.collection("users").document(userId).collection("pools")
    }


    fun updateUserBase(userId: String, data: HashMap<String, Any>): Task<Void> {
        return mFirebaseDatabaseInstance.collection("users").document(userId).update(data)
    }
}