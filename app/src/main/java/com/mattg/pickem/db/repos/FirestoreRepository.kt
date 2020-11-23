package com.mattg.pickem.db.repos




import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.mattg.pickem.models.firebase.PickForDisplay
import com.mattg.pickem.models.firebase.Pool


class FirestoreRepository {

    private var mFirebaseDatabaseInstance = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser


    fun listenForInvitations(): DocumentReference {
        return mFirebaseDatabaseInstance.collection("users").document(currentUser!!.uid)
    }

    fun resetUserInvitesField() {
        val usersInviteReceivedCollection = getUserBaseCollection(currentUser?.uid!!).collection("invitesReceived")
        usersInviteReceivedCollection.addSnapshotListener { value, _ ->
            if (value!!.isEmpty) {
                getUserBaseCollection(currentUser?.uid!!).update("invites", false)
            }
        }
    }

    fun checkAreStillInvites(): Boolean {
        var returnBoolean = false
        getUserBaseCollection(currentUser?.uid!!).collection("invitesReceived")
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
             mFirebaseDatabaseInstance.collection("users")
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

    fun createPool(userId: String, poolData: HashMap<String, Any>, playerToAdd: HashMap<String, Any>): Pair<Boolean, String?> {
        var bool = false
        var idToPass = ""
        val userRef = getUserBaseCollection(userId).collection("pools")

        userRef.add(poolData).addOnSuccessListener {
            bool = true
            idToPass = it.id
            //add reference to the document inside the document after it is created
            idToPass.let { it1 ->
                 userRef.document(it1).update("documentId", it1)
                userRef.document(it1).collection("players").add(playerToAdd)
            }

        }
                .addOnFailureListener {
                    bool = false
                }
        return Pair(bool, idToPass)
    }

    fun deletePool(poolId: String): Boolean {
        val poolToDelete = mFirebaseDatabaseInstance.collection("users/${currentUser?.uid!!}/pools").document(poolId)
        val docList = ArrayList<String>()
         poolToDelete.collection("players").addSnapshotListener { snapshot, error ->
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



    fun createPoolsOnAccept(poolId: String, senderId: String, poolToCopy: Pool, playersToAdd: ArrayList<HashMap<String, Any>>) {
        val userRef = getUserBaseCollection(currentUser?.uid!!)
        val senderRef = getUserBaseCollection(senderId)
        var idHolder: String
        //add the pool to the current users collection
        val newPoolForCurrentUser = userRef.collection("pools").add(poolToCopy)
        newPoolForCurrentUser.addOnCompleteListener { docRef ->
            val id = docRef.result!!.id
            idHolder = id
            //add the document id to the document in a field
            for(player in playersToAdd) {
                userRef.collection("pools").document(idHolder).collection("players").add(player)

                userRef.collection("pools").document(id).update("documentId", id)

            }
        }

        val senderPoolRef = senderRef.collection("pools").document(poolId)

        val idFrom = senderPoolRef.collection("players")
        val docId = idFrom.id
        for(player in playersToAdd){
            idFrom.add(player)
            idFrom.document(docId).update("documentId", docId)
        }


        resetUserInvitesField()
        checkAreStillInvites()
    }


    fun deleteInvitation(inviteId: String) {
        mFirebaseDatabaseInstance.collection("users").document(currentUser?.uid!!)
                .collection("invitesReceived").document(inviteId).delete()

        resetUserInvitesField()
        checkAreStillInvites()


    }


    fun getUserBaseCollection(userId: String): DocumentReference {
        return mFirebaseDatabaseInstance.collection("users").document(userId)
    }

    fun getUserPoolsBasePath(userId: String): CollectionReference {
        //do the business logic in the viewmodel
        return mFirebaseDatabaseInstance.collection("users").document(userId).collection("pools")
    }


    fun addPicksToPoolDocument(personId: String, documentId: String, data: HashMap<String, Any>){
        mFirebaseDatabaseInstance.collection("users").document(personId).collection("pools")
                .document(documentId).collection("playerPicks").add(data).addOnSuccessListener {
                it.update("documentId", it.id)
            }

    }

    fun getPoolPlayersBasePath(userId: String, poolId: String): CollectionReference{
        return mFirebaseDatabaseInstance.collection("users").document(userId).collection("pools").document(poolId).collection("players")
    }


    fun updateUserBase(userId: String, data: HashMap<String, Any>): Task<Void> {
        return mFirebaseDatabaseInstance.collection("users").document(userId).update(data)
    }

    fun getPickDocumentForDelete(userId: String, poolDocId: String, playerPicksDocId: String){
        mFirebaseDatabaseInstance.document("/users/{$userId}/pools/{$poolDocId}/playerPicks/{$playerPicksDocId}").delete()
    }
}