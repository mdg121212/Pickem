package com.mattg.pickem.parsebackend.models


import com.google.gson.annotations.SerializedName

data class ParsePick(
    @SerializedName("finalPoints")
    var finalPoints: String?,
    @SerializedName("ownerEmail")
    var ownerEmail: String?,
    @SerializedName("ownerName")
    var ownerName: String?,
    @SerializedName("picks")
    var picks: String?,
    @SerializedName("week")
    var week: String?,
    @SerializedName("objectId")
    var objectId: String?
)