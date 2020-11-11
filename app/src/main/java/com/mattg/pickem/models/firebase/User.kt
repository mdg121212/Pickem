package com.mattg.pickem.models.firebase

import com.mattg.pickem.db.Pick


class User {
    lateinit var name: String
    lateinit var email: String
    lateinit var userId: String
    var picks = ArrayList<Pick>()
    var type: String? = null
    private var pools: String? = null
    private var invites: Boolean = false
    var picksIn: Boolean? = null

    constructor()

    constructor(name: String, email: String, userId: String) {
        this.name = name
        this.email = email
        this.userId = userId
        this.picks
        type = null
        pools = null
        this.picksIn
    }

    constructor(name: String, email: String, userId: String, picks: ArrayList<Pick>?, typeString: String?) {
        this.name = name
        this.email = email
        this.userId = userId
        if (picks != null) {
            this.picks = picks
        }
        this.type = typeString
        pools = null
        this.invites = false
        this.picksIn
    }

    fun setPicksIn(){
        this.picksIn = true
    }
    fun addPicksToList(input: Pick){
        picks.add(input)
    }
}