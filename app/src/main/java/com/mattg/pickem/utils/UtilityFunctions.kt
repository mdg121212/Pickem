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
import com.mattg.pickem.models.Week
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDateTime
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

fun getProgressDrawable(context: Context) : CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f //creates a spinner to display in image before it is loaded
        centerRadius = 50f
        start()
    }

}

fun getWinnerByFinalPoints(firstName: String, firstScore: Int, secondName: String, secondScore: Int, finalScore: Int) : String {

    var returnString = "TIE"

    if(firstScore == secondScore){
        return returnString
    } else
    if(firstScore > finalScore && secondScore < finalScore){
        returnString =  secondName
    }else
    if(secondScore > finalScore && firstScore < finalScore){
        returnString = firstName
    }else
    if(firstScore <= finalScore && secondScore <= finalScore){
        val firstDiff = finalScore - firstScore
        val secondDiff = finalScore - secondScore
        if(firstDiff > secondDiff){
            returnString = secondName
        }
        if(secondDiff > firstDiff){
            returnString = firstName
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

 fun getDate() : Date {
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

private val weeksArray = arrayListOf(
    Week(1, Date(2020, 9, 10)),
    Week(2, Date(2020, 9, 17)),
    Week(3, Date(2020, 9, 24)),
    Week(4, Date(2020, 10, 1)),
    Week(5, Date(2020, 10, 8)),
    Week(6, Date(2020, 10, 18)),
    Week(7, Date(2020, 10, 22)),
    Week(8, Date(2020, 10, 29)),
    Week(9, Date(2020, 11, 5)),
    Week(10, Date(2020, 11, 12)),
    Week(11, Date(2020, 11, 19)),
    Week(12, Date(2020, 11, 26)),
    Week(13, Date(2020, 12, 3)),
    Week(14, Date(2020, 12, 10)),
    Week(15, Date(2020, 12, 17)),
    Week(16, Date(2020, 12, 25)),
    Week(17, Date(2021, 1, 3)),
)

fun setWeek(input: Int) : String {
    val titleText = "Week $input"
    return titleText
}

fun getWeekToPick(date: Date) : String  {
    when {
        date.before(weeksArray[0].startDate) -> {
            val weekString = setWeek(1)
            return weekString
        }
        date.before(weeksArray[1].startDate) && date.after(weeksArray[0].startDate) || date.equals(weeksArray[1].startDate) -> {
            val weekString = setWeek(2)
            return weekString
        }
        date.before(weeksArray[2].startDate)&&date.after(weeksArray[1].startDate)|| date.equals(weeksArray[2].startDate) -> {
            val weekString = setWeek(3)
            return weekString
        }
        date.before(weeksArray[3].startDate)&&date.after(weeksArray[2].startDate)|| date.equals(weeksArray[3].startDate) -> {
            val weekString = setWeek(4)
            return weekString
        }
        date.before(weeksArray[4].startDate)&&date.after(weeksArray[3].startDate)|| date.equals(weeksArray[4].startDate) -> {
            val weekString = setWeek(5)
            return weekString
        }
        date.before(weeksArray[5].startDate)&&date.after(weeksArray[4].startDate)|| date.equals(weeksArray[5].startDate) -> {
            val weekString = setWeek(6)
            return weekString
        }
        date.before(weeksArray[6].startDate)&&date.after(weeksArray[5].startDate)|| date.equals(weeksArray[6].startDate) -> {
            val weekString = setWeek(7)
            return weekString
        }
        date.before(weeksArray[7].startDate)&&date.after(weeksArray[6].startDate)|| date.equals(weeksArray[7].startDate) -> {
            val weekString = setWeek(8)
            return weekString
        }
        date.before(weeksArray[8].startDate)&&date.after(weeksArray[7].startDate)|| date.equals(weeksArray[8].startDate) -> {
            val weekString = setWeek(9)
            return weekString
        }
        date.before(weeksArray[9].startDate)&&date.after(weeksArray[8].startDate)|| date.equals(weeksArray[9].startDate) -> {
            val weekString = setWeek(10)
            return weekString
        }
        date.before(weeksArray[10].startDate)&&date.after(weeksArray[9].startDate)|| date.equals(weeksArray[10].startDate) -> {
            val weekString = setWeek(11)
            return weekString
        }
        date.before(weeksArray[11].startDate)&&date.after(weeksArray[10].startDate)|| date.equals(weeksArray[11].startDate) -> {
            val weekString = setWeek(12)
            return weekString
        }
        date.before(weeksArray[12].startDate)&&date.after(weeksArray[11].startDate)|| date.equals(weeksArray[12].startDate) -> {
            val weekString = setWeek(13)
            return weekString
        }
        date.before(weeksArray[13].startDate)&&date.after(weeksArray[12].startDate)|| date.equals(weeksArray[13].startDate) -> {
            val weekString = setWeek(14)
            return weekString
        }
        date.before(weeksArray[14].startDate)&&date.after(weeksArray[13].startDate)|| date.equals(weeksArray[14].startDate) -> {
            val weekString = setWeek(15)
            return weekString
        }
        date.before(weeksArray[15].startDate)&&date.after(weeksArray[14].startDate)|| date.equals(weeksArray[15].startDate) -> {
            val weekString = setWeek(16)
            return weekString
        }
        date.before(weeksArray[16].startDate)&&date.after(weeksArray[15].startDate)|| date.equals(weeksArray[16].startDate) -> {
            val weekString = setWeek(17)
            return weekString
        }
        else -> return ""

    }
}

 fun getImageFromTeam(input: String) : Int {
    when(input) {
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
        "LAR" ->{
            return R.drawable.rams
        }
        "CIN" -> {
            return R.drawable.bengals
        }

        else -> return R.drawable.item_gradient

    }
}
