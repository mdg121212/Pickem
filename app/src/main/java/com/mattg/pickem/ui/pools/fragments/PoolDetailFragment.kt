package com.mattg.pickem.ui.pools.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattg.pickem.R
import com.mattg.pickem.parsebackend.models.ParsePoolPlayer
import com.mattg.pickem.parsebackend.models.WinnerItem
import com.mattg.pickem.ui.home.adapters.ParseInviteAdapter
import com.mattg.pickem.ui.pools.adapters.ParsePoolPlayersAdapter
import com.mattg.pickem.ui.pools.adapters.WinnersAdapter
import com.mattg.pickem.ui.pools.viewModel.PoolViewModel
import com.mattg.pickem.utils.BaseFragment
import com.mattg.pickem.utils.RecyclerClickListener
import com.mattg.pickem.utils.SharedPrefHelper
import com.mattg.pickem.utils.shortToast
import com.parse.ParseUser
import kotlinx.android.synthetic.main.dialog_choose_week.*
import kotlinx.android.synthetic.main.fragment_pool_detail.*
import kotlinx.android.synthetic.main.invite_search_dialog.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


class PoolDetailFragment : BaseFragment() {

    private lateinit var poolViewModel: PoolViewModel
    private lateinit var inviteClickListener: RecyclerClickListener
    private lateinit var winnersAdapter: WinnersAdapter
    private lateinit var currentWeek: String
    private lateinit var lastWeek: String
    private lateinit var timeForWinners: String
    private val args: PoolDetailFragmentArgs by navArgs()
    private var werePicksPicked = false
    private lateinit var poolOwnerName: String
    private lateinit var poolId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        werePicksPicked = args.werePicksJustSelected
        poolOwnerName = args.poolOwnerName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        poolViewModel = ViewModelProvider(requireActivity()).get(PoolViewModel::class.java)
        return inflater.inflate(R.layout.fragment_pool_detail, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentWeek = SharedPrefHelper.getWeekFromPrefs(requireContext()).toString()
        Timber.i(",,,,,,,,,,,,,,current week in pools from prefs is $currentWeek")

        lastWeek = SharedPrefHelper.getLastOrCurrentWeekFromPrefs(requireContext()).toString()
        timeForWinners = SharedPrefHelper.getDateToCheckFromPrefs(requireContext()).toString()
        poolViewModel.getWeekToCheckWinnerApi()
        poolId = poolViewModel.currentParsePoolId.value.toString()

        poolViewModel.weekToCheckWinnerApi.observe(viewLifecycleOwner) {
            if (it != null) {
                val weekString = "Week ${it.toInt() - 1}"
                Timber.i("**********week adjusted to one is $weekString should be one less than $lastWeek time for winners is $timeForWinners")
                poolViewModel.setDateToCheck(timeForWinners)
                poolViewModel.checkIfNeedWinner(weekString)
            } else {
                poolViewModel.getWeekToCheckWinnerApi()
            }
        }

        getPoolDetails()

        poolViewModel.checkIfNeedWinner(lastWeek)
        poolViewModel.callApiForLastCompletedWeek()

        observeParsePool()

        tv_pool_detail_upcoming_week.text = "Can still submit picks for: $currentWeek"
        tv_pool_detail_week.text = "Current Week is: $lastWeek"
        if (werePicksPicked) {
            requireContext().shortToast("Your picks were submitted!")
        }
        werePicksPicked = false


        btn_invite_players.setOnClickListener {

            if (poolId != null) {
                showParseInviteDialog()
            } else {

                requireContext().shortToast("Select a pool")
            }
        }

        observeParsePool()
    }

    private fun getPoolDetails() {

        val currentParsePoolId = poolViewModel.currentParsePoolId.value
        Timber.i("**********current parse pool id from detail frag is $currentParsePoolId")

        if (currentParsePoolId != null) {
            Timber.i("*****currentParsePool id was not null")
            poolViewModel.getParsePoolPlayers(currentParsePoolId)
            poolViewModel.getWinners(currentParsePoolId)
            observeParsePool()
        }

        if (poolOwnerName == ParseUser.getCurrentUser().username) {
            tv_pool_detail_owner.text = getString(R.string.your_pool_text)
        } else {
            "Owner: $poolOwnerName".also { tv_pool_detail_owner.text = it }
        }

        val poolName = args.poolName
        if (!poolName.isNullOrEmpty()) {
            tv_pool_detail_title.text = poolName
        }

        poolViewModel.callApiForLastCompletedWeek()
    }


