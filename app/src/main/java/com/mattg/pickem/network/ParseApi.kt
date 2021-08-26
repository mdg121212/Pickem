package com.mattg.pickem.network

import com.mattg.pickem.parsebackend.models.ParseUser
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ParseApi {

    @Headers(
        "X-Parse-Application-Id: eg4nAdShJCVsRjT7QCyoA3ohlSOfxq7rhEsvquqD",
        "X-Parse-REST-API-Key: vQgPdgsnz7QSFQzblmhBc6JZL8GvPuc2iFTtlyL4",
        "X-Parse-Revocable-Session: 1",
        "Content-Type: application/json"
    )
    @POST("users")
    fun signUpUser(@Body user: ParseUser): Call<ParseUser>

    @Headers(
        "X-Parse-Application-Id: eg4nAdShJCVsRjT7QCyoA3ohlSOfxq7rhEsvquqD",
        "X-Parse-REST-API-Key: vQgPdgsnz7QSFQzblmhBc6JZL8GvPuc2iFTtlyL4",
        "X-Parse-Revocable-Session: 1"
    )
    @GET("login")
    fun loginUser(): Call<ParseUser>

    /**
     * Deploys a cloud function to trigger data retrieval from parse backend
     * followed by function to retrieve the data
     */
    @Headers(
        "X-Parse-Application-Id: eg4nAdShJCVsRjT7QCyoA3ohlSOfxq7rhEsvquqD",
        "X-Parse-REST-API-Key: vQgPdgsnz7QSFQzblmhBc6JZL8GvPuc2iFTtlyL4",
        "X-Parse-Revocable-Session: 1"
    )
    @GET("data")
    fun apiDataWebhook(): Call<String>

    /**
     * Gets the last updated api data
     */
    @GET("data")
    fun apiDataRefresh(): Call<String>

}