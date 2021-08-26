package com.mattg.pickem.parsebackend.models

class ParsePool {
    lateinit var poolName: String
    lateinit var ownerName: String
    lateinit var ownerId: String
    lateinit var objectId: String
    var week: Int? = null
    lateinit var players: ArrayList<String>
    lateinit var winners: ArrayList<String>
    lateinit var picks: ArrayList<String>


    constructor()

    constructor(poolName: String, poolOwner: String, poolOwnerId: String, poolObjectId: String) {
        this.poolName = poolName
        this.ownerName = poolOwner
        this.ownerId = poolOwnerId
        this.objectId = poolObjectId
        this.week

    }

    constructor(
        poolName: String,
        poolOwner: String,
        poolOwnerId: String,
        poolObjectId: String,
        winners: ArrayList<String>,
        players: ArrayList<String>,
        picks: ArrayList<String>
    ) {
        this.poolName = poolName
        this.ownerName = poolOwner
        this.ownerId = poolOwnerId
        this.objectId = poolObjectId
        this.picks = picks
        this.players = players
        this.winners = winners
        this.week

    }

    fun getCurrentWeek(): Int? {
        return if (week != null) {
            week
        } else null
    }

    fun setCurrentWeek(newWeek: Int) {
        week = newWeek
    }

    fun addPlayer(player: ParseUser) {
        player.username?.let { this.players.add(it) }
    }

    fun removePlayer(player: ParseUser) {
        this.players.remove(player)

    }

    fun addWinner(winner: WinnerItem) {
        this.winners.add(winner.name)
    }

    fun addPick(pick: String) {
        this.picks.add(pick)
    }


}