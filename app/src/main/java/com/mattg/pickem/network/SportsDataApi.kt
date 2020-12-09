package com.mattg.pickem.network

import com.mattg.pickem.models.iomodels.IOScheduleReponse
import com.mattg.pickem.models.iomodels.IOScoresResponse
import com.mattg.pickem.models.iomodels.IOWeekScoresResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SportsDataApi {

    @GET("Schedules/{year}?")
    fun getScheduleByYear(@Path("year")year: Int,
                          @Query("key")key: String)
    : Call<IOScheduleReponse>

    @GET("ScoresByWeek/{year}/{week}?")
    fun getScoresByWeek(@Path("year") year: Int,
                        @Path("week") week: Int,
                        @Query("key") key: String)
            : Call<IOScoresResponse>

    @GET("LastCompletedWeek?")
    fun getLastCompletedWeek(@Query("key") key: String): Call<String>

    @GET("CurrentWeek?")
    fun getCurrentWeek(@Query("key") key: String): Call<String>

    @GET("UpcomingWeek?")
    fun getWeekForCheckingScore(@Query("key") key: String): Call<String>

    @GET("ScoresByWeek/{season}/{week}?")
    fun getScoresFromWeek(@Path("season") year: String,
                          @Path("week") week: Int,
                          @Query("key") key: String)
            : Call<IOWeekScoresResponse>
}