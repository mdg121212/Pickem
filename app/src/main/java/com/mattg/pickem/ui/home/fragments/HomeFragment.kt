package com.mattg.pickem.ui.home.fragments

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mattg.pickem.LoginActivityParse
import com.mattg.pickem.R
import com.mattg.pickem.models.general.Game
import com.mattg.pickem.ui.home.viewModels.HomeViewModel
import com.mattg.pickem.utils.*
import kotlinx.android.synthetic.main.dialog_choose_week.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.info_dialog_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel

    //   private var selectionsString = ""
    private var checkBoxList = ArrayList<RadioGroup>()
    private var pairList = ArrayList<Pair<RadioGroup, TextView>>()
    private var gamesCount = 0
    private var currentDateToSaveForCache: String? = null
    private var weekToCheck: String? = null
    private var showExpertPicks: Boolean = false
    var myViewGroup: ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        // myViewGroup = container
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //create a list to iterate over button groups
        //myViewGroup = view.container
        myViewGroup = view.cl_home_fragment
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
            tv_directions.visibility = View.GONE
            emptyHomeScreenButtonClick()
            checkGameCount()
        }
        button_picks.setOnClickListener {
            setPicksButtonFunction()
        }

        floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivityParse::class.java)
            startActivity(intent)
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

    /**
     * Saves the current picks object to the device Room database for text, email, and
     * re-usability (sending multiple variants of picks for any week)
     */
    private fun savePicksToDatabase(picks: String, picksFormatted: String) {
        val pick = formatForPickDatabase(requireContext(), picks, picksFormatted)
        homeViewModel.savePickToDatabase(pick)
    }


    private fun handleDate() {
        //calender instance with get first field retrieves the correct year
        val calender = Calendar.getInstance()
        val year = calender.get(1)
        // weekToCheck = checkDate(getDate(), year, homeViewModel, requireContext())
        weekToCheck = checkDate(getDate(), 2020, homeViewModel, requireContext())
    }


    private fun checkGameCount() {
        homeViewModel.gameCount.observe(viewLifecycleOwner) {
            gamesCount = it
        }
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

    private fun showPercentageInfoDialog(percent: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.info_dialog_layout)
        val titleText = dialog.tv_title
        val largerText = dialog.tv_percentage
        largerText.setText("$percent%")
        largerText.setTextColor(ViewUtils.colorForPercentage(percent))
        val close = dialog.dialog_layout
        close.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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


    private fun observeViewModel() {
        homeViewModel.setShouldShowExperts(SharedPrefHelper.getShowExpert(requireContext()))
        homeViewModel.showExperts.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), "Should Show experts == $it", Toast.LENGTH_SHORT)
                    .show()
                when (it) {
                    true -> {
                        showPercentageViews()
                    }
                    false -> {
                        showPercentageViews()
                    }
                    null -> {
                        Timber.d(">>>>>>expert value was null?......")
                    }
                }

            }
        }
        homeViewModel.teamsAndImages.observe(viewLifecycleOwner) {
            if (it != null) {
                setText(it)
                button.visibility = View.GONE
                button_picks.visibility = View.VISIBLE
                et_home_monday_points.visibility = View.VISIBLE
            }
        }
        homeViewModel.dateToCheckWinner.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {

                SharedPrefHelper.addDateToCheckToPrefs(requireContext(), it, weekToCheck!!)
                Timber.i("[[[[[[ just added $it to shared prefs as date to check winner need to delete this when actually choosing the winner")
            }
        }
        homeViewModel.showSpinner.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    showFootBallSpinner()
                }
                false -> {
                    hideFootballSpinner()
                }
            }
        }
        homeViewModel.apiCallErrorMessage.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Timber.d("++++++++error should be toasted $it")
                requireContext().shortToast(it)
                this.view?.let { it1 -> Snackbar.make(it1, "$it", 3) }
            }
        }
    }

    /**
     * TODO Uncomment first version when views actually need to be shown
     *
     */
    private fun showPercentageViews() {
        //  myViewGroup?.forEach { it -> if(it.tag == R.string.expert_percent_view_tag) it.visibility = (View.VISIBLE)  else it.visibility = (View.GONE)  }
        myViewGroup?.forEach { it ->
            if (it.tag != null && it.tag.toString()
                    .equals(("v_percent"))
            ) Timber.d(">>>>>>${it.tag} will show it ") else Timber.d(">>>>>>${it.tag} will not show")
        }
    }


    private fun animation() {
        val animator = ObjectAnimator.ofFloat(iv_football_loading, View.ROTATION, -360f, 0f)
        animator.repeatCount = 1
        animator.duration = 1000
        animator.start()
    }

    private fun showFootBallSpinner() {
        iv_football_loading.apply {
            View.VISIBLE
        }
        animation()
    }

    private fun hideFootballSpinner() {
        iv_football_loading.visibility = View.GONE
    }

    private fun emptyHomeScreenButtonClick() {
        homeViewModel.setShowSpinner(true)
        homeViewModel.setUpcomingWeek(5)
        homeViewModel.upcomingWeek.value?.let { it1 ->
            Timber.d(">>>>>>upcoming week value is %s", it1)
            CoroutineScope(Dispatchers.IO).launch {
                homeViewModel.getMatchupsFiltered(it1)
            }
        }
    }

    private fun getMatchupsForDifferentWeek(week: Int) {
        homeViewModel.setShowSpinner(true)
        tv_home_title.text = "Week $week"
        CoroutineScope(Dispatchers.IO).launch {
            homeViewModel.getMatchupsFiltered(week)
        }
    }

    private fun showWeekDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_choose_week)
        // val editText = dialog.et_week
        //val searchButton = dialog.btn_search_week
        // val cancelButton = dialog.btn_cancel
        dialog.btn_search_week.setOnClickListener {
            if (dialog.et_week.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Enter a week", Toast.LENGTH_SHORT).show()
            }
            if (dialog.et_week.text.toString().toInt() > 17 || dialog.et_week.text.toString()
                    .toInt() < 1
            ) {
                Toast.makeText(requireContext(), "Enter a valid week", Toast.LENGTH_SHORT).show()
            }
            val week = dialog.et_week.text.toString().trim().toInt()
            getMatchupsForDifferentWeek(week)
            dialog.dismiss()
        }
        dialog.btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnu_change_week -> {
                showWeekDialog()
            }
            R.id.mnu_home_logout -> {
                logout()
            }
            R.id.mnu_view_saved_picks -> {
                findNavController().navigate(R.id.action_navigation_home_to_currentList)
            }
            R.id.mnu_home_refresh -> {
                handleDate()
                homeViewModel.clearMatchups()
                homeViewModel.resetHomeScreen()
                findNavController().navigate(R.id.action_navigation_home_self)
            }

            R.id.home_settings -> findNavController().navigate(R.id.action_navigation_home_to_settingsFragment)
        }
        return true
    }


}





