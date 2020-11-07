package com.mattg.pickem.network

import com.mattg.pickem.models.iomodels.IOScheduleReponse
import com.mattg.pickem.models.iomodels.IOScoresResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// SEASON
//https://api.sportsdata.io/v3/nfl/scores/json/Schedules/2020?key=2797584ca788420d943fd25c365589ce
//scores by week             5? is the week                  SEASON       API KEY
// https://api.sportsdata.io/v3/nfl/scores/json/ScoresByWeek/2020/5?key=2797584ca788420d943fd25c365589ce

interface SportsDataApi {

    @GET("Schedules/{year}?")
    fun getScheduleByYear(@Path("year")year: Int,
                          @Query("key")key: String)
    : Call<IOScheduleReponse>

    @GET("ScoresByWeek/{year}/{week}?")
    fun getScoresByWeek(@Path("year")year: Int,
                        @Path("week")week: Int,
                        @Query("key")key: String)
    :Call<IOScoresResponse>
}