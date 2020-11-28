package com.mattg.pickem.ui.pools.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattg.pickem.R
import com.mattg.pickem.models.firebase.User
import com.mattg.pickem.models.firebase.WinnerItem
import com.mattg.pickem.ui.pools.viewModel.PoolViewModel
import com.mattg.pickem.ui.pools.adapters.PoolPlayerListAdapter
import com.mattg.pickem.ui.pools.adapters.WinnersAdapter
import com.mattg.pickem.ui.home.adapters.InviteAdapter
import com.mattg.pickem.utils.*
import kotlinx.android.synthetic.main.dialog_choose_week.*
import kotlinx.android.synthetic.main.fragment_pool_detail.*
import kotlinx.android.synthetic.main.invite_search_dialog.*
import timber.log.Timber


class PoolDetailFragment : Fragment() {

    private lateinit var poolViewModel: PoolViewModel
    private lateinit var clickListener: PoolPlayerListClickListener
    private lateinit var picksClickListener: PicksClickListener
    private lateinit var inviteClickListener: RecyclerClickListener
    private lateinit var currentWeek: String
    private lateinit var lastWeek: String
    private var currentId: String? = null
    val args: PoolDetailFragmentArgs by navArgs()
    private var werePicksPicked = false


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         currentWeek = SharedPrefHelper.getWeekFromPrefs(requireContext()).toString()
         lastWeek = SharedPrefHelper.getLastOrCurrentWeekFromPrefs(requireContext()).toString()


        getPoolDetails()
        observeViewModel()


        tv_pool_detail_upcoming_week.text = "Can still submit picks for: $currentWeek"
        tv_pool_detail_week.text = "Current Week is: $lastWeek"
        if (werePicksPicked) {
            submitPicks()
        }
        werePicksPicked = false

//        if (poolViewModel.currentPoolOwnerId.value == poolViewModel.user.uid) {
//            Timber.i("NEWSCREEN this person is the pool owner can do more logic for managers only here")
//            btn_get_scores.apply {
//                visibility = View.VISIBLE
//                setOnClickListener {
//                    if (lastWeek != null) {
//                        getScoresDialog(lastWeek)
//                    }
//                }
//            }
//        }

