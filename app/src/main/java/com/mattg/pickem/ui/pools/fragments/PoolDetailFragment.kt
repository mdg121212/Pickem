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
import com.mattg.pickem.models.firebase.User
import com.mattg.pickem.models.firebase.WinnerItem
import com.mattg.pickem.ui.home.adapters.InviteAdapter
import com.mattg.pickem.ui.pools.adapters.PoolPlayerListAdapter
import com.mattg.pickem.ui.pools.adapters.WinnersAdapter
import com.mattg.pickem.ui.pools.viewModel.PoolViewModel
import com.mattg.pickem.utils.*
import kotlinx.android.synthetic.main.dialog_choose_week.*
import kotlinx.android.synthetic.main.fragment_pool_detail.*
import kotlinx.android.synthetic.main.invite_search_dialog.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


class PoolDetailFragment : BaseFragment() {

    private lateinit var poolViewModel: PoolViewModel
    private lateinit var clickListener: PoolPlayerListClickListener
    private lateinit var inviteClickListener: RecyclerClickListener
    private lateinit var winnersAdapter: WinnersAdapter
    private lateinit var currentWeek: String
    private lateinit var lastWeek: String
    private lateinit var timeForWinners: String
    private var currentId: String? = null
    private val args: PoolDetailFragmentArgs by navArgs()
    private var werePicksPicked = false
    private lateinit var userId: String
    private lateinit var ownerId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        werePicksPicked = args.werePicksJustSelected

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        poolViewModel = ViewModelProvider(requireActivity()).get(PoolViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pool_detail, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentWeek = SharedPrefHelper.getWeekFromPrefs(requireContext()).toString()
        lastWeek = SharedPrefHelper.getLastOrCurrentWeekFromPrefs(requireContext()).toString()
        timeForWinners = SharedPrefHelper.getDateToCheckFromPrefs(requireContext()).toString()
        poolViewModel.getWeekToCheckWinnerApi()

        poolViewModel.weekToCheckWinnerApi.observe(viewLifecycleOwner) {
            if (it != null) {
                val weekString = "Week ${it.toInt() - 1}"
                Timber.i("**********week adjusted to one is $weekString should be one less than $lastWeek time for winners is $timeForWinners")

            } else {
                poolViewModel.getWeekToCheckWinnerApi()
            }
        }
        /**
         * JUST ABOVE GETS RETURNS FROM API A CURRENT VERSION OF THE CURRENT WEEK AS FAR AS PICKS IN ACTION GO
         * NEED TO CHECK THIS, AND IF IT IS EQUAL TO CURRENTWEEK + 1, THEN IT IS TIME TO CALCULATE SCORES
         * CHECK THE DATE AS IS FIRST, IF ITS THE DATE CHECK THIS, IF THIS = CURRENTWEEK OF PICKS + 1 THEN CHECK SCORES
         */
        getPoolDetails()
        poolViewModel.checkArePicksForWeek(lastWeek)
        poolViewModel.checkIfNeedWinner(lastWeek)
        poolViewModel.callApiForLastCompletedWeek()
        observeViewModel()

        tv_pool_detail_upcoming_week.text = "Can still submit picks for: $currentWeek"
        tv_pool_detail_week.text = "Current Week is: $lastWeek"
        if (werePicksPicked) {
            submitPicks()
        }
        werePicksPicked = false

        btn_invite_players.setOnClickListener {

            if (poolViewModel.currentPool.value != null) {
                showInviteDialog()
            } else {

                requireContext().shortToast("Select a pool")
            }
        }


    }

