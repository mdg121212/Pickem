package com.mattg.pickem.parsebackend

import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.mattg.pickem.parsebackend.models.ParsePick
import com.mattg.pickem.parsebackend.models.ParsePool
import com.mattg.pickem.parsebackend.models.ParsePoolPlayer
import com.parse.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONArray
import timber.log.Timber


class ParsePoolRepository {

    val justCreatedPoolId = MutableLiveData<String>()
    val userPoolsRetrieved = MutableLiveData<ArrayList<ParsePool>>()
    val isCurrentIdReady = MutableLiveData<Boolean>()

    val poolRepoScope = CoroutineScope(Dispatchers.IO)

    val gson = GsonBuilder().create()


    suspend fun createPool(
        poolName: String,
        poolOwnerName: String,
        poolOwnerEmail: String,
        poolPlayers: ArrayList<String>
    ) = coroutineScope {
        isCurrentIdReady.value = false
        val jsonArrayPlayers = JSONArray(poolPlayers)
        val pool = ParseObject("Pool")
        pool.put("players", jsonArrayPlayers)
        pool.put("poolName", poolName)
        pool.put("ownerName", poolOwnerName)
        pool.put("ownerEmail", poolOwnerEmail)
        pool.put("picks", JSONArray())
        pool.put("winners", JSONArray())
        pool.put("week", 0)


        pool.saveInBackground { e ->
            if (e == null) {
                //saved
                val id = pool.objectId
                justCreatedPoolId.value = id
                isCurrentIdReady.value = true

            } else
                Timber.i("******parse error creating pool : code - ${e.code} cause - ${e.cause} message - ${e.message} ")

        }
    }

//    suspend fun getPoolsByListOfString(listFromPools: List<String>) {
//        val query = ParseQuery.getQuery<ParseObject>("Pool")
//        val arrayForDisplay = ArrayList<ParsePool>()
//        var counter = 0
//        val end = listFromPools.size - 1
//        for(item in listFromPools){
//            if(counter == end){
//                userPoolsRetrieved.value = arrayForDisplay
//                break
//            }
//            return query.getInBackground(item, GetCallback { pool, e ->
//                if(e == null) {
//                    //do stuff with pool log pool make sure the type of response
//                    val ownerName = pool.get("ownerName").toString()
//                    val ownerEmail = pool.get("ownerEmail").toString()
//                    val name = pool.get("poolName").toString()
//                    val week = pool.get("week")
//                    val objectId = pool.objectId.toString()
//                    val players = pool.get("players").toString() //convert json array to string, then from string to list
//                    val playersJson = JSONArray(players)
//                    val playersArrayList = ArrayList<String>()
//                    for(x in 0 until playersJson.length()){
//                        playersArrayList.add(playersJson[x].toString())
//                    }
//                    val picks = pool.get("picks").toString()
//                    val picksJson = JSONArray(picks)
//                    val length = picksJson.length()
//                    val picksArrayList = ArrayList<String>()
//                    for(x in 0 until picksJson.length()){
//                        picksArrayList.add(picksJson[x].toString())
//                    }
//
//                    val winners = pool.get("winners").toString()
//                    val winnersJson = JSONArray(winners)
//                    val winnersArrayList = ArrayList<String>()
//                    for(x in 0 until winnersJson.length()){
//                        winnersArrayList.add(winnersJson[x].toString())
//                    }
//
//
//                    val returnPool = ParsePool(
//                        name,
//                        ownerName,
//                        ownerEmail,
//                        objectId,
//                        winnersArrayList,
//                        playersArrayList,
//                        picksArrayList
//                    )
//                    arrayForDisplay.add(returnPool)
//                    counter++
//                } else
//                    Timber.i("******parse error getting pools : code - ${e.code} cause - ${e.cause} message - ${e.message} ")
//
//            })
//
//
//
//        }
//    }

    suspend fun getPoolPlayers(poolId: String): ArrayList<ParsePoolPlayer>? = coroutineScope {
        val deferred = async(Dispatchers.IO) {
            val query = ParseQuery.getQuery<ParseObject>("Pool")
            query.whereEqualTo("objectId", poolId)
            Timber.i("**********query about to look for pool to get players $poolId")

            try {

                val results = query.find()
                val result = results[0]
                val players = result.getList<String>("players")
                val arrayListOfPlayers = ArrayList<ParsePoolPlayer>()
                if (players != null) {
                    Timber.i("%%%%players was not null, it was $players")
                    for (player in players) {
                        Timber.i("%%%%about to hit user repo with this $player")
                        val playerToAdd = ParseUserRepository().getUserByName(player)
                        if (playerToAdd != null) {
                            arrayListOfPlayers.add(playerToAdd)
                        }
                    }
                }
                return@async arrayListOfPlayers

            } catch (e: ParseException) {
                Timber.i("****error getting players from pool $e")
                return@async null
            }

        }
        return@coroutineScope deferred.await()

    }

