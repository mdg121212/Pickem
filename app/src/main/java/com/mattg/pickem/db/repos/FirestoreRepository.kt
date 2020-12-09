package com.mattg.pickem.db.repos




import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.mattg.pickem.models.firebase.Pool
import timber.log.Timber


class FirestoreRepository {

    private var mFirebaseDatabaseInstance = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser


    fun listenForInvitations(): DocumentReference {
        return mFirebaseDatabaseInstance.collection("users").document(currentUser!!.uid)
    }

    fun resetUserInvitesField() {
        val usersInviteReceivedCollection = getUserBaseCollection(currentUser?.uid!!).collection("invitesReceived")
        usersInviteReceivedCollection.addSnapshotListener { value, _ ->
           try{
               if (value!!.isEmpty) {
                   getUserBaseCollection(currentUser?.uid!!).update("invites", false)
               }
           } catch(e: Exception){
               Timber.i("ERROR: ${e.message}")
               e.printStackTrace()
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

    fun deletePool(poolId: String, poolName: String, isOwner: Boolean): Boolean {
        Timber.i(",,,,,,,, is owner is passed as $isOwner")
        val poolToDelete = mFirebaseDatabaseInstance.collection("users/${currentUser?.uid!!}/pools").document(poolId)
        //Need to only remove from pool if you don't own it
        if (isOwner) {
            Timber.i(",,,,,,, is owner was true so the true delete path is fired")
            val docList = ArrayList<String>()
            val playersInPoolToDelete = poolToDelete.collection("players").addSnapshotListener { value, error ->
                if (value != null) {
                    for (player in value!!.documents) {
                        val playerId = player.get("playerId").toString()
                        mFirebaseDatabaseInstance.collection("users").document(playerId).collection("pools")
                                .whereEqualTo("poolName", poolName).get().addOnSuccessListener {
                                    for (doc in it.documents) {
                                        val id = doc.get("documentId").toString()
                                        mFirebaseDatabaseInstance.collection("users").document(playerId)
                                                .collection("pools").document(id).delete()
                                    }
                                }
                    }
                }
            }
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
        } else {
            //this is a pool that the person is not an owner of, only remove it for them
            //and remove them from the other pool instances
            Timber.i(",,,,,,, is owner was false so the false delete path is fired")
            val docList = ArrayList<String>()
            val playersInPoolToDelete = poolToDelete.collection("players").addSnapshotListener { value, error ->
                if (value != null) {
                    for (player in value.documents) {
                        val playerId = player.get("playerId").toString()
                        mFirebaseDatabaseInstance.collection("users").document(playerId).collection("pools")
                                .whereEqualTo("poolName", poolName).get().addOnSuccessListener {
                                    for (doc in it.documents) {
                                        val id = doc.get("documentId").toString()
                                        val playerInOtherPool = mFirebaseDatabaseInstance.collection("users").document(playerId)
                                                .collection("pools").document(id).collection("players").whereEqualTo("playerId", currentUser!!.uid)
                                                .get()
                                        playerInOtherPool.addOnSuccessListener {
                                            it.documents.forEach { it.reference.delete() }
                                        }
                                    }
                                }
                    }
                }
            }
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

    fun addWinnerToPools(userId: String, poolId: String, winnerData: HashMap<String, Any>, poolName: String, listOfPlayerIds: ArrayList<String>){
        //check if the week already has a winner, if not add
        val checkRef = getUserPoolsBasePath(userId).document(poolId).collection("winners").get()
                checkRef.addOnSuccessListener {
                    val winners = it.documents
                    if(winners.size == 0 ){
                        //go ahead and add

                    }
                        for(winner in winners) {
                            val week = winner.get("week").toString()
                            val newWeek = winnerData["week"].toString()
                            if(week == newWeek) {
                                //don't add

                                return@addOnSuccessListener
                            }
                        }

                    for(playerId in listOfPlayerIds){
                        val poolRef = getUserPoolsBasePath(playerId)
                        poolRef.get().addOnSuccessListener {
                            val docs = it.documents

                            for(doc in docs){

                                if(doc.get("poolName").toString() == poolName){

                                    val idToAddTo = doc.id
                                    getUserPoolsBasePath(playerId).document(idToAddTo).collection("winners").add(winnerData)
                                }
                            }
                        }
                    }





                }



    }

    fun arePicksForWeek(userId: String, poolId: String, weekString: String, poolName: String, poolOwnerName: String) {
        val listToReturn = ArrayList<String>()
        val picksRef = getUserPoolsBasePath(userId).get()
        picksRef.addOnSuccessListener {
            val picks = it.documents
            for (doc in picks){
                val docPoolName = doc["poolName"].toString().trim()
                val docPoolOwnerName = doc["ownerName"].toString().trim()
                Timber.i("[[[[[[[[poolname: $docPoolName ownername: $docPoolOwnerName")
                Timber.i("[[[[[ inside the pools document check")

                if(docPoolName == poolName && docPoolOwnerName == poolOwnerName){
                    Timber.i("[[[[[  checking for picks, found the doc that matches the pool")
                    val id = doc["documentId"].toString()

                    val checkPicksRef = getUserPoolsBasePath(userId)
                        .document(id).collection("playerPicks").get()
                        checkPicksRef.addOnSuccessListener {
                            val picks = it.documents
                            if(picks.size == 0){
                                Timber.i("[[[[[[[ no picks at all in this collection, return false")

                                return@addOnSuccessListener
                            }

                            for(pick in picks){
                                val pickWeek = pick["week"].toString().trim()
                                if(pickWeek == weekString.trim() ){
                                    Timber.i("[[[[[[[found one pick that matches the week, should return true")


                                    return@addOnSuccessListener
                                }
                            }

                        }
                } else {
                    Timber.i("[[[[[ no documents found for this pool")

                }
            }
        }.addOnCompleteListener {

        }

    }

    fun getUserBaseCollection(userId: String): DocumentReference {
        return mFirebaseDatabaseInstance.collection("users").document(userId)
    }

    fun getUserPoolsBasePath(userId: String): CollectionReference {
        return mFirebaseDatabaseInstance.collection("users").document(userId).collection("pools")
    }

    fun getPool(userId: String, poolId: String): DocumentReference {
        return mFirebaseDatabaseInstance.collection("users").document(userId).collection("pools")
            .document(poolId)
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

    fun getPoolPlayerPicks(userId: String, poolId: String ): CollectionReference {
        return getUserBaseCollection(userId).collection("pools")
            .document(poolId).collection("playerPicks")
    }

    fun updateUserBase(userId: String, data: HashMap<String, Any>): Task<Void> {
        return mFirebaseDatabaseInstance.collection("users").document(userId).update(data)
    }

    fun getPickDocumentForDelete(userId: String, poolDocId: String, playerPicksDocId: String){
        mFirebaseDatabaseInstance.document("/users/{$userId}/pools/{$poolDocId}/playerPicks/{$playerPicksDocId}").delete()
    }

    fun closeInstance(){

    }
}