    private fun getPoolDetails() {
        //use id to get list of players
        currentId = poolViewModel.currentPool.value.toString()
        userId = poolViewModel.user.uid
        ownerId = poolViewModel.currentPoolOwnerId.value.toString()

        if (currentId != null) {
            poolViewModel.getPoolPlayers(currentId!!)
            poolViewModel.getSpecificPool(currentId!!)
            poolViewModel.getPoolPicks(currentId!!, lastWeek)
            poolViewModel.getWinners(currentId!!)
            //for setting the pool owner display
            val poolOwnerName = poolViewModel.currentPoolOwnerName.value.toString()
            if (poolOwnerName == poolViewModel.user.displayName) {
                tv_pool_detail_owner.text = getString(R.string.your_pool_text)
            } else {
                "Owner: $poolOwnerName".also { tv_pool_detail_owner.text = it }
            }
        }
        val poolName = args.poolName
        if (!poolName.isNullOrEmpty()) {
            tv_pool_detail_title.text = poolName
        }

        poolViewModel.callApiForLastCompletedWeek()
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
            if (editText.text.toString().toInt() > 0) {
                val weekInt = lastWeek.filter { it.isDigit() }.trim().toInt()
                getScores(weekInt, lastWeek, editText.text.toString().toInt())
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

    private fun getScores(week: Int, lastWeek: String, score: Int) {
        val dateToGetWinners = SharedPrefHelper.getDateToCheckFromPrefs(requireContext())
        val poolId = poolViewModel.currentPool.value!!
        poolViewModel.getScoresForWeek(week)
        poolViewModel.finalScoresFromWeek.observe(viewLifecycleOwner) {
            if (it != null) {
                poolViewModel.decideWinner(lastWeek, poolId, score)
                poolViewModel.playerScoresCalculatedList.observe(viewLifecycleOwner) { scoresList ->
                    if (it != null) {
                        poolViewModel.displayWinner(scoresList, lastWeek, score)
                        poolViewModel.getWinners(poolId)

                    }

                }
            }
        }

    }

    private fun populateInviteRecycler(list: ArrayList<User>?) {
        if (!list.isNullOrEmpty()) {
            showInviteRecycler()
            hidePlayersRecycler()
            val recycler = rv_invitations
            inviteClickListener = RecyclerClickListener { user, _, id ->
                poolViewModel.sendInvitation(id)
                list.remove(user)
                requireContext().shortToast("Invitation sent to ${user.name}")
                hideInviteRecycler()
                showPlayersRecycler()
            }
            val userAdapter = InviteAdapter(requireContext(), list, inviteClickListener)
            val userLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recycler.apply {
                layoutManager = userLayoutManager
                adapter = userAdapter
            }
        } else {
            requireContext().shortToast("No user found with that email...")
        }

    }

    private fun showInviteDialog() {
        val invitationDialog = Dialog(requireContext())
        invitationDialog.apply {
            setContentView(R.layout.invite_search_dialog)
            btn_invite_search.setOnClickListener {
                //clicked search, change visibility here
                if (et_email_search.text.toString().isBlank()) {
                    requireContext().shortToast("Please enter an email")
                } else {
                    poolViewModel.searchForUsers(et_email_search.text.toString().trim())
                    invitationDialog.dismiss()
                }
            }
            btn_invite_cancel.setOnClickListener {
                invitationDialog.dismiss()
            }
        }.show()

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        updateWinnerRecycler()
    }

    private fun updateWinnerRecycler() {
        val winnersList = poolViewModel.winnersForRecycler.value
        if (winnersList != null) {
            populateWinnersRecycler(winnersList)
        }
    }

    private fun observeViewModel() {
        poolViewModel.currentPoolPlayers.observe(viewLifecycleOwner) { playerList ->
            setupPoolPlayerListRecycler(playerList)
        }
        poolViewModel.usersList.observe(viewLifecycleOwner) { it1 ->
            populateInviteRecycler(it1)
        }
        poolViewModel.callApiForCurrentWeek()

        poolViewModel.winnersForRecycler.observe(viewLifecycleOwner) { winnerList ->
            if (winnerList.isNotEmpty()) {
                populateWinnersRecycler(winnerList)
            }


        }

        poolViewModel.dateToCheck.observe(viewLifecycleOwner) {
            timeForWinners = it
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            if (timeForWinners != "null") {
                val checkDate = formatter.parse(timeForWinners)
                val nowDate = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Date.from(Instant.now())
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
                if (nowDate.hours == checkDate?.hours?.plus(5) && nowDate.date == checkDate.date || nowDate.after(checkDate)) {
                    poolViewModel.getWeekToCheckWinnerApi()
                    poolViewModel.weekToCheckWinnerApi.observe(viewLifecycleOwner) { week ->
                        if (week != null) {
                            val weekString = "Week ${week.toInt() - 1}"
                            //here when the week "it" is equal, it is actually one more hence the -1 above, this means its time to check winners
                            if (weekString == lastWeek) {
                                observeForWinners()
                            }
                        }
                    }

                    poolViewModel.winnerName.observe(viewLifecycleOwner) { name ->
                        if (name != null) {
                            val action = PoolDetailFragmentDirections.actionPoolDetailFragmentToWinnerSplashFragment(name)
                            findNavController().navigate(action)
                            poolViewModel.resetWinnerName()

                        }
                    }
                }

            }
        }

    }

    private fun observeForWinners() {
        poolViewModel.needWinners.observe(viewLifecycleOwner) { needWinners ->
            when (needWinners) {
                true -> {
                    if (poolViewModel.currentPoolOwnerId.value == userId) {
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
                    Timber.i("do not need winners")
                }
            }

        }
    }

    private fun populateWinnersRecycler(winnerList: ArrayList<WinnerItem>?) {
        val recycler = rv_winners
        winnersAdapter = winnerList?.let { WinnersAdapter(requireContext(), it) }!!
        val winnersLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            adapter = winnersAdapter
            layoutManager = winnersLayoutManager
        }
    }

    private fun setupPoolPlayerListRecycler(list: ArrayList<User>) {
        val recycler = rv_pool_players
        clickListener = PoolPlayerListClickListener { _, _, int ->
            when (int) {
                1 -> {

                }
            }
        }

        poolViewModel.currentPoolPlayersPicks.observe(viewLifecycleOwner) {
            for (item in it) {
                val pickSubmitterId = item.playerId
                for (user in list) {
                    val idToMatch = user.userId
                    if (idToMatch == pickSubmitterId) {
                        user.picksIn = true
                    }
                }
            }

            val poolPlayersAdapter = PoolPlayerListAdapter(requireContext(), list, clickListener)
            val poolPlayersLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recycler.apply {
                layoutManager = poolPlayersLayoutManager
                adapter = poolPlayersAdapter
            }
        }
    }


    private fun submitPicks() {
        val dateToPick = SharedPrefHelper.getDateToCheckFromPrefs(requireContext())
        if (dateToPick != null) {
            poolViewModel.submitPicks(dateToPick)
            requireContext().shortToast("Picks Submitted!")

        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.pool_detail_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnu_submit_picks -> {

                val action = PoolDetailFragmentDirections
                        .actionPoolDetailFragmentToCurrentList(true, tv_pool_detail_title.text.toString(), currentWeek)
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