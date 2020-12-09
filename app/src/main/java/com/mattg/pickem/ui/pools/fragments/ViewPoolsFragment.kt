package com.mattg.pickem.ui.pools.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattg.pickem.R
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.Pool
import com.mattg.pickem.ui.home.adapters.InviteRecyclerAdapter
import com.mattg.pickem.ui.home.adapters.UserPoolsAdapter
import com.mattg.pickem.ui.pools.viewModel.PoolViewModel
import com.mattg.pickem.utils.BaseFragment
import com.mattg.pickem.utils.InvitesClickListener
import com.mattg.pickem.utils.SharedPrefHelper
import com.mattg.pickem.utils.UserPoolClickListener
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


        btn_create_pool.setOnClickListener {
            showCreatePoolDialog()
        }

    }

    private fun observeViewModel(){

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
                    Timber.i(",,,,,,,, poolOwner from recycler is $poolOwner and current uid is ${poolViewModel.user.uid}")
                    if (poolViewModel.user.uid == poolOwner) {
                        Timber.i(",,,,,,,,,, true, or is the same owner is fired")
                        deletePoolDialog(poolDocId, poolName, true)
                    } else {
                        Timber.i(",,,,,,,,,, false, or is not the owner is fired")
                        deletePoolDialog(poolDocId, poolName, false)
                    }
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

    private fun deletePoolDialog(poolId: String, poolName: String, isOwner: Boolean) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Pool?")
                .setPositiveButton("Delete") { dialog, _ ->
                    poolViewModel.deletePool(poolId, poolName, isOwner)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
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
            R.id.mnu_pool_settings -> {
                findNavController().navigate(R.id.action_navigation_pools_to_settingsFragment)
            }
        }
        return true
    }

}