package com.mattg.pickem.db

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PicksRepository(private val dao: PicksDao) {

    val coroutinContext = CoroutineScope(Dispatchers.Default)

    private val picks : LiveData<Pick> =
        dao.getPicksLiveData()

    suspend fun deletePicks(picks: Pick) = coroutinContext.launch {
        dao.deletePick(picks)
    }

    suspend fun updatePicks(picks: Pick) = coroutinContext.launch {
        dao.updatePicks(picks)
    }

    suspend fun insertPicks(picks: Pick) = coroutinContext.launch {
        dao.insertPicks(picks)
    }

}