package com.mattg.pickem.models.firebase

class Invite {
    var isAccepted: Boolean = false
    var sender: String ?= null
    var senderEmail: String ?= null
    var senderId: String ?= null
    var invitee: String ?= null
    var poolId: String ?= null
    var inviteId: String? = null
    var sentInviteId: String? = null


    constructor(source: String, target: String, groupId: String, sourceId: String, email: String, documentId: String, sentInviteId: String){
    this.sender = source
    this.invitee = target
        this.senderEmail = email
    isAccepted = false
    this.poolId = groupId
    this.senderId = sourceId
        this.inviteId = documentId
        this.sentInviteId = sentInviteId
}
    constructor(source: String, target: String, sourceId: String, email: String){
        this.sender = source
        this.invitee = target
        isAccepted = false
        this.senderEmail = email
        this.poolId = null
        this.senderId = sourceId
        this.sentInviteId
    }
    constructor(accpeted: Boolean, source: String, target: String, id: String){
        this.sender = source
        this.invitee = target
        isAccepted = accpeted
        this.poolId = id
        this.senderEmail = null
        this.senderId
        this.sentInviteId

    }

}