package com.mattg.pickem.models.iomodels


import com.google.gson.annotations.SerializedName

data class GeoCoordinates(
    @SerializedName("latitude")
    var latitude: Double?,
    @SerializedName("longitude")
    var longitude: Double?,
    @SerializedName("type")
    var type: String?
)