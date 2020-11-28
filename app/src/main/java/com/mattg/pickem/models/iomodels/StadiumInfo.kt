package com.mattg.pickem.models.iomodels


import com.google.gson.annotations.SerializedName
import com.mattg.pickem.models.iomodels.Location

data class StadiumInfo(
        @SerializedName("id")
    var id: String?,
        @SerializedName("location")
    var location: Location?,
        @SerializedName("name")
    var name: String?
)