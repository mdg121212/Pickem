package com.mattg.pickem.models.iomodels

import com.google.gson.annotations.SerializedName

/**
 * This wrapper contains a list of most pertinent weekly game stats by team
 */
data class TeamGameStats(

	@field:SerializedName("TeamGameStats")
	val teamGameStats: List<TeamGameStatsItem?>? = null
)

/**
 * A list of statitistics relevant to any given team on a weekly basis
 */
data class TeamGameStatsItem(

	@field:SerializedName("Safeties")
	val safeties: Int? = null,

	@field:SerializedName("OpponentPassingYards")
	val opponentPassingYards: Int? = null,

	@field:SerializedName("FourthDownConversions")
	val fourthDownConversions: Int? = null,

	@field:SerializedName("OpponentPassesDefended")
	val opponentPassesDefended: Int? = null,

	@field:SerializedName("GoalToGoPercentage")
	val goalToGoPercentage: Double? = null,

	@field:SerializedName("OpponentTacklesForLoss")
	val opponentTacklesForLoss: Int? = null,

	@field:SerializedName("TwoPointConversionReturns")
	val twoPointConversionReturns: Int? = null,

	@field:SerializedName("WindSpeed")
	val windSpeed: Int? = null,

	@field:SerializedName("ExtraPointRushingAttempts")
	val extraPointRushingAttempts: Int? = null,

	@field:SerializedName("PassingDropbacks")
	val passingDropbacks: Int? = null,

	@field:SerializedName("OpponentFieldGoalsMade")
	val opponentFieldGoalsMade: Int? = null,

	@field:SerializedName("Takeaways")
	val takeaways: Int? = null,

	@field:SerializedName("OpponentQuarterbackHitsPercentage")
	val opponentQuarterbackHitsPercentage: Double? = null,

	@field:SerializedName("GoalToGoConversions")
	val goalToGoConversions: Int? = null,

	@field:SerializedName("Fumbles")
	val fumbles: Int? = null,

	@field:SerializedName("OpponentThirdDownAttempts")
	val opponentThirdDownAttempts: Int? = null,

	@field:SerializedName("Kickoffs")
	val kickoffs: Int? = null,

	@field:SerializedName("InterceptionReturnTouchdowns")
	val interceptionReturnTouchdowns: Int? = null,

	@field:SerializedName("OpponentPuntYards")
	val opponentPuntYards: Int? = null,

	@field:SerializedName("FumblesForced")
	val fumblesForced: Int? = null,

	@field:SerializedName("KickoffTouchbacks")
	val kickoffTouchbacks: Int? = null,

	@field:SerializedName("OpponentRushingYardsPerAttempt")
	val opponentRushingYardsPerAttempt: Double? = null,

	@field:SerializedName("PassingAttempts")
	val passingAttempts: Int? = null,

	@field:SerializedName("OpponentPassingInterceptions")
	val opponentPassingInterceptions: Int? = null,

	@field:SerializedName("PassingInterceptions")
	val passingInterceptions: Int? = null,

	@field:SerializedName("OpponentFirstDownsByRushing")
	val opponentFirstDownsByRushing: Int? = null,

	@field:SerializedName("OpponentFumbles")
	val opponentFumbles: Int? = null,

	@field:SerializedName("BlockedKickReturnYards")
	val blockedKickReturnYards: Int? = null,

	@field:SerializedName("Week")
	val week: Int? = null,

	@field:SerializedName("OpponentRushingTouchdowns")
	val opponentRushingTouchdowns: Int? = null,

	@field:SerializedName("OpponentTimeOfPossessionSeconds")
	val opponentTimeOfPossessionSeconds: Int? = null,

	@field:SerializedName("OpponentBlockedKickReturnYards")
	val opponentBlockedKickReturnYards: Int? = null,

	@field:SerializedName("OpponentPasserRating")
	val opponentPasserRating: Double? = null,

	@field:SerializedName("ScoreOvertime")
	val scoreOvertime: Int? = null,

	@field:SerializedName("SoloTackles")
	val soloTackles: Int? = null,

	@field:SerializedName("OpponentPuntNetYards")
	val opponentPuntNetYards: Int? = null,

	@field:SerializedName("Humidity")
	val humidity: Int? = null,

	@field:SerializedName("QuarterbackHits")
	val quarterbackHits: Int? = null,

	@field:SerializedName("OpponentPuntNetAverage")
	val opponentPuntNetAverage: Double? = null,

	@field:SerializedName("Team")
	val team: String? = null,

	@field:SerializedName("Season")
	val season: Int? = null,

	@field:SerializedName("PlayingSurface")
	val playingSurface: String? = null,

	@field:SerializedName("PasserRating")
	val passerRating: Double? = null,

	@field:SerializedName("GoalToGoAttempts")
	val goalToGoAttempts: Int? = null,

	@field:SerializedName("OpponentOffensiveYardsPerPlay")
	val opponentOffensiveYardsPerPlay: Double? = null,

	@field:SerializedName("OpponentSacks")
	val opponentSacks: Int? = null,

	@field:SerializedName("OpponentSackYards")
	val opponentSackYards: Int? = null,

	@field:SerializedName("OpponentTwoPointConversionReturns")
	val opponentTwoPointConversionReturns: Int? = null,

	@field:SerializedName("HomeOrAway")
	val homeOrAway: String? = null,

	@field:SerializedName("OpponentInterceptionReturnTouchdowns")
	val opponentInterceptionReturnTouchdowns: Int? = null,

	@field:SerializedName("QuarterbackHitsDifferential")
	val quarterbackHitsDifferential: Int? = null,

	@field:SerializedName("OpponentFumblesRecovered")
	val opponentFumblesRecovered: Int? = null,

	@field:SerializedName("OpponentTimeOfPossession")
	val opponentTimeOfPossession: String? = null,

	@field:SerializedName("RedZoneAttempts")
	val redZoneAttempts: Int? = null,

	@field:SerializedName("OpponentPenaltyYards")
	val opponentPenaltyYards: Int? = null,

	@field:SerializedName("FumblesLost")
	val fumblesLost: Int? = null,

	@field:SerializedName("PassingYardsPerCompletion")
	val passingYardsPerCompletion: Double? = null,

	@field:SerializedName("ExtraPointPassingAttempts")
	val extraPointPassingAttempts: Int? = null,

	@field:SerializedName("TimeOfPossession")
	val timeOfPossession: String? = null,

	@field:SerializedName("OpponentFieldGoalAttempts")
	val opponentFieldGoalAttempts: Int? = null,

	@field:SerializedName("PassingYardsPerAttempt")
	val passingYardsPerAttempt: Double? = null,

	@field:SerializedName("GlobalTeamID")
	val globalTeamID: Int? = null,

	@field:SerializedName("OpponentTacklesForLossPercentage")
	val opponentTacklesForLossPercentage: Double? = null,

	@field:SerializedName("OpponentKickReturns")
	val opponentKickReturns: Int? = null,

	@field:SerializedName("RushingAttempts")
	val rushingAttempts: Int? = null,

	@field:SerializedName("KickoffsInEndZone")
	val kickoffsInEndZone: Int? = null,

	@field:SerializedName("OpponentRushingYards")
	val opponentRushingYards: Int? = null,

	@field:SerializedName("KickReturns")
	val kickReturns: Int? = null,

	@field:SerializedName("FieldGoalAttempts")
	val fieldGoalAttempts: Int? = null,

	@field:SerializedName("OpponentKickReturnLong")
	val opponentKickReturnLong: Int? = null,

	@field:SerializedName("OpponentKickReturnTouchdowns")
	val opponentKickReturnTouchdowns: Int? = null,

	@field:SerializedName("OpponentFumbleReturnYards")
	val opponentFumbleReturnYards: Int? = null,

	@field:SerializedName("OpponentKickReturnYards")
	val opponentKickReturnYards: Int? = null,

	@field:SerializedName("PassingTouchdowns")
	val passingTouchdowns: Int? = null,

	@field:SerializedName("FieldGoalsHadBlocked")
	val fieldGoalsHadBlocked: Int? = null,

	@field:SerializedName("OpponentFieldGoalsHadBlocked")
	val opponentFieldGoalsHadBlocked: Int? = null,

	@field:SerializedName("KickReturnTouchdowns")
	val kickReturnTouchdowns: Int? = null,

	@field:SerializedName("OpponentExtraPointsHadBlocked")
	val opponentExtraPointsHadBlocked: Int? = null,

	@field:SerializedName("OpponentFieldGoalReturnYards")
	val opponentFieldGoalReturnYards: Int? = null,

	@field:SerializedName("Score")
	val score: Int? = null,

	@field:SerializedName("InterceptionReturns")
	val interceptionReturns: Int? = null,

	@field:SerializedName("FieldGoalReturnYards")
	val fieldGoalReturnYards: Int? = null,

	@field:SerializedName("OpponentFourthDownConversions")
	val opponentFourthDownConversions: Int? = null,

	@field:SerializedName("OpponentPuntReturnLong")
	val opponentPuntReturnLong: Int? = null,

	@field:SerializedName("InterceptionReturnYards")
	val interceptionReturnYards: Int? = null,

	@field:SerializedName("OpponentAssistedTackles")
	val opponentAssistedTackles: Int? = null,

	@field:SerializedName("FumbleReturnTouchdowns")
	val fumbleReturnTouchdowns: Int? = null,

	@field:SerializedName("OpponentTimesSackedYards")
	val opponentTimesSackedYards: Int? = null,

	@field:SerializedName("OpponentGoalToGoConversions")
	val opponentGoalToGoConversions: Int? = null,

	@field:SerializedName("TeamName")
	val teamName: String? = null,

	@field:SerializedName("OpponentPuntAverage")
	val opponentPuntAverage: Double? = null,

	@field:SerializedName("OpponentPuntReturns")
	val opponentPuntReturns: Int? = null,

	@field:SerializedName("FirstDownsByPassing")
	val firstDownsByPassing: Int? = null,

	@field:SerializedName("PuntsHadBlocked")
	val puntsHadBlocked: Int? = null,

	@field:SerializedName("FirstDowns")
	val firstDowns: Int? = null,

	@field:SerializedName("OffensiveYardsPerPlay")
	val offensiveYardsPerPlay: Double? = null,

	@field:SerializedName("GlobalGameID")
	val globalGameID: Int? = null,

	@field:SerializedName("DayOfWeek")
	val dayOfWeek: String? = null,

	@field:SerializedName("OpponentInterceptionReturnYards")
	val opponentInterceptionReturnYards: Int? = null,

	@field:SerializedName("ThirdDownAttempts")
	val thirdDownAttempts: Int? = null,

	@field:SerializedName("OpponentTimesSackedPercentage")
	val opponentTimesSackedPercentage: Double? = null,

	@field:SerializedName("Giveaways")
	val giveaways: Int? = null,

	@field:SerializedName("ScoreID")
	val scoreID: Int? = null,

	@field:SerializedName("PuntNetAverage")
	val puntNetAverage: Double? = null,

	@field:SerializedName("OpponentOffensiveYards")
	val opponentOffensiveYards: Int? = null,

	@field:SerializedName("TacklesForLossDifferential")
	val tacklesForLossDifferential: Int? = null,

	@field:SerializedName("TacklesForLossPercentage")
	val tacklesForLossPercentage: Double? = null,

	@field:SerializedName("SeasonType")
	val seasonType: Int? = null,

	@field:SerializedName("OpponentQuarterbackHits")
	val opponentQuarterbackHits: Int? = null,

	@field:SerializedName("ThirdDownPercentage")
	val thirdDownPercentage: Double? = null,

	@field:SerializedName("RushingYards")
	val rushingYards: Int? = null,

	@field:SerializedName("TimesSackedYards")
	val timesSackedYards: Int? = null,

	@field:SerializedName("PuntAverage")
	val puntAverage: Double? = null,

	@field:SerializedName("OpponentPassingDropbacks")
	val opponentPassingDropbacks: Int? = null,

	@field:SerializedName("BlockedKicks")
	val blockedKicks: Int? = null,

	@field:SerializedName("OpponentFirstDownsByPenalty")
	val opponentFirstDownsByPenalty: Int? = null,

	@field:SerializedName("OpponentPunts")
	val opponentPunts: Int? = null,

	@field:SerializedName("FumblesRecovered")
	val fumblesRecovered: Int? = null,

	@field:SerializedName("RushingTouchdowns")
	val rushingTouchdowns: Int? = null,

	@field:SerializedName("OpponentSoloTackles")
	val opponentSoloTackles: Int? = null,

	@field:SerializedName("OpponentTacklesForLossDifferential")
	val opponentTacklesForLossDifferential: Int? = null,

	@field:SerializedName("DateTime")
	val dateTime: String? = null,

	@field:SerializedName("ScoreQuarter1")
	val scoreQuarter1: Int? = null,

	@field:SerializedName("Penalties")
	val penalties: Int? = null,

	@field:SerializedName("RedZoneConversions")
	val redZoneConversions: Int? = null,

	@field:SerializedName("OpponentExtraPointKickingAttempts")
	val opponentExtraPointKickingAttempts: Int? = null,

	@field:SerializedName("OpponentFumbleReturnTouchdowns")
	val opponentFumbleReturnTouchdowns: Int? = null,

	@field:SerializedName("ScoreQuarter4")
	val scoreQuarter4: Int? = null,

	@field:SerializedName("OpponentTakeaways")
	val opponentTakeaways: Int? = null,

	@field:SerializedName("ScoreQuarter2")
	val scoreQuarter2: Int? = null,

	@field:SerializedName("OpponentRedZoneAttempts")
	val opponentRedZoneAttempts: Int? = null,

	@field:SerializedName("OpponentKickoffTouchbacks")
	val opponentKickoffTouchbacks: Int? = null,

	@field:SerializedName("OpponentBlockedKicks")
	val opponentBlockedKicks: Int? = null,

	@field:SerializedName("ScoreQuarter3")
	val scoreQuarter3: Int? = null,

	@field:SerializedName("TacklesForLoss")
	val tacklesForLoss: Int? = null,

	@field:SerializedName("TimesSackedPercentage")
	val timesSackedPercentage: Double? = null,

	@field:SerializedName("OpponentID")
	val opponentID: Int? = null,

	@field:SerializedName("OpponentFourthDownAttempts")
	val opponentFourthDownAttempts: Int? = null,

	@field:SerializedName("PuntReturns")
	val puntReturns: Int? = null,

	@field:SerializedName("OpponentInterceptionReturns")
	val opponentInterceptionReturns: Int? = null,

	@field:SerializedName("QuarterbackSacksDifferential")
	val quarterbackSacksDifferential: Int? = null,

	@field:SerializedName("OpponentTimesSacked")
	val opponentTimesSacked: Int? = null,

	@field:SerializedName("ExtraPointRushingConversions")
	val extraPointRushingConversions: Int? = null,

	@field:SerializedName("PassesDefended")
	val passesDefended: Int? = null,

	@field:SerializedName("OpponentPuntReturnTouchdowns")
	val opponentPuntReturnTouchdowns: Int? = null,

	@field:SerializedName("KickReturnYards")
	val kickReturnYards: Int? = null,

	@field:SerializedName("IsGameOver")
	val isGameOver: Boolean? = null,

	@field:SerializedName("OpponentPassingAttempts")
	val opponentPassingAttempts: Int? = null,

	@field:SerializedName("PuntReturnTouchdowns")
	val puntReturnTouchdowns: Int? = null,

	@field:SerializedName("OpponentScoreOvertime")
	val opponentScoreOvertime: Int? = null,

	@field:SerializedName("TeamID")
	val teamID: Int? = null,

	@field:SerializedName("PuntReturnYards")
	val puntReturnYards: Int? = null,

	@field:SerializedName("TeamGameID")
	val teamGameID: Int? = null,

	@field:SerializedName("KickReturnLong")
	val kickReturnLong: Int? = null,

	@field:SerializedName("OpponentRedZonePercentage")
	val opponentRedZonePercentage: Double? = null,

	@field:SerializedName("OpponentPassingYardsPerAttempt")
	val opponentPassingYardsPerAttempt: Double? = null,

	@field:SerializedName("OffensivePlays")
	val offensivePlays: Int? = null,

	@field:SerializedName("OffensiveYards")
	val offensiveYards: Int? = null,

	@field:SerializedName("GameKey")
	val gameKey: String? = null,

	@field:SerializedName("OpponentFirstDowns")
	val opponentFirstDowns: Int? = null,

	@field:SerializedName("Sacks")
	val sacks: Int? = null,

	@field:SerializedName("OpponentGiveaways")
	val opponentGiveaways: Int? = null,

	@field:SerializedName("ExtraPointsHadBlocked")
	val extraPointsHadBlocked: Int? = null,

	@field:SerializedName("OpponentFieldGoalReturnTouchdowns")
	val opponentFieldGoalReturnTouchdowns: Int? = null,

	@field:SerializedName("OverUnder")
	val overUnder: Double? = null,

	@field:SerializedName("OpponentReturnYards")
	val opponentReturnYards: Int? = null,

	@field:SerializedName("OpponentFirstDownsByPassing")
	val opponentFirstDownsByPassing: Int? = null,

	@field:SerializedName("PointSpread")
	val pointSpread: Double? = null,

	@field:SerializedName("OpponentFumblesForced")
	val opponentFumblesForced: Int? = null,

	@field:SerializedName("PassingCompletions")
	val passingCompletions: Int? = null,

	@field:SerializedName("OpponentQuarterbackSacksDifferential")
	val opponentQuarterbackSacksDifferential: Int? = null,

	@field:SerializedName("FieldGoalsMade")
	val fieldGoalsMade: Int? = null,

	@field:SerializedName("OpponentBlockedKickReturnTouchdowns")
	val opponentBlockedKickReturnTouchdowns: Int? = null,

	@field:SerializedName("OpponentExtraPointRushingConversions")
	val opponentExtraPointRushingConversions: Int? = null,

	@field:SerializedName("Day")
	val day: String? = null,

	@field:SerializedName("OpponentRedZoneConversions")
	val opponentRedZoneConversions: Int? = null,

	@field:SerializedName("PuntReturnLong")
	val puntReturnLong: Int? = null,

	@field:SerializedName("ReturnYards")
	val returnYards: Int? = null,

	@field:SerializedName("OpponentGoalToGoAttempts")
	val opponentGoalToGoAttempts: Int? = null,

	@field:SerializedName("OpponentSafeties")
	val opponentSafeties: Int? = null,

	@field:SerializedName("PenaltyYards")
	val penaltyYards: Int? = null,

	@field:SerializedName("OpponentKickoffsInEndZone")
	val opponentKickoffsInEndZone: Int? = null,

	@field:SerializedName("OpponentPuntReturnYards")
	val opponentPuntReturnYards: Int? = null,

	@field:SerializedName("OpponentThirdDownConversions")
	val opponentThirdDownConversions: Int? = null,

	@field:SerializedName("OpponentTimeOfPossessionMinutes")
	val opponentTimeOfPossessionMinutes: Int? = null,

	@field:SerializedName("OpponentFourthDownPercentage")
	val opponentFourthDownPercentage: Double? = null,

	@field:SerializedName("OpponentFumblesLost")
	val opponentFumblesLost: Int? = null,

	@field:SerializedName("OpponentExtraPointKickingConversions")
	val opponentExtraPointKickingConversions: Int? = null,

	@field:SerializedName("ThirdDownConversions")
	val thirdDownConversions: Int? = null,

	@field:SerializedName("FieldGoalReturnTouchdowns")
	val fieldGoalReturnTouchdowns: Int? = null,

	@field:SerializedName("OpponentScoreQuarter4")
	val opponentScoreQuarter4: Int? = null,

	@field:SerializedName("OpponentScoreQuarter3")
	val opponentScoreQuarter3: Int? = null,

	@field:SerializedName("CompletionPercentage")
	val completionPercentage: Double? = null,

	@field:SerializedName("OpponentThirdDownPercentage")
	val opponentThirdDownPercentage: Double? = null,

	@field:SerializedName("AssistedTackles")
	val assistedTackles: Int? = null,

	@field:SerializedName("OpponentScoreQuarter2")
	val opponentScoreQuarter2: Int? = null,

	@field:SerializedName("OpponentCompletionPercentage")
	val opponentCompletionPercentage: Double? = null,

	@field:SerializedName("OpponentScoreQuarter1")
	val opponentScoreQuarter1: Int? = null,

	@field:SerializedName("OpponentTurnoverDifferential")
	val opponentTurnoverDifferential: Int? = null,

	@field:SerializedName("QuarterbackHitsPercentage")
	val quarterbackHitsPercentage: Double? = null,

	@field:SerializedName("OpponentPassingCompletions")
	val opponentPassingCompletions: Int? = null,

	@field:SerializedName("TimesSacked")
	val timesSacked: Int? = null,

	@field:SerializedName("TurnoverDifferential")
	val turnoverDifferential: Int? = null,

	@field:SerializedName("FourthDownPercentage")
	val fourthDownPercentage: Double? = null,

	@field:SerializedName("OpponentTouchdowns")
	val opponentTouchdowns: Int? = null,

	@field:SerializedName("OpponentExtraPointRushingAttempts")
	val opponentExtraPointRushingAttempts: Int? = null,

	@field:SerializedName("ExtraPointKickingConversions")
	val extraPointKickingConversions: Int? = null,

	@field:SerializedName("OpponentKickoffs")
	val opponentKickoffs: Int? = null,

	@field:SerializedName("PuntYards")
	val puntYards: Int? = null,

	@field:SerializedName("OpponentOffensivePlays")
	val opponentOffensivePlays: Int? = null,

	@field:SerializedName("OpponentRushingAttempts")
	val opponentRushingAttempts: Int? = null,

	@field:SerializedName("Punts")
	val punts: Int? = null,

	@field:SerializedName("PuntNetYards")
	val puntNetYards: Int? = null,

	@field:SerializedName("TimeOfPossessionMinutes")
	val timeOfPossessionMinutes: Int? = null,

	@field:SerializedName("Touchdowns")
	val touchdowns: Int? = null,

	@field:SerializedName("OpponentQuarterbackHitsDifferential")
	val opponentQuarterbackHitsDifferential: Int? = null,

	@field:SerializedName("OpponentPuntsHadBlocked")
	val opponentPuntsHadBlocked: Int? = null,

	@field:SerializedName("ExtraPointPassingConversions")
	val extraPointPassingConversions: Int? = null,

	@field:SerializedName("Stadium")
	val stadium: String? = null,

	@field:SerializedName("FirstDownsByRushing")
	val firstDownsByRushing: Int? = null,

	@field:SerializedName("Temperature")
	val temperature: Int? = null,

	@field:SerializedName("OpponentPenalties")
	val opponentPenalties: Int? = null,

	@field:SerializedName("OpponentGoalToGoPercentage")
	val opponentGoalToGoPercentage: Double? = null,

	@field:SerializedName("BlockedKickReturnTouchdowns")
	val blockedKickReturnTouchdowns: Int? = null,

	@field:SerializedName("RedZonePercentage")
	val redZonePercentage: Double? = null,

	@field:SerializedName("RushingYardsPerAttempt")
	val rushingYardsPerAttempt: Double? = null,

	@field:SerializedName("GlobalOpponentID")
	val globalOpponentID: Int? = null,

	@field:SerializedName("Opponent")
	val opponent: String? = null,

	@field:SerializedName("OpponentPassingYardsPerCompletion")
	val opponentPassingYardsPerCompletion: Double? = null,

	@field:SerializedName("TotalScore")
	val totalScore: Int? = null,

	@field:SerializedName("TimeOfPossessionSeconds")
	val timeOfPossessionSeconds: Int? = null,

	@field:SerializedName("FourthDownAttempts")
	val fourthDownAttempts: Int? = null,

	@field:SerializedName("OpponentExtraPointPassingAttempts")
	val opponentExtraPointPassingAttempts: Int? = null,

	@field:SerializedName("Date")
	val date: String? = null,

	@field:SerializedName("PassingYards")
	val passingYards: Int? = null,

	@field:SerializedName("FumbleReturnYards")
	val fumbleReturnYards: Int? = null,

	@field:SerializedName("SackYards")
	val sackYards: Int? = null,

	@field:SerializedName("OpponentPassingTouchdowns")
	val opponentPassingTouchdowns: Int? = null,

	@field:SerializedName("OpponentExtraPointPassingConversions")
	val opponentExtraPointPassingConversions: Int? = null,

	@field:SerializedName("ExtraPointKickingAttempts")
	val extraPointKickingAttempts: Int? = null,

	@field:SerializedName("OpponentScore")
	val opponentScore: Int? = null,

	@field:SerializedName("FirstDownsByPenalty")
	val firstDownsByPenalty: Int? = null
)
