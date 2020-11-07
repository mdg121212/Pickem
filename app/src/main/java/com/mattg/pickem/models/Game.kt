package com.mattg.pickem.models

data class Game(val homeTeam: String,
                val awayTeam: String,
                val homeImage: Int,
                val awayImage: Int,
                val game: Int,
                val details: String) {
}