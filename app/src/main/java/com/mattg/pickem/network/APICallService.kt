package com.mattg.pickem.network


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APICallService {

    private const val IO_BASE_URL = "https://api.sportsdata.io/v3/nfl/scores/json/"
    private const val RSS_BASE_URL = "https://www.espn.com/espn/rss/nfl/"
    private const val RSS_JSON_CONVERTED = "https://feed2json.org/"
    private const val PARSE_BASE_URL = "https://parseapi.back4app.com/"
//    //5mb cache
//    private val cacheSize = (5 * 1024 * 1024).toLong()
//    //cache variable
//    val myCache = Cache(context.cacheDir, cacheSize)

    private var ioApi: SportsDataApi? = null
    private var rssApi: RSSApi? = null
    private var parseApi: ParseApi? = null

    private fun getParseApi(): ParseApi {
        if (parseApi == null) {
            val okHttpClient = OkHttpClient.Builder()
            parseApi = Retrofit.Builder()
                .baseUrl(PARSE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build()
                .create(ParseApi::class.java)
        }
        return parseApi!!
    }

    private fun getRssApi(): RSSApi {
        if (rssApi == null) {
            val okHttpClient = OkHttpClient.Builder()
            rssApi = Retrofit.Builder()
                .baseUrl(RSS_JSON_CONVERTED)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build()
                .create(RSSApi::class.java)
        }
        return rssApi!!
    }

    private fun getSportsIOApiCall(): SportsDataApi {
        if(ioApi == null){
            val okHttpClient = OkHttpClient.Builder()
            //specify cache
//                .cache(myCache)
//                .addInterceptor { chain ->
//                    var request = chain.request()
//                    //test for internet connection to see if cache has to be used
//                    request = if(hasNetwork(context)!!)
//                        request.newBuilder().header("Cache-Control", "public, max-age =" + 5).build()
//
//                    else
//                     //if no internet get cache from 7 days ago if older than 7 days discard it
//                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale="+ 60 * 60 * 24 * 1).build()
//                    chain.proceed(request)
//                }


            ioApi = Retrofit.Builder()
                .baseUrl(IO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build()
                .create(SportsDataApi::class.java)
        }
        return ioApi!!
    }

    fun fetchParseApi() = getParseApi()

    fun fetchIOApi() = getSportsIOApiCall()

    fun fetchRssApi() = getRssApi()
}