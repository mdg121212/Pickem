package com.mattg.pickem.models


import com.google.gson.annotations.SerializedName

data class StadiumInfo(
    @SerializedName("id")
    var id: String?,
    @SerializedName("location")
    var location: Location?,
    @SerializedName("name")
    var name: String?
)