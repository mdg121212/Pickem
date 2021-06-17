package com.mattg.pickem.utils

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.get
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mattg.pickem.R
import com.mattg.pickem.db.Pick
import com.mattg.pickem.models.general.Week
import com.mattg.pickem.ui.home.viewModels.HomeViewModel
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

fun startCustomTab(url: String, context: Context) {
    val builder = CustomTabsIntent.Builder()
    val colorInt = Color.BLUE
    builder.setToolbarColor(colorInt)
    val customTabIntent = builder.build()
    customTabIntent.launchUrl(context, Uri.parse(url))
}

fun getWinnerByFinalPoints(firstName: String, firstScore: Int, secondName: String, secondScore: Int, finalScore: Int):
        String {

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

fun getWinnerByFinalPointsMoreThanTwo(inputList: ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>, finalScore: Int):
        String {

    var returnString = ""

    val winnersListUnder =  ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>()
    val winnersListExact = ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>()
    val winnerListOver = ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>()


    for(item in inputList){
        if(item.second.second == finalScore){
            winnersListExact.add(item)
        } else if(item.second.second < finalScore){
            winnersListUnder.add(item)
        } else if(item.second.second > finalScore){
            winnerListOver.add(item)
        }
    }

    if(winnerListOver.size > 0 && winnersListUnder.size == 0 && winnersListExact.size == 0) {
        //only people who went over the score exist
        val noExactWinnerListOver = ArrayList<Pair<String, Int>>()
        for(item in winnerListOver){
            val diff = item.second.second - finalScore
            noExactWinnerListOver.add(Pair(item.first.first, diff))
        }
        noExactWinnerListOver.sortBy { it.second }
        val newMoreFilteredWinnerList= ArrayList<Pair<String, Int>>()
        val measureAgainst = noExactWinnerListOver[0].second
        var winnerCount = 0
        var winnerIndex = 0
        for(item in noExactWinnerListOver){
            val diff = item.second
            if(diff <= measureAgainst){
                winnerCount++
                winnerIndex = noExactWinnerListOver.indexOf(item)
                newMoreFilteredWinnerList.add(item)
            }
        }

        if(winnerCount == 1){
            returnString = noExactWinnerListOver[winnerIndex].first
        } else {
            val winnerListSize = newMoreFilteredWinnerList.size - 1
            var counter = 0
            for(item in newMoreFilteredWinnerList){
                if(counter == winnerListSize){
                    returnString += "and " + item.first + " tied!"
                    return returnString
                }
                returnString += item.first + ", "
                counter++
            }
        }
    } else

    if(winnersListExact.size > 0){
        if(winnersListExact.size == 1) {
            //this is the winner
            returnString = winnersListExact[0].first.first
        } else {
            var count = 0
            returnString = ""
            for(item in winnersListExact){
                if(count == winnersListExact.size-1){
                    returnString += "and ${item.first.first} tied!"
                    break
                }
                returnString += item.first.first + ", "
                count++
            }

        }
        return returnString
    }

    if(winnersListExact.size == 0  && winnersListUnder.size != 0){

        val noExactWinnerList = ArrayList<Pair<String, Int>>()
        //no direct matches to score, will use the second list of players who are beneath the final points
       for(item in winnersListUnder){
           val points = item.second.second
           val difference = finalScore - points
           noExactWinnerList.add(Pair(item.first.first, difference))
       }


            noExactWinnerList.sortBy { it.second }
            val newMoreFilteredWinnerList= ArrayList<Pair<String, Int>>()
            val measureAgainst = noExactWinnerList[0].second
            var winnerCount = 0
            var winnerIndex = 0
            for(item in noExactWinnerList){
                val diff = item.second
                if(diff <= measureAgainst){
                    winnerCount++
                    winnerIndex = noExactWinnerList.indexOf(item)
                    newMoreFilteredWinnerList.add(item)
                }
            }

            if(winnerCount == 1){
                returnString = noExactWinnerList[winnerIndex].first
            } else {
                val winnerListSize = newMoreFilteredWinnerList.size - 1
                var counter = 0
                returnString = ""
                for(item in newMoreFilteredWinnerList){
                    if(counter == winnerListSize){
                        returnString += "and " + item.first + " tied!"
                        return returnString
                    }
                    returnString += item.first + ", "
                    counter++
                }
            }


    }


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

fun Context.shortToast(message: String) {
    Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun checkDate(date: Date, year: Int, model: HomeViewModel, context: Context): String {
    val week = model.getWeekToPick(date)
    //adding the week string to shared prefs to use app wide
    SharedPrefHelper.addWeekToPrefs(context, week.first)
    SharedPrefHelper.addLastOrCurrentWeekToPrefs(context, week.second)
    model.setDate(date, year)
    return week.first;
}

fun generatePickList(
    list: ArrayList<RadioGroup>,
    score: String,
    week: String,
    date: String
): Pair<String, String> {
    val pickList = ArrayList<String>()
    val pickList2 = ArrayList<String>()
    for (group in list) {
        val homeTeamButton = (group[1] as RadioButton)
        val awayTeamButton = (group[2] as RadioButton)
        if (homeTeamButton.isChecked) {
            val text = homeTeamButton.text.toString()
            pickList.add(text)
            pickList2.add(text)
        } else if (awayTeamButton.isChecked) {
            val text = awayTeamButton.text.toString()
            pickList.add(text)
            pickList2.add(text)
        }

    }
    pickList.add(score)
    pickList.add(week)
    pickList.add(date)
    val returnString = pickList.toString()
    val returnFormattedString = pickList2.toString()

    Timber.i(returnString)
    return Pair(returnString, returnFormattedString)
}

fun formatForPickDatabase(context: Context, picks: String, picksFormatted: String): Pick {
    val saveString = picks.replace("[", "").replace("]", "")

    val splitString = saveString.split(",")
    val objectList = splitString.toList()
    val listSize = objectList.size
    val weekString = objectList[listSize - 2]
    val finalPoints = objectList[listSize - 3]
    val dateString = objectList[listSize - 1]

    val subList = objectList.subList(0, listSize - 3)
    Timber.i(
            "$weekString = variable to save for week.\n$finalPoints = final points.\n$dateString = date to save.\n${
                subList.toString().trim()
            } = picks to save."
    )

    return Pick(
            weekString.trim(),
            "name for now",
            subList.toString().trim(),
            picksFormatted,
            finalPoints.trim(),
            SharedPrefHelper.getDateToCheckFromPrefs(context) ?: "none"
    )
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




