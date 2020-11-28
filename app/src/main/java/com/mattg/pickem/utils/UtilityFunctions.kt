package com.mattg.pickem.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.ImageView
import android.widget.RadioButton
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mattg.pickem.R
import com.mattg.pickem.models.general.Week
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*



fun ImageView.setTeamImage(imageString: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions()
            .placeholder(progressDrawable)
            .error(R.mipmap.ic_launcher_round)
    Glide.with(this.context)
            .setDefaultRequestOptions(options)
            .load(imageString)
            .into(this)

}

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f //creates a spinner to display in image before it is loaded
        centerRadius = 50f
        start()
    }

}

fun getWinnerByFinalPoints(firstName: String, firstScore: Int, secondName: String, secondScore: Int, finalScore: Int): String {

    var returnString = "TIE"

    if (firstScore == secondScore) {
        Timber.i("scoretest----it was a tie")
        return returnString

    } else

        if (firstScore > finalScore && secondScore < finalScore) {
            Timber.i("scoretest---- final score is $finalScore firstScore is $firstScore name $firstName, second is $secondScore name $secondName returning $secondName")
            returnString = secondName
            return returnString
        } else

            if (secondScore > finalScore && firstScore < finalScore) {
                Timber.i("scoretest----final score is $finalScore firstScore is $firstScore name $firstName, second is $secondScore name $secondName returning $firstName")
                returnString = firstName
                return returnString
            }
            else
                if (firstScore <= finalScore && secondScore <= finalScore) {
                    val firstDiff = finalScore - firstScore
                    val secondDiff = finalScore - secondScore
                    if (firstDiff > secondDiff) {
                        returnString = secondName
                        return returnString
                    }
                    if (secondDiff > firstDiff) {
                        returnString = firstName
                        return returnString
                    }
                }
    else
        if(firstScore > finalScore && secondScore > finalScore){
            //both are over find difference
            val firstDiff = firstScore - finalScore
            val secondDiff = secondScore - finalScore
            if (firstDiff > secondDiff) {
                returnString = secondName
                return returnString
            }
            if (secondDiff > firstDiff) {
                returnString = firstName
                return returnString
            }
        }
    Timber.i("scoretest---- final score is $finalScore firstScore is $firstScore, second is $secondScore returning $returnString not sure if its correct")
    return returnString
}

fun RadioButton.setTeamImage(imageId: Int) {
    this.setBackgroundResource(imageId)
}

fun hasNetwork(context: Context): Boolean? {
    var isConnected: Boolean? = false // Initial Value
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}

fun getDate(): Date {
    val currentTime = SimpleDateFormat(
            " yyyy-M-dd",
            Locale.US
    ).format(Date())

    Timber.i("TESTINGDATABASE ----> current date is: $currentTime")

    val splitDateString = currentTime.split("-")

    val dateToCheck = Date(
            splitDateString[0].trim().toInt(),
            splitDateString[1].trim().toInt(),
            splitDateString[2].trim().toInt()
    )
    val secondDateToCheck = Date()


    Timber.i("TESTINGDATABASE --------> date to check = $dateToCheck second one = $secondDateToCheck")
    Timber.i(currentTime)
    return dateToCheck

}

private val weekArray: ArrayList<Any>
   = arrayListOf(
            Week(1,Date(2020, 9, 10, 12, 0)),
            Week(2, Date(2020, 9, 17, 12, 0)),
            Week(3, Date(2020, 9, 24, 12,0)),
            Week(4, Date(2020, 10, 1, 12, 0)),
            Week(5, Date(2020, 10, 8, 12, 0)),
            Week(6, Date(2020, 10, 18, 12, 0)),
            Week(7, Date(2020, 10, 22, 12, 0)),
            Week(8, Date(2020, 10, 29, 12, 0)),
            Week(9, Date(2020, 11, 5, 12, 0)),
            Week(10, Date(2020, 11, 12, 12, 0)),
            Week(11, Date(2020, 11, 19, 12, 0)),
            Week(12, Date(2020, 11, 26, 12, 0)),
            Week(13, Date(2020, 12, 3, 12, 0)),
            Week(14, Date(2020, 12, 10, 12, 0)),
            Week(15, Date(2020, 12, 17, 12, 0)),
            Week(16, Date(2020, 12, 25, 12, 0)),
            Week(17, Date(2021, 1, 3, 12, 0)),
    )




