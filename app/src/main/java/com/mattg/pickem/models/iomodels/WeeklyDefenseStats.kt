package com.mattg.pickem.models.iomodels

import com.google.gson.annotations.SerializedName

data class WeeklyDefenseStats(

	@field:SerializedName("BlockedKicks")
	val blockedKicks: Double? = null,

	@field:SerializedName("Safeties")
	val safeties: Double? = null,

	@field:SerializedName("PlayerID")
	val playerID: Int? = null,

	@field:SerializedName("FumblesRecovered")
	val fumblesRecovered: Double? = null,

	@field:SerializedName("FourthDownConversions")
	val fourthDownConversions: Double? = null,

	@field:SerializedName("TwoPointConversionReturns")
	val twoPointConversionReturns: Double? = null,

	@field:SerializedName("WindSpeed")
	val windSpeed: Int? = null,

	@field:SerializedName("FantasyDraftFantasyPointsAllowed")
	val fantasyDraftFantasyPointsAllowed: Double? = null,

	@field:SerializedName("DateTime")
	val dateTime: String? = null,

	@field:SerializedName("InterceptionReturnTouchdowns")
	val interceptionReturnTouchdowns: Double? = null,

	@field:SerializedName("FantasyDefenseID")
	val fantasyDefenseID: Int? = null,

	@field:SerializedName("WideReceiverFantasyPointsAllowed")
	val wideReceiverFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FantasyDraftQuarterbackFantasyPointsAllowed")
	val fantasyDraftQuarterbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FumblesForced")
	val fumblesForced: Double? = null,

	@field:SerializedName("TacklesForLoss")
	val tacklesForLoss: Double? = null,

	@field:SerializedName("FantasyDraftSalary")
	val fantasyDraftSalary: Any? = null,

	@field:SerializedName("ScoringDetails")
	val scoringDetails: List<Any?>? = null,

	@field:SerializedName("OpponentID")
	val opponentID: Int? = null,

	@field:SerializedName("PuntReturns")
	val puntReturns: Double? = null,

	@field:SerializedName("FanDuelKickerFantasyPointsAllowed")
	val fanDuelKickerFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FantasyPointsDraftKings")
	val fantasyPointsDraftKings: Double? = null,

	@field:SerializedName("FantasyDraftTightEndFantasyPointsAllowed")
	val fantasyDraftTightEndFantasyPointsAllowed: Double? = null,

	@field:SerializedName("OffensiveYardsAllowed")
	val offensiveYardsAllowed: Double? = null,

	@field:SerializedName("BlockedKickReturnYards")
	val blockedKickReturnYards: Double? = null,

	@field:SerializedName("Week")
	val week: Int? = null,

	@field:SerializedName("PassesDefended")
	val passesDefended: Double? = null,

	@field:SerializedName("PointsAllowed")
	val pointsAllowed: Double? = null,

	@field:SerializedName("KickReturnYards")
	val kickReturnYards: Double? = null,

	@field:SerializedName("YahooQuarterbackFantasyPointsAllowed")
	val yahooQuarterbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("IsGameOver")
	val isGameOver: Boolean? = null,

	@field:SerializedName("PuntReturnTouchdowns")
	val puntReturnTouchdowns: Double? = null,

	@field:SerializedName("TeamID")
	val teamID: Int? = null,

	@field:SerializedName("PuntReturnYards")
	val puntReturnYards: Double? = null,

	@field:SerializedName("SoloTackles")
	val soloTackles: Double? = null,

	@field:SerializedName("FantasyDataSalary")
	val fantasyDataSalary: Int? = null,

	@field:SerializedName("KickReturnLong")
	val kickReturnLong: Double? = null,

	@field:SerializedName("VictivSalary")
	val victivSalary: Any? = null,

	@field:SerializedName("QuarterbackHits")
	val quarterbackHits: Double? = null,

	@field:SerializedName("Humidity")
	val humidity: Int? = null,

	@field:SerializedName("FantasyPointsAllowed")
	val fantasyPointsAllowed: Double? = null,

	@field:SerializedName("Team")
	val team: String? = null,

	@field:SerializedName("Season")
	val season: Int? = null,

	@field:SerializedName("FanDuelSalary")
	val fanDuelSalary: Int? = null,

	@field:SerializedName("GameKey")
	val gameKey: String? = null,

	@field:SerializedName("Sacks")
	val sacks: Double? = null,

	@field:SerializedName("YahooTightEndFantasyPointsAllowed")
	val yahooTightEndFantasyPointsAllowed: Double? = null,

	@field:SerializedName("HomeOrAway")
	val homeOrAway: String? = null,

	@field:SerializedName("Interceptions")
	val interceptions: Double? = null,

	@field:SerializedName("YahooWideReceiverFantasyPointsAllowed")
	val yahooWideReceiverFantasyPointsAllowed: Double? = null,

	@field:SerializedName("YahooRunningbackFantasyPointsAllowed")
	val yahooRunningbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FantasyDraftWideReceiverFantasyPointsAllowed")
	val fantasyDraftWideReceiverFantasyPointsAllowed: Double? = null,

	@field:SerializedName("Day")
	val day: String? = null,

	@field:SerializedName("PuntReturnLong")
	val puntReturnLong: Double? = null,

	@field:SerializedName("FanDuelPosition")
	val fanDuelPosition: Any? = null,

	@field:SerializedName("SpecialTeamsTouchdowns")
	val specialTeamsTouchdowns: Double? = null,

	@field:SerializedName("GlobalTeamID")
	val globalTeamID: Int? = null,

	@field:SerializedName("DraftKingsFantasyPointsAllowed")
	val draftKingsFantasyPointsAllowed: Double? = null,

	@field:SerializedName("DraftKingsSalary")
	val draftKingsSalary: Int? = null,

	@field:SerializedName("DraftKingsQuarterbackFantasyPointsAllowed")
	val draftKingsQuarterbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("DraftKingsWideReceiverFantasyPointsAllowed")
	val draftKingsWideReceiverFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FantasyDraftRunningbackFantasyPointsAllowed")
	val fantasyDraftRunningbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("ThirdDownConversions")
	val thirdDownConversions: Double? = null,

	@field:SerializedName("FieldGoalReturnTouchdowns")
	val fieldGoalReturnTouchdowns: Double? = null,

	@field:SerializedName("KickReturns")
	val kickReturns: Double? = null,

	@field:SerializedName("AssistedTackles")
	val assistedTackles: Double? = null,

	@field:SerializedName("OpponentPositionRank")
	val opponentPositionRank: Int? = null,

	@field:SerializedName("YahooFantasyPointsAllowed")
	val yahooFantasyPointsAllowed: Double? = null,

	@field:SerializedName("RunningbackFantasyPointsAllowed")
	val runningbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("QuarterbackFantasyPointsAllowed")
	val quarterbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FantasyPointsFanDuel")
	val fantasyPointsFanDuel: Double? = null,

	@field:SerializedName("TouchdownsScored")
	val touchdownsScored: Double? = null,

	@field:SerializedName("KickReturnTouchdowns")
	val kickReturnTouchdowns: Double? = null,

	@field:SerializedName("FanDuelWideReceiverFantasyPointsAllowed")
	val fanDuelWideReceiverFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FanDuelQuarterbackFantasyPointsAllowed")
	val fanDuelQuarterbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FieldGoalReturnYards")
	val fieldGoalReturnYards: Double? = null,

	@field:SerializedName("Stadium")
	val stadium: String? = null,

	@field:SerializedName("InterceptionReturnYards")
	val interceptionReturnYards: Double? = null,

	@field:SerializedName("Temperature")
	val temperature: Int? = null,

	@field:SerializedName("FanDuelFantasyPointsAllowed")
	val fanDuelFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FumbleReturnTouchdowns")
	val fumbleReturnTouchdowns: Double? = null,

	@field:SerializedName("KickerFantasyPointsAllowed")
	val kickerFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FantasyPointsYahoo")
	val fantasyPointsYahoo: Double? = null,

	@field:SerializedName("DraftKingsRunningbackFantasyPointsAllowed")
	val draftKingsRunningbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("OpponentRank")
	val opponentRank: Int? = null,

	@field:SerializedName("YahooSalary")
	val yahooSalary: Int? = null,

	@field:SerializedName("BlockedKickReturnTouchdowns")
	val blockedKickReturnTouchdowns: Double? = null,

	@field:SerializedName("FanDuelRunningbackFantasyPointsAllowed")
	val fanDuelRunningbackFantasyPointsAllowed: Double? = null,

	@field:SerializedName("DraftKingsKickerFantasyPointsAllowed")
	val draftKingsKickerFantasyPointsAllowed: Double? = null,

	@field:SerializedName("PointsAllowedByDefenseSpecialTeams")
	val pointsAllowedByDefenseSpecialTeams: Double? = null,

	@field:SerializedName("DraftKingsPosition")
	val draftKingsPosition: Any? = null,

	@field:SerializedName("DefensiveTouchdowns")
	val defensiveTouchdowns: Double? = null,

	@field:SerializedName("GlobalOpponentID")
	val globalOpponentID: Int? = null,

	@field:SerializedName("FantasyPointsFantasyDraft")
	val fantasyPointsFantasyDraft: Double? = null,

	@field:SerializedName("Opponent")
	val opponent: String? = null,

	@field:SerializedName("FanDuelTightEndFantasyPointsAllowed")
	val fanDuelTightEndFantasyPointsAllowed: Double? = null,

	@field:SerializedName("YahooKickerFantasyPointsAllowed")
	val yahooKickerFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FourthDownAttempts")
	val fourthDownAttempts: Double? = null,

	@field:SerializedName("GlobalGameID")
	val globalGameID: Int? = null,

	@field:SerializedName("YahooPosition")
	val yahooPosition: Any? = null,

	@field:SerializedName("FantasyDraftPosition")
	val fantasyDraftPosition: Any? = null,

	@field:SerializedName("FantasyPoints")
	val fantasyPoints: Double? = null,

	@field:SerializedName("ThirdDownAttempts")
	val thirdDownAttempts: Double? = null,

	@field:SerializedName("FantasyDraftKickerFantasyPointsAllowed")
	val fantasyDraftKickerFantasyPointsAllowed: Double? = null,

	@field:SerializedName("TightEndFantasyPointsAllowed")
	val tightEndFantasyPointsAllowed: Double? = null,

	@field:SerializedName("ScoreID")
	val scoreID: Int? = null,

	@field:SerializedName("Date")
	val date: String? = null,

	@field:SerializedName("DraftKingsTightEndFantasyPointsAllowed")
	val draftKingsTightEndFantasyPointsAllowed: Double? = null,

	@field:SerializedName("FumbleReturnYards")
	val fumbleReturnYards: Double? = null,

	@field:SerializedName("SeasonType")
	val seasonType: Int? = null,

	@field:SerializedName("SackYards")
	val sackYards: Double? = null
)
