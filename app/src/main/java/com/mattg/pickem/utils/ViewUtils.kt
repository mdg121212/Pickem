package com.mattg.pickem.utils

import android.graphics.Color
import com.mattg.pickem.R

class ViewUtils {

    companion object {
        /**
         * Sets the value for pick percentage text color (red, green, yellow)
         * For color blind, maybe just grey tints?
         */
        fun colorForPercentage(percent: Int): Int {

            return when (percent) {
                in 1..40 -> {
                    Color.RED
                }
                in 41..49 -> {
                    Color.YELLOW
                }
                in 50..59 -> {
                    R.color.appLightGreen
                }
                in 60..100 -> {
                    R.color.appDarkGreen
                }
                else -> Color.BLACK
            }
        }

        fun getImageFromTeam(input: String): Int {
            when (input) {
                "GB" -> {
                    return R.drawable.packers
                }
                "SF" -> {
                    return R.drawable.niners
                }
                "ATL" -> {
                    return R.drawable.falcons
                }
                "BUF" -> {
                    return R.drawable.bills
                }
                "NE" -> {
                    return R.drawable.patriots
                }
                "KC" -> {
                    return R.drawable.cheifs
                }
                "TB" -> {
                    return R.drawable.buccaneers
                }
                "LV" -> {
                    return R.drawable.raiders
                }
                "DEN" -> {
                    return R.drawable.broncos
                }
                "SEA" -> {
                    return R.drawable.seahawks
                }
                "BAL" -> {
                    return R.drawable.ravens
                }
                "IND" -> {
                    return R.drawable.colts
                }
                "HOU" -> {
                    return R.drawable.texans
                }
                "JAX" -> {
                    return R.drawable.jaguars
                }
                "CAR" -> {
                    return R.drawable.panthers
                }
                "DET" -> {
                    return R.drawable.lions
                }
                "MIN" -> {
                    return R.drawable.vikings
                }
                "CHI" -> {
                    return R.drawable.bears
                }
                "TEN" -> {
                    return R.drawable.titans
                }
                "NYG" -> {
                    return R.drawable.giants
                }
                "WAS" -> {
                    return R.drawable.washington
                }
                "LAC" -> {
                    return R.drawable.chargers
                }
                "MIA" -> {
                    return R.drawable.dolphins
                }
                "ARI" -> {
                    return R.drawable.cardinals
                }
                "NO" -> {
                    return R.drawable.saints
                }
                "NYJ" -> {
                    return R.drawable.jets
                }
                "PIT" -> {
                    return R.drawable.steelers
                }
                "DAL" -> {
                    return R.drawable.cowboys
                }
                "PHI" -> {
                    return R.drawable.eagles
                }
                "CLE" -> {
                    return R.drawable.browns
                }
                "LAR" -> {
                    return R.drawable.rams
                }
                "CIN" -> {
                    return R.drawable.bengals
                }

                else -> return R.drawable.item_gradient

            }
        }

    }
}