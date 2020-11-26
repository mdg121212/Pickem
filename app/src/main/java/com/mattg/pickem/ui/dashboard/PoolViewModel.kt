package com.mattg.pickem.ui.dashboard

import android.app.Application
import androidx.lifecycle.*
import com.mattg.pickem.db.Pick
import com.mattg.pickem.db.repos.ApiCallRepository
import com.mattg.pickem.db.repos.FirestoreRepository
import com.mattg.pickem.db.repos.RoomRepo
import com.mattg.pickem.models.firebase.*
import com.mattg.pickem.models.iomodels.IOWeekScoresResponse
import com.mattg.pickem.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PoolViewModel(application: Application) : AndroidViewModel(application) {

    private val roomRepository = RoomRepo(application)
    private val apiRepository = ApiCallRepository(application)
    private val repository = FirestoreRepository()
    val user = repository.currentUser!!

    private val _finalScoresFromWeek = MutableLiveData<Pair<ArrayList<Triple<String, String, String>>, String>>()
    val finalScoresFromWeek : LiveData<Pair<ArrayList<Triple<String, String, String>>, String>> = _finalScoresFromWeek

    private val _apiCallCurrentWeek = MutableLiveData<String>()
    val apiCallCurrentWeek: LiveData<String> = _apiCallCurrentWeek

    private val _apiCallLastCompletedWeek = MutableLiveData<String>()
    val apiCallLastCompletedWeek: LiveData<String> = _apiCallLastCompletedWeek

    private val _picksFromDatabase = MutableLiveData<List<Pick>>()
    val picksFromDatabase : LiveData<List<Pick>> = _picksFromDatabase

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

    private val _currentPoolPlayersPicks = MutableLiveData<ArrayList<PickForDisplay>>()
    val currentPoolPlayersPicks: LiveData<ArrayList<PickForDisplay>> = _currentPoolPlayersPicks

    private val _currentPoolPlayersPicksFiltered = MutableLiveData<ArrayList<PickForDisplay>>()
    val currentPoolPlayersPicksFiltered: LiveData<ArrayList<PickForDisplay>> = _currentPoolPlayersPicksFiltered

    private val _currentPoolName = MutableLiveData<String>()
    val currentPoolName: LiveData<String> = _currentPoolName

    private val _currentPoolOwnerId = MutableLiveData<String>()
     val currentPoolOwnerId : LiveData<String> = _currentPoolOwnerId

    private val _currentPoolOwnerName = MutableLiveData<String>()
    val currentPoolOwnerName: LiveData<String> = _currentPoolOwnerName

    private val _currentPoolPlayers = MutableLiveData<ArrayList<User>>()
    val currentPoolPlayers : LiveData<ArrayList<User>> = _currentPoolPlayers

    private val _listForInviteRecycler = MutableLiveData<ArrayList<Invite>>()
    val listForInviteRecycler: LiveData<ArrayList<Invite>> = _listForInviteRecycler

    private val _userPoolsList = MutableLiveData<ArrayList<Pool>>()
    val userPoolsList: LiveData<ArrayList<Pool>> = _userPoolsList

    private val _currentSelectedPicks = MutableLiveData<Pick>()
    val currentSelectedPicks: LiveData<Pick> = _currentSelectedPicks

    private val _picksForScore = MutableLiveData<ArrayList<HashMap<String, String>>>()

    private val _weekSort = MutableLiveData<String>()

    private val _retrievedWinningTeams = MutableLiveData<ArrayList<String>>()
    val retrievedWinningTeams: LiveData<ArrayList<String>> = _retrievedWinningTeams

    private val _playerScoresCalculatedList = MutableLiveData<ArrayList<Pair<Pair<String,String>, Pair<Int, Int>>>>()
    val playerScoresCalculatedList: LiveData<ArrayList<Pair<Pair<String,String>, Pair<Int, Int>>>> = _playerScoresCalculatedList

    private val _winnersForRecycler = MutableLiveData<ArrayList<WinnerItem>>()
    val winnersForRecycler: LiveData<ArrayList<WinnerItem>> = _winnersForRecycler

    fun retrievePicksFromDatabase(){
        viewModelScope.launch {
            _picksFromDatabase.value = roomRepository.getListOfPicks()
        }
    }

    fun setPicks(picks: Pick){
        _currentSelectedPicks.value = picks
    }

    fun filterPicks(week: String){
        val list = _currentPoolPlayersPicks.value
        if (list != null) {
            for(item in list){
                if(item.week.toLowerCase() != "week $week")
                    list.remove(item)
            }
            _currentPoolPlayersPicks.value = list
        }
    }

    fun resetPoolPlayerPicksList(list: ArrayList<PickForDisplay>){
        _currentPoolPlayersPicks.value = list
    }

    private val _areInvites = MutableLiveData<Boolean>().apply {
        value = false
    }
    val areInvites: LiveData<Boolean> = _areInvites

    fun setCurrentPool(poolId: String, poolName: String, poolOwnerId: String, poolOwnerName: String) {
        _currentPool.postValue(poolId)
        _currentPoolName.value = poolName
        Timber.i("TESTING--->setting _currentPoolName to $poolName")
        _currentPoolOwnerId.value = poolOwnerId
        Timber.i("<<<<<<<<retrieving the owner name in the set function $poolOwnerName is being passed in to _currentpoolownername")
        _currentPoolOwnerName.value = poolOwnerName
    }

    fun setCurrentList(list: ArrayList<User>){
        _currentPoolPlayers.postValue(list)
    }


    fun resetSelectedPicks() {
        _currentSelectedPicks.postValue(null)
    }

    fun resetPicksList(){
        _currentSelectedPicks.value = null
    }


    private fun setAreInvites(input: Boolean) {
        _areInvites.value = input
    }

    fun deletePool(poolId: String, poolName: String) {
        if (repository.deletePool(poolId, poolName)) {
            Timber.i("pool was deleted need to update adapter")
            getUserPools()
        }
    }


    fun createPool(userId: String, name: String, currentWeek: String): Boolean {
        //change the fields of the user (recipient)
        repository.getUserBaseCollection(userId)
        val poolData = HashMap<String, Any>()
        poolData["ownerId"] = user.uid
        poolData["ownerName"] = user.displayName.toString()
        poolData["poolName"] = name
        poolData["currentWeek"] = currentWeek
        poolData["playerCount"] = 1

        val playerToAdd = HashMap<String, Any>()
        playerToAdd["playerName"] = user.displayName!!
        playerToAdd["playerId"] = user.uid
        playerToAdd["playerEmail"] = user.email!!

        //Will return true if pool was created, false if it failed
        val returnPair = repository.createPool(userId, poolData, playerToAdd)

        _poolIdHolder.postValue(returnPair.second)
        return returnPair.first

    }
    fun filteredGetPicks(poolId: String, filter: String){
        Timber.i("----------filter is passed as $filter")
        val listOfPicks = ArrayList<PickForDisplay>()
       val picksGotten =  repository.getUserBaseCollection(user.uid).collection("pools")
                .document(poolId).collection("playerPicks").whereEqualTo("week", " "+filter).get()
                .addOnSuccessListener {

                    Timber.i("--------------inSuccessListener")
                    val picksDocuments = it.documents
                    if (picksDocuments != null) {
                        Timber.i("--------------inSuccessListener documents are not null size is ${picksDocuments.size}")
                        for(doc in picksDocuments){
                            Timber.i("------------------ " + doc.get("week").toString())
                            val picktoCopy = PickForDisplay(
                                    doc.get("finalPoints").toString(),
                                    doc.get("picks").toString(),
                                    doc.get("playerId").toString(),
                                    doc.get("playerName").toString(),
                                    doc.get("week").toString().trim()
                            )
                            Timber.i("--------------values for pick -- ${picktoCopy.week} filter -- $filter")
                                if(picktoCopy.week == filter.trim()) {
                                    Timber.i("--------------filter matched adding pick ${picktoCopy.week} matched $filter")
                                    //the pick doesn't match the filter, dont add it to the list
                                    listOfPicks.add(picktoCopy)
                                }


                                Timber.i("------------------${listOfPicks.size}")
                        }
                     //   Timber.i("----------------- $listOfPicks finshed with list of picks, its size is ${listOfPicks.size}}")
                     //   _currentPoolPlayersPicks.value = listOfPicks

                    }

                }
            picksGotten.addOnCompleteListener {
    _currentPoolPlayersPicks.value = listOfPicks
    }
    }

    fun getPoolPicks(poolId: String)  {

        val listOfPicks = ArrayList<PickForDisplay>()
        repository.getUserBaseCollection(user.uid).collection("pools")
                .document(poolId).collection("playerPicks")
                .addSnapshotListener { value, _ ->
            val picksDocuments = value?.documents
            if (picksDocuments != null) {
                for(doc in picksDocuments){

                    val picktoCopy = PickForDisplay(
                            doc.get("finalPoints").toString(),
                            doc.get("picks").toString(),
                            doc.get("playerId").toString(),
                            doc.get("playerName").toString(),
                            doc.get("week").toString()
                    )


                       listOfPicks.add(picktoCopy)



                }
                Timber.i("======= ${listOfPicks }}")
                listOfPicks.sortBy { pickForDisplay -> pickForDisplay.week}
                _currentPoolPlayersPicks.value = listOfPicks


            }

        }

    }

    fun getUserPools() {
        val poolsRef = repository.getUserPoolsBasePath(user.uid)
        poolsRef.addSnapshotListener { snapshots, error ->
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
                    val ownerId = pool.get("ownerId").toString()
                    val ownerName = pool.get("ownerName").toString()
                    val poolName = pool.get("poolName").toString()

                    Timber.i("<<<<<owner name set to $ownerName")

                    val poolToAdd = Pool(
                            poolName,
                            ownerName,
                            ownerId,
                            id,

                            )


                    Timber.i("<<<<<<Pool owner name saved as ${poolToAdd.ownerName}")
                    list.add(poolToAdd)
                    count++
                }

                _userPoolsList.value = (list)
                return@addSnapshotListener
            }

        }
    }


    fun getPoolPlayers(poolId: String){
        val returnList = ArrayList<User>()
        val players= repository.getPoolPlayers(poolId)

        players.get().addOnSuccessListener { snapshot ->
            if(snapshot == null){
                Timber.i("TESTINGPOOLS snapshot was null")
                return@addOnSuccessListener
            }

            for(item in snapshot){

                val id = item.get("playerId").toString()
                val name = item.get("playerName").toString()
                val email = item.get("playerEmail").toString()

                val userToAdd = User(name, email, id)

                val idForPicks = item.id
                players.document(idForPicks).collection("playerPicks").get().addOnSuccessListener {
                    for(document in it.documents){
                        // data["week"] = picks.week
                        //        data["picks"] = picks.picksGamesOnly
                        //        data["finalPoints"] = picks.finalPoints
                        val points = document["finalPoints"].toString()
                        val week = document["week"].toString()
                        val retrievedPicks = document["picks"].toString()
                        val retrievedPicksObject = Pick(
                                week = week,
                                name = name,
                                "",
                                picksGamesOnly = retrievedPicks,
                                finalPoints = points
                        )

                        userToAdd.picks.add(retrievedPicksObject)
                    }
                }
                returnList.add(userToAdd)
                Timber.i("TESTING, list size while iteration = ${returnList.size}")

            }
            _currentPoolPlayers.value = (returnList)
            Timber.i("TESTING, list size after updating live data = ${returnList.size}")
            Timber.i("TESTING, live data after updating live data = ${_currentPoolPlayers.value}")

        }



    }

    fun declineInvitation(inviteId: String) {
        repository.deleteInvitation(inviteId)
        //after declining/deleting an invitation, check to see if any are left and if not reset the are invites field
        val checkedInvitations = repository.checkAreStillInvites()
        setAreInvites(checkedInvitations)
    }

    fun searchForUsers(input: String) {
        val returnList = ArrayList<User>()
        val result = repository.searchForUsers(input.toLowerCase(Locale.ROOT))

        result.addOnSuccessListener {
            for (item in it) {
                if (item.exists()) {
                    //create user object for recycler
                    val foundUser = User(
                            item["name"].toString(),
                            item["email"].toString(),
                            item["userId"].toString()
                    )

                    if(foundUser.email == user.email){
                        return@addOnSuccessListener
                    } else{
                        returnList.add(foundUser)
                    }
                    Timber.i("Generated User: ${foundUser.name}, ${foundUser.email}")

                }
            }
            _usersList.value = (returnList)
        }.addOnFailureListener { exception ->
            Timber.i("<<<<failed to get users to invite ${exception.message}")
        }

    }


    fun listenForInvitations(): Boolean {
        var booleanReturn = false
        repository.listenForInvitations().addSnapshotListener { _, error ->
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
        var areInvites = false
        val listForRecycler = ArrayList<Invite>()
        val userReference = repository.getUserBaseCollection(user.uid)
        repository.listenForInvitations()
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.i("Error updating")
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

    fun getSpecificPool(poolId: String){
        repository.getUserPoolsBasePath(user.uid).document(poolId).addSnapshotListener{ document, _ ->

            val name = document?.get("poolName")?.toString()
            val ownerName = document?.get("ownerName").toString()
            val ownerId = document?.get("ownerId").toString()
            _poolOwnerIdForAddingPicks.value = ownerId

            Timber.i("TESTINGPOOLS----> name $name, ownerName $ownerName, ownerId = $ownerId")
        }

    }

    fun sendInvitation(receivingId: String): Boolean {
        if (_currentPool.value == null) {
            return false
        } else {
            val poolId = _currentPool.value

            val currentUser = repository.currentUser
            val sentInvitationId = ""

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
                                inviteReceivedHash["userWhoSentInvite"] = currentUser.uid
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
        }
        return true
    }



    fun acceptInvitation(poolId: String, senderId: String, inviteId: String) {

        val senderReference = repository.getUserBaseCollection(senderId)
        //for players list
        val playersToInclude = ArrayList<HashMap<String, Any>>()

        senderReference.collection("pools").document(poolId).collection("players").whereEqualTo("playerId", senderId).get()
                .addOnSuccessListener { document ->

                    for(doc in document){
                        val playerToAdd = HashMap<String, Any>()
                        playerToAdd["playerEmail"] = doc.get("playerEmail").toString()
                        playerToAdd["playerId"] = doc.get("playerId").toString()
                        playerToAdd["playerName"] = doc.get("playerName").toString()
                        playersToInclude.add(playerToAdd)
                    }
                }


        senderReference.collection("pools").document(poolId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception == null) {
                        //if no exception occurs, generate a pool object from the document
                        if (snapshot!!.exists()) {
                            val poolToCopy = Pool(
                                snapshot.get("poolName").toString(),
                                snapshot.get("ownerName").toString(),
                                snapshot.get("ownerId").toString(),
                                snapshot.get("documentId").toString(),

                                )

                            Timber.i("<<<<<Pool to copy saving ownerName as ${snapshot.get("ownerName").toString()}")


                            //create a new player to add to to pool collection
                            val playerToAdd = HashMap<String, Any>()
                            playerToAdd["playerName"] = user.displayName!!
                            playerToAdd["playerId"] = user.uid
                            playerToAdd["playerEmail"] = user.email!!

                            playersToInclude.add(playerToAdd)

                            //add players to collection in both the new, and original pool
                            repository.createPoolsOnAccept(
                                poolId, senderId, poolToCopy, playersToInclude,

                                )
                            //delete the invitations
                            repository.deleteInvitation(inviteId)


                            return@addSnapshotListener
                        }

                    }
                }

        val checkedInvitations = repository.checkAreStillInvites()
        setAreInvites(checkedInvitations)
        getUserPools()
    }



    fun addPicksForUserInPool(picks: Pick, userId: String) {
        //picks data to add to documents
        val data = HashMap<String, Any>()
        data["week"] = picks.week
        data["picks"] = picks.picksGamesOnly
        data["finalPoints"] = picks.finalPoints
        data["playerId"] = user.uid
        data["playerName"] = user.displayName!!

        val poolId = _currentPool.value.toString()

        val userIds = ArrayList<String>()
        repository.getPoolPlayersBasePath(userId, poolId).get().addOnSuccessListener {
            for (document in it) {
                //go over documents and grab the player ids
                val playerId = document.get("playerId").toString()
                userIds.add(playerId)

            }

            for (item in userIds) {

                addPicksForEachPlayerFunction(item, data)

            }
        }

    }

    private fun addPicksForEachPlayerFunction(item: String , data: HashMap<String, Any>){
        repository.getUserPoolsBasePath(item).whereEqualTo("ownerId", _poolOwnerIdForAddingPicks.value).get()
                .addOnSuccessListener {
                    for (doc in it.documents) {
                        val id = doc.id
                        if(doc["ownerId"].toString() == _poolOwnerIdForAddingPicks.value) {
                            repository.addPicksToPoolDocument(item, id, data)
                        }
                    }
                }
    }



    fun deletePicksFromAllPools(poolId: String, pick: PickForDisplay, filter: String?){
        repository.getUserPoolsBasePath(user.uid).document(poolId).get().addOnSuccessListener {
            val ownerId = it.get("ownerId").toString()
            //now have the pool owner id to remove picks from all pools
            val list = _currentPoolPlayers.value!!
            for(item in list){
                Timber.i("----------Inside current pool players list, player is ${item.name} id is ${item.userId}")
                val id = item.userId
                repository.getUserPoolsBasePath(id).whereEqualTo("ownerId", ownerId).get().addOnSuccessListener { pools ->
                    val documents = pools.documents
                    for (doc in documents){
                        val documentId = doc.id
                       val deleting =  repository.getUserPoolsBasePath(id).document(documentId).collection("playerPicks")
                                .whereEqualTo("picks", pick.picks)
                                .whereEqualTo("week", pick.week)
                                .whereEqualTo("finalPoints", pick.finalPoints)
                                .whereEqualTo("playerId", user.uid)
                                .get().addOnSuccessListener {
                                    for(document in it.documents){
                                        val deleteId = document.id
                                        repository.getUserPoolsBasePath(id).document(documentId).collection("playerPicks")
                                                .document(deleteId).delete()
                                        Timber.i("------------just deleted")
                                    }
                                }

                        deleting.addOnCompleteListener {
                            getPoolPicks(poolId)
                            Timber.i("------------Before filter getting regular list")


                        }
                    }
                }
            }

        }


    }



    fun callApiForLastCompletedWeek(){
        apiRepository.getApiService().getLastCompletedWeek(Constants.key).enqueue(object :
            Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result = response.body()
                _apiCallLastCompletedWeek.value = result
                Timber.i("TESTINGAPI ---> last completed week == $result")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
               Timber.i("TESTINGAPI   call failed for last current week")
            }
        })
    }

    fun callApiForCurrentWeek(){
        Timber.i("TESTINGAPI ---> callforcurrent week fired")
        apiRepository.getApiService().getCurrentWeek(Constants.key).enqueue(object:
            Callback<String>{
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

    fun getScoresForWeek(week: Int){
        apiRepository.getApiService().getScoresFromWeek("2020REG", week, Constants.key).enqueue( object:
        Callback<IOWeekScoresResponse>{
            override fun onResponse(
                call: Call<IOWeekScoresResponse>,
                response: Response<IOWeekScoresResponse>
            ) {
                val result = response.body()

                Timber.i("------in get scores, result = $result")
                if (result != null) {
                    //empty lists to populate with winner items
                    /**
                     * The first list, winningTeams would work well if the free api data wasn't scrambled.
                     * Because it is, the scores are irrelevant and for practical testing the final score
                     * will have to be added manually, and the second list winningTeamsStringsOnly will
                     * be referenced for the winners along with the input number.
                     */
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
                                    Timber.i("WINNING TEAM IS $homeTeam")
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
                                    Timber.i("WINNING TEAM IS $awayTeam")
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

//                        winningTeams.sortByDescending { triple: Triple<String, String, String> -> triple.second }
//                        val finalScore = winningTeams[winningTeams.size - 1].third
//                        //setting the live data variable after sorting
//                        val returnPair = Pair(winningTeams, finalScore)
//                        _finalScoresFromWeek.value = returnPair
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

    fun decideWinner(weekFilter: String, poolId: String, finalScore: Int) {
        Timber.i("-------decide winner called")
       // getScoresForWeek(11)   //testing a variable here need to pass the Week 11 eleven part
      //  retrievePicksForScore(poolId)
        val retrievedPicks = ArrayList<HashMap<String, String>>()
        val picksRef = repository.getUserBaseCollection(user.uid).collection("pools")
                .document(poolId).collection("playerPicks").get()

        picksRef.addOnSuccessListener {
            Timber.i("-------In on success listner for retrieving picks to see who won, doc count is :  ${it.documents.size}")
            val picks = it.documents
            for(pick in picks){
                val pickString = pick.getString("picks")
                val points = pick.getString("finalPoints")
                val playerId = pick.getString("playerId")
                val week = pick.getString("week")

                val retrievedPick = HashMap<String, String>()
                retrievedPick["picks"] = pickString!!
                retrievedPick["points"] = points!!
                retrievedPick["playerId"] = playerId!!
                retrievedPick["week"] = week!!
                Timber.i("--------- retrieved pick is week:${retrievedPick["week"].toString()} points: ${retrievedPick["points"].toString()}  picks: ${retrievedPick["picks"].toString()} ")
                retrievedPicks.add(retrievedPick)
            }
            _picksForScore.value = retrievedPicks

            Timber.i("-----picksForScore value is : ${_picksForScore.value} size: ${_picksForScore.value!!.size}")
        }
        picksRef.addOnCompleteListener {
            val leaguePicksList = _picksForScore.value
            val winningScoreToCheck = _finalScoresFromWeek.value
            val scrambledAlternateList = _retrievedWinningTeams.value

            Timber.i("-----------in on complete listener picksForScore value is ${_picksForScore.value}")

            if(_picksForScore.value != null){
                val list = _picksForScore.value
                val finalList = ArrayList<HashMap<String, String>>()
                if (list != null) {
                    for(item in list){
                        if(item["week"].toString().trim() == weekFilter.trim()){
                            Timber.i("-----------made it to where a pick matches the week")
                            finalList.add(item)
                        }
                    }
                }

                Timber.i("----------Final list value/size is ${finalList.size}")
                val scores = _finalScoresFromWeek.value
                val finalPoints = finalScore
                val scoreString = winningScoreToCheck?.first


                Timber.i("------scoreString retrieved from backend is $scoreString")
                /**
                 * ADDING DUMMY DATA
                 */
                val newPick = PickForDisplay(
                        "35",
                        "[AZ, WAS, NO, PIT, HOU, PHI, CAR, BAL, NYJ, MIA, MIN, IND, KC, TB]",
                        "asldkfja;lskaaaadjf",
                        "pickOnePlayer",
                        "Week 11"
                )

                val newPick2 = PickForDisplay(
                        "55",
                        "[AZ, WAS, NO, PIT, HOU, PHI, CAR, BAL, NYJ, MIA, MIN, GB, KC, TB]",
                        "asldkfjaffsf;lskdjf",
                        "pickTwoPlayer",
                        "Week 11"
                )

                val newPick3 = PickForDisplay(
                        "45",
                        "[SEA, WAS, NO, PIT, HOU, PHI, CAR, BAL, LAC, MIA, MIN, IND, KC, TB]",
                        "asldkfja;lsksdfsddjf",
                        "pickThreePlayer",
                        "Week 11"
                )

                val newPick4 = PickForDisplay(
                        "34",
                        "[SEA, WAS, NO, PIT, HOU, PHI, CAR, TEN, NYJ, MIA, MIN, IND, KC, TB]",
                        "asldkfja;lskdjf",
                        "pickFourPlayer",
                        "Week 11"
                )

                val testList = arrayListOf(
                        newPick,
                        newPick2,
                        newPick3,
                        newPick4
                )

                /**
                 * To get real value replace testlist with the real peoples list of picks
                 */
                val playerScoresList = ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>()
                Timber.i("----final score is $finalPoints, and the winning string = $scoreString")


                for(item in testList){
                    val playerId = item.playerId
                    val picksString = item.picks
                    val picksPoints = item.finalPoints.toInt()
                    val picksStringEdited = picksString.removePrefix("[").removeSuffix("]")
                    val onlyChars = picksStringEdited.replace(",", "")
                    Timber.i("-----only chars  = $onlyChars")
                    val teamArray = onlyChars.split(" ") as ArrayList<String>
                    Timber.i("-------team array is $teamArray")

                    //NOW THE TEAMS ARRAYS ARE EQUIVALENT, LOOP THROUGH THE WINNING TEAMS ARRAY AND COMPARE
                    //MATCHES WITH A COUNTER
                    var count = 0
                   for(item2 in teamArray){
                       if(scrambledAlternateList!!.contains(item2)){
                           count++
                       }
                   }

                    Timber.i("+++++++count should be number correct: ${item.playerId} 's count is $count")

                    val numberCorrect = count
                    val name = item.playerName
                    val id = item.playerId
                    val scoreItem = Pair(Pair(name,id), Pair(numberCorrect, picksPoints))
                    playerScoresList.add(scoreItem)
                }
                _playerScoresCalculatedList.value = playerScoresList

            }
        }

    }

    fun updateWinners(data: HashMap<String, Any>) {
        val ids = ArrayList<String>()
        val listToGetIdsFrom = _currentPoolPlayers.value!!
        for(player in listToGetIdsFrom) {
            val id = player.userId
            ids.add(id)
        }

        repository.addWinnerToPools(user.uid, _currentPool.value!!, data, _currentPoolName.value!!, ids)
    }

    fun getWinners(poolId: String) {
        repository.getUserPoolsBasePath(user.uid).document(poolId).collection("winners").get().addOnSuccessListener {
            val listOfWinners = ArrayList<WinnerItem>()
            val winners = it.documents
            for(winner in winners){

                val winnerToAdd = WinnerItem(
                        winner.get("playerName").toString(),
                        winner.get("week").toString()
                )
                listOfWinners.add(winnerToAdd)

            }
            _winnersForRecycler.value = listOfWinners

        }
    }




}