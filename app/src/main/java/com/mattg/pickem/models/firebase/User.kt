package com.mattg.pickem.models.firebase

import com.mattg.pickem.db.Pick


class User {
    lateinit var name: String
    lateinit var email: String
    lateinit var userId: String
    var picks: List<Pick>? = null
    var type: String? = null
    var pools: String? = null
    var invites: Boolean = false
    var numberOfInvitesSent = 0
    var listOfSentInvites = ArrayList<Map<String, String>>()
    var listOfInviters = ArrayList<Map<String, String>>()
    var activePools = ArrayList<Map<String, String>>()

    constructor()

    constructor(name: String, email: String, userId: String) {
        this.name = name
        this.email = email
        this.userId = userId
        picks = null
        type = null
        pools = null
    }
    constructor(name: String, email: String, userId: String, typeString: String?) {
        this.name = name
        this.email = email
        this.userId = userId
        picks = null
        this.type = typeString
        pools = null
        this.invites = false
    }
    constructor(name: String, email: String, userId: String, picks: List<Pick>?, typeString: String?) {
        this.name = name
        this.email = email
        this.userId = userId
        this.picks = picks
        this.type = typeString
        pools = null
        this.invites = false
    }
    constructor(name: String, email: String, userId: String, picks: List<Pick>?, typeString: String?, pools: String?) {
        this.name = name
        this.email = email
        this.userId = userId
        this.picks = picks
        this.type = typeString
        this.pools = pools
        this.invites = false
    }






}