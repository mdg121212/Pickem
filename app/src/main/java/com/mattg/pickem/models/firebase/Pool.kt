package com.mattg.pickem.models.firebase

class Pool {
    lateinit var name: String
    lateinit var owner: String
    lateinit var ownerId: String
    lateinit var documentId: String
    var players = ArrayList<HashMap<String, Any>>()
    //?= null
    var weeks = ArrayList<HashMap<String, Any>>() //?= null
    var week: Int? = null

    constructor()

    constructor(poolName: String, poolOwner: String, poolOwnerId: String, poolDocumentId: String) {
        this.name = poolName
        this.owner = poolOwner
        this.ownerId = poolOwnerId
        this.documentId = poolDocumentId
    }

    fun setPlayersFromList(list: List<HashMap<String, Any>>) {
        players.addAll(list)
    }

    fun addWeek(week: HashMap<String, Any>) {
        weeks.add(week)
    }

    fun getCurrentWeek(): Int? {
        return if (week != null) {
            week
        } else null
    }

    fun setWeek(newWeek: Int) {
        week = newWeek
    }

    fun removePlayer(playerToRemove: HashMap<String, Any>): Boolean {
        for (player in players) {
            if (player.containsKey(playerToRemove.keys)) {
                players.remove(player)
                return true
            }
        }
        return false

    }


}