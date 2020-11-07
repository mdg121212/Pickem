package com.mattg.pickem.network

//Request :
//
//GET /games?fs={gameTime,homeTeam{fullName,nickName,abbr},visitorTeam{fullName,nickName,abbr},stadiumInfo}&s={"$query":{"week.season":2014,"week.seasonType":"REG","week.week":1}}
//
//Response :
interface NFLApi {
}

//with scores    Request :
//
//GET /games?s={"$query":{"week.season":2014,"week.seasonType":"REG","week.week":1}}
//
//Response :