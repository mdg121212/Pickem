package com.mattg.pickem.ui.pools.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.mattg.pickem.LoginActivity
import com.mattg.pickem.R
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.Pool
import com.mattg.pickem.ui.pools.viewModel.PoolViewModel
import com.mattg.pickem.ui.home.adapters.*
import com.mattg.pickem.utils.*
import kotlinx.android.synthetic.main.create_pool_dialog.*
import kotlinx.android.synthetic.main.fragment_view_pools.*
import kotlinx.android.synthetic.main.invite_dialog.*
import timber.log.Timber

class ViewPoolsFragment : BaseFragment() {

    private lateinit var poolViewModel: PoolViewModel
    private var poolIdHolder: String ?= null
    private lateinit var inviteClickListener: InvitesClickListener
    private lateinit var userPoolsClickListener: UserPoolClickListener
    private var currentPool: String = ""
    private var upcomingWeek: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("PoolManagement")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        poolViewModel = ViewModelProvider(requireActivity()).get(PoolViewModel::class.java)

        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pools, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isInvitations = poolViewModel.listenForInvitations()
        if(isInvitations){
            Toast.makeText(requireContext(), "Invitations Pending", Toast.LENGTH_SHORT).show()
        }

        upcomingWeek = SharedPrefHelper.getWeekFromPrefs(requireContext())
        poolViewModel.getUserPools()
        observeViewModel()

      //  testingShitObserveViewModel()

        btn_create_pool.setOnClickListener {
            showCreatePoolDialog()
        }

    }

    private fun testingShitObserveViewModel(){
        poolViewModel.callApiForLastCompletedWeek()
        poolViewModel.callApiForCurrentWeek()
        poolViewModel.apiCallLastCompletedWeek.observe(viewLifecycleOwner){
            Timber.i("TESTINGAPIFROMFRAGMENT-----> observed value for last completed week is: $it")
            poolViewModel.getScoresForWeek(it.toInt())
        }
        poolViewModel.finalScoresFromWeek.observe(viewLifecycleOwner){
            val listOfWinningTeams = it.first
            val finalScore = it.second

        }
        poolViewModel.apiCallCurrentWeek.observe(viewLifecycleOwner){
            Timber.i("TESTINGAPIFROMFRAGMENT-----> observed value for current week is: $it")
        }
    }

    private fun observeViewModel(){
       // poolViewModel.callApiForLastCompletedWeek()

        poolViewModel.areInvites.observe(viewLifecycleOwner){
            if(it == true) {
                poolViewModel.listForInviteRecycler.value?.let { it1 -> generateDialogOfInvites(it1) }
                 }
           }
        poolViewModel.userPoolsList.observe(viewLifecycleOwner){
                setUpPoolRecycler(it)
            }

    }

    private fun showCreatePoolDialog() {
        val createPoolDialog = Dialog(requireContext())
        createPoolDialog.apply {
            setContentView(R.layout.create_pool_dialog)
            btn_create_pool_create.setOnClickListener {
                if(et_pool_name.text.toString().isBlank()){
                    Toast.makeText(requireContext(), "Please name your pool", Toast.LENGTH_SHORT).show()
                } else {
                    poolViewModel.createPool(poolViewModel.user.uid, et_pool_name.text.toString(), upcomingWeek ?: "not working")
                    createPoolDialog.dismiss()
                }
            }
            btn_create_pool_cancel.setOnClickListener {
                createPoolDialog.dismiss()
            }
        }.show()
    }
    private fun generateDialogOfInvites(listForRecycler: ArrayList<Invite>) {
        val inviteDialog = Dialog(requireContext())
        inviteDialog.apply {
            setContentView(R.layout.invite_dialog)
            btn_dialog_invites_close.setOnClickListener {
                inviteDialog.dismiss()
            }
            val recycler = rv_invites
            inviteClickListener = InvitesClickListener { invite, position, delete ->
                when (delete) {
                    1 -> { //accept invite
                        poolViewModel.acceptInvitation(invite.poolId!!, invite.senderId!!, invite.inviteId!!)
                        listForRecycler.remove(invite)
                        listForRecycler.trimToSize()
                        Toast.makeText(requireContext(), "Joined ${invite.poolName}!", Toast.LENGTH_SHORT).show()
                        recycler.adapter?.notifyItemRemoved(position)

                    }
                    2 -> { //decline and delete invite
                        poolViewModel.declineInvitation(invite.inviteId!!)
                        listForRecycler.remove(invite)
                        listForRecycler.trimToSize()
                        Toast.makeText(requireContext(), "Declined to join ${invite.poolName}", Toast.LENGTH_SHORT).show()
                        recycler.adapter?.notifyItemRemoved(position)

                    }
                }
            }

            val inviteAdapter =
                InviteRecyclerAdapter(requireContext(), listForRecycler, inviteClickListener)
            val inviteLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recycler.apply {
                adapter = inviteAdapter
                layoutManager = inviteLayoutManager
            }


        }.show()
    }

    private fun setUpPoolRecycler(list: ArrayList<Pool>) {
        userPoolsClickListener = UserPoolClickListener{ poolDocId, poolOwner, position, buttonInt, poolName, poolOwnerName ->
            when (buttonInt){
                1 -> {
                    setPool(poolDocId, poolOwner, poolName, poolOwnerName)
                }
                2 -> {
                   deletePoolDialog(poolDocId, poolName)
                }
            }
        }
        val recycler = rv_pools
        val poolsAdapter = UserPoolsAdapter(requireContext(), list, userPoolsClickListener )
        val poolsLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            adapter = poolsAdapter
            layoutManager = poolsLayoutManager
        }
    }

    private fun deletePoolDialog(poolId: String, poolName: String){
        AlertDialog.Builder(requireContext()).setTitle("Delete Pool?")
            .setPositiveButton("Delete"){ dialog, _ ->
                poolViewModel.deletePool(poolId, poolName)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.cancel()
            }.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setPool(poolIdToSet: String, poolOwnerId: String, poolName: String, poolOwnerName: String) {
        currentPool = "$poolOwnerId : $poolIdToSet"
        poolIdHolder = poolIdToSet

        poolViewModel.setCurrentPool(poolIdToSet, poolName, poolOwnerId, poolOwnerName)

            val action = ViewPoolsFragmentDirections.actionNavigationDashboardToPoolDetailFragment(
                null,
                poolName)
            findNavController().navigate(action)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.pool_management_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mnu_pool_mng_logout -> {
               logout()
            }
            R.id.mnu_pool_mng_todo -> {

            }
        }
        return true
    }
    private fun logout(){
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