    private fun observeParsePool() {
        poolViewModel.callApiForCurrentWeek()

        poolViewModel.parseUserEmailSearchStrings.observe(viewLifecycleOwner) {
            if (it != null) {
                populateParseInviteRecycler(it)
            }
        }

        poolViewModel.parsePoolPlayers.observe(viewLifecycleOwner) {
            setupParsePlayerRecycler(it)
        }

        poolViewModel.needWinners.observe(viewLifecycleOwner) { needWinners ->
            Timber.i("********observing need winners from parse pool function, its value is $needWinners")
            if (needWinners) {
                Timber.i("********observing need winners was true, about to observe dateToCheck")
                poolViewModel.dateToCheck.observe(viewLifecycleOwner) { dateToCheck ->
                    timeForWinners = dateToCheck
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    Timber.i("**********observing date to check it is $dateToCheck")
                    if (timeForWinners != "null") {
                        val checkDate = formatter.parse(timeForWinners)
                        Timber.i("**********observing date to check it was not null and was formatted to $checkDate")
                        val nowDate =
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                Date.from(Instant.now())
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }
                        Timber.i("*********observing in parse pool, about to check $nowDate vs $checkDate")
                        if (nowDate.hours == checkDate?.hours?.plus(5) || nowDate.date == checkDate?.date || nowDate.after(
                                checkDate
                            )
                        ) {
                            Timber.i("**************about to call winner for week to check api")
                            poolViewModel.weekToCheckWinnerApi.observe(viewLifecycleOwner) { week ->

                                val weekString = "Week ${week.toInt() - 1}"

                                Timber.i("************week observed was not null, formatted it is $weekString checking against $lastWeek")
                                //here when the week "it" is equal, it is actually one more hence the -1 above, this means its time to check winners
                                if (weekString == lastWeek) {

                                    observeForWinners()
                                }

                            }

                            poolViewModel.winnerName.observe(viewLifecycleOwner) { name ->
                                if (name != null) {
                                    val action =
                                        PoolDetailFragmentDirections.actionPoolDetailFragmentToWinnerSplashFragment(
                                            name
                                        )
                                    findNavController().navigate(action)
                                    poolViewModel.resetWinnerName()

                                }
                            }
                        }
                        Timber.i("*********THE DATE WASNT TIME TO GET THE WINNERS YET BIATCH")

                    }
                }
            }
        }

