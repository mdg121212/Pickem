package com.mattg.pickem.ui.pools.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.mattg.pickem.db.Pick
import com.mattg.pickem.db.repos.ApiCallRepository
import com.mattg.pickem.db.repos.FirestoreRepository
import com.mattg.pickem.models.firebase.*
import com.mattg.pickem.models.iomodels.IOWeekScoresResponse
import com.mattg.pickem.network.APICallService
import com.mattg.pickem.parsebackend.*
import com.mattg.pickem.parsebackend.models.ParseInvite
import com.mattg.pickem.parsebackend.models.ParsePick
import com.mattg.pickem.parsebackend.models.ParsePool
import com.mattg.pickem.parsebackend.models.ParsePoolPlayer
import com.mattg.pickem.utils.Constants
import com.mattg.pickem.utils.getWinnerByFinalPoints
import com.mattg.pickem.utils.getWinnerByFinalPointsMoreThanTwo
import com.parse.ParseUser
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@SuppressWarnings("unused")
class PoolViewModel(application: Application) : AndroidViewModel(application) {


    private val apiRepository = ApiCallRepository(application)
    private val repository = FirestoreRepository()
    val user = repository.currentUser!!

    private val parsePoolRepository = ParsePoolRepository()
    private val parseUserRepository = ParseUserRepository()
    private val parsePickRepository = ParsePickRepository()
    private val parseQueryRepository = ParseQueryRepository()
    private val parseInviteRepository = ParseInviteRepository()

    private val _parsePoolError = MutableLiveData<String>()
    val parsePoolError: LiveData<String> = _parsePoolError

    private val _parsePools = MutableLiveData<ArrayList<ParsePool>>()
    val parsePools: LiveData<ArrayList<ParsePool>> = _parsePools

    private val _parsePoolPlayers = MutableLiveData<ArrayList<ParsePoolPlayer>>()
    val parsePoolPlayers: LiveData<ArrayList<ParsePoolPlayer>> = _parsePoolPlayers

    private val _currentParsePoolId = MutableLiveData<String>()
    val currentParsePoolId: LiveData<String> = _currentParsePoolId

    private val _parseInvitesList = MutableLiveData<ArrayList<ParseInvite>>()
    val parseInvitesList: LiveData<ArrayList<ParseInvite>> = _parseInvitesList

    private val _parseRetrievedPoolPicks = MutableLiveData<ArrayList<ParsePick>>()
    val parseRetrievedPoolPicks: LiveData<ArrayList<ParsePick>> = _parseRetrievedPoolPicks

    private val _currentParsePoolData = MutableLiveData<ParsePool>()
    val currentParsePoolData: LiveData<ParsePool> = _currentParsePoolData

    private val _isPoolIdReady: MutableLiveData<Boolean> = parsePoolRepository.isCurrentIdReady
    val isPoolIdReady: LiveData<Boolean> = _isPoolIdReady

    private val _justCreatedParsePoolId: MutableLiveData<String> =
        parsePoolRepository.justCreatedPoolId

    private val _parseUserEmailSearchStrings = MutableLiveData<ArrayList<ParseUser>>()
    val parseUserEmailSearchStrings: LiveData<ArrayList<ParseUser>> = _parseUserEmailSearchStrings


    private val _finalScoresFromWeek =
        MutableLiveData<Pair<ArrayList<Triple<String, String, String>>, String>>()
    val finalScoresFromWeek: LiveData<Pair<ArrayList<Triple<String, String, String>>, String>> =
        _finalScoresFromWeek

    private val _apiCallCurrentWeek = MutableLiveData<String>()
    val apiCallCurrentWeek: LiveData<String> = _apiCallCurrentWeek

    private val _apiCallLastCompletedWeek = MutableLiveData<String>()
    val apiCallLastCompletedWeek: LiveData<String> = _apiCallLastCompletedWeek

    private val _picksFromDatabase = MutableLiveData<List<Pick>>()
    val picksFromDatabase: LiveData<List<Pick>> = _picksFromDatabase

    private val _usersList = MutableLiveData<ArrayList<User>>()
    val usersList: LiveData<ArrayList<User>> = _usersList

    private val _isInviteListShowing = MutableLiveData<Boolean>()
    val isInviteListShowing: LiveData<Boolean> = _isInviteListShowing

    private val _poolOwnerIdForAddingPicks = MutableLiveData<String>()

    private val _listOfInvitationsFromRepo = MutableLiveData<ArrayList<Invite>>()
    val listOfInvitationsFromRepo: LiveData<ArrayList<Invite>> = _listOfInvitationsFromRepo

    private val _poolIdHolder = MutableLiveData<String>()
    val poolIdHolder: LiveData<String> = _poolIdHolder

    private val _currentPool = MutableLiveData<String>()
    val currentPool: LiveData<String> = _currentPool

    private val _areInvites = MutableLiveData<Boolean>().apply {
        value = false
    }
    val areInvites: LiveData<Boolean> = _areInvites

    private val _currentPoolPlayersPicks = MutableLiveData<ArrayList<PickForDisplay>>()
    val currentPoolPlayersPicks: LiveData<ArrayList<PickForDisplay>> = _currentPoolPlayersPicks

    private val _currentPoolPlayersPicksFiltered = MutableLiveData<ArrayList<PickForDisplay>>()
    val currentPoolPlayersPicksFiltered: LiveData<ArrayList<PickForDisplay>> = _currentPoolPlayersPicksFiltered

    private val _currentPoolName = MutableLiveData<String>()
    val currentPoolName: LiveData<String> = _currentPoolName

    private val _currentPoolOwnerId = MutableLiveData<String>()
    val currentPoolOwnerId: LiveData<String> = _currentPoolOwnerId

