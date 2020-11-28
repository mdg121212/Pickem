package com.mattg.pickem.models.iomodels


import com.google.gson.annotations.SerializedName

data class GamesResponseItem(
        @SerializedName("gameTime")
    var gameTime: String?,
        @SerializedName("homeTeam")
    var homeTeam: HomeTeam?,
        @SerializedName("stadiumInfo")
    var stadiumInfo: StadiumInfo?,
        @SerializedName("visitorTeam")
    var visitorTeam: VisitorTeam?
)