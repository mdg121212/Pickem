package com.mattg.pickem.db.repos

import androidx.lifecycle.LiveData
import com.mattg.pickem.db.Pick
import com.mattg.pickem.db.PicksDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PicksRepository(private val dao: PicksDao) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val picks : LiveData<Pick> =
        dao.getPicksLiveData()

    suspend fun deletePicks(picks: Pick) = coroutineScope.launch {
        dao.deletePick(picks)
    }

    suspend fun updatePicks(picks: Pick) = coroutineScope.launch {
        dao.updatePicks(picks)
    }

    suspend fun insertPicks(picks: Pick) = coroutineScope.launch {
        dao.insertPicks(picks)
    }

}