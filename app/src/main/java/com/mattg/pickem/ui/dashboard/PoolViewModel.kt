package com.mattg.pickem.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mattg.pickem.db.FirestoreRepositiory
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.Pool
import com.mattg.pickem.models.firebase.User
import timber.log.Timber


class PoolViewModel : ViewModel() {

    private var database: FirebaseFirestore? = null
    private val _usersList = MutableLiveData<ArrayList<User>>()
    val usersList: LiveData<ArrayList<User>> = _usersList

    private val repository = FirestoreRepositiory()
    val user = repository.currentUser

    private val _isInviteListShowing = MutableLiveData<Boolean>()
    val isInviteListShowing: LiveData<Boolean> = _isInviteListShowing

    private val _listOfInvitationsFromRepo = MutableLiveData<ArrayList<Invite>>()
    val listOfInvitationsFromRepo: LiveData<ArrayList<Invite>> = _listOfInvitationsFromRepo

    private val _poolIdHolder = MutableLiveData<String>()
    val poolIdHolder: LiveData<String> = _poolIdHolder

    private val _currentPool = MutableLiveData<String>()
    val currentPool: LiveData<String> = _currentPool

    private val _listForInviteRecycler = MutableLiveData<ArrayList<Invite>>()
    val listForInviteRecycler: LiveData<ArrayList<Invite>> = _listForInviteRecycler

    private val _userPoolsList = MutableLiveData<ArrayList<Pool>>()
    val userPoolsList: LiveData<ArrayList<Pool>> = _userPoolsList

    private val _areInvites = MutableLiveData<Boolean>().apply {
        value = false
    }
    val areInvites: LiveData<Boolean> = _areInvites

    fun showInviteList() {
        _isInviteListShowing.value = true
    }

    fun hideInviteList() {
        _isInviteListShowing.value = false
    }

    fun setCurrentPool(poolId: String) {
        _currentPool.postValue(poolId)
    }

    //call this from view to get invitaitons hopefully
    fun observeInvitations() {
        if (checkForCurrentInvitations()) {
            _listOfInvitationsFromRepo.value = repository.listOfInvitations.value
        }

    }

    fun areInvites(): Boolean? {
        return _areInvites.value
    }

    fun deletePool(poolId: String){
        repository.deletePool(poolId)
    }

    fun declineInvitation(inviteId: String, senderId: String, sentInvitationId: String) {
        val returnPair = repository.deleteInvitation(inviteId, senderId, sentInvitationId)
        val recieverEnd = returnPair.first
        val senderEnd = returnPair.second

        recieverEnd.addOnSuccessListener {
            //send toast
            Timber.i("receiver end deleted")
        }
        senderEnd.addOnSuccessListener {
            //send toast or other notification
            Timber.i("sender end document deleted")
        }
    }