    private val _currentPoolOwnerName = MutableLiveData<String>()
    val currentPoolOwnerName: LiveData<String> = _currentPoolOwnerName

    private val _currentPoolPlayers = MutableLiveData<ArrayList<User>>()
    val currentPoolPlayers: LiveData<ArrayList<User>> = _currentPoolPlayers

    private val _listForInviteRecycler = MutableLiveData<ArrayList<Invite>>()
    val listForInviteRecycler: LiveData<ArrayList<Invite>> = _listForInviteRecycler

    private val _userPoolsList = MutableLiveData<ArrayList<Pool>>()
    val userPoolsList: LiveData<ArrayList<Pool>> = _userPoolsList

    private val _currentSelectedPicks = MutableLiveData<Pick>()
    val currentSelectedPicks: LiveData<Pick> = _currentSelectedPicks

    private val _picksForScore = MutableLiveData<ArrayList<HashMap<String, String>>>()

    private val _retrievedWinningTeams = MutableLiveData<ArrayList<String>>()
    val retrievedWinningTeams: LiveData<ArrayList<String>> = _retrievedWinningTeams

    private val _playerScoresCalculatedList = MutableLiveData<ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>>()
    val playerScoresCalculatedList: LiveData<ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>> = _playerScoresCalculatedList

    private val _winnersForRecycler = MutableLiveData<ArrayList<WinnerItem>>()
    val winnersForRecycler: LiveData<ArrayList<WinnerItem>> = _winnersForRecycler

    private val _needWinners = MutableLiveData<Boolean>()
    val needWinners: LiveData<Boolean> = _needWinners

    private val _arePicksForWeek = MutableLiveData<Boolean>()

    private val _dateToCheck = MutableLiveData<String>()
    val dateToCheck: LiveData<String> = _dateToCheck

    private val _winnerName = MutableLiveData<String>()
    val winnerName: LiveData<String> = _winnerName

    private val _weekToCheckWinnerAPI = MutableLiveData<String>()
    val weekToCheckWinnerApi: LiveData<String> = _weekToCheckWinnerAPI

