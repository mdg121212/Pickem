package com.mattg.pickem.parsebackend

import com.google.gson.GsonBuilder
import com.mattg.pickem.parsebackend.models.ParsePool
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import timber.log.Timber

class ParseQueryRepository {
    val gson = GsonBuilder().create()

    fun getUsersPools(userName: String)
            : ArrayList<ParsePool>? {
        //GETS ALL PARSE POOLS WHERE THE OWNER IS EITHER THE CURRENT USER OR THE CURRENT USER IS IN THE POOL
        val query = ParseQuery<ParseObject>("Pool")
        query.whereEqualTo("ownerName", userName)
        query.whereContains("players", userName)

        try {
            val poolsList = ArrayList<ParsePool>()
            val results = query.find()
            for (result in results) {
                Timber.i("*******in pools query found this pool $result ${result.objectId}")
                val ownerName = result.get("ownerName").toString()
                val ownerEmail = result.get("ownerEmail").toString()
                val name = result.get("poolName").toString()
                val week = result.get("week")
                val objectId = result.objectId.toString()
                val players = result.getList<String>("players")
                val playersLength =
                    players?.size//convert json array to string, then from string to list
                Timber.i("***********players result is $players its size is $playersLength")
                val playersArrayList = ArrayList<String>()
                players?.forEach { item -> playersArrayList.add(item) }
                Timber.i("********after adding players to arraylist it is $playersArrayList size ${playersArrayList.size}")
                val picks = result.getList<String>("picks")
                val picksArrayList = ArrayList<String>()
                picks?.forEach { item -> picksArrayList.add(item) }
                Timber.i("*******the list data for picks $picks and the length is ${picks?.size} array list size is ${picksArrayList.size}")
                val winners = result.getList<String>("winners")
                val winnersArrayList = ArrayList<String>()
                winners?.forEach { item -> winnersArrayList.add(item) }
                Timber.i("*******the json data for winners $winners array list size is ${winnersArrayList.size}")


                val returnPool = ParsePool(
                    name,
                    ownerName,
                    ownerEmail,
                    objectId,
                    winnersArrayList,
                    playersArrayList,
                    picksArrayList
                )
                poolsList.add(returnPool)
            }
            return poolsList
        } catch (e: ParseException) {
            Timber.i("*******searching for pools and oooops, got a ${e.code} ${e.message}")
            return null
        }
    }
}