        btn_invite_players.setOnClickListener {

            if (poolViewModel.currentPool.value != null) {
                showInviteDialog()
            } else {
                Toast.makeText(requireContext(), "Select a pool", Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun getScoresDialog(lastWeek: String){
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
        if(editText.text.toString().toInt() > 0){
            val weekInt = lastWeek.filter { it.isDigit() }.trim().toInt()
            getScores(weekInt, lastWeek, editText.text.toString().toInt())
            dialog.dismiss()
        } else {
            Toast.makeText(requireContext(), "Please enter a valid score", Toast.LENGTH_SHORT).show()
        }


        }
        dialog.show()
    }

    private fun getScores(week: Int, lastWeek: String, score: Int){
        poolViewModel.getScoresForWeek(week)
        poolViewModel.finalScoresFromWeek.observe(viewLifecycleOwner){
            if(it != null){
                poolViewModel.decideWinner(lastWeek, poolViewModel.currentPool.value!!, score)
                poolViewModel.playerScoresCalculatedList.observe(viewLifecycleOwner){ scoresList ->
                    if(it != null){
                        displayWinner(scoresList, lastWeek, score)
                    }
                }
            }
        }
    }

    private fun displayWinner(inputList: ArrayList<Pair<Pair<String, String>, Pair<Int, Int>>>, lastWeek: String, finalScore: Int) {
        val list = inputList.sortedByDescending { it.second.first }
        Timber.i("+++++++++list of picks sorted is $list")//this sorts by the number correct
        val highScore = list.first().second.first
        Timber.i("++++++++highScore is $highScore")//this gets the number correct
        val highScoreTiesList = ArrayList<Pair<Pair<String,String>, Pair<Int, Int>>>()
        val data = HashMap<String, Any>()
        for(item in inputList){
            if (item.second.first == highScore){
                highScoreTiesList.add(item)
            }
        }
        Timber.i("+++++++highScoresTiesList = $highScoreTiesList")
        if(highScoreTiesList.size == 1){
            val item = list.first()
            Toast.makeText(requireContext(), "The winner is ${item.first.first}", Toast.LENGTH_SHORT).show()
            data["playerId"] = item.first.second
            data["playerName"] = item.first.first
            data["week"] = lastWeek
            poolViewModel.updateWinners(data)
            return
        }
        if(highScoreTiesList.size > 1){
            if(highScoreTiesList.size == 2){
                val firstPlayer = highScoreTiesList[0]
                val secondPlayer = highScoreTiesList[1]
                Timber.i("++++++++firstPlayer is ${firstPlayer.first} with ${firstPlayer.second.second} points")
                Timber.i("++++++++secondPlayer is ${secondPlayer.first} with ${secondPlayer.second.second} points")
                val winner =
                        getWinnerByFinalPoints(firstPlayer.first.first, firstPlayer.second.second,
                                secondPlayer.first.first, secondPlayer.second.second, finalScore)
                //if we get a tie lets see what the names show as
                if(winner == "TIE") {
                    val nameOne = firstPlayer.first.first
                    val nameTwo = secondPlayer.first.first
                    val tiedWinner = "$nameOne and $nameTwo tied!"

                    val data = HashMap<String, Any>()
                    data["playerId"] = "TIE"
                    data["playerName"] = tiedWinner
                    data["week"] = lastWeek
                    Toast.makeText(requireContext(), "The winner is $tiedWinner", Toast.LENGTH_SHORT).show()
                    poolViewModel.updateWinners(data)
                    return
                }

                Toast.makeText(requireContext(), "The winner is $winner", Toast.LENGTH_SHORT).show()


                for (item in highScoreTiesList){
                    if (item.first.first.trim() == winner.trim()){

                        data["playerId"] = item.first.second
                        data["playerName"] = item.first.first
                        data["week"] = lastWeek

                        poolViewModel.updateWinners(data)
                        return
                    }
                }

            }
        }
        //here the list is 1 and we have a clear winner
        else
        Toast.makeText(requireContext(), "The winner is ${list.first().first}", Toast.LENGTH_SHORT).show()
    }

    private fun populateRecycler(list: ArrayList<User>?) {
        if(!list.isNullOrEmpty()){
            showInviteRecycler()
            hidePlayersRecycler()
            val recycler = rv_invitations
            inviteClickListener = RecyclerClickListener { user, _, id ->
                //  sendInvite(user, id)
                poolViewModel.sendInvitation(id)
                list.remove(user)
                Toast.makeText(requireContext(), "Invitation sent to ${user.name}", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "No user found with that email", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showInviteDialog() {
        val invitationDialog = Dialog(requireContext())
        invitationDialog.apply {
            setContentView(R.layout.invite_search_dialog)
            btn_invite_search.setOnClickListener {
                //clicked search, change visibility here
                if (et_email_search.text.toString().isBlank()) {
                    Toast.makeText(requireContext(), "Please enter an email", Toast.LENGTH_SHORT).show()
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

    private fun showInviteRecycler() {
        val recycler = rv_invitations
        recycler.visibility = View.VISIBLE
    }
    private fun showPlayersRecycler() {
        val recycler = rv_pool_players
        recycler.visibility = View.VISIBLE
    }
    private fun hideInviteRecycler(){
        val recycler = rv_invitations
        recycler.visibility = View.INVISIBLE
    }

    private fun hidePlayersRecycler() {
        val recycler = rv_pool_players
        recycler.visibility = View.INVISIBLE
    }

    private fun getPoolDetails() {
        //use id to get list of players
        currentId = poolViewModel.currentPool.value.toString()

        if (currentId != null) {
            poolViewModel.getPoolPlayers(currentId!!)
            poolViewModel.getSpecificPool(currentId!!)
            poolViewModel.getPoolPicks(currentId!!)
            poolViewModel.getWinners(currentId!!)

            //for setting the pool owner display
            val poolOwnerName =  poolViewModel.currentPoolOwnerName.value.toString()
            if(poolOwnerName == poolViewModel.user.displayName){
                tv_pool_detail_owner.text = getString(R.string.your_pool_text)
            } else {
                "Owner: $poolOwnerName".also { tv_pool_detail_owner.text = it }
            }

        }
        val poolName = args.poolName
        if (!poolName.isNullOrEmpty()) {
            tv_pool_detail_title.text = poolName
        }


    }

    private fun observeViewModel() {
        poolViewModel.currentPoolPlayers.observe(viewLifecycleOwner) { playerList ->
            setupPoolPlayerListRecycler(playerList)
        }
        poolViewModel.usersList.observe(viewLifecycleOwner) { it1 ->
            populateRecycler(it1)
        }
        poolViewModel.callApiForCurrentWeek()

        poolViewModel.winnersForRecycler.observe(viewLifecycleOwner){ winnerList ->
            populateWinnersRecycler(winnerList)
        }
        poolViewModel.checkIfNeedWinner(lastWeek)
        poolViewModel.needWinners.observe(viewLifecycleOwner){ needWinners ->
            when(needWinners){
                true -> {
                    if (poolViewModel.currentPoolOwnerId.value == poolViewModel.user.uid) {
                        Timber.i("NEWSCREEN this person is the pool owner can do more logic for managers only here")
                        btn_get_scores.apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                getScoresDialog(lastWeek)
                            }
                        }
                    }
                }
                    false -> {

                    }
            }
        }
        /**
         * NEED TO ROUGHLY IN THIS ORDER: call winners list, check if it has a winner for the last week(
         * will need to check weeks in some way with date to determine when this should show a value), like,
         * call this function, wait for return to be checked, then update live data to observe below this function
         * that will trigger the button to be shown, which will trigger the dialog for inputting score,
         * which will trigger a winner to be posted, which will update the initial functions purpose, determining if a winner
         * needs to be chosen, and reset it to no it doesnt, which will update the live data, which will hide the button untill
         * the next time a winner is needed.  NEED TO ACCOUNT FOR, there not being any picks for the week in question, like
         * the pool was started week 10, no picks for anything before week 10. I think just the last week should suffice here,
         * will always only check after one  week has passed.  so is last week, if so is winner for matching last week, if so dont show
         * else show input
         */
    }

    private fun populateWinnersRecycler(winnerList: ArrayList<WinnerItem>?) {
        val recycler = rv_winners
        val winnersAdapter = winnerList?.let { WinnersAdapter(requireContext(), it) }
        val winnersLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply{
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
        val currentPicks = poolViewModel.currentSelectedPicks.value
        currentPicks?.let {
            poolViewModel.addPicksForUserInPool(it, poolViewModel.user.uid)
            Toast.makeText(requireContext(), "Picks added", Toast.LENGTH_LONG).show()
            poolViewModel.resetSelectedPicks()
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


        }

        return true
    }

}