    //blueprint for async coroutine calls
    suspend fun getPoolPicks(objectId: String): ArrayList<ParsePick>? = coroutineScope {
        val deferred = async(Dispatchers.IO) {
            val query = ParseQuery.getQuery<ParseObject>("Pool")
                .whereEqualTo("objectId", objectId)
            try {
                val results = query.find()
                val pool = results[0]
                val searchListOfIds = ArrayList<String>()
                val picks = pool.getList<String>("picks")
                picks?.forEach { item -> searchListOfIds.add(item) }
                val returnList = ArrayList<ParsePick>()
                searchListOfIds.forEach { idString ->
                    val pickToAdd = ParsePickRepository().getPickById(idString)
                    if (pickToAdd != null) {
                        returnList.add(pickToAdd)
                    }
                }
                return@async returnList

            } catch (e: ParseException) {
                Timber.i("*****trying to get pool picks failed $e ${e.message} ${e.code}")
                return@async null
            }

        }
        return@coroutineScope deferred.await()

    }

    suspend fun getUserPools(user: ParseUser): ArrayList<ParsePool>? = coroutineScope {
        val deferred = async(Dispatchers.IO) {
            val query = ParseQuery<ParseObject>("Pool")
                .whereContains("players", user.username)
            try {
                val results = query.find()
                if (results.isNotEmpty()) {
                    val returnList = ArrayList<ParsePool>()
                    for (pool in results) {
                        val ownerName = pool.get("ownerName").toString()
                        val ownerEmail = pool.get("ownerEmail").toString()
                        val name = pool.get("poolName").toString()
                        val week = pool.get("week")
                        val objectId = pool.objectId.toString()
                        val players = pool.getList<String>("players")
                        val winners = pool.getList<String>("winners")
                        val picks = pool.getList<String>("picks")
                        val playersArrayList = ArrayList<String>()
                        val winnersArrayList = ArrayList<String>()
                        val picksArrayList = ArrayList<String>()

                        players?.forEach { playerName ->
                            playersArrayList.add(playerName)
                        }
                        winners?.forEach { winnerName ->
                            winnersArrayList.add(winnerName)
                        }
                        picks?.forEach { pickId ->
                            picksArrayList.add(pickId)
                        }

                        val returnPool = ParsePool(
                            name,
                            ownerName,
                            ownerEmail,
                            objectId,
                            winnersArrayList,
                            playersArrayList,
                            picksArrayList
                        )
                        returnList.add(returnPool)

                    }
                    return@async returnList
                } else
                    Timber.i("*******tried to get userpools failed results were empty")
                return@async null
            } catch (e: ParseException) {
                Timber.i("******failed to get pools $e ${e.message} ${e.code} ${e.cause}")
                return@async null
            }
        }
        return@coroutineScope deferred.await()
    }

    suspend fun getPoolById(poolId: String): ParsePool? = coroutineScope {
        val deferred = async(Dispatchers.IO) {
            val query = ParseQuery.getQuery<ParseObject>("Pool")
            query.whereEqualTo("objectId", poolId)
            Timber.i("**********query about to look for pool $poolId")
            try {
                val results = query.find()
                val result = results[0]
                val ownerName = result.get("ownerName").toString()
                val ownerEmail = result.get("ownerEmail").toString()
                val name = result.get("poolName").toString()
                val week = result.get("week")
                val objectId = result.objectId.toString()
                val players = result.getList<String>("players")

                val playersArrayList = ArrayList<String>()
                players?.forEach { item -> playersArrayList.add(item) }

                val picks = result.getList<String>("picks")

                val picksArrayList = ArrayList<String>()
                picks?.forEach { item -> picksArrayList.add(item) }


                val winners = result.getList<String>("winners")

                val winnersArrayList = ArrayList<String>()
                winners?.forEach { item -> winnersArrayList.add(item) }

                val returnPool = ParsePool(
                    name,
                    ownerName,
                    ownerEmail,
                    objectId,
                    winnersArrayList,
                    playersArrayList,
                    picksArrayList
                )
                return@async returnPool
            } catch (e: ParseException) {
                Timber.i("**********parse exception $e")
                return@async null
            }

        }
        return@coroutineScope deferred.await()

    }


