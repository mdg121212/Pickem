package com.mattg.pickem.models.iomodels


import com.google.gson.annotations.SerializedName

class IOScheduleReponse : ArrayList<IOScheduleReponse.IOreponseItem>(){
    data class IOreponseItem(
        @SerializedName("AwayTeam")
        var awayTeam: String?,
        @SerializedName("AwayTeamMoneyLine")
        var awayTeamMoneyLine: Int?,
        @SerializedName("Canceled")
        var canceled: Boolean?,
        @SerializedName("Channel")
        var channel: String?,
        @SerializedName("Date")
        var date: String?,
        @SerializedName("DateTime")
        var dateTime: String?,
        @SerializedName("Day")
        var day: String?,
        @SerializedName("ForecastDescription")
        var forecastDescription: String?,
        @SerializedName("ForecastTempHigh")
        var forecastTempHigh: Int?,
        @SerializedName("ForecastTempLow")
        var forecastTempLow: Int?,
        @SerializedName("ForecastWindChill")
        var forecastWindChill: Int?,
        @SerializedName("ForecastWindSpeed")
        var forecastWindSpeed: Int?,
        @SerializedName("GameKey")
        var gameKey: String?,
        @SerializedName("GeoLat")
        var geoLat: Any?,
        @SerializedName("GeoLong")
        var geoLong: Any?,
        @SerializedName("GlobalAwayTeamID")
        var globalAwayTeamID: Int?,
        @SerializedName("GlobalGameID")
        var globalGameID: Int?,
        @SerializedName("GlobalHomeTeamID")
        var globalHomeTeamID: Int?,
        @SerializedName("HomeTeam")
        var homeTeam: String?,
        @SerializedName("HomeTeamMoneyLine")
        var homeTeamMoneyLine: Int?,
        @SerializedName("OverUnder")
        var overUnder: Double?,
        @SerializedName("PointSpread")
        var pointSpread: Double?,
        @SerializedName("ScoreID")
        var scoreID: Int?,
        @SerializedName("Season")
        var season: Int?,
        @SerializedName("SeasonType")
        var seasonType: Int?,
        @SerializedName("StadiumDetails")
        var stadiumDetails: StadiumDetails?,
        @SerializedName("StadiumID")
        var stadiumID: Int?,
        @SerializedName("Status")
        var status: String?,
        @SerializedName("Week")
        var week: Int?
    ) {
        data class StadiumDetails(
            @SerializedName("Capacity")
            var capacity: Int?,
            @SerializedName("City")
            var city: String?,
            @SerializedName("Country")
            var country: String?,
            @SerializedName("GeoLat")
            var geoLat: Double?,
            @SerializedName("GeoLong")
            var geoLong: Double?,
            @SerializedName("Name")
            var name: String?,
            @SerializedName("PlayingSurface")
            var playingSurface: String?,
            @SerializedName("StadiumID")
            var stadiumID: Int?,
            @SerializedName("State")
            var state: String?,
            @SerializedName("Type")
            var type: String?
        )
    }
}