    fun getWeekToCheckWinnerApi() {
        APICallService.fetchIOApi().getWeekForCheckingScore(Constants.key).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Timber.i("$$$$$ week to check scores from api is ${response.body()}")
                _weekToCheckWinnerAPI.value = response.body()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setPicks(picks: Pick) {
        _currentSelectedPicks.value = picks
    }

    fun resetWinnerName() {
        _winnerName.value = null
    }

//    fun submitPicks(dateToCheck: String) {
//        val currentPicks = currentSelectedPicks.value
//        currentPicks?.let {
//            addPicksForUserInPool(it, user.uid, dateToCheck)
//            addPicksToParsePool(it)
//            resetSelectedPicks()
//        }
//    }

    fun setCurrentPool(
        poolId: String,
        poolName: String,
        poolOwnerId: String,
        poolOwnerName: String
    ) {
        _currentPool.postValue(poolId)
        _currentPoolName.value = poolName
        Timber.i("TESTING--->setting _currentPoolName to $poolName")
        _currentPoolOwnerId.value = poolOwnerId
        Timber.i("<<<<<<<<retrieving the owner name in the set function $poolOwnerName is being passed in to _currentpoolownername")
        _currentPoolOwnerName.value = poolOwnerName
    }

    private fun resetSelectedPicks() {
        _currentSelectedPicks.postValue(null)
    }

    private fun setAreInvites(input: Boolean) {
        _areInvites.value = input
    }

//    fun deletePool(poolId: String, poolName: String, isOwner: Boolean) {
//        if (repository.deletePool(poolId, poolName, isOwner)) {
//            Timber.i("pool was deleted need to update adapter")
//            getUserPools()
//        }
//    }

    fun setNeedWinners(input: Boolean) {
        _needWinners.value = input
    }


//    fun createPool(userId: String, name: String, currentWeek: String): Boolean {
//        //change the fields of the user (recipient)
//        repository.getUserBaseCollection(userId)
//        val poolData = HashMap<String, Any>()
//        poolData["ownerId"] = user.uid
//        poolData["ownerName"] = user.displayName.toString()
//        poolData["poolName"] = name
//        poolData["currentWeek"] = currentWeek
//        poolData["playerCount"] = 1
//
//        val playerToAdd = HashMap<String, Any>()
//        playerToAdd["playerName"] = user.displayName!!
//        playerToAdd["playerId"] = user.uid
//        playerToAdd["playerEmail"] = user.email!!
//
//        //Will return true if pool was created, false if it failed
//        val returnPair = repository.createPool(userId, poolData, playerToAdd)
//
//        _poolIdHolder.postValue(returnPair.second)
//        return returnPair.first
//
//    }

    fun displayWinner(
        inputList: ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>,
        lastWeek: String,
        finalScore: Int
    ) {
        val list = inputList.sortedByDescending { it.second.first }
        Timber.i("+++++++++list of picks sorted is $list")//this sorts by the number correct
        val highScore = list.first().second.first
        Timber.i("++++++++highScore is $highScore")//this gets the number correct
        val highScoreTiesList = ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>()
        val data = HashMap<String, Any>()
        for (item in inputList) {
            if (item.second.first == highScore) {
                highScoreTiesList.add(item)
            }
        }
        Timber.i("+++++++highScoresTiesList = $highScoreTiesList")
        if(highScoreTiesList.size == 1){
            val item = list.first()
           // Toast.makeText(requireContext(), "The winner is ${item.first.first}", Toast.LENGTH_SHORT).show()
            data["playerId"] = item.first.second
            data["playerName"] = item.first.first
            data["week"] = lastWeek
            updateWinners(data)
            return
        }
        if(highScoreTiesList.size > 1){
            if(highScoreTiesList.size == 2){
                val firstPlayer = highScoreTiesList[0]
                val secondPlayer = highScoreTiesList[1]
                Timber.i("++++++++firstPlayer is ${firstPlayer.first} with ${firstPlayer.second.second} points")
                Timber.i("++++++++secondPlayer is ${secondPlayer.first} with ${secondPlayer.second.second} points")
                val winner =
                        getWinnerByFinalPoints(firstPlayer.first.first, firstPlayer.second.second,
                                secondPlayer.first.first, secondPlayer.second.second, finalScore)
                //if we get a tie lets see what the names show as
                if (winner == "TIE") {
                    val nameOne = firstPlayer.first.first
                    val nameTwo = secondPlayer.first.first
                    val tiedWinner = "$nameOne and $nameTwo tied!"
                    val dataForWinners = HashMap<String, Any>()
                    dataForWinners["playerId"] = "TIE"
                    dataForWinners["playerName"] = tiedWinner
                    dataForWinners["week"] = lastWeek
                    _winnerName.value = tiedWinner
                    updateWinners(dataForWinners)
                    return
                }

                for (item in highScoreTiesList) {
                    if (item.first.first.trim() == winner.trim()) {

                        data["playerId"] = item.first.second
                        data["playerName"] = item.first.first
                        data["week"] = lastWeek

                        updateWinners(data)
                        _winnerName.value = item.first.first
                        return
                    }
                }

            }
            if (highScoreTiesList.size > 2) {
                val winner = getWinnerByFinalPointsMoreThanTwo(highScoreTiesList, finalScore)
                if (winner.contains("tied!")) {
                    data["playerId"] = "TIE"
                    data["playerName"] = winner
                    data["week"] = lastWeek
                    updateWinners(data)
                    _winnerName.value = winner
                    return
                }
                for (item in highScoreTiesList) {
                    if (item.first.first.trim() == winner.trim()) {
                        data["playerId"] = item.first.second
                        data["playerName"] = item.first.first
                        data["week"] = lastWeek

                        updateWinners(data)
                        _winnerName.value = item.first.first
                        return
                    }
                }

            }
        }

    }
//
//    fun filteredGetPicks(poolId: String, filter: String) {
//
//        val listOfPicks = ArrayList<PickForDisplay>()
//        val picksGotten =
//                repository.getPoolPlayerPicks(user.uid, poolId)
//                        .whereEqualTo("week", " $filter").get()
//                .addOnSuccessListener {
//
//                    Timber.i("--------------inSuccessListener")
//                    val picksDocuments = it.documents
//                    if (picksDocuments != null) {
//                        for (doc in picksDocuments) {
//
//                            val picktoCopy = PickForDisplay(
//                                    doc.get("finalPoints").toString(),
//                                    doc.get("picks").toString(),
//                                    doc.get("playerId").toString(),
//                                    doc.get("playerName").toString(),
//                                    doc.get("week").toString().trim()
//                            )
//
//                            if (picktoCopy.week == filter.trim()) {
//                                //the pick doesn't match the filter, dont add it to the list
//                                listOfPicks.add(picktoCopy)
//                            }
//
//
//
//                        }
//                        //   Timber.i("----------------- $listOfPicks finshed with list of picks, its size is ${listOfPicks.size}}")
//                        //   _currentPoolPlayersPicks.value = listOfPicks
//
//                    }
//
//                }
//        picksGotten.addOnCompleteListener {
//            _currentPoolPlayersPicks.value = listOfPicks
//        }
//    }

//    fun getPoolPicks(poolId: String, lastWeek: String) {
//        val listOfPicks = ArrayList<PickForDisplay>()
//        repository.getPoolPlayerPicks(user.uid, poolId)
//                .addSnapshotListener { value, _ ->
//                    val picksDocuments = value?.documents
//                    if (picksDocuments != null) {
//                        var count = 0
//                       for (doc in picksDocuments) {
//                            if(doc["week"].toString() == lastWeek && count == 0) {
//                                _dateToCheck.value = doc["dateToCheck"].toString()
//                                count++
//                            }
//                            val picktoCopy = PickForDisplay(
//                                    doc.get("finalPoints").toString(),
//                                    doc.get("picks").toString(),
//                                    doc.get("playerId").toString(),
//                                    doc.get("playerName").toString(),
//                                    doc.get("week").toString()
//                            )
//                            listOfPicks.add(picktoCopy)
//                        }
//                        listOfPicks.sortBy { pickForDisplay -> pickForDisplay.week }
//                        }
//                        _currentPoolPlayersPicks.value = listOfPicks
//
//
//                    }
//
//                }
//
//

//    fun getUserPools() {
//        val poolsRef = repository.getUserPoolsBasePath(user.uid)
//        poolsRef.addSnapshotListener { snapshots, error ->
//            if (error != null) {
//                return@addSnapshotListener
//            }
//            if (snapshots != null) {
//                val list = ArrayList<Pool>()
//                val pools = snapshots.documents
//                //get size of snapshot documents, so that loop can be broken with a count
//                val poolSize = pools.size
//                var count = 0
//                for (pool in pools) {
//                    if (count > poolSize) {
//                        break
//                    }
//
//                    val id = pool.get("documentId").toString()
//                    val ownerId = pool.get("ownerId").toString()
//                    val ownerName = pool.get("ownerName").toString()
//                    val poolName = pool.get("poolName").toString()
//
//                    Timber.i("<<<<<owner name set to $ownerName")
//
//                    val poolToAdd = Pool(
//                            poolName,
//                            ownerName,
//                            ownerId,
//                            id,
//
//                            )
//
//                    Timber.i("<<<<<<Pool owner name saved as ${poolToAdd.ownerName}")
//                    list.add(poolToAdd)
//                    count++
//                }
//
//                _userPoolsList.value = (list)
//                return@addSnapshotListener
//            }
//
//        }
//    }

//    fun getPoolPlayers(poolId: String) {
//        val returnList = ArrayList<User>()
//        val players = repository.getPoolPlayers(poolId)
//        players.get().addOnSuccessListener { snapshot ->
//            if (snapshot == null) {
//                Timber.i("TESTINGPOOLS snapshot was null")
//                return@addOnSuccessListener
//            }
//
//            for (item in snapshot) {
//
//                val id = item.get("playerId").toString()
//                val name = item.get("playerName").toString()
//                val email = item.get("playerEmail").toString()
//
//                val userToAdd = User(name, email, id)
//                Timber.i("******************* ${userToAdd.name}")
//                val idForPicks = item.id
//                players.document(idForPicks).collection("playerPicks").get().addOnSuccessListener {
//                    for (document in it.documents) {
//                        val points = document["finalPoints"].toString()
//                        val week = document["week"].toString()
//                        val retrievedPicks = document["picks"].toString()
//                        val dateToCheck = document["dateToCheck"].toString()
//                        val retrievedPicksObject = Pick(
//                                week = week,
//                                name = name,
//                                "",
//                                picksGamesOnly = retrievedPicks,
//                                finalPoints = points,
//                                dateToCheck = dateToCheck
//                        )
//                        userToAdd.picks.add(retrievedPicksObject)
//                    }
//                }
//                if(returnList.contains(userToAdd)){
//                    Timber.i("****************list contains $userToAdd")
//                } else {
//                    returnList.add(userToAdd)
//                }
//
//            }
//            _currentPoolPlayers.value = (returnList)
//        }
//
//
//    }

//    fun declineInvitation(inviteId: String) {
//        repository.deleteInvitation(inviteId)
//        //after declining/deleting an invitation, check to see if any are left and if not reset the are invites field
//        val checkedInvitations = repository.checkAreStillInvites()
//        setAreInvites(checkedInvitations)
//    }

//    fun searchForUsers(input: String) {
//        val returnList = ArrayList<User>()
//        val result = repository.searchForUsers(input.toLowerCase(Locale.ROOT))
//
//        result.addOnSuccessListener {
//            for (item in it) {
//                if (item.exists()) {
//                    //create user object for recycler
//                    val foundUser = User(
//                            item["name"].toString(),
//                            item["email"].toString(),
//                            item["userId"].toString()
//                    )
//
//                    if (foundUser.email == user.email) {
//                        return@addOnSuccessListener
//                    } else {
//                        returnList.add(foundUser)
//                    }
//                }
//            }
//            _usersList.value = (returnList)
//        }.addOnFailureListener { exception ->
//            Timber.i("<<<<failed to get users to invite ${exception.message}")
//        }
//
//    }


//    fun listenForInvitations(): Boolean {
//        var booleanReturn = false
//        val listener = repository.listenForInvitations()
//        listener.addSnapshotListener { _, error ->
//            if (error != null) {
//                Timber.i(error)
//                return@addSnapshotListener
//            } else {
//                booleanReturn = checkForCurrentInvitations()
//            }
//        }
//
//        return booleanReturn
//    }

//    private fun checkForCurrentInvitations(): Boolean {
//        var areInvites = false
//        val listForRecycler = ArrayList<Invite>()
//        val userReference = repository.getUserBaseCollection(user.uid)
//        repository.listenForInvitations()
//                .addSnapshotListener { snapshot, error ->
//                    if (error != null) {
//                        Timber.i("Error updating")
//                        return@addSnapshotListener
//                    }
//                    areInvites = repository.checkForInvitations(snapshot!!)
//
//                    when (areInvites) {
//                        true -> {
//                            val invites = userReference.collection("invitesReceived").get()
//                            //iterate over them and delete later if declined, turn true and create pool on both ends if accepted
//                            invites.addOnSuccessListener { documents ->
//                                if (documents.isEmpty) {
//                                    repository.resetUserInvitesField()
//                                    repository.setInvitesToNone()
//                                    return@addOnSuccessListener
//                                } else
//                                    for (document in documents) {
//                                        val data = document.data
//                                        Timber.i("DATA = $data")
//                                        val email = data["userWhoSentInviteEmail"].toString()
//                                        val name = data["userWhoSentInviteName"].toString()
//                                        val poolId = data["poolId"].toString()
//                                        val poolName = data["poolName"].toString()
//                                        val senderId = data["userWhoSentInvite"].toString()
//                                        val inviteId = data["documentId"].toString()
//                                        val sentInvitationId = data["sentInviteId"].toString()
//
//                                        val inviteForList = Invite(
//                                                name,
//                                                user.displayName!!,
//                                                poolId,
//                                                poolName,
//                                                senderId,
//                                                email,
//                                                inviteId,
//                                                sentInvitationId
//                                        )
//                                        //add invite object to list for recycler
//                                        listForRecycler.add(inviteForList)
//                                    }
//                                //shows a list of current invitations
//                                _listForInviteRecycler.postValue(listForRecycler)
//                                _areInvites.postValue(areInvites)
//                            }
//                        }
//                        false -> {
//                            return@addSnapshotListener
//                        }
//                    }
//                }
//        return areInvites
//    }

//    fun getSpecificPool(poolId: String) {
//            val poolRef = repository.getPool(user.uid, poolId)
//            poolRef.addSnapshotListener { document, _ ->
//            val name = document?.get("poolName")?.toString()
//            val ownerName = document?.get("ownerName").toString()
//            val ownerId = document?.get("ownerId").toString()
//            _poolOwnerIdForAddingPicks.value = ownerId
//            Timber.i("TESTINGPOOLS----> name $name, ownerName $ownerName, ownerId = $ownerId")
//        }
//
//    }

//    fun sendInvitation(receivingId: String): Boolean {
//        if (_currentPool.value == null) {
//            return false
//        } else {
//            val poolId = _currentPool.value
//
//            val currentUser = repository.currentUser
//            val sentInvitationId = ""
//            //for the person sending the invite
//            val senderRef =
//                    repository.getUserBaseCollection(currentUser?.uid!!)
//            senderRef.update("numberOfInvitesSet", +1)
//            //for the recieving end
//            val playerRef = repository.getUserBaseCollection(receivingId)
//            playerRef.update("invites", true)
//            playerRef.update("pendingInvites", +1)
//            //create an invitation document on the receiving end
//            val inviteReceivedHash = HashMap<String, Any>()
//            inviteReceivedHash["userWhoSentInvite"] = currentUser.uid
//            inviteReceivedHash["accepted"] = false
//            inviteReceivedHash["userWhoSentInviteName"] = currentUser.displayName!!
//            inviteReceivedHash["userWhoSentInviteEmail"] = currentUser.email!!
//            inviteReceivedHash["poolName"] = _currentPoolName.value!!
//            poolId?.let { it -> inviteReceivedHash["poolId"] = it }
//            inviteReceivedHash["idOfSentInvite"] = sentInvitationId
//            //store the value of the sent invitation, so that it can later be deleted
//            playerRef.collection("invitesReceived").add(inviteReceivedHash)
//                    .addOnSuccessListener {
//                        //add a field to the new document that references its id
//                        it?.id?.let { it1 ->
//                            playerRef
//                                    .collection("invitesReceived")
//                                    .document(it1).update("documentId", it1)
//                        }
//                    }
//        }
//        return true
//    }
//

//    fun acceptInvitation(poolId: String, senderId: String, inviteId: String) {
//
//        val senderReference = repository.getUserBaseCollection(senderId)
//        //for players list
//        val playersToInclude = ArrayList<HashMap<String, Any>>()
//
//        senderReference.collection("pools").document(poolId).collection("players").whereEqualTo("playerId", senderId).get()
//                .addOnSuccessListener { document ->
//
//                    for (doc in document) {
//                        val playerToAdd = HashMap<String, Any>()
//                        playerToAdd["playerEmail"] = doc.get("playerEmail").toString()
//                        playerToAdd["playerId"] = doc.get("playerId").toString()
//                        playerToAdd["playerName"] = doc.get("playerName").toString()
//                        playersToInclude.add(playerToAdd)
//                    }
//                }
//
//
//        senderReference.collection("pools").document(poolId)
//                .addSnapshotListener { snapshot, exception ->
//                    if (exception == null) {
//                        //if no exception occurs, generate a pool object from the document
//                        if (snapshot!!.exists()) {
//                            val poolToCopy = Pool(
//                                    snapshot.get("poolName").toString(),
//                                    snapshot.get("ownerName").toString(),
//                                    snapshot.get("ownerId").toString(),
//                                    snapshot.get("documentId").toString(),
//
//                                    )
//
//                            Timber.i("<<<<<Pool to copy saving ownerName as ${snapshot.get("ownerName").toString()}")
//
//
//                            //create a new player to add to to pool collection
//                            val playerToAdd = HashMap<String, Any>()
//                            playerToAdd["playerName"] = user.displayName!!
//                            playerToAdd["playerId"] = user.uid
//                            playerToAdd["playerEmail"] = user.email!!
//
//                            playersToInclude.add(playerToAdd)
//
//                            //add players to collection in both the new, and original pool
//                            repository.createPoolsOnAccept(
//                                    poolId, senderId, poolToCopy, playersToInclude,
//
//                                    )
//                            //delete the invitations
//                            repository.deleteInvitation(inviteId)
//
//
//                            return@addSnapshotListener
//                        }
//
//                    }
//                }
//
//        val checkedInvitations = repository.checkAreStillInvites()
//        setAreInvites(checkedInvitations)
//        getUserPools()
//    }
//

//    private fun addPicksForUserInPool(picks: Pick, userId: String, dateToCheckFromPrefs: String?) {
//        //picks data to add to documents
//        val data = HashMap<String, Any>()
//        data["week"] = picks.week
//        data["picks"] = picks.picksGamesOnly
//        data["finalPoints"] = picks.finalPoints
//        data["playerId"] = user.uid
//        data["playerName"] = user.displayName!!
//        data["dateToCheck"] = dateToCheckFromPrefs!!
//
//        val poolId = _currentPool.value.toString()
//
//        val userIds = ArrayList<String>()
//        repository.getPoolPlayersBasePath(userId, poolId).get().addOnSuccessListener {
//            for (document in it) {
//                //go over documents and grab the player ids
//                val playerId = document.get("playerId").toString()
//                userIds.add(playerId)
//
//            }
//
//            for (item in userIds) {
//
//                addPicksForEachPlayerFunction(item, data)
//
//            }
//        }
//
//    }

//    private fun addPicksForEachPlayerFunction(item: String, data: HashMap<String, Any>) {
//        repository.getUserPoolsBasePath(item).whereEqualTo("ownerId", _poolOwnerIdForAddingPicks.value).get()
//                .addOnSuccessListener {
//                    for (doc in it.documents) {
//                        val id = doc.id
//                        if (doc["ownerId"].toString() == _poolOwnerIdForAddingPicks.value) {
//                            repository.addPicksToPoolDocument(item, id, data)
//                        }
//                    }
//                }
//    }


//    fun deletePicksFromAllPools(poolId: String, pick: PickForDisplay, filter: String?, lastWeek: String?) {
//        repository.getUserPoolsBasePath(user.uid).document(poolId).get().addOnSuccessListener { poolsBasePath ->
//            val ownerId = poolsBasePath.get("ownerId").toString()
//            //now have the pool owner id to remove picks from all pools
//            val list = _currentPoolPlayers.value!!
//            for (item in list) {
//
//                Timber.i("----------Inside current pool players list, player is ${item.name} id is ${item.userId}")
//                val id = item.userId
//
//                repository.getUserPoolsBasePath(id).whereEqualTo("ownerId", ownerId).get().addOnSuccessListener { pools ->
//                    val documents = pools.documents
//                    for (doc in documents) {
//                        val documentId = doc.id
//                        val deleting = repository.getUserPoolsBasePath(id).document(documentId).collection("playerPicks")
//                                .whereEqualTo("picks", pick.picks)
//                                .whereEqualTo("week", pick.week)
//                                .whereEqualTo("finalPoints", pick.finalPoints)
//                                .whereEqualTo("playerId", pick.playerId)
//                                .get()
//
//
//                        deleting.addOnSuccessListener {
//
//                            Timber.i("------------On Success got player picks where equal to to delete doc size is ${it.documents.size}")
//                            for (document in it.documents) {
//                                val deleteId = document.id
//                                Timber.i("------------deleteId = $deleteId")
//                                repository.getUserPoolsBasePath(id).document(documentId).collection("playerPicks")
//                                        .document(deleteId).delete()
//                                Timber.i("------------just deleted")
//                            }
//                        }.addOnFailureListener {
//                            Timber.i("------------Failed to get to delete")
//                        }
//
//                        deleting.addOnCompleteListener {
//                            Timber.i("------------Deleting is on complete getting regular list")
//                            if (lastWeek != null) {
//                                getPoolPicks(poolId, lastWeek)
//                            }
//                            Timber.i("------------Before filter getting regular list")
//
//
//                        }
//                        deleting.addOnFailureListener {
//                            Timber.i("------------failed delete")
//                        }
//                    }
//                }
//            }
//
//        }
//
//
//    }


    fun callApiForLastCompletedWeek() {
        apiRepository.getApiService().getLastCompletedWeek(Constants.key).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result = response.body()
                _apiCallLastCompletedWeek.value = "Week $result"
                Timber.i("************** ---> last completed week == $result")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.i("*************  call failed for last current week")
            }
        })
    }

    fun callApiForCurrentWeek() {
        Timber.i("TESTINGAPI ---> callforcurrent week fired")
        apiRepository.getApiService().getCurrentWeek(Constants.key).enqueue(object :
                Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val currentWeek = response.body()
                _apiCallCurrentWeek.value = currentWeek
                Timber.i("TESTINGAPI  current week is $currentWeek")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.i("TESTINGAPI  current week call failed")
            }
        })
    }

    fun getScoresForWeek(week: Int) {
        apiRepository.getApiService().getScoresFromWeek("2020REG", week, Constants.key).enqueue(
            object :
                Callback<IOWeekScoresResponse> {
                override fun onResponse(
                    call: Call<IOWeekScoresResponse>,
                    response: Response<IOWeekScoresResponse>
                ) {
                    val result = response.body()

                    Timber.i("[[[[[[[[[[[[[[[in get scores, result = $result")
                    if (result != null) {

                        /**
                         * The first list, winningTeams would work well if the free api data wasn't scrambled.
                         * Because it is, the scores are irrelevant and for practical testing the final score
                         * will have to be added manually.
                         */
                        //empty lists to populate with winner items
                        val winningTeams = ArrayList<Triple<String, String, String>>()
                        val winningTeamsStringsOnly = ArrayList<String>()

                        for (item in result) {
                            val homeScore = item.homeScore
                            val awayScore = item.awayScore
                            val homeTeam = item.homeTeam
                            val awayTeam = item.awayTeam


                            if (homeScore != null && awayScore != null) {
                                val total = (homeScore.plus(awayScore)).toString()
                                //get the winner here by comparing scores
                                when {
                                    homeScore > awayScore -> {
                                        //home team won
                                        Timber.i("[[[[[[[[[[[[WINNING TEAM IS $homeTeam")
                                        if (homeTeam != null) {
                                            winningTeams.add(
                                                Triple(
                                                    homeTeam.toString(),
                                                    item.date.toString(),
                                                    total
                                                )
                                            )
                                            winningTeamsStringsOnly.add(homeTeam.toString())
                                        }
                                    }
                                    awayScore > homeScore -> {
                                        //away team won
                                        Timber.i("[[[[[[[[[[[WINNING TEAM IS $awayTeam")
                                        if (awayTeam != null) {
                                            winningTeams.add(
                                                Triple(
                                                    awayTeam.toString(),
                                                    item.date.toString(),
                                                    total
                                                )
                                            )
                                            winningTeamsStringsOnly.add(awayTeam.toString())
                                        }
                                    }
                                }
                            }

                        }

                        winningTeams.sortByDescending { triple: Triple<String, String, String> -> triple.second }

                        val finalScore = winningTeams[winningTeams.size - 1].third
                        //setting the live data variable after sorting
                        val returnPair = Pair(winningTeams, finalScore)

                        _retrievedWinningTeams.value = winningTeamsStringsOnly

                        _finalScoresFromWeek.value = returnPair

                        Timber.i("--------IN GET SCORES: finalscoreslivedata value is ${_finalScoresFromWeek.value}")
                        Timber.i("--------IN GET SCORES: winningTeamsStrings livedata value is ${_retrievedWinningTeams.value}")
                    }
                }

                override fun onFailure(call: Call<IOWeekScoresResponse>, t: Throwable) {
                    Timber.i("TESTINGAPI---> call failed for week scores")
                }
            })
    }