    suspend fun deletePoolPicks(pick: ParsePick, poolId: String) = coroutineScope {
        val query = ParseQuery.getQuery<ParseObject>("Pool")
            .whereEqualTo("objectId", poolId)
        try {
            val results = query.find()
            if (results.isNotEmpty()) {
                val pool = results[0]
                val picks = pool.getList<String>("picks")
                picks?.forEach { item ->
                    if (item == pick.objectId) {
                        picks.remove(item)
                    }
                }
                if (picks != null) {
                    pool.put("picks", picks)
                }
                pool.save()
                val query2 = ParseQuery.getQuery<ParseObject>("Pick")
                    .whereEqualTo("objectId", pick.objectId)
                val results2 = query2.find()
                if (results.isNotEmpty()) {
                    results2[0].delete()
                }

            }

        } catch (e: ParseException) {
            Timber.i("******tried to delete picks from pool, failed due to $e ${e.message}")
        }
    }


    suspend fun deletePoolById(objectId: String) = coroutineScope {
        val query = ParseQuery.getQuery<ParseObject>("Pool")
        query.getInBackground(objectId, GetCallback { pool, e ->
            if (e == null) {
                pool.deleteInBackground()
            } else
                Timber.i("******parse error deleting pool : code - ${e.code} cause - ${e.cause} message - ${e.message} ")

        })
    }

    fun addPlayerToPool(player: ParseUser, poolId: String) {
        val query = ParseQuery.getQuery<ParseObject>("Pool")
            .whereEqualTo("objectId", poolId)

        try {
            val results = query.find()
            if (results.isNotEmpty()) {
                val pool = results[0]
                pool.addUnique("players", player.username)
                pool.save()
                Timber.i("***********pool updated with player")
            }

        } catch (e: ParseException) {
            Timber.i("******Trying to save player to pool failed $e ${e.code} ${e.message} ${e.cause}")
        }

    }

    suspend fun addWinnerToPool(poolId: String, data: HashMap<String, String>) {
        val query = ParseQuery<ParseObject>("Pool")
            .whereEqualTo("objectId", poolId)
        try {
            val results = query.find()
            if (results.isNotEmpty()) {
                val pool = results[0]
                pool.addUnique(
                    "winners",
                    "${data["playerName"].toString()} : ${data["week"].toString()}"
                )
                pool.save()
            } else
                Timber.i("******no pool found to add WINNERS, wtf")

        } catch (e: ParseException) {
            Timber.i("********tried to add winner to pool, failed $e ${e.message} ${e.code} ${e.cause}")
        }

    }

    suspend fun getWinners(poolId: String): ArrayList<Pair<String, String>>? = coroutineScope {
        val deferred = async(Dispatchers.IO) {
            val query = ParseQuery<ParseObject>("Pool")
                .whereEqualTo("objectId", poolId)
            try {
                val results = query.find()
                if (results.isNotEmpty()) {
                    val pool = results[0]
                    val winners = pool.getList<String>("winners")

                    val returnList = ArrayList<Pair<String, String>>()
                    if (winners != null) {
                        for (item in winners) {
                            val split = item.split(":")
                            val winnerNameString = split[0]
                            val winnerWeek = split[1]
                            val pair = Pair(winnerNameString.trim(), winnerWeek.trim())
                            returnList.add(pair)
                        }

                        return@async returnList
                    } else return@async null
                } else return@async null
            } catch (e: ParseException) {
                return@async null
            }

        }
        return@coroutineScope deferred.await()

    }

    suspend fun removePlayerFromPool(poolId: String, user: ParseUser) = coroutineScope {
        val query = ParseQuery<ParseObject>("Pool")
            .whereEqualTo("objectId", poolId)

        try {
            val results = query.find()
            if (results.isNotEmpty()) {
                val pool = results[0]
                pool.removeAll("players", listOf(user.username))
                pool.save()
                Timber.i("*******successfully removed player ${user.username} from pool $poolId")
            }

        } catch (e: ParseException) {
            Timber.i("*****tried to remove player from pool, failed: $e ${e.message} ${e.code} ${e.cause}")
        }
    }


}