    fun getUsersForRecycler() {
        //initialize firestore
        database = FirebaseFirestore.getInstance()
        //get reference to document
        val docRef = repository.mFirebaseDatabaseInstance.collection("users").orderBy(
            "name", com.google.firebase.firestore.Query.Direction.ASCENDING
        )
        docRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val list = ArrayList<User>()
            if (snapshots != null) {
                val users = snapshots.documents
                //iterate over user documents and create a list for the recycler view
                for (user in users) {
                    val name = user.get("name").toString()
                    val email = user.get("email").toString()
                    val id = user.get("userId").toString()
                    val picks = user.get("picks").toString()
                    val userToAdd = User(name, email, id, null)
                    //add each user to the holder list
                    list.add(userToAdd)
                }
                //post list value to live data variables
                _usersList.value = list
            }
        }
    }

    fun getUserPools() {
        val poolsReference = repository.getUserPoolsBasePath(user!!.uid)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val list = ArrayList<Pool>()
                if (snapshots != null) {
                    val pools = snapshots.documents
                    for (pool in pools) {
                        val id = pool.get("documentId").toString()
                        val owner = pool.get("owner").toString()
                        val players = pool.get("players") as List<*>
                        val poolName = pool.get("poolName").toString()

                        val poolToAdd = user.uid.let {
                            Pool(
                                poolName, owner,
                                it, id
                            )
                        }

                        list.add(poolToAdd)

                    }
                    _userPoolsList.postValue(list)
                }
            }

    }


    fun listenForInvitations(): Boolean {
        var booleanReturn = false
        repository.listenForInvitations().addSnapshotListener { snapshot, error ->
            if (error != null) {
                Timber.i(error)
                return@addSnapshotListener
            } else {
                booleanReturn = checkForCurrentInvitations()
            }
        }
        return booleanReturn
    }

    private fun checkForCurrentInvitations(): Boolean {
        var areInvites: Boolean = false
        val listForRecycler = ArrayList<Invite>()
        val userReference = repository.getUserBaseCollection(user!!.uid)
        repository.listenForInvitations()
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.i("Error upadating")
                    return@addSnapshotListener
                }
                areInvites = repository.checkForInvitations(snapshot!!)

                if (snapshot.data?.get("invites") != null) {
                    if (snapshot.data?.get("invites")!!.equals(true)) {
                        //getting all of the invites for the current user, that have not been accepted
                        val invites = userReference.collection("invitesReceived")
                            .get()
                        //iterate over them and delete later if declined, turn true and create pool on both ends if accepted
                        invites.addOnSuccessListener { documents ->
                            Timber.i("Result of checking for invites = ${documents.documents}")
                            //                                    val inviteReceivedHash = HashMap<String, Any>()
                            //                                    inviteReceivedHash["userWhoSentInvite"] = auth.currentUser!!.uid
                            //                                    inviteReceivedHash["accepted"] = false
                            //                                    inviteReceivedHash["userWhoSentInviteName"] = auth.currentUser!!.displayName!!
                            //                                    inviteReceivedHash["userWhoSentInviteEmail"] = auth.currentUser!!.email!!

                            if (documents.isEmpty) {
                                repository.resetUserInvitesField()
                                return@addOnSuccessListener
                            } else
                                for (document in documents) {
                                    val data = document.data
                                    Timber.i("DATA = $data")
                                    val email = data["userWhoSentInviteEmail"].toString()
                                    val name = data["userWhoSentInviteName"].toString()
                                    val poolId = data["poolId"].toString()
                                    val acceptBoolean = data["isAccepted"].toString()
                                    val senderId = data["userWhoSentInvite"].toString()
                                    val inviteId = data["documentId"].toString()
                                    val sentInvitationId = data["sentInviteId"].toString()

                                    val inviteForList = Invite(
                                        name,
                                        user.displayName!!,
                                        poolId,
                                        senderId,
                                        email,
                                        inviteId,
                                        sentInvitationId
                                    )
                                    //add invite object to list for recycler
                                    listForRecycler.add(inviteForList)
                                }
                            Timber.i("Amount of invitations found ${listForRecycler.size}")
                            //shows a list of current invitations
                            //  generateDialogOfInvites(listForRecycler)
                            _listForInviteRecycler.postValue(listForRecycler)
                            _areInvites.postValue(areInvites)
                        }

                    }
                }
            }


        return areInvites
    }

    fun sendInvitation(receivingId: String): Boolean {
        if (_currentPool.value == null) {
            return false
            Timber.i("NO CURRENT POOL SELECTED MIGHT HAVE TO SHOW TOAST HERE")
        } else {
            val poolId = _currentPool.value
            val currentUser = repository.currentUser
            var sentInvitationId = ""

            //for the person sending the invite
            val senderRef =
                repository.getUserBaseCollection(currentUser?.uid!!)
            senderRef.update("numberOfInvitesSet", +1)
            //creating a key value for collection of people who were sent invites
            //there is an id mapped to "userWhoGotInvite" and a boolean set to false.  If they respond yes
            //they will have to seek this boolean out and change it back, it will have to be listened too
            val inviteHash = HashMap<String, Any>()
            inviteHash["userWhoGotInvite"] = receivingId
            inviteHash["isAccepted"] = false
            currentUser.uid.let {
                senderRef.collection("invitesSent").add(inviteHash)
                    .addOnSuccessListener {
                        //add a field in new document to reference its own id
                        it?.id?.let { it1 ->
                            senderRef.collection("invitesSent").document(it1)
                                .update("documentId", it1)
                            sentInvitationId = it1
                            Timber.i(" IT1 = $it1 should be equal to sentInvitaionId = $sentInvitationId")

                            //for the recieving end
                            val playerRef = repository.getUserBaseCollection(receivingId)
                            playerRef.update("invites", true)
                            playerRef.update("pendingInvites", +1)
                            //create an invitation document on the receiving end
                            val inviteReceivedHash = HashMap<String, Any>()
                            inviteReceivedHash["userWhoSentInvite"] = currentUser!!.uid
                            inviteReceivedHash["accepted"] = false
                            inviteReceivedHash["userWhoSentInviteName"] = currentUser.displayName!!
                            inviteReceivedHash["userWhoSentInviteEmail"] = currentUser.email!!
                            poolId?.let { it -> inviteReceivedHash["poolId"] = it }
                            inviteReceivedHash["idOfSentInvite"] = sentInvitationId
                            //update the recieving end

                            //store the value of the sent invitation, so that it can later be deleted
                            playerRef.collection("invitesReceived").add(inviteReceivedHash)
                                .addOnSuccessListener {
                                    //add a field to the new document that references its id
                                    it?.id?.let { it1 ->
                                        playerRef
                                            .collection("invitesReceived")
                                            .document(it1).update("documentId", it1)
                                    }
                                }
                        }
                    }
            }


        }
        return true
    }

    fun acceptInvitation(poolId: String) {
        val userReceivedInvitesReference = repository.getUserRecievedInvites(user!!.uid)
        val userReferenece = repository.getUserBaseCollection(user.uid)
        var wasSuccessReiever = false
        var wasSuccessSender = false
        userReceivedInvitesReference.get()
            .addOnSuccessListener { documents ->
                //iterate over the recieved invitations
                for (document in documents) {

                    if (document.get("poolId")?.equals(poolId)!!) {
                        //get the pool id from the invite
                        val poolIdRetrieved = document.get("poolId").toString()
                        val documentToPullFrom =
                            repository.getUserRecievedInvites(user.uid).document(document.id).get()
                        //empty variable for the owner/sender of invite
                        var ownerName = ""
                        //assign name to variable
                        documentToPullFrom.addOnSuccessListener {
                            ownerName = it.get("userWhoSentInviteName").toString()
                            Timber.i("Userwhosentinvite = $ownerName")
                        }
                        //create list of owner and current user to fill in fields on users pool document
                        val poolMembers = listOf(ownerName, user.displayName)
                        Timber.i("LIST CREATED WITH ${poolMembers[0]}  ${poolMembers[1]}")
                        //get the pool reference from the sender, and copy it to the user
                        val originalPoolDocument =
                            repository.mFirebaseDatabaseInstance.collection("users")
                                .document(document["userWhoSentInvite"].toString())
                                .collection("pools")
                                .whereEqualTo("poolId", poolIdRetrieved)
                                .get()
                        val data = originalPoolDocument.addOnSuccessListener {
                            it.documents.forEach { it ->
                                if (it.exists()) {
                                    Timber.i("${it.data}")
                                    userReferenece.collection("pools").add(it)
                                }
                            }


                        }

                    }
                }
            }

    }

    fun createPool(userId: String, name: String): Boolean {
        val user = repository.currentUser
        //change the fields of the user (recipient)
        val userRef = repository.getUserBaseCollection(userId)
        val poolData = HashMap<String, Any>()
        poolData["owner"] = user!!.uid
        poolData["ownerName"] = user.displayName.toString()
        poolData["players"] = emptyList<Pair<String, String>>()
        poolData["poolName"] = name
        //Will return true if pool was created, false if it failed
        val returnPair = repository.createPool(userId, poolData)
        _poolIdHolder.postValue(returnPair.second)
        return returnPair.first

    }


}