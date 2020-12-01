package com.mattg.pickem.utils


import org.junit.Assert.*
import org.junit.Test

class UtilityFunctionsKtTest {


    val itemOne = Pair(Pair("playerOne", "null"), Pair(0, 12))
    val itemTwo = Pair(Pair("playerTwo", "null"), Pair(0, 10))
    val itemThree = Pair(Pair("playerThree", "null"), Pair(0, 17))
    val itemFour = Pair(Pair("playerFour", "null"), Pair(0, 14))
    val itemFive = Pair(Pair("playerFive", "null"), Pair(0, 12))
    val itemSix = Pair(Pair("playerSix", "null"), Pair(0, 13))
    val itemSeven =  Pair(Pair("playerSeven", "null"), Pair(0, 12))


    val testListOneWinner = arrayListOf(itemOne, itemTwo, itemThree, itemFour)
    val testListOneTie = arrayListOf(itemOne, itemTwo, itemThree, itemFour, itemFive)
    val testListThreeTie = arrayListOf(itemOne, itemTwo, itemThree, itemFour, itemFive, itemSix, itemSeven)
    val testListAllOverOneWinner = arrayListOf(itemOne, itemThree, itemFour, itemSix)
    val testListAllOverTie = arrayListOf(itemOne, itemThree, itemFour, itemFive, itemSeven)
    //fun getWinnerByFinalPointsMoreThanTwo
    @Test
    fun one_winner(){
        assertEquals("playerOne", getWinnerByFinalPointsMoreThanTwo(testListOneWinner, 13))
        assertEquals("playerFour", getWinnerByFinalPointsMoreThanTwo(testListThreeTie, 15))
    }

    @Test
    fun one_tie(){
        assertEquals("playerOne, and playerFive tied!", getWinnerByFinalPointsMoreThanTwo(testListOneTie, 13))
    }
    @Test
    fun three_way_tie(){
        assertEquals("playerOne, playerFive, and playerSeven tied!", getWinnerByFinalPointsMoreThanTwo(testListThreeTie, 12))

    }

    @Test
    fun one_winner_all_scores_above_final(){
        assertEquals("playerOne", getWinnerByFinalPointsMoreThanTwo(testListAllOverOneWinner, 10))
    }

    @Test
    fun tie_all_scores_over(){
        assertEquals("playerOne, playerFive, and playerSeven tied!", getWinnerByFinalPointsMoreThanTwo(testListAllOverTie, 11))
    }



}