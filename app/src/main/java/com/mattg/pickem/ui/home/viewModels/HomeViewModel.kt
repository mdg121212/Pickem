package com.mattg.pickem.ui.home.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mattg.pickem.R
import com.mattg.pickem.db.ApiResponseCached
import com.mattg.pickem.db.Pick
import com.mattg.pickem.db.repos.ApiCallRepository
import com.mattg.pickem.db.repos.RoomRepo
import com.mattg.pickem.models.Game
import com.mattg.pickem.models.Week
import com.mattg.pickem.models.iomodels.IOScheduleReponse
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApiCallRepository()
    private val roomRepository = RoomRepo(application)

    private val weeksArray = arrayListOf(
        Week(1, Date(2020, 9, 10)),
        Week(2, Date(2020, 9, 17)),
        Week(3, Date(2020, 9, 24)),
        Week(4, Date(2020, 10, 1)),
        Week(5, Date(2020, 10, 8)),
        Week(6, Date(2020, 10, 18)),
        Week(7, Date(2020, 10, 22)),
        Week(8, Date(2020, 10, 29)),
        Week(9, Date(2020, 11, 5)),
        Week(10, Date(2020, 11, 12)),
        Week(11, Date(2020, 11, 19)),
        Week(12, Date(2020, 11, 26)),
        Week(13, Date(2020, 12, 3)),
        Week(14, Date(2020, 12, 10)),
        Week(15, Date(2020, 12, 17)),
        Week(16, Date(2020, 12, 25)),
        Week(17, Date(2021, 1, 3)),
    )
    private val _text = MutableLiveData<String>().apply {
        value = "Weekly Pick All"
    }
    val text: LiveData<String> = _text

    private val _gameCount = MutableLiveData<Int>()
    val gameCount : MutableLiveData<Int> = _gameCount

    private val _currentUserNameId = MutableLiveData<HashMap<String, String>>()
    val currentUserNameId: LiveData<HashMap<String, String>> = _currentUserNameId

    private val _currentDate = MutableLiveData<Date>()

    private val _scheduleResult = MutableLiveData<IOScheduleReponse>()
    val scheduleResult: LiveData<IOScheduleReponse> = _scheduleResult

    private val _listOfGames = MutableLiveData<ArrayList<IOScheduleReponse.IOreponseItem>>()
    val listOfGames: LiveData<ArrayList<IOScheduleReponse.IOreponseItem>> = _listOfGames

    private val _upcomingWeek = MutableLiveData<Int>()
    val upcomingWeek: LiveData<Int> = _upcomingWeek

    private val _teamsAndImages = MutableLiveData<ArrayList<Game>>()
    val teamsAndImages: LiveData<ArrayList<Game>> = _teamsAndImages

    private val _picksString = MutableLiveData<String>()
    val picksString: LiveData<String> = _picksString

    fun setPicksString(input: String) {
        _picksString.value = input
    }

    fun savePickToDatabase(pick: Pick){
        viewModelScope.launch {
            roomRepository.savePicksToDatabase(pick)
            roomRepository.getListOfPicks()
        }
    }

    fun setUserName(name: String, id: String){
        val map = HashMap<String, String>()
        map[name] = id

        _currentUserNameId.value = map
    }

    fun cacheResponseToDatabase(date: String, response: String){
        val objectToInsert = ApiResponseCached(date, response)
        viewModelScope.launch {
            roomRepository.saveScheduleForYearResponseToRoom(objectToInsert)
        }
    }

    private val _shouldShowFirstButton = MutableLiveData<Boolean>().apply {
        true
    }
    val shouldShowFirstButton: LiveData<Boolean> = _shouldShowFirstButton

    fun setShouldShowFirstButton(bool: Boolean) {
        _shouldShowFirstButton.postValue(bool)
    }

    fun setDate(date: Date){
        _currentDate.value = date
    }

    fun callForSchedule(year: Int){

        val dateToCheckForCache = _currentDate.value

        _scheduleResult.value = repository.getSchedule(year)
    }

    fun getScheduleForWeek(week: Int) {
        _listOfGames.value = repository.getScheduleForWeek(week)
                //  Timber.i("SCHEDULE RESULT TURNED TO LIST OF GAMES = ${listOfGames.value}")

    }

    fun getWeekToPick(date: Date) {
        when {
            date.before(weeksArray[0].startDate) -> {
                setWeek(1)
            }
            date.before(weeksArray[1].startDate)&&date.after(weeksArray[0].startDate) -> {
                setWeek(2)
            }
            date.before(weeksArray[2].startDate)&&date.after(weeksArray[1].startDate) -> {
                setWeek(3)
            }
            date.before(weeksArray[3].startDate)&&date.after(weeksArray[2].startDate) -> {
                setWeek(4)
            }
            date.before(weeksArray[4].startDate)&&date.after(weeksArray[3].startDate) -> {
                setWeek(5)
            }
            date.before(weeksArray[5].startDate)&&date.after(weeksArray[4].startDate) -> {
                setWeek(6)
            }
            date.before(weeksArray[6].startDate)&&date.after(weeksArray[5].startDate) -> {
                setWeek(7)
            }
            date.before(weeksArray[7].startDate)&&date.after(weeksArray[6].startDate) -> {
                setWeek(8)
            }
            date.before(weeksArray[8].startDate)&&date.after(weeksArray[7].startDate) -> {
                setWeek(9)
            }
            date.before(weeksArray[9].startDate)&&date.after(weeksArray[8].startDate) -> {
                setWeek(10)
            }
            date.before(weeksArray[10].startDate)&&date.after(weeksArray[9].startDate) -> {
                setWeek(11)
            }
            date.before(weeksArray[11].startDate)&&date.after(weeksArray[10].startDate) -> {
                setWeek(12)
            }
            date.before(weeksArray[12].startDate)&&date.after(weeksArray[11].startDate) -> {
                setWeek(13)
            }
            date.before(weeksArray[13].startDate)&&date.after(weeksArray[12].startDate) -> {
                setWeek(14)
            }
            date.before(weeksArray[14].startDate)&&date.after(weeksArray[13].startDate) -> {
                setWeek(15)
            }
            date.before(weeksArray[15].startDate)&&date.after(weeksArray[14].startDate) -> {
                setWeek(16)
            }
            date.before(weeksArray[16].startDate)&&date.after(weeksArray[15].startDate) -> {
                setWeek(17)
            }
        }
    }
    private fun setWeek(input: Int){
        _upcomingWeek.value = input
        _text.value = "Week $input"
    }

    fun setGameCount(games: Int){
        _gameCount.value = games
    }

    fun setUpPickSheet() {
        val returnList = ArrayList<Game>()
        val resultWeek =_listOfGames.value
       // Timber.i("_listOfGames value = ${listOfGames.value}")
        if (resultWeek != null) {
            var count = 0

            for(item in resultWeek){
                count++
                //create detail string
                //var detailString = "${item.dateTime}"
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val formatter = SimpleDateFormat("EE, MMM yyyy")
                val formattedDate = formatter.format( parser.parse(item.dateTime))
                //create a game object

                val newGame = Game(
                    item.homeTeam!!,
                    item.awayTeam!!,
                    getImageFromTeam(item.homeTeam!!),
                    getImageFromTeam(item.awayTeam!!),
                    count,
                    formattedDate
                )
                Log.i("TEST", "${newGame.homeTeam}, ${newGame.awayTeam}, GAME ${newGame.game}")
                //add to list for return value
                returnList.add(newGame)
                //set live data value

            }
            _teamsAndImages.value = returnList
            setGameCount(count)
        }

        Timber.i( " TEAMS AND IMAGES VALUE = ${_teamsAndImages.value}")
    }

    fun getImageFromTeam(input: String) : Int {
        when(input) {
            "GB" -> {
                return R.drawable.packers
            }
            "SF" -> {
                return R.drawable.niners
            }
            "ATL" -> {
                return R.drawable.falcons
            }
            "BUF" -> {
                return R.drawable.bills
            }
            "NE" -> {
                return R.drawable.patriots
            }
            "KC" -> {
                return R.drawable.cheifs
            }
            "TB" -> {
                return R.drawable.buccaneers
            }
            "LV" -> {
                return R.drawable.raiders
            }
            "DEN" -> {
                return R.drawable.broncos
            }
            "SEA" -> {
                return R.drawable.seahawks
            }
            "BAL" -> {
                return R.drawable.ravens
            }
            "IND" -> {
                return R.drawable.colts
            }
            "HOU" -> {
                return R.drawable.texans
            }
            "JAX" -> {
                return R.drawable.jaguars
            }
            "CAR" -> {
                return R.drawable.panthers
            }
            "DET" -> {
                return R.drawable.lions
            }
            "MIN" -> {
                return R.drawable.vikings
            }
            "CHI" -> {
                return R.drawable.bears
            }
            "TEN" -> {
                return R.drawable.titans
            }
            "NYG" -> {
                return R.drawable.giants
            }
            "WAS" -> {
                return R.drawable.washington
            }
            "LAC" -> {
                return R.drawable.chargers
            }
            "MIA" -> {
                return R.drawable.dolphins
            }
            "ARI" -> {
                return R.drawable.cardinals
            }
            "NO" -> {
                return R.drawable.saints
            }
            "NYJ" -> {
                return R.drawable.jets
            }
            "PIT" -> {
                return R.drawable.steelers
            }
            "DAL" -> {
                return R.drawable.cowboys
            }
            "PHI" -> {
                return R.drawable.eagles
            }
            "CLE" -> {
                return R.drawable.browns
            }
            "LAR" ->{
                return R.drawable.rams
            }
            "CIN" -> {
                return R.drawable.bengals
            }

            else -> return R.drawable.item_gradient

        }
    }
}