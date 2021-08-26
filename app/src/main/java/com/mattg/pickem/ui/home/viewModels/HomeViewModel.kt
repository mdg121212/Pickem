package com.mattg.pickem.ui.home.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mattg.pickem.db.Pick
import com.mattg.pickem.db.WeekMatchUp
import com.mattg.pickem.db.repos.ApiCallRepository
import com.mattg.pickem.db.repos.RoomRepo
import com.mattg.pickem.models.general.Game
import com.mattg.pickem.models.general.Week
import com.mattg.pickem.models.iomodels.IOScheduleReponse
import com.mattg.pickem.models.iomodels.IOScoresResponse
import com.mattg.pickem.utils.Constants
import com.mattg.pickem.utils.DatabaseConverters
import com.mattg.pickem.utils.ViewUtils.Companion.getImageFromTeam
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApiCallRepository(application)
    private val roomRepository = RoomRepo(application)

    //TODO replace this harcoded list with data fetched from backend
    private val weeksArray = arrayListOf(
            Week(1, Date(2020, 9, 10, 12, 0)),
            Week(2, Date(2020, 9, 17, 12, 0)),
            Week(3, Date(2020, 9, 24, 12, 0)),
            Week(4, Date(2020, 10, 1, 12, 0)),
            Week(5, Date(2020, 10, 8, 12, 0)),
            Week(6, Date(2020, 10, 18, 12, 0)),
            Week(7, Date(2020, 10, 22, 12, 0)),
            Week(8, Date(2020, 10, 29, 12, 0)),
            Week(9, Date(2020, 11, 5, 12, 0)),
            Week(10, Date(2020, 11, 12, 12, 0)),
            Week(11, Date(2020, 11, 19, 12, 0)),
            Week(12, Date(2020, 11, 26, 12, 0)),
            Week(13, Date(2020, 12, 3, 12, 0)),
            Week(14, Date(2020, 12, 10, 12, 0)),
            Week(15, Date(2020, 12, 17, 12, 0)),
            Week(16, Date(2020, 12, 25, 12, 0)),
            Week(17, Date(2021, 1, 3, 12, 0)),
    )
    private val _text = MutableLiveData<String>().apply {
        value = "Weekly Pick All"
    }
    val text: LiveData<String> = _text

    private val _gameCount = MutableLiveData<Int>()
    val gameCount: MutableLiveData<Int> = _gameCount

    private val _showSpinner = MutableLiveData<Boolean>()
    val showSpinner: LiveData<Boolean> = _showSpinner


    private val _currentUserNameId = MutableLiveData<HashMap<String, String>>()
    val currentUserNameId: LiveData<HashMap<String, String>> = _currentUserNameId

    private val _currentDate = MutableLiveData<Date>()
    private val _currentYear = MutableLiveData<Int>()

    private val _apiCallErrorMessage = MutableLiveData<String>()
    val apiCallErrorMessage: LiveData<String> = _apiCallErrorMessage

    private val _scheduleResult = MutableLiveData<IOScheduleReponse>()
    val scheduleResult: LiveData<IOScheduleReponse> = _scheduleResult

    private val _listOfGamesByWeek = MutableLiveData<ArrayList<IOScoresResponse.IOScoresResponseItem>>()
    val listOfGamesByWeek: LiveData<ArrayList<IOScoresResponse.IOScoresResponseItem>> = _listOfGamesByWeek

    private val _upcomingWeek = MutableLiveData<Int>()
    val upcomingWeek: LiveData<Int> = _upcomingWeek

    private val _teamsAndImages = MutableLiveData<ArrayList<Game>>()
    val teamsAndImages: LiveData<ArrayList<Game>> = _teamsAndImages

    private val _picksString = MutableLiveData<String>()
    val picksString: LiveData<String> = _picksString

    private val _picksFromDatabase = MutableLiveData<List<Pick>>()
    val picksFromDatabase: LiveData<List<Pick>> = _picksFromDatabase

    private val _dateToCheckWinner = MutableLiveData<String>()
    val dateToCheckWinner: LiveData<String> = _dateToCheckWinner

    private val _showExperts = MutableLiveData<Boolean>()
    val showExperts: LiveData<Boolean> = _showExperts

    fun setShouldShowExperts(shouldShow: Boolean) {
        Timber.d(">>>>>>>>>>setting should show to value $shouldShow")
        _showExperts.value = shouldShow
    }

    fun setShowSpinner(input: Boolean) {
        _showSpinner.value = input
    }

    fun setPicksString(input: String) {
        _picksString.value = input
    }

    fun savePickToDatabase(pick: Pick) {
        viewModelScope.launch {
            roomRepository.savePicksToDatabase(pick)
        }
    }

    fun resetHomeScreen() {
        clearMatchups()
        _listOfGamesByWeek.value = null
    }

    fun clearMatchups() {
        _teamsAndImages.value = null
    }

    /**
     * Takes the week and year, checks if this information exists locally, and if it does gets matchups data
     * from the local db, otherwise makes a rest api call
     * @param year Int season year
     * @param week Int season week
     */
    private suspend fun getWeekData(year: Int, week: Int) {
        when (checkIfDatabaseNeedsUpdating()) {
            true -> {
                Timber.d(">>>>>>>calling api for matchups with $year $week")
                callApiForMatchups(year, week, false)
            }
            false -> {
                viewModelScope.launch {
                    getMatchupsFromDatabase()
                }
            }
        }
    }

    fun getMatchupsFiltered(week: Int) {
        callApiForMatchups(_currentYear.value!!, week, true)

    }

    fun retrievePicksFromDatabase() {
        viewModelScope.launch {
            _picksFromDatabase.value = roomRepository.getListOfPicks()
        }
    }

    fun retrievePicksFromDatabaseForSubmit(week: String) {
        viewModelScope.launch {
            _picksFromDatabase.value = roomRepository.getPicksForSubmit(week.trim())
        }
    }

    /**
     * Retrieves the current information from the backend, mostly to avoid hardcoding and to allow testing
     * by changing these to past dates/weeks (possibly add history/historical feature to compare previous outcomes)
     */
    private fun getWeekYearFromBackend() {
        //  repository.nflApi.getTimeData();
    }

    /**
     * Get the matchups to display, and set the games list value with filtered results.
     * Then triggers method to ready that value for display
     * @param year Int: Season year
     * @param week Int: Week of the NFL Season
     * @param wasFiltered Boolean: has this list been filtered already (from database or api)
     */
    private fun callApiForMatchups(year: Int, week: Int, wasFiltered: Boolean) {
        repository.getApiService().getScoresByWeek(year, week, Constants.key).enqueue(object :
            Callback<IOScoresResponse> {
            override fun onResponse(
                call: Call<IOScoresResponse>,
                response: Response<IOScoresResponse>
            ) {
                _listOfGamesByWeek.value = filterPicks(response, week, wasFiltered)
                setUpPickSheet()
            }

            override fun onFailure(call: Call<IOScoresResponse>, t: Throwable) {
                Timber.i("Api call failed")
                setShowSpinner(false)
                _apiCallErrorMessage.postValue("Api Call Failed for reason: ${t.localizedMessage}")
            }
        })
    }

    private fun filterPicks(
        response: Response<IOScoresResponse>,
        week: Int,
        wasFiltered: Boolean
    ): ArrayList<IOScoresResponse.IOScoresResponseItem> {
        val listToReturn = ArrayList<IOScoresResponse.IOScoresResponseItem>()
        val responseData = response.body()
        Timber.i("$responseData")
        if (responseData != null) {
            for (item in responseData) {
                if (item.week == week && item.awayTeam != "BYE" && item.homeTeam != "BYE" && item.canceled != true) {
                    listToReturn.add(item)
                }
            }
        }
        if (wasFiltered) {
            setWeek(week)
        }
        return listToReturn;
    }


    @SuppressLint("LogNotTimber")
    fun setUpPickSheet() {
        val returnList = ArrayList<Game>()
        val resultWeek = _listOfGamesByWeek.value
        val weekToSave: Int?
        if (resultWeek?.size!! > 0) {
            if (resultWeek != null) {
                var count = 0
                weekToSave = resultWeek[0].week


                for (item in resultWeek) {
                    count++
                    //create detail string
                    Timber.i("TESTING============day = ${item.day}")
                    //var detailString = "${item.dateTime}"
                    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                    val formatter = SimpleDateFormat("EE, MMM yyyy", Locale.US)
                    val formattedDate = formatter.format(parser.parse(item.dateTime))
                    //create a game object
                    val newGame = Game(
                            item.homeTeam!!,
                            item.awayTeam!!,
                            getImageFromTeam(item.homeTeam!!),
                            getImageFromTeam(item.awayTeam!!),
                            count,
                            formattedDate,
                            item.dateTime!!
                    )
                    Log.i("TEST", "${newGame.homeTeam}, ${newGame.awayTeam}, GAME ${newGame.game}")
                    //add to list for return value
                    returnList.add(newGame)
                    returnList.sortBy { it.dateTime }
                    //set live data value
                }
                _teamsAndImages.postValue(returnList)
                _dateToCheckWinner.value = returnList.last().dateTime
                setGameCount(count)


                val forDatabase = DatabaseConverters.fromValuesToString(returnList)
                if (forDatabase != null) {
                    Timber.i("TESTING==================for database not null saving after this")
                    if (weekToSave != null) {
                        saveMatchupsToDatabase(forDatabase, weekToSave)
                    }
                }
                setShowSpinner(false)
                Timber.i(" TEAMS AND IMAGES VALUE = ${_teamsAndImages.value}")
            }
        } else {
            Timber.i("couldn't get picks")
            _apiCallErrorMessage.postValue("There was a network issue, try again later.")
        }
    }

    private suspend fun getMatchupsFromDatabase() {
        val returnedString = roomRepository.getMatchupsString()
        val returnList = returnedString.let { it.first?.let { retrievedString -> DatabaseConverters.toOptionValuesList(retrievedString) } }
        val returnedDataWeek = returnedString.let { it.second?.let { retrievedWeek -> checkIfNeedToUpdate(retrievedWeek) } }
        if (returnedDataWeek == true) {
            Timber.i("WEEK NEEDS UPDATATING, CALLING API")
            getWeekData(_currentYear.value!!, _upcomingWeek.value!!)
        } else
            Timber.i("WEEK NEEDS NO UPDATATING, USING DATABASE")
            Timber.i("MATCHUPSTRING======at right before adding to live data : $returnList")
            setGameCount(returnList?.size!!)
            _teamsAndImages.postValue(returnList)
            _dateToCheckWinner.value = returnList.last().dateTime
            setShowSpinner(false)
    }

    private fun checkIfNeedToUpdate(retrievedWeek: Int): Boolean {
        return retrievedWeek < _upcomingWeek.value!!
    }

    private suspend fun checkIfDatabaseNeedsUpdating(): Boolean {
        val returnBoolean: Boolean
        val result = roomRepository.areDatabaseMatchupsEmpty()
        //true if the return is empty
        return analyzeRoomData(result)

    }

    private fun analyzeRoomData(
            result: Pair<Boolean, ArrayList<WeekMatchUp>>): Boolean {
        val returnList: ArrayList<WeekMatchUp>
        var returnBoolean = false
        when (result.first) {

            false -> {
                returnList = result.second
                //check returned list and if its old/outdated create a new one

                val weekFromDatabase = returnList[0].week

                when {
                    weekFromDatabase < _upcomingWeek.value!! -> {
                        //the database entry is older than the current upcoming week
                        returnBoolean = true
                    }
                    weekFromDatabase == upcomingWeek.value!! -> {
                        //the database entry is for the current upcoming week
                        returnBoolean = false
                    }
                    else -> Timber.i("TESTING DATABASE ==== error")
                }
            }
            true -> {
                returnBoolean = true
            }
        }
        return returnBoolean
    }

    fun deletePicksFromDatabase(id: Int) {
        viewModelScope.launch {
            roomRepository.deletePicks(id)
        }
    }

    /**
     * Inserts the matchups string and week, casts to a data class and then saves to the database
     * asynchronously
     * @param matchupsString
     * @param week
     */
    private fun saveMatchupsToDatabase(matchupsString: String, week: Int) {
        val currentTimestamp = System.currentTimeMillis().toString()
        val matchupToSave = WeekMatchUp(
            currentTimestamp,
            matchupsString,
            week
        )
        viewModelScope.launch {
            roomRepository.saveMatchupsToDatabase(matchupToSave)
        }

    }


    fun setDate(date: Date, year: Int) {
        _currentDate.value = date
        _currentYear.value = year
    }

    /**
     * Takes the parameter date and compares it to the weeks of the current season, to determine which week is upcoming
     * @param date
     * @return Pair<String, String> of the current week, last week
     */
    fun getWeekToPick(date: Date): Pair<String, String> {
        when {
            date.before(weeksArray[0].startDate) -> {
                val weekString = setWeek(1)
                return Pair(weekString, "null")
            }
            date.before(weeksArray[1].startDate) && date.after(weeksArray[0].startDate) || date == weeksArray[1].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(1)
                val weekString = setWeek(2)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[2].startDate) && date.after(weeksArray[1].startDate) || date == (weeksArray[2].startDate) && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(2)
                val weekString = setWeek(3)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[3].startDate) && date.after(weeksArray[2].startDate) || date == (weeksArray[3].startDate) && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(3)
                val weekString = setWeek(4)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[4].startDate) && date.after(weeksArray[3].startDate) || date == weeksArray[4].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(4)
                val weekString = setWeek(5)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[5].startDate) && date.after(weeksArray[4].startDate) || date == weeksArray[5].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(5)
                val weekString = setWeek(6)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[6].startDate) && date.after(weeksArray[5].startDate) || date == weeksArray[6].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(6)
                val weekString = setWeek(7)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[7].startDate) && date.after(weeksArray[6].startDate) || date == weeksArray[7].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(7)
                val weekString = setWeek(8)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[8].startDate) && date.after(weeksArray[7].startDate) || date == weeksArray[8].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(8)
                val weekString = setWeek(9)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[9].startDate) && date.after(weeksArray[8].startDate) || date == weeksArray[9].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(9)
                val weekString = setWeek(10)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[10].startDate) && date.after(weeksArray[9].startDate) || date == weeksArray[10].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(10)
                val weekString = setUpcomingWeek(11)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[11].startDate) && date.after(weeksArray[10].startDate) || date == weeksArray[11].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(11)
                val weekString = setUpcomingWeek(12)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[12].startDate) && date.after(weeksArray[11].startDate) || date == weeksArray[12].startDate && date.time <= weeksArray[1].startDate.time -> {
                val lastWeekString = setWeek(12)
                val weekString = setUpcomingWeek(13)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[13].startDate) && date.after(weeksArray[12].startDate) || date == weeksArray[13].startDate && date.time <= weeksArray[1].startDate.time -> {
                val weekString = setUpcomingWeek(14)
                val lastWeekString = setWeek(13)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[14].startDate) && date.after(weeksArray[13].startDate) || date == weeksArray[14].startDate && date.time <= weeksArray[1].startDate.time -> {
                val weekString = setUpcomingWeek(15)
                val lastWeekString = setWeek(14)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[15].startDate) && date.after(weeksArray[14].startDate) || date == weeksArray[15].startDate && date.time <= weeksArray[1].startDate.time -> {
                val weekString = setUpcomingWeek(16)
                val lastWeekString = setWeek(15)
                return Pair(weekString, lastWeekString)
            }
            date.before(weeksArray[16].startDate) && date.after(weeksArray[15].startDate) || date == weeksArray[16].startDate && date.time <= weeksArray[1].startDate.time -> {
                val weekString = setUpcomingWeek(17)
                val lastWeekString = setWeek(16)
                return Pair(weekString, lastWeekString)
            }
            else -> return Pair("", "")

        }
    }

    private fun setWeek(input: Int): String {
        return "Week $input"
    }

    /**
     * Sets a live data value, and a text value (formatted) with the input number
     * @param input Int: value of the week being set
     * @return String: the formatted string value
     */
    fun setUpcomingWeek(input: Int): String {
        _upcomingWeek.value = input
        val titleText = "Week $input"
        _text.value = titleText
        return titleText
    }

    private fun setGameCount(games: Int) {
        _gameCount.value = games
    }

    /**
     * Provide a week to return all game stats for all teams, and use this data for display
     * and analysis of a given match up.
     */
    fun getAllTeamsStatsPreviousWeek() {

    }


}