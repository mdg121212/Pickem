package com.mattg.pickem.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mattg.pickem.models.iomodels.IOScheduleReponse
import com.mattg.pickem.network.APICallService
import com.mattg.pickem.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ApiCallRepository {

    val nflApi = APICallService.fetchIOApi()
//    val apiDao = PicksDatabase.getInstance().apiDao()

    private val _scheduleApiForYearResponse = MutableLiveData<IOScheduleReponse>()
    val scheduleApiForYearResponse: LiveData<IOScheduleReponse> = _scheduleApiForYearResponse

    private val _scheduleApiError = MutableLiveData<String>()
     val scheduleApiError : LiveData<String> = _scheduleApiError

    fun getSchedule(year: Int) : IOScheduleReponse? {
        nflApi.getScheduleByYear(year, Constants.key)
            .enqueue(object: Callback<IOScheduleReponse>{
                override fun onResponse(
                    call: Call<IOScheduleReponse>,
                    response: Response<IOScheduleReponse>) {
                   _scheduleApiForYearResponse.value = response.body()

                }

                override fun onFailure(call: Call<IOScheduleReponse>, t: Throwable) {
                    _scheduleApiError.value = t.message
                }
            })
        return scheduleApiForYearResponse.value
    }

    fun saveScheduleForYearResponseToRoom(response: String){

    }

    fun getScheduleForWeek(week: Int) : ArrayList<IOScheduleReponse.IOreponseItem>{
        val listToReturn = ArrayList<IOScheduleReponse.IOreponseItem>()
        val resultWeek = _scheduleApiForYearResponse.value
        //  Timber.i("Result week value in getSchedulefunction = $resultWeek")
        if (resultWeek != null) {
            for(item in resultWeek){
                if(item.week == week && item.awayTeam != "BYE" && item.homeTeam != "BYE"){
                    listToReturn.add(item)
                    Timber.i("Item added = ${item.awayTeam} : ${item.homeTeam} : ${item.date}")
                }
            }
        } else {
            Timber.i("FROM REPO, THE VALUE OF THE INPUT IS SHOWING AS NULL")
        }
        return listToReturn
    }
}