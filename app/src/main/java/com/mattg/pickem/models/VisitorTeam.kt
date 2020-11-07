package com.mattg.pickem.models


import com.google.gson.annotations.SerializedName

data class VisitorTeam(
    @SerializedName("abbr")
    var abbr: String?,
    @SerializedName("fullName")
    var fullName: String?,
    @SerializedName("nickName")
    var nickName: String?
)