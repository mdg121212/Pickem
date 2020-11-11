package com.mattg.pickem.models.firebase

class Pool {
    lateinit var poolName: String
    lateinit var owner: String
    lateinit var ownerId: String
    lateinit var documentId: String
    var week: Int? = null
    var playerCount = 0

    constructor()

    constructor(poolName: String, poolOwner: String, poolOwnerId: String, poolDocumentId: String) {
        this.poolName = poolName
        this.owner = poolOwner
        this.ownerId = poolOwnerId
        this.documentId = poolDocumentId
        this.playerCount
        this.week

    }
    constructor(poolName: String, poolOwner: String, poolOwnerId: String, poolDocumentId: String, playerCount: Int) {
        this.poolName = poolName
        this.owner = poolOwner
        this.ownerId = poolOwnerId
        this.documentId = poolDocumentId
        this.playerCount = playerCount
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



}