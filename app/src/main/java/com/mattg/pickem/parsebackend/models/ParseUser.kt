package com.mattg.pickem.parsebackend.models


import com.google.gson.annotations.SerializedName

data class ParseUser(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("invitations")
    var invitations: List<Any?>?,
    @SerializedName("objectId")
    var objectId: String?,
    @SerializedName("password")
    var password: String?,
    @SerializedName("pools")
    var pools: List<Any?>?,
    @SerializedName("updatedAt")
    var updatedAt: String?,
    @SerializedName("username")
    var username: String?
)