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




