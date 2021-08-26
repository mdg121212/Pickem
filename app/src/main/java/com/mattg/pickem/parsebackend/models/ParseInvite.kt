package com.mattg.pickem.parsebackend.models

import com.google.gson.annotations.SerializedName

class ParseInvite(
    @SerializedName("senderName")
    val sender: String,
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("poolId")
    val poolId: String,
    @SerializedName("poolName")
    val poolName: String,
    @SerializedName("objectId")
    val inviteId: String,
    @SerializedName("receiverId")
    val receiverId: String,
    @SerializedName("receiverName")
    val receiverName: String
)