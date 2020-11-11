package com.mattg.pickem.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mattg.pickem.db.repos.FirestoreRepositiory
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.Pool
import com.mattg.pickem.models.firebase.User
import timber.log.Timber


class PoolViewModel : ViewModel() {


    private val _usersList = MutableLiveData<ArrayList<User>>()
    val usersList: LiveData<ArrayList<User>> = _usersList

    private val repository = FirestoreRepositiory()

    val user = repository.currentUser!!

    private val _isInviteListShowing = MutableLiveData<Boolean>()
    val isInviteListShowing: LiveData<Boolean> = _isInviteListShowing

    private val _listOfInvitationsFromRepo = MutableLiveData<ArrayList<Invite>>()
    val listOfInvitationsFromRepo: LiveData<ArrayList<Invite>> = _listOfInvitationsFromRepo

    private val _poolIdHolder = MutableLiveData<String>()
    val poolIdHolder: LiveData<String> = _poolIdHolder

    private val _currentPool = MutableLiveData<String>()
    val currentPool: LiveData<String> = _currentPool

    private val _currentPoolName = MutableLiveData<String>()
    val currentPoolName: LiveData<String> = _currentPool

    private val _currentPoolPlayers = MutableLiveData<ArrayList<User>>()
    val currentPoolPlayers : LiveData<ArrayList<User>> = _currentPoolPlayers

    private val _listForInviteRecycler = MutableLiveData<ArrayList<Invite>>()
    val listForInviteRecycler: LiveData<ArrayList<Invite>> = _listForInviteRecycler

    private val _userPoolsList = MutableLiveData<ArrayList<Pool>>()
    val userPoolsList: LiveData<ArrayList<Pool>> = _userPoolsList

    private val _areInvites = MutableLiveData<Boolean>().apply {
        value = false
    }
    val areInvites: LiveData<Boolean> = _areInvites

    fun setCurrentPool(poolId: String, poolName: String) {
        _currentPool.postValue(poolId)
        _currentPoolName.value = (poolName)
    }


    fun setAreInvites(input: Boolean) {
        _areInvites.value = input
    }

    fun deletePool(poolId: String) {
        if (repository.deletePool(poolId)) {
            Timber.i("pool was deleted need to update adapter")
            getUserPools()
        }
    }

    fun getPoolPlayers(poolId: String){
        val players= repository.getPoolPlayers(poolId)
        players.get().addOnSuccessListener { snapshot ->
            val returnList = ArrayList<User>()
            for(item in snapshot){
                Timber.i("TESTING, item = ${item.get("playerName").toString()}, email  = ${item.get("playerEmail").toString()}")
                val id = item.get("playerId").toString()
                val name = item.get("playerName").toString()
                val email = item.get("playerEmail").toString()
                /**
                 * WILL NEED TO ACCOMODATE FOR PICKS AT SOME POINT
                 */
                val userToAdd = User(name, email, id)
                returnList.add(userToAdd)
                Timber.i("TESTING, list size while iteration = ${returnList.size}")
            }
            _currentPoolPlayers.value = returnList
            Timber.i("TESTING, list size after updating live data = ${returnList.size}")
            Timber.i("TESTING, live data after updating live data = ${_currentPoolPlayers.value}")

        }

    }

    fun declineInvitation(inviteId: String, senderId: String, sentInvitationId: String) {
        repository.deleteInvitation(inviteId)
        //after declining/deleting an invitation, check to see if any are left and if not reset the are invites field
        val checkedInvitations = repository.checkAreStillInvites()
        setAreInvites(checkedInvitations)
    }

