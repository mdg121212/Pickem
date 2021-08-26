package com.mattg.pickem.network

import com.mattg.pickem.models.RssFeedItem
import retrofit2.Call
import retrofit2.http.GET

interface RSSApi {
    @GET("convert?url=https%3A%2F%2Fwww.espn.com%2Fespn%2Frss%2Fnfl%2Fnews")
    fun getNewsRss(): Call<RssFeedItem>
}