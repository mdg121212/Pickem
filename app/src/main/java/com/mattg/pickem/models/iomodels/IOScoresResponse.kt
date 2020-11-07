package com.mattg.pickem.models.iomodels


import com.google.gson.annotations.SerializedName

class IOScoresResponse : ArrayList<IOScoresResponse.IOScoresResponseItem>(){
    data class IOScoresResponseItem(
        @SerializedName("AwayRotationNumber")
        var awayRotationNumber: Int?,
        @SerializedName("AwayScore")
        var awayScore: Int?,
        @SerializedName("AwayScoreOvertime")
        var awayScoreOvertime: Int?,
        @SerializedName("AwayScoreQuarter1")
        var awayScoreQuarter1: Int?,
        @SerializedName("AwayScoreQuarter2")
        var awayScoreQuarter2: Int?,
        @SerializedName("AwayScoreQuarter3")
        var awayScoreQuarter3: Int?,
        @SerializedName("AwayScoreQuarter4")
        var awayScoreQuarter4: Int?,
        @SerializedName("AwayTeam")
        var awayTeam: String?,
        @SerializedName("AwayTeamID")
        var awayTeamID: Int?,
        @SerializedName("AwayTeamMoneyLine")
        var awayTeamMoneyLine: Int?,
        @SerializedName("Canceled")
        var canceled: Boolean?,
        @SerializedName("Channel")
        var channel: String?,
        @SerializedName("Closed")
        var closed: Boolean?,
        @SerializedName("Date")
        var date: String?,
        @SerializedName("DateTime")
        var dateTime: String?,
        @SerializedName("Day")
        var day: String?,
        @SerializedName("Distance")
        var distance: String?,
        @SerializedName("Down")
        var down: Any?,
        @SerializedName("DownAndDistance")
        var downAndDistance: Any?,
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
        @SerializedName("GameEndDateTime")
        var gameEndDateTime: String?,
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
        @SerializedName("Has1stQuarterStarted")
        var has1stQuarterStarted: Boolean?,
        @SerializedName("Has2ndQuarterStarted")
        var has2ndQuarterStarted: Boolean?,
        @SerializedName("Has3rdQuarterStarted")
        var has3rdQuarterStarted: Boolean?,
        @SerializedName("Has4thQuarterStarted")
        var has4thQuarterStarted: Boolean?,
        @SerializedName("HasStarted")
        var hasStarted: Boolean?,
        @SerializedName("HomeRotationNumber")
        var homeRotationNumber: Int?,
        @SerializedName("HomeScore")
        var homeScore: Int?,
        @SerializedName("HomeScoreOvertime")
        var homeScoreOvertime: Int?,
        @SerializedName("HomeScoreQuarter1")
        var homeScoreQuarter1: Int?,
        @SerializedName("HomeScoreQuarter2")
        var homeScoreQuarter2: Int?,
        @SerializedName("HomeScoreQuarter3")
        var homeScoreQuarter3: Int?,
        @SerializedName("HomeScoreQuarter4")
        var homeScoreQuarter4: Int?,
        @SerializedName("HomeTeam")
        var homeTeam: String?,
        @SerializedName("HomeTeamID")
        var homeTeamID: Int?,
        @SerializedName("HomeTeamMoneyLine")
        var homeTeamMoneyLine: Int?,
        @SerializedName("IsInProgress")
        var isInProgress: Boolean?,
        @SerializedName("IsOver")
        var isOver: Boolean?,
        @SerializedName("IsOvertime")
        var isOvertime: Boolean?,
        @SerializedName("LastPlay")
        var lastPlay: String?,
        @SerializedName("LastUpdated")
        var lastUpdated: String?,
        @SerializedName("NeutralVenue")
        var neutralVenue: Boolean?,
        @SerializedName("OverUnder")
        var overUnder: Double?,
        @SerializedName("PointSpread")
        var pointSpread: Double?,
        @SerializedName("PointSpreadAwayTeamMoneyLine")
        var pointSpreadAwayTeamMoneyLine: Int?,
        @SerializedName("PointSpreadHomeTeamMoneyLine")
        var pointSpreadHomeTeamMoneyLine: Int?,
        @SerializedName("Possession")
        var possession: Any?,
        @SerializedName("Quarter")
        var quarter: String?,
        @SerializedName("QuarterDescription")
        var quarterDescription: String?,
        @SerializedName("RedZone")
        var redZone: Any?,
        @SerializedName("RefereeID")
        var refereeID: Int?,
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
        @SerializedName("TimeRemaining")
        var timeRemaining: Any?,
        @SerializedName("Week")
        var week: Int?,
        @SerializedName("YardLine")
        var yardLine: Any?,
        @SerializedName("YardLineTerritory")
        var yardLineTerritory: Any?
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