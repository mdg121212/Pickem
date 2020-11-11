package com.mattg.pickem.ui.dashboard.fragments

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
import com.google.firebase.firestore.FirebaseFirestore
import com.mattg.pickem.LoginActivity
import com.mattg.pickem.R
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.Pool
import com.mattg.pickem.models.firebase.User
import com.mattg.pickem.ui.dashboard.PoolViewModel
import com.mattg.pickem.ui.home.adapters.*
import com.mattg.pickem.utils.BaseFragment
import kotlinx.android.synthetic.main.create_pool_dialog.*
import kotlinx.android.synthetic.main.fragment_pool_management.*
import kotlinx.android.synthetic.main.invite_dialog.*
import kotlinx.android.synthetic.main.invite_search_dialog.*
import timber.log.Timber

class InvitePoolFragment : BaseFragment() {

    private lateinit var poolViewModel: PoolViewModel
    private var mFirebaseDataBaseInstance: FirebaseFirestore?= null
    private lateinit var clickListener: RecyclerClickListener
    private var poolIdHolder: String ?= null
    private lateinit var inviteClickListener: InvitesClickListener
    private lateinit var userPoolsClickListener: UserPoolClickListener
    private var currentPool: String = ""

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
        return inflater.inflate(R.layout.fragment_pool_management, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // poolViewModel.getUsersForRecycler()

        mFirebaseDataBaseInstance = FirebaseFirestore.getInstance()

        val isInvitations = poolViewModel.listenForInvitations()
        if(isInvitations){
            Toast.makeText(requireContext(), "Invitations Pending", Toast.LENGTH_SHORT).show()
        }

        poolViewModel.getUserPools()
        observeViewModel()

        btn_create_pool.setOnClickListener {
            showCreatePoolDialog()
        }
        btn_open_pool.setOnClickListener {
            if(poolViewModel.currentPool.value != null){
                findNavController().navigate(R.id.action_navigation_dashboard_to_poolDetailFragment)
            }
        }

        btn_invite.setOnClickListener {
            if(poolViewModel.currentPool.value != null){
                showInviteDialog()
            } else {
                Toast.makeText(requireContext(), "Select a pool", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel(){
        poolViewModel.usersList.observe(viewLifecycleOwner){
                if (it != null) {
                    Timber.i("Observing usersList = ${it.size}")
                    populateRecycler(it)
                    rv_invitePlayers.visibility = View.VISIBLE
                }
            }
        poolViewModel.areInvites.observe(viewLifecycleOwner){
            if(it == true) {
                poolViewModel.listForInviteRecycler.value?.let { it1 -> generateDialogOfInvites(it1) }
                 }
           }
        poolViewModel.userPoolsList.observe(viewLifecycleOwner){
                setUpPoolRecycler(it)
            }

    }


    private fun showInviteDialog() {
        val invitationDialog = Dialog(requireContext())
        invitationDialog.apply {
            setContentView(R.layout.invite_search_dialog)
            btn_invite_search.setOnClickListener {
                if(et_email_search.text.toString().isBlank()){
                    Toast.makeText(requireContext(), "Please enter an email", Toast.LENGTH_SHORT).show()
                } else {
                    val result = poolViewModel.searchForUsers(et_email_search.text.toString().trim())
                    Timber.i("RESULT OF SEARCH: = ${result.size}")
                    poolViewModel.usersList.value?.let { it1 -> populateRecycler(it1) }
                    invitationDialog.dismiss()
                }
            }
            btn_invite_cancel.setOnClickListener {
                invitationDialog.dismiss()
            }
        }.show()
    }

    private fun showCreatePoolDialog() {
        val createPoolDialog = Dialog(requireContext())
        createPoolDialog.apply {
            setContentView(R.layout.create_pool_dialog)
            btn_create_pool_create.setOnClickListener {
                if(et_pool_name.text.toString().isBlank()){
                    Toast.makeText(requireContext(), "Please name your pool", Toast.LENGTH_SHORT).show()
                } else {
                    poolViewModel.createPool(poolViewModel.user?.uid!!, et_pool_name.text.toString())
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
                            inviteDialog.dismiss()
                        recycler.adapter?.notifyItemRemoved(position)

                    }
                    2 -> { //decline and delete invite
                        poolViewModel.declineInvitation(invite.inviteId!!, invite.senderId!!, invite.sentInviteId!!)
                        listForRecycler.remove(invite)
                        listForRecycler.trimToSize()
                        Toast.makeText(requireContext(), "Declined to join ${invite.poolName}", Toast.LENGTH_SHORT).show()
                            inviteDialog.dismiss()
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

    private fun populateRecycler(list: ArrayList<User>) {
        val recycler = rv_invitePlayers
        clickListener = RecyclerClickListener { user, _, id ->
            //  sendInvite(user, id)
            poolViewModel.sendInvitation(id)
            list.remove(user)
            Toast.makeText(requireContext(), "Invitation sent to ${user.name}", Toast.LENGTH_SHORT).show()
        }
        val userAdapter = InviteAdapter(requireContext(), list, clickListener)
        val userLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            layoutManager = userLayoutManager
            adapter = userAdapter
            visibility = View.VISIBLE
        }
    }

    private fun setUpPoolRecycler(list: ArrayList<Pool>) {
        userPoolsClickListener = UserPoolClickListener{ poolId, poolOwner, _, buttonInt, poolName ->
            when (buttonInt){
                1 -> {
                    setPool(poolId, poolOwner, poolName)
                }
                2 -> {
                   deletePoolDialog(poolId)
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

    private fun deletePoolDialog(poolId: String){
        AlertDialog.Builder(requireContext()).setTitle("Delete Pool?")
            .setPositiveButton("Delete"){ dialog, _ ->
                poolViewModel.deletePool(poolId)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.cancel()
            }.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setPool(poolIdToSet: String, poolOwner: String, poolName: String) {
        currentPool = "$poolOwner : $poolIdToSet"
        poolIdHolder = poolIdToSet
        tv_current_pool.text = "Current Pool: $poolName"
        poolViewModel.setCurrentPool(poolIdToSet, poolName)
        btn_invite.visibility = View.VISIBLE
        btn_open_pool.visibility = View.VISIBLE
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