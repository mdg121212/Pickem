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
import com.mattg.pickem.parsebackend.models.ParseInvite
import com.mattg.pickem.parsebackend.models.ParsePool
import com.mattg.pickem.ui.home.adapters.ParseInviteRecyclerAdapter
import com.mattg.pickem.ui.pools.adapters.ParsePoolsAdapter
import com.mattg.pickem.ui.pools.viewModel.PoolViewModel
import com.mattg.pickem.utils.BaseFragment
import com.mattg.pickem.utils.ParseInvitesClickListener
import com.mattg.pickem.utils.SharedPrefHelper
import com.mattg.pickem.utils.UserPoolClickListener
import com.parse.ParseUser
import kotlinx.android.synthetic.main.create_pool_dialog.*
import kotlinx.android.synthetic.main.fragment_view_pools.*
import kotlinx.android.synthetic.main.invite_dialog.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber

class ViewPoolsFragment : BaseFragment() {

    private lateinit var poolViewModel: PoolViewModel
    private var poolIdHolder: String? = null
    private lateinit var inviteClickListener: ParseInvitesClickListener
    private lateinit var userPoolsClickListener: UserPoolClickListener
    private var currentPool: String = ""
    private var upcomingWeek: String? = null

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
        upcomingWeek = SharedPrefHelper.getWeekFromPrefs(requireContext())

        fragmentScope.launch {
            poolViewModel.getParsePools()
            poolViewModel.checkParseInvites()
        }

        btn_create_pool.setOnClickListener {
            showCreatePoolDialog()
        }
        observeParseViewModel()
    }

    private fun observeParseViewModel() {

        poolViewModel.parsePools.observe(viewLifecycleOwner) {
            Timber.i("*********observing parse pools in fragment, value is $it")
            if (it != null) {
                setUpPoolRecyclerParse(it)
            }
        }

        poolViewModel.parseInvitesList.observe(viewLifecycleOwner) {
            Timber.i("*************observing parse invites, its value is $it")
            Toast.makeText(requireContext(), "You have an invitation", Toast.LENGTH_SHORT).show()
            if (!it.isNullOrEmpty()) {
                generateDialogOfParseInvites(it)
            }
        }

        poolViewModel.parsePoolError.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), "Pools gathering error $it", Toast.LENGTH_SHORT)
                    .show()
                poolViewModel.resetPoolError()
            }
        }
    }


    private fun showCreatePoolDialog() {
        val createPoolDialog = Dialog(requireContext())
        createPoolDialog.apply {
            setContentView(R.layout.create_pool_dialog)
            btn_create_pool_create.setOnClickListener {
                if (et_pool_name.text.toString().isBlank()) {
                    Toast.makeText(requireContext(), "Please name your pool", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    fragmentScope.launch {
                        poolViewModel.createParsePool(
                            poolName = et_pool_name.text.toString().trim()
                        )
                        poolViewModel.getParsePoolsByQuery()
                    }

                    findNavController().navigate(R.id.action_navigation_pools_self)
                    createPoolDialog.dismiss()
                }
            }
            btn_create_pool_cancel.setOnClickListener {
                createPoolDialog.dismiss()
            }
        }.show()
    }

    private fun generateDialogOfParseInvites(listForRecycler: ArrayList<ParseInvite>) {
        val inviteDialog = Dialog(requireContext())
        inviteDialog.apply {
            setContentView(R.layout.invite_dialog)
            btn_dialog_invites_close.setOnClickListener {
                inviteDialog.dismiss()
            }
            val recycler = rv_invites
            inviteClickListener = ParseInvitesClickListener { invite, position, delete ->
                when (delete) {
                    1 -> { //accept invite
                        poolViewModel.acceptInviteFromParsePool(
                            ParseUser.getCurrentUser(),
                            invite.inviteId,
                            true
                        )
                        listForRecycler.remove(invite)
                        listForRecycler.trimToSize()
                        Toast.makeText(
                            requireContext(),
                            "Joined ${invite.poolName}!",
                            Toast.LENGTH_SHORT
                        ).show()
                        recycler.adapter?.notifyItemRemoved(position)

                    }
                    2 -> { //decline and delete invite
                        poolViewModel.acceptInviteFromParsePool(
                            ParseUser.getCurrentUser(),
                            invite.inviteId,
                            false
                        )
                        listForRecycler.remove(invite)
                        listForRecycler.trimToSize()
                        Toast.makeText(
                            requireContext(),
                            "Declined to join ${invite.poolName}",
                            Toast.LENGTH_SHORT
                        ).show()
                        recycler.adapter?.notifyItemRemoved(position)

                    }
                }
            }

            val inviteAdapter =
                ParseInviteRecyclerAdapter(requireContext(), listForRecycler, inviteClickListener)
            val inviteLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recycler.apply {
                adapter = inviteAdapter
                layoutManager = inviteLayoutManager
            }


        }.show()
    }

    private fun setUpPoolRecyclerParse(list: ArrayList<ParsePool>) {
        userPoolsClickListener =
            UserPoolClickListener { poolDocId, poolOwner, _, buttonInt, poolName, poolOwnerName ->
                when (buttonInt) {
                    1 -> {
                        fragmentScope.launch {
                            setPool(poolDocId, poolOwner, poolName, poolOwnerName)
                        }
                    }
                    2 -> {
                        Timber.i("*******before owner check, names: ${ParseUser.getCurrentUser().username} == $poolOwner, other var  poolname $poolName poolOwnername $poolOwnerName")
                        if (ParseUser.getCurrentUser().username.trim() == poolOwnerName.trim()) {
                            Timber.i("*******saying is the owner, names: ${ParseUser.getCurrentUser().username} == $poolOwnerName")
                            deletePoolDialog(poolDocId, poolName, true)
                        } else {
                            Timber.i("********saying not the owner, only deleting person from the pool")
                            deletePoolDialog(poolDocId, poolName, false)
                        }
                    }
                }
            }
        val recycler = rv_pools
        val poolsAdapter = ParsePoolsAdapter(requireContext(), list, userPoolsClickListener)
        val poolsLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            adapter = poolsAdapter
            layoutManager = poolsLayoutManager
        }
    }

    private fun deletePoolDialog(poolId: String, poolName: String, isOwner: Boolean) {
        AlertDialog.Builder(requireContext()).setTitle("Delete Pool?")
            .setPositiveButton("Delete") { dialog, _ ->
                //      poolViewModel.deletePool(poolId, poolName, isOwner)
                poolViewModel.deleteParsePool(poolId, poolName, isOwner)
                findNavController().navigate(R.id.action_navigation_pools_self)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setPool(
        poolIdToSet: String,
        poolOwnerId: String,
        poolName: String,
        poolOwnerName: String
    ) {
        currentPool = "$poolOwnerId : $poolIdToSet"
        poolIdHolder = poolIdToSet
        fragmentScope.launch {
            Timber.i("******setting pool with id $poolIdToSet")
            poolViewModel.setCurrentParsePool(poolIdToSet)
            poolViewModel.getParsePoolById(poolIdToSet)
        }
        MainScope().launch {
            val action = ViewPoolsFragmentDirections.actionNavigationDashboardToPoolDetailFragment(
                null,
                poolName,
                false,
                poolOwnerName
            )
            findNavController().navigate(action)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.pool_management_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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