//
//    fun checkArePicksForWeek(week: String) : Boolean {
//        var returnBool = false
//
//        val picksRef = repository.getUserPoolsBasePath(user.uid).get()
//                picksRef.addOnSuccessListener {
//            val picks = it.documents
//            for (doc in picks){
//                val docPoolName = doc["poolName"].toString().trim()
//                val docPoolOwnerName = doc["ownerName"].toString().trim()
//
//                if(docPoolName == _currentPoolName.value && docPoolOwnerName == _currentPoolOwnerName.value) {
//
//                    val id = doc["documentId"].toString()
//                    repository.getUserPoolsBasePath(user.uid)
//                            .document(id).collection("playerPicks").get().addOnSuccessListener { playerPicks ->
//                                val poolPicks = playerPicks.documents
//                                if (poolPicks.size == 0) {
//
//                                    _arePicksForWeek.value = false
//                                    return@addOnSuccessListener
//                                }
//
//                                for (pick in poolPicks) {
//                                    val pickWeek = pick["week"].toString().trim()
//                                    if (pickWeek == week.trim()) {
//                                        _arePicksForWeek.value = true
//                                        returnBool = true
//
//                                        return@addOnSuccessListener
//                                    }
//                                }
//
//                            }
//                } else {
//                    Timber.i("[[[[[ no documents found for this pool")
//                    _arePicksForWeek.value = false
//                }
//            }
//        }.addOnCompleteListener {
//            Timber.i("[[[[[ the checkarepicks function is completed now")
//        }
//
//        Timber.i("[[[[[[   returnBool checking for picks is $returnBool")
//       return returnBool
//    }

    fun checkIfNeedWinner(week: String) {
        //   val weekInt = week.filter { it.isDigit() }.trim().toInt()
        val picksRef = repository.getUserBaseCollection(user.uid).collection("pools")
            .document(_currentPool.value!!).collection("winners").get()
        picksRef.addOnSuccessListener {
            var winners = 0
            if (it.documents.isEmpty()) {
//                val arePicksForWeek = checkArePicksForWeek(week)
                val needPicks = _arePicksForWeek.value
                _needWinners.value = true
                return@addOnSuccessListener
            }
            for(doc in it.documents){
                if(doc["week"].toString().trim() == week.trim()){
                    winners++
                }
            }
            _needWinners.value = winners <= 0
        }
    }

    fun decideWinner(weekFilter: String, poolId: String, finalScore: Int) {

        // getScoresForWeek(11)   //testing a variable here need to pass the Week 11 eleven part
        //  retrievePicksForScore(poolId)
        val retrievedPicks = ArrayList<HashMap<String, String>>()
        val picksRef = repository.getUserBaseCollection(user.uid).collection("pools")
                .document(poolId).collection("playerPicks").get()

        picksRef.addOnSuccessListener {

            val picks = it.documents
            for (pick in picks) {
                val pickString = pick.getString("picks")
                val points = pick.getString("finalPoints")
                val playerId = pick.getString("playerId")
                val week = pick.getString("week")
                val playerName = pick.getString("playerName")

                val retrievedPick = HashMap<String, String>()
                retrievedPick["picks"] = pickString!!
                retrievedPick["points"] = points!!
                retrievedPick["playerId"] = playerId!!
                retrievedPick["week"] = week!!
                retrievedPick["playerName"] = playerName!!

                if (retrievedPick["week"].toString() == weekFilter) {
                    retrievedPicks.add(retrievedPick)
                }
//                retrievedPicks.add(retrievedPick)
            }
            _picksForScore.value = retrievedPicks

        }
        picksRef.addOnCompleteListener {
            val leaguePicksList = _picksForScore.value
            val winningScoreToCheck = _finalScoresFromWeek.value
            val scrambledAlternateList = _retrievedWinningTeams.value

            if (_picksForScore.value != null) {
                val list = _picksForScore.value
                val finalList = ArrayList<HashMap<String, String>>()
                if (list != null) {
                    for (item in list) {
                        if (item["week"].toString().trim() == weekFilter.trim()) {
                            finalList.add(item)
                        }
                    }
                }

                val scores = _finalScoresFromWeek.value
                val finalPoints = finalScore
                val scoreString = winningScoreToCheck?.first

                val playerScoresList = ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>()


                if (leaguePicksList != null) {
                    for (item in leaguePicksList) {
                        val picksString = item["picks"].toString().trim()
                        val picksPoints = item["points"].toString().trim().toInt()
                        val picksStringEdited = picksString.removePrefix("[").removeSuffix("]")
                        val onlyChars = picksStringEdited.replace(",", "")

                        val teamArray = onlyChars.split(" ") as ArrayList<String>
                        //NOW THE TEAMS ARRAYS ARE EQUIVALENT, LOOP THROUGH THE WINNING TEAMS ARRAY AND COMPARE
                        //MATCHES WITH A COUNTER
                        var count = 0
                        for (item2 in teamArray) {
                            if (scrambledAlternateList!!.contains(item2)) {
                                count++
                            }
                        }
                        val numberCorrect = count
                        val name = item["playerName"].toString().trim()
                        val id = item["playerId"].toString().trim()
                        val scoreItem = Pair(Pair(name, id), Pair(numberCorrect, picksPoints))
                        playerScoresList.add(scoreItem)
                    }
                }
                _playerScoresCalculatedList.value = playerScoresList

            }
        }

    }

    private fun updateWinners(data: HashMap<String, Any>) {
        val ids = ArrayList<String>()
        val listToGetIdsFrom = _currentPoolPlayers.value!!
        for (player in listToGetIdsFrom) {
            val id = player.userId
            ids.add(id)
        }

        repository.addWinnerToPools(user.uid, _currentPool.value!!, data, _currentPoolName.value!!, ids)
    }

    fun getWinners(poolId: String) {
        repository.getUserPoolsBasePath(user.uid).document(poolId).collection("winners").get().addOnSuccessListener {
            val listOfWinners = ArrayList<WinnerItem>()
            val winners = it.documents
            for (winner in winners) {
                val winnerToAdd = WinnerItem(
                    winner.get("playerName").toString(),
                    winner.get("week").toString()
                )
                listOfWinners.add(winnerToAdd)

            }
            _winnersForRecycler.value = listOfWinners
        }
    }


    fun createParsePool(poolName: String) {
        viewModelScope.launch {
            val currentUser = ParseUser.getCurrentUser()
            val initialArrayList = ArrayList<String>()
            initialArrayList.add(currentUser.username)
            ParsePoolRepository().createPool(
                poolName,
                currentUser.username,
                currentUser.email,
                initialArrayList
            )
        }

    }

    fun deleteParsePool(poolId: String, poolName: String, isOwner: Boolean) {
        if (isOwner) {
            viewModelScope.launch {
                parsePoolRepository.deletePoolById(poolId)
                resetPoolsList()
            }
        } else
            viewModelScope.launch {
                parsePoolRepository.removePlayerFromPool(poolId, ParseUser.getCurrentUser())
                resetPoolsList()
            }
    }

    fun resetPoolsList() {
        _parsePools.value = null
    }

    fun resetParsePicksList() {
        _parseRetrievedPoolPicks.postValue(null)
    }

    fun searchParseUsersToInvite(input: String) {
        viewModelScope.launch {
            val listOfEmails = parseUserRepository.queryUsers(input)
            if (listOfEmails != null) {
                _parseUserEmailSearchStrings.postValue(listOfEmails)
            }
        }
    }

    suspend fun checkParseInvites() {
        viewModelScope.launch {
            val areInvites = parseInviteRepository.checkForInvites(ParseUser.getCurrentUser())
            Timber.i("********called check parse invites, it returned $areInvites")
            if (areInvites != null && areInvites == true) {
                val listOfInvites = parseInviteRepository.getInvites(ParseUser.getCurrentUser())
                Timber.i("******invites was true, got this value for them when grabbing $listOfInvites")
                _parseInvitesList.postValue(listOfInvites)
            }
        }
    }

    fun createParseInvite(
        receiverName: String,
        receiverId: String,
        senderName: String,
        senderId: String,
        poolId: String,
        poolName: String
    ) {
        viewModelScope.launch {
            val inviteId = parseInviteRepository.createInvite(
                receiverName,
                receiverId,
                senderName,
                senderId,
                poolId,
                poolName
            )

        }
    }

    fun clearInviteListForRecycler() {
        _parseUserEmailSearchStrings.postValue(null)
    }

    fun getParsePoolPlayers(poolId: String) {
        viewModelScope.launch {
            val players = parsePoolRepository.getPoolPlayers(poolId)
            _parsePoolPlayers.postValue(players)
        }

    }

    fun getJustCreatedId(): String? {
        Timber.i("**********getting just created id from view model, its value is ${_justCreatedParsePoolId.value}")
        return _justCreatedParsePoolId.value
    }

    fun resetPoolError() {
        _parsePoolError.value = null
    }

    suspend fun getParsePools(id: String) {
        viewModelScope.launch {
            //  parseUserRepository.addPoolToCurrentUser(id)
            val list = parsePoolRepository.getUserPools(ParseUser.getCurrentUser())
            _parsePools.postValue(list)

        }
    }

    fun getParsePoolsByQuery() {
        viewModelScope.launch {
            val poolsList = parseQueryRepository.getUsersPools(ParseUser.getCurrentUser().username)
            _parsePools.value = poolsList
        }

    }

    fun deleteParsePickFromPool(pick: ParsePick) {
        viewModelScope.launch {
            _currentParsePoolId.value?.let { parsePoolRepository.deletePoolPicks(pick, it) }
            getParsePoolPicks()
        }
    }

    fun setCurrentParsePool(id: String) {
        _currentParsePoolId.postValue(id)

    }

    fun getParsePoolById(id: String) {
        viewModelScope.launch {
            val pool = parsePoolRepository.getPoolById(id)
            _currentParsePoolData.postValue(pool)
        }


    }

    fun sendInviteToParsePool() {

    }

    fun acceptInviteFromParsePool(user: ParseUser, inviteId: String, isAccepted: Boolean) {
        viewModelScope.launch {
            Timber.i("*******calling accept pool from vm is accepted = $isAccepted")
            parseInviteRepository.acceptInvite(user, inviteId, isAccepted)
        }

    }

    fun getParsePoolPicks() {
        viewModelScope.launch {
            val listOfPoolPicks =
                _currentParsePoolId.value?.let { parsePoolRepository.getPoolPicks(it) }
            _parseRetrievedPoolPicks.postValue(null)
            _parseRetrievedPoolPicks.postValue(listOfPoolPicks)
        }

    }

    fun addPicksToParsePool(picks: Pick) {
        //need to add picks data to parse pool
        val id = parsePickRepository.createPick(picks, ParseUser.getCurrentUser())
        if (id != null) {
            parsePickRepository.addPicksToPool(id, _currentParsePoolData.value?.objectId)
        }
    }


}