package com.mattg.pickem.models.iomodels


import com.google.gson.annotations.SerializedName

data class HomeTeam(
    @SerializedName("abbr")
    var abbr: String?,
    @SerializedName("fullName")
    var fullName: String?,
    @SerializedName("nickName")
    var nickName: String?
)