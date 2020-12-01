package com.mattg.pickem.db.repos


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mattg.pickem.db.PicksDatabase
import com.mattg.pickem.models.iomodels.IOScheduleReponse
import com.mattg.pickem.network.APICallService
import com.mattg.pickem.network.SportsDataApi


class ApiCallRepository(application: Context) {

    val nflApi = APICallService.fetchIOApi()
    private val database = PicksDatabase.getInstance(application)

    private val _scheduleApiForYearResponse = MutableLiveData<IOScheduleReponse>()
     val scheduleApiForYearResponse: LiveData<IOScheduleReponse> = _scheduleApiForYearResponse

    private val _scheduleApiError = MutableLiveData<String>()
     val scheduleApiError : LiveData<String> = _scheduleApiError


    fun getApiService(): SportsDataApi {
        return nflApi
    }


}