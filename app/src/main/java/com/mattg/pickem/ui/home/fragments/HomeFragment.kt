package com.mattg.pickem.ui.home.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.mattg.pickem.LoginActivity
import com.mattg.pickem.R
import com.mattg.pickem.db.Pick
import com.mattg.pickem.models.Game
import com.mattg.pickem.ui.home.viewModels.HomeViewModel
import com.mattg.pickem.utils.SharedPrefHelper
import com.mattg.pickem.utils.getDate
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
 //   private var selectionsString = ""
    private var checkBoxList = ArrayList<RadioGroup>()
    private var pairList = ArrayList<Pair<RadioGroup, TextView>>()
    private var gamesCount = 0
    private var currentDateToSaveForCache: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //create a list to iterate over button groups
        checkBoxList = arrayListOf(
            rg_gameOne,
            rg_gameTwo,
            rg_gameThree,
            rg_gameFour,
            rg_gameFive,
            rg_gameSix,
            rg_gameSeven,
            rg_gameEight,
            rg_gameNine,
            rg_gameTen,
            rg_gameEleven,
            rg_gameTwelve,
            rg_gameThirteen,
            rg_gameFourteen,
        )
        //list to associate text views with radio groups
        pairList = arrayListOf(
            Pair(rg_gameOne, tv_gameOne),
            Pair(rg_gameTwo, tv_gameTwo),
            Pair(rg_gameThree, tv_gameThree),
            Pair(rg_gameFour, tv_gameFour),
            Pair(rg_gameFive, tv_gameFive),
            Pair(rg_gameSix, tv_gameSix),
            Pair(rg_gameSeven, tv_gameSeven),
            Pair(rg_gameEight, tv_gameEight),
            Pair(rg_gameNine, tv_gameNine),
            Pair(rg_gameTen, tv_gameTen),
            Pair(rg_gameEleven, tv_gameEleven),
            Pair(rg_gameTwelve, tv_gameTwelve),
            Pair(rg_gameThirteen, tv_gameThirteen),
            Pair(rg_gameFourteen, tv_gameFourteen)
        )


        homeViewModel.text.observe(viewLifecycleOwner, {
            tv_home_title.text = it
        })

        handleDate()
        val date = Date()
        currentDateToSaveForCache = date.toString()
        observeViewModel()

        button.setOnClickListener {
            emptyHomeScreenButtonClick()
            checkGameCount()
        }
        button_picks.setOnClickListener {
            setPicksButtonFunction()

        }


    }


    private fun setPicksButtonFunction() {
        if (makeSureAllButtonsChecked(checkBoxList)) {
            if (!et_home_monday_points.text.isNullOrBlank()) {
                //take number and save with results
                val pointsString = et_home_monday_points.text.toString()
                val weekString = tv_home_title.text.toString()
                val finalString = currentDateToSaveForCache?.let { date ->
                    generatePickList(checkBoxList, pointsString, weekString, date)

                }

                if (finalString != null) {
                    savePicksToDatabase(finalString.first, finalString.second)
                    homeViewModel.setPicksString(finalString.first)
                }
                findNavController().navigate(R.id.action_navigation_home_to_currentList)
            } else {
                Toast.makeText(requireContext(), "Fill out points", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(requireContext(), "Didn't check all games", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun savePicksToDatabase(picks: String, picksFormatted: String) {
        val pick = formatForPickDatabase(picks, picksFormatted)
        homeViewModel.savePickToDatabase(pick)
    }

    private fun formatForPickDatabase(picks: String, picksFormatted: String): Pick {
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
            weekString,
            "name for now",
            subList.toString().trim(),
            picksFormatted,
            finalPoints.trim()
        )
    }


    private fun handleDate(){
        Timber.i("TESTINGTESTING-------->DATE ${getDate()}")
        checkDate(getDate())
    }


    private fun checkGameCount() {
        homeViewModel.gameCount.observe(viewLifecycleOwner) {
            gamesCount = it
        }
    }

    private fun checkDate(date: Date) {
        val week = homeViewModel.getWeekToPick(date)
        //adding the week string to shared prefs to use app wide
        SharedPrefHelper.addWeekToPrefs(requireContext(), week.first)
        SharedPrefHelper.addLastOrCurrentWeekToPrefs(requireContext(), week.second)

        homeViewModel.setDate(date)
        Timber.i("Home ViewModel upcoming week value is = ${homeViewModel.upcomingWeek.value}")
    }

    private fun setText(list: ArrayList<Game>) {
        val gamesNumber = homeViewModel.gameCount.value
        //setting text values for list
        var endString = ""
        var listIterationCounter = 0

        for ((count, item) in list.withIndex()) {
            if (listIterationCounter > gamesNumber!!) {
                break
            }
            if (count > (pairList.size - 1)) {
                break
            }
            listIterationCounter++
            val gameText = "${item.homeTeam} ::: ${item.awayTeam}\n"
            endString += gameText

            setUpRadioGroup(
                pairList[count].second,
                checkBoxList[count],
                item.homeTeam,
                item.awayTeam,
                item.homeImage,
                item.awayImage,
                item.details
            )
            Timber.i("setUpRadioGroup called")
            gamesCount = count

        }

    }


    private fun setUpRadioGroup(
        tv: TextView,
        group: RadioGroup,
        teamOne: String,
        teamTwo: String,
        teamOneImage: Int,
        teamTwoImage: Int,
        gameInfo: String
    ) {
        group.visibility = View.VISIBLE
        //by design groups only have 2 elements
        val homeImage = group[0] as ImageView
        val homeButton = group[1] as RadioButton
        val awayButton = group[2] as RadioButton
        val awayImage = group[3] as ImageView

        tv.apply {
            visibility = View.VISIBLE
            text = gameInfo
        }
        homeImage.apply {
            setImageResource(teamOneImage)
            scaleType = ImageView.ScaleType.FIT_CENTER
            visibility = View.VISIBLE
        }
        homeButton.apply {
            text = teamOne
            visibility = View.VISIBLE

        }
        awayImage.apply {
            setImageResource(teamTwoImage)
            scaleType = ImageView.ScaleType.FIT_CENTER
            visibility = View.VISIBLE
        }
        awayButton.apply {
            text = teamTwo
            visibility = View.VISIBLE


        }
    }


    private fun makeSureAllButtonsChecked(list: ArrayList<RadioGroup>): Boolean {
        var count = 0
        for (group in list) {
            Timber.i(group.checkedRadioButtonId.toString())
            if ((group[1] as RadioButton).isChecked || (group[2] as RadioButton).isChecked) {
                count++
            }
        }
        return count == list.size
    }

    private fun generatePickList(
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

    private fun observeViewModel() {
        homeViewModel.teamsAndImages.observe(viewLifecycleOwner) {
            if (it != null) {
                setText(it)
                button.visibility = View.GONE
                button_picks.visibility = View.VISIBLE
                et_home_monday_points.visibility = View.VISIBLE
            }
        }
    }

    private fun emptyHomeScreenButtonClick() {

        homeViewModel.upcomingWeek.value?.let { it1 ->
           CoroutineScope(Dispatchers.IO).launch {
               homeViewModel.getWeekData(2020, it1)
           }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mnu_start_pool -> {
//                navController.navigate(R.id.action_navigation_home_to_poolManagementFragment)
                Toast.makeText(requireContext(), "TODO()", Toast.LENGTH_SHORT).show()
            }
            R.id.mnu_logout -> {
                logout()
            }
            R.id.mnu_view_saved_picks ->{
                val action = HomeFragmentDirections.actionNavigationHomeToCurrentList(false, null)
                findNavController().navigate(R.id.action_navigation_home_to_currentList)
            }
        }
        return true
    }

    private fun logout() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                //user is now signed out
                Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
    }

}
