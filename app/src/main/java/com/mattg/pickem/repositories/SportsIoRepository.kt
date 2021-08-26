package com.mattg.pickem.repositories

import com.mattg.pickem.models.iomodels.TeamGameStats
import com.mattg.pickem.network.APICallService
import com.mattg.pickem.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repository class for interacting with sports data apis and returning results
 */
class SportsIoRepository {

    val api = APICallService.fetchIOApi();

    /**
     * Will retrieve all team statistics by week
     */
    fun getTeamStatsByWeek(year: String, week: Int) {
        api.getAllTeamGameStatsByBeek(year, week, Constants.key).enqueue(object :
            Callback<TeamGameStats> {
            override fun onResponse(call: Call<TeamGameStats>, response: Response<TeamGameStats>) {
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<TeamGameStats>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })


    }
}