        poolViewModel.parsePoolWinnersList.observe(viewLifecycleOwner) {
            Timber.i("************OBSERVING THE FUCKING WINNERS LIST from parse , its value is $it")
            if (it.isNotEmpty()) {
                populateWinnersRecycler(it)
            }
        }
    }

    private fun getScoresDialog(lastWeek: String) {
        /**
         * NEED TO ATTACH THIS TO THE MONDAY NIGHT DEADLINE FOR THE GAME OVER FOR THE CURRENT WEEK
         * POSSIBLY PASS THIS TIME/DATE FROM HOME VIEW MODEL TO HERE AND CHECK
         */
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_choose_week)
        val editText = dialog.et_week
        editText.hint = "Monday combined score:"
        val submitButton = dialog.btn_search_week
        val cancelButton = dialog.btn_cancel
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        submitButton.setOnClickListener {
            val inputScore = editText.text.toString().toInt()
            if (inputScore > 0) {
                val weekInt = lastWeek.filter { it.isDigit() }.trim().toInt()
                getScores(weekInt, inputScore)
                Timber.i("********submitting $inputScore for $weekInt")
                btn_get_scores.apply {
                    visibility = View.GONE
                }
                dialog.dismiss()
            } else {
                requireContext().shortToast("Please enter a valid score")
            }
        }
        dialog.show()
    }

    private fun getScores(week: Int, score: Int) {

        val poolId = poolViewModel.currentParsePoolId.value
        poolViewModel.getScoresForWeek(week)
        poolViewModel.finalScoresFromWeek.observe(viewLifecycleOwner) {
            Timber.i("************observing final score from week, its value is $it pool id is $poolId")
            if (it != null) {
                if (poolId != null) {
                    Timber.i("************scores and id were not null, about to call decide winner")
                    poolViewModel.decideWinner(
                        "Week 14",
                        poolId,
                        score
                    )
                }
                poolViewModel.playerScoresCalculatedList.observe(viewLifecycleOwner) { scoresList ->
                    Timber.i("**********observing player calculated scores its value is $scoresList")
                    if (poolId != null) {
                        Timber.i("************in calc scores, id was not null, calling get winners")
                        poolViewModel.getWinners(poolId)
                    }
                }
            }
        }

    }

    private fun populateParseInviteRecycler(list: ArrayList<ParseUser>?) {
        if (!list.isNullOrEmpty()) {
            showInviteRecycler()
            hidePlayersRecycler()
            val recycler = rv_invitations
            inviteClickListener = RecyclerClickListener { user, _, _ ->
                poolViewModel.currentParsePoolId.value?.let {
                    poolViewModel.createParseInvite(
                        user.username, user.objectId,
                        ParseUser.getCurrentUser().username, ParseUser.getCurrentUser().objectId,
                        it, poolViewModel.currentParsePoolData.value?.poolName ?: "No Name"
                    )
                }
                list.remove(user)
                requireContext().shortToast("Invitation sent to ${user.username}")
                hideInviteRecycler()
                poolViewModel.clearInviteListForRecycler()
                showPlayersRecycler()
            }
            val userAdapter = ParseInviteAdapter(requireContext(), list, inviteClickListener)
            val userLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recycler.apply {
                layoutManager = userLayoutManager
                adapter = userAdapter
            }
        } else {
            requireContext().shortToast("No users found...")
        }

    }


    private fun showParseInviteDialog() {
        val invitationDialog = Dialog(requireContext())
        invitationDialog.apply {
            setContentView(R.layout.invite_search_dialog)
            btn_invite_search.setOnClickListener {
                //clicked search, change visibility here
                if (et_email_search.text.toString().isBlank()) {
                    requireContext().shortToast("Please enter an email")
                } else {
                    poolViewModel.searchParseUsersToInvite(et_email_search.text.toString().trim())
                    invitationDialog.dismiss()
                }
            }
            btn_invite_cancel.setOnClickListener {
                invitationDialog.dismiss()
            }
        }.show()

        observeParsePool()
    }

    override fun onResume() {
        super.onResume()
        updateWinnerRecycler()
    }

    private fun updateWinnerRecycler() {
        val winnersList = poolViewModel.parsePoolWinnersList.value
        if (winnersList != null) {
            populateWinnersRecycler(winnersList)
        }
    }


    private fun observeForWinners() {
        Timber.i("**********observe for winners called")
        poolViewModel.needWinners.observe(viewLifecycleOwner) { needWinners ->
            Timber.i("*********need winners value is $needWinners")
            when (needWinners) {
                true -> {
                    Timber.i("*********need winners value was true")
                    Timber.i(
                        "*********about to check 2 values ${poolViewModel.currentParsePoolData.value!!.ownerId.trim()} : vs : ${
                            ParseUser.getCurrentUser().email.toString().trim()
                        }"
                    )
                    if (poolViewModel.currentParsePoolData.value!!.ownerId == ParseUser.getCurrentUser().email) {
                        btn_get_scores.apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                getScoresDialog(lastWeek)
                                poolViewModel.setNeedWinners(false)
                            }

                        }
                    }
                }
                false -> {
                    Timber.i("*********need winners value was false")
                    Timber.i("do not need winners")
                }
            }

        }
    }

    private fun populateWinnersRecycler(winnerList: ArrayList<WinnerItem>?) {
        val recycler = rv_winners
        winnersAdapter = winnerList?.let { WinnersAdapter(requireContext(), it) }!!
        val winnersLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            adapter = winnersAdapter
            layoutManager = winnersLayoutManager
        }
    }

    private fun setupParsePlayerRecycler(list: ArrayList<ParsePoolPlayer>) {
        val recycler = rv_pool_players
        val parseAdapter = ParsePoolPlayersAdapter(requireContext(), list)
        val poolPlayersLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            layoutManager = poolPlayersLayoutManager
            adapter = parseAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.pool_detail_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnu_submit_picks -> {
                val action = PoolDetailFragmentDirections
                    .actionPoolDetailFragmentToCurrentList(
                        true,
                        tv_pool_detail_title.text.toString(),
                        currentWeek
                    )
                findNavController().navigate(action)
            }
            R.id.mnu_view_all_picks -> {
                this.onDetach()
                findNavController().navigate(R.id.action_poolDetailFragment_to_poolPlayersPicksFragment)
            }
            R.id.mnu_pool_detail_logout -> logout()
            R.id.pool_detail_settings -> findNavController().navigate(R.id.action_poolDetailFragment_to_settingsFragment)
        }
        return true
    }


    private fun showInviteRecycler() {
        val recycler = rv_invitations
        recycler.visibility = View.VISIBLE
    }

    private fun showPlayersRecycler() {
        val recycler = rv_pool_players
        recycler.visibility = View.VISIBLE
    }

    private fun hideInviteRecycler() {
        val recycler = rv_invitations
        recycler.visibility = View.INVISIBLE
    }

    private fun hidePlayersRecycler() {
        val recycler = rv_pool_players
        recycler.visibility = View.INVISIBLE
    }


}