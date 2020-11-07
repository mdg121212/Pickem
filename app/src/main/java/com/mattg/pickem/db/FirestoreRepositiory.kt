package com.mattg.pickem.db


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

import com.mattg.pickem.models.firebase.Invite
import timber.log.Timber

class FirestoreRepositiory {

    var mFirebaseDatabaseInstance = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser

    val _listOfInvitations =  MutableLiveData<ArrayList<Invite>>()
    val listOfInvitations: LiveData<ArrayList<Invite>> = _listOfInvitations


    fun resetUserInvitesField()  {
        val usersInviteReceivedCollection = getUserBaseCollection(currentUser?.uid!!).collection("invitesReceived")
        usersInviteReceivedCollection.addSnapshotListener { value, error ->
            if(value!!.isEmpty){
                getUserBaseCollection(currentUser?.uid!!).update("invites", false)
            }
        }
    }

    fun createPool(userId: String, poolData: HashMap<String, Any>): Pair<Boolean, String?> {
        var bool  = false
        var idToPass = ""
        val userRef = getUserBaseCollection(userId).collection("pools")

            userRef.add(poolData).addOnSuccessListener {
            bool = true
            idToPass = it.id
            //add reference to the document inside the document after it is created
           idToPass.let { it1 -> userRef.document(it1).update("documentId", it1) }

        }
            .addOnFailureListener{
                bool = false
            }
        return Pair(bool, idToPass)
    }

    fun deletePool(poolId: String){
        mFirebaseDatabaseInstance.collection("users/${currentUser?.uid!!}/pools").document(poolId).delete()
    }

    fun listenForInvitations(): DocumentReference {
       return  mFirebaseDatabaseInstance.collection("users").document(currentUser!!.uid)
    }

    fun checkForInvitations(snapshot: DocumentSnapshot): Boolean {
        var returnBoolean = false
        if (snapshot.exists()) {
            //  val listForRecycler = ArrayList<Invite>()
            Timber.d("Snapshot data: ${snapshot.data}")
            val userRef = mFirebaseDatabaseInstance.collection("users")
                .document(currentUser!!.uid)

            if (snapshot.data?.get("invites") != null) {
                if (snapshot.data?.get("invites")!!.equals(true)) {
                    //getting all of the invites for the current user, that have not been accepted
                    val invites = userRef.collection("invitesReceived")
                        .get()
                    returnBoolean = true
                } else{
                    returnBoolean = false
                }
            }
        }
        return returnBoolean
    }

    fun deleteInvitation(inviteId: String, senderId: String, sentInvitationId: String): Pair<Task<Void>, Task<Void>> {

        return Pair(
            mFirebaseDatabaseInstance.collection("users").document(currentUser?.uid!!)
            .collection("invitesReceived").document(inviteId).delete(),

            mFirebaseDatabaseInstance.collection("users").document(senderId).collection("invitesSent")
            .document(sentInvitationId).delete()
        )
    }

    fun getUserBaseCollection(userId: String) : DocumentReference {
       val userReference = mFirebaseDatabaseInstance.collection("users").document(userId)
        return userReference
    }

    fun getUserPoolsBasePath(userId: String): CollectionReference {
        //do the business logic in the viewmodel
        return mFirebaseDatabaseInstance.collection("users").document(userId).collection("pools")
    }
    fun getUserSentInvites(userId: String): CollectionReference {
        return mFirebaseDatabaseInstance.collection("users/$userId/invitesSent")
    }
    fun getUserRecievedInvites(userId: String): CollectionReference {
        return mFirebaseDatabaseInstance.collection("users/$userId/invitesReceived")
    }
    fun updateUserBase(userId: String, data: HashMap<String, Any>): Task<Void> {
        return mFirebaseDatabaseInstance.collection("users").document(userId).update(data)
    }
}