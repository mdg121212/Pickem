package com.mattg.pickem.db.repos

import android.app.Application
import com.mattg.pickem.db.Pick
import com.mattg.pickem.db.PicksDatabase
import com.mattg.pickem.db.WeekMatchUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RoomRepo(context: Application) {

    private val database = PicksDatabase.getInstance(context)

    suspend fun getListOfPicks(): List<Pick> {
        val list = database.picksDao().getAllPics()
        return list
    }

    suspend fun getPicksForSubmit(week: String): List<Pick> {
        val list = database.picksDao().getPicksByWeek(week)
        return list
    }

    suspend fun savePicksToDatabase(picks: Pick) {
        try {
            CoroutineScope(Dispatchers.Default).launch {
                database.picksDao().insertPicks(picks)
            }
        } catch (e: Error) {
            Timber.i("Error: ${e.message}")
        }

    }

    suspend fun deletePicks(id: Int) {
        database.picksDao().deletePickById(id)

    }

    suspend fun saveMatchupsToDatabase(matchups: WeekMatchUp) {
        database.matchupDao().insertMatchups(matchups)
    }


    fun clearPicksDatabase() {
        try {
            CoroutineScope(Dispatchers.Default).launch {
                database.picksDao().clearPicks()
            }

        } catch (e: Error) {
            Timber.i("Error: ${e.message}")
        }
    }

    suspend fun areDatabaseMatchupsEmpty(): Pair<Boolean, ArrayList<WeekMatchUp>> {
        val returnList = ArrayList<WeekMatchUp>()
        val job = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
            val list = database.matchupDao().getMatchups()
            list.forEach { item -> returnList.add(item) }
        }
        Timber.i("TESTINGDATABASE========at database check value of list is: $returnList")
        val isEmpty = returnList.isEmpty()
        Timber.i("TESTINGDATABASE=============isEmpty = $isEmpty")
        val returnPair = Pair(isEmpty, returnList)
        Timber.i("TESTINGDATABASE ==== return pair first = ${returnPair.first} second = ${returnPair.second}")
        return returnPair

    }


    suspend fun getMatchupsString(): Pair<String?, Int?> {
        val list = database.matchupDao().getMatchups()
        var string = ""
        var week: Int = 0
        for (item in list) {
            string = item.games
            week = item.week
            break
        }
        Timber.i("MATCHUPSTRING ===== from database after conversion: $string")
        return Pair(string, week)
    }


}