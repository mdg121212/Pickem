package com.mattg.pickem.models.iomodels


import com.google.gson.annotations.SerializedName
import com.mattg.pickem.models.iomodels.GeoCoordinates

data class Location(
        @SerializedName("city")
    var city: String?,
        @SerializedName("country")
    var country: String?,
        @SerializedName("formattedAddress")
    var formattedAddress: String?,
        @SerializedName("geoCoordinates")
    var geoCoordinates: GeoCoordinates?,
        @SerializedName("postalCode")
    var postalCode: String?,
        @SerializedName("street")
    var street: String?
)