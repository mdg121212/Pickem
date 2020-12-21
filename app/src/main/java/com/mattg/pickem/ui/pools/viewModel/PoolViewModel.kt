package com.mattg.pickem.ui.pools.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mattg.pickem.db.Pick
import com.mattg.pickem.db.repos.ApiCallRepository
import com.mattg.pickem.models.iomodels.IOWeekScoresResponse
import com.mattg.pickem.network.APICallService
import com.mattg.pickem.parsebackend.*
import com.mattg.pickem.parsebackend.models.*
import com.mattg.pickem.utils.Constants
import com.mattg.pickem.utils.getWinnerByFinalPoints
import com.mattg.pickem.utils.getWinnerByFinalPointsMoreThanTwo
import com.parse.ParseUser
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.collections.set

@SuppressWarnings("unused")
class PoolViewModel(application: Application) : AndroidViewModel(application) {

    private val apiRepository = ApiCallRepository(application)
    private val parsePoolRepository = ParsePoolRepository()
    private val parseUserRepository = ParseUserRepository()
    private val parsePickRepository = ParsePickRepository()
    private val parseQueryRepository = ParseQueryRepository()
    private val parseInviteRepository = ParseInviteRepository()


    private val _parsePoolError = MutableLiveData<String>()
    val parsePoolError: LiveData<String> = _parsePoolError

    private val _parsePoolWinnersList = MutableLiveData<ArrayList<WinnerItem>>()
    val parsePoolWinnersList: LiveData<ArrayList<WinnerItem>> = _parsePoolWinnersList

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

//    private val _usersList = MutableLiveData<ArrayList<User>>()
//    val usersList: LiveData<ArrayList<User>> = _usersList

    private val _isInviteListShowing = MutableLiveData<Boolean>()
    val isInviteListShowing: LiveData<Boolean> = _isInviteListShowing

    private val _poolOwnerIdForAddingPicks = MutableLiveData<String>()
//
//    private val _listOfInvitationsFromRepo = MutableLiveData<ArrayList<Invite>>()
//    val listOfInvitationsFromRepo: LiveData<ArrayList<Invite>> = _listOfInvitationsFromRepo

    private val _poolIdHolder = MutableLiveData<String>()
    val poolIdHolder: LiveData<String> = _poolIdHolder

    private val _currentPool = MutableLiveData<String>()
    val currentPool: LiveData<String> = _currentPool

    private val _areInvites = MutableLiveData<Boolean>().apply {
        value = false
    }


    private val _currentPoolName = MutableLiveData<String>()
    val currentPoolName: LiveData<String> = _currentPoolName

    private val _currentPoolOwnerId = MutableLiveData<String>()
    val currentPoolOwnerId: LiveData<String> = _currentPoolOwnerId

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

    fun setDateToCheck(input: String) {
        _dateToCheck.value = input
    }

    fun setPicks(picks: Pick) {
        _currentSelectedPicks.value = picks
    }

    fun resetWinnerName() {
        _winnerName.value = null
    }



    fun setNeedWinners(input: Boolean) {
        _needWinners.value = input
    }

    private suspend fun displayWinner(
        inputList: ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>,
        lastWeek: String,
        finalScore: Int
    ) {
        val list = inputList.sortedByDescending { it.second.first }
        Timber.i("+++++++++list of picks sorted is $list")//this sorts by the number correct
        val highScore = list.first().second.first
        Timber.i("++++++++highScore is $highScore")//this gets the number correct
        val highScoreTiesList = ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>()
        val data = HashMap<String, String>()
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
                    val dataForWinners = HashMap<String, String>()
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


    private suspend fun updateWinners(data: HashMap<String, String>) {
        parsePoolRepository.addWinnerToPool(_currentParsePoolId.value.toString(), data)
    }

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

    suspend fun checkIfNeedWinner(week: String) {
        Timber.i("**********checkIfNeedWinnerCalled for week: $week with pool value ${_currentParsePoolId.value!!}")
        val winnersList = parsePoolRepository.getWinners(_currentParsePoolId.value!!)
        var winners = 0
        Timber.i("*********just got winners list it is: $winnersList")
        if (winnersList?.isNotEmpty() == true) {
            for (item in winnersList) {
                if (item.second == week) {
                    //the second is the week string, if it equals input week then winner exists in pool for this week
                    // so increment counter
                    winners++
                }
            }
            Timber.i("*************about to set boolean based on $winners being 0 or more ")
            _needWinners.postValue(winners <= 0)
        } else
            Timber.i("*************need winners boolean about to be set to true since no winners matching $week were found ")
        _needWinners.postValue(true)
    }

    suspend fun decideWinner(weekFilter: String, poolId: String, finalScore: Int) {
        Timber.i("*****************calling decide winner with week filter $weekFilter")
        val retrievedPicks = ArrayList<HashMap<String, String>>()

        val parsePicks = parsePoolRepository.getPoolPicks(poolId)
        Timber.i("*****************decide winner got the parsePicks, its value is $parsePicks")
        if (parsePicks != null) {
            for (pick in parsePicks) {
                Timber.i("***********in for loop of parse picks, pick = $pick")
                val points = pick.finalPoints
                val pickString = pick.picks
                val week = pick.week
                val player = pick.ownerName

                val retrievedPick = HashMap<String, String>()
                retrievedPick["picks"] = pickString!!
                retrievedPick["points"] = points!!
                retrievedPick["week"] = week!!
                retrievedPick["playerName"] = player!!
                if (week == weekFilter) {
                    retrievedPicks.add(retrievedPick)
                }

            }
            Timber.i("*****************decide winner posting filtered list $retrievedPicks to picksforscorevalue")
            _picksForScore.value = retrievedPicks
        }
        val leaguePicksList = _picksForScore.value
        val winningScoreToCheck = _finalScoresFromWeek.value
        val scrambledAlternateList = _retrievedWinningTeams.value

        val finalList = ArrayList<HashMap<String, String>>()
        if (retrievedPicks.isNotEmpty()) {
            for (item in retrievedPicks) {
                //matching the week filter input with any picks in the list,
                // then final list will only include appropriate picks to check
                if (item["week"].toString().trim() == weekFilter.trim()) {
                    finalList.add(item)
                }
            }
        }

        val playerScoresList = ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>()

        Timber.i("************checking leaugePicksList: $leaguePicksList should not be null")
        if (leaguePicksList != null) {
            Timber.i("************in a loop for $leaguePicksList should not be null")
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
        displayWinner(playerScoresList, weekFilter, finalScore)
                _playerScoresCalculatedList.value = playerScoresList

    }


    fun getWinners(poolId: String) {
        Timber.i("********getWInners called")
        viewModelScope.launch {
            val winnersList = parsePoolRepository.getWinners(poolId)
            Timber.i("********winners list is equal to $winnersList")


            if (winnersList != null) {
                val listToPost = ArrayList<WinnerItem>()
                for (item in winnersList) {
                    val itemToAdd = WinnerItem(item.first, item.second)
                    listToPost.add(itemToAdd)
                }
                _parsePoolWinnersList.postValue(listToPost)
            }
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

    fun getParsePools() {
        viewModelScope.launch {
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
        val id = parsePickRepository.createPick(picks, ParseUser.getCurrentUser())
        if (id != null) {
            parsePickRepository.addPicksToPool(id, _currentParsePoolData.value?.objectId)
        }
    }


}