    fun searchForUsers(input: String): ArrayList<User> {
        var returnList = ArrayList<User>()
        val result = repository.searchForUsers(input.toLowerCase())

        result.addOnSuccessListener {
            for (item in it) {
                if (item.exists()) {
                    Timber.i("Item found is : $item\n Name = ${item["name"]} Email = ${item["email"]}")
                    //create user object for recycler
                    val foundUser = User(
                            item["name"].toString(),
                            item["email"].toString(),
                            item["userId"].toString()
                    )
                    Timber.i("Generated User: ${foundUser.name}, ${foundUser.email}")
                    returnList.add(foundUser)
                }
            }
            _usersList.postValue(returnList)
        }

        Timber.i("_usersList value = ${_usersList.value}")
        return returnList

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

                when (areInvites) {
                    true -> {
                        val invites = userReference.collection("invitesReceived")
                            .get()
                        //iterate over them and delete later if declined, turn true and create pool on both ends if accepted
                        invites.addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                repository.resetUserInvitesField()
                                repository.setInvitesToNone()
                                return@addOnSuccessListener
                            } else
                                for (document in documents) {
                                    val data = document.data
                                    Timber.i("DATA = $data")
                                    val email = data["userWhoSentInviteEmail"].toString()
                                    val name = data["userWhoSentInviteName"].toString()
                                    val poolId = data["poolId"].toString()
                                    val poolName = data["poolName"].toString()
                                    val acceptBoolean = data["isAccepted"].toString()
                                    val senderId = data["userWhoSentInvite"].toString()
                                    val inviteId = data["documentId"].toString()
                                    val sentInvitationId = data["sentInviteId"].toString()

                                    val inviteForList = Invite(
                                        name,
                                        user.displayName!!,
                                        poolId,
                                        poolName,
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
                    false -> {
                        return@addSnapshotListener
                    }
                }
            }
        return areInvites
    }

    fun sendInvitation(receivingId: String): Boolean {
        if (_currentPool.value == null) {
            return false
        } else {
            val poolId = _currentPool.value

            val currentUser = repository.currentUser
            var sentInvitationId = ""

            //for the person sending the invite
            val senderRef =
                    repository.getUserBaseCollection(currentUser?.uid!!)
            senderRef.update("numberOfInvitesSet", + 1)

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
                                inviteReceivedHash["poolName"] = _currentPoolName.value!!
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
           //        }
             //      }
       //     }


        }
        return true
    }

    fun getUserPools() {

        repository.getUserPoolsBasePath(user!!.uid)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        val list = ArrayList<Pool>()
                        val pools = snapshots.documents
                        //get size of snapshot documents, so that loop can be broken with a count
                        val poolSize = pools.size
                        var count = 0
                        for (pool in pools) {
                            if(count > poolSize){
                                break
                            }
                            val id = pool.get("documentId").toString()
                            val owner = pool.get("owner").toString()
                            val poolName = pool.get("poolName").toString()
                            Timber.i("POOL NAME FROM SNAPSHOT = $poolName")
                            val poolToAdd = Pool(
                                    poolName,
                                    owner,
                                    user.uid,
                                    id
                            )
                            Timber.i("POOLTOADD NAME = ${poolToAdd.poolName}")
                            list.add(poolToAdd)
                            count++
                        }
                        Timber.i("POOLS LIST SIZE = ${list.size}")
                        _userPoolsList.postValue(list)
                        return@addSnapshotListener
                    }

                }
    }


    fun acceptInvitation(poolId: String, senderId: String, inviteId: String) {

        val senderReference = repository.getUserBaseCollection(senderId)

        senderReference.collection("pools").document(poolId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        //if no exception occurs, generate a pool object from the document
                        if (snapshot!!.exists()) {
                            val poolToCopy = Pool(
                                    snapshot.get("poolName").toString(),
                                    snapshot.get("ownerName").toString(),
                                    snapshot.get("owner").toString(),
                                    snapshot.get("documentId").toString(),
                                    snapshot.get("playerCount").toString().toInt(),
                            )

                            //create a new player to add to to pool collection
                            val playerToAdd = HashMap<String, Any>()
                            playerToAdd["playerName"] = user?.displayName!!
                            playerToAdd["playerId"] = user.uid
                            playerToAdd["playerEmail"] = user?.email!!


                            //add players to collection in both the new, and original pool
                            repository.createPoolsOnAccept(poolId, senderId, poolToCopy, playerToAdd)
                            //delete the invitations
                            repository.deleteInvitation(inviteId)
                            val checkedInvitations = repository.checkAreStillInvites()
                            setAreInvites(checkedInvitations)
                            getUserPools()
                            return@addSnapshotListener
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
        poolData["poolName"] = name
        poolData["playerCount"] = 0
        //Will return true if pool was created, false if it failed
        val returnPair = repository.createPool(userId, poolData)
        _poolIdHolder.postValue(returnPair.second)
        return returnPair.first

    }


}