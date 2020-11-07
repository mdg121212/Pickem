package com.mattg.pickem.ui.dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mattg.pickem.R
import com.mattg.pickem.models.firebase.Invite
import com.mattg.pickem.models.firebase.Pool
import com.mattg.pickem.models.firebase.User
import com.mattg.pickem.ui.home.adapters.*
import com.mattg.pickem.utils.BaseFragment
import kotlinx.android.synthetic.main.create_pool_dialog.*
import kotlinx.android.synthetic.main.fragment_pool_management.*
import kotlinx.android.synthetic.main.invite_dialog.*
import timber.log.Timber

class InvitePoolFragment : BaseFragment() {

    private lateinit var poolViewModel: PoolViewModel
    private lateinit var auth : FirebaseAuth
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


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pool_management, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        poolViewModel.getUsersForRecycler()
//        auth = FirebaseAuth.getInstance()
//        val currentUser = auth.currentUser
        mFirebaseDataBaseInstance = FirebaseFirestore.getInstance()
       // listenForInvites(currentUser)
        val isInvitations = poolViewModel.listenForInvitations()
        if(isInvitations){
            Toast.makeText(requireContext(), "Invitations Pending", Toast.LENGTH_SHORT).show()
        }
       // getUserPools(currentUser!!.uid)
        poolViewModel.getUserPools()
        observeViewModel()
        btn_create_pool.setOnClickListener {
            //this works, creates a pool with a reference to itself inside itself
           // poolIdHolder = createPool()
            showCreatePoolDialog()
           // poolViewModel.createPool(poolViewModel.user?.uid!!)
        }
    }

    private fun showCreatePoolDialog() {
        val createPoolDialog = Dialog(requireContext())
        createPoolDialog.apply {
            setContentView(R.layout.create_pool_dialog)
           btn_create_pool_create.setOnClickListener {
                if(et_pool_name.text.toString().isNullOrBlank()){
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


    private fun observeViewModel(){
        poolViewModel.apply {

            usersList.observe(viewLifecycleOwner){
                if (it != null){
                    populateRecycler(it)
                }
            }
           areInvites.observe(viewLifecycleOwner){
            if(it == true){
                poolViewModel.listForInviteRecycler.value?.let { it1 -> generateDialogOfInvites(it1) }
            }
           }
            userPoolsList.observe(viewLifecycleOwner){
                setUpPoolRecycler(it)
            }
        }
    }
    private fun generateDialogOfInvites(listForRecycler: ArrayList<Invite>) {
        val inviteDialog = Dialog(requireContext())
        inviteDialog.apply {
            setContentView(R.layout.invite_dialog)
            btn_dialog_invites_close.setOnClickListener {
                inviteDialog.dismiss()
            }
            inviteClickListener = InvitesClickListener { invite, position, delete ->
                when (delete) {
                    1 -> { //accept invite
                        invite.poolId?.let {
                            poolViewModel.acceptInvitation(it)
                            // acceptInvite(it)
                        }
                        // inviteDialog.dismiss()
                    }
                    2 -> { //decline and delete invite
                        invite.inviteId?.let { it1 ->
                            invite.senderId?.let {

                             invite.sentInviteId?.let { it2 ->
                                 poolViewModel.declineInvitation(it1, it,
                                     it2
                                 )
                             }
                                    Toast.makeText(requireContext(), "Invitation declined", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
            }
            val recycler = this.rv_invites
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
        clickListener = RecyclerClickListener { user, position, id ->
            //  sendInvite(user, id)
            poolViewModel.sendInvitation(id)
        }
        val userAdapter = PoolAdapter(requireContext(), list, clickListener)
        val userLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            layoutManager = userLayoutManager
            adapter = userAdapter
        }
    }

    private fun setUpPoolRecycler(list: ArrayList<Pool>) {
        userPoolsClickListener = UserPoolClickListener{poolId, poolOwner, position, buttonInt, poolName ->
            openPoolDetailFragment(poolId)
            when (buttonInt){
                1 -> {
                    setPool(poolId, poolOwner, poolName)
                }
                2 ->{
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

    fun deletePoolDialog(poolId: String){
        AlertDialog.Builder(requireContext()).setTitle("Delete Pool?")
            .setPositiveButton("Delete"){ dialog, _ ->
                poolViewModel.deletePool(poolId)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun setPool(poolIdToSet: String, poolOwner: String, poolName: String) {
        currentPool = "$poolOwner : $poolIdToSet"
        poolIdHolder = poolIdToSet
        tv_current_pool.text = "Current Pool: $poolName"
        poolViewModel.setCurrentPool(poolIdToSet)
    }

    private fun openPoolDetailFragment(poolId: String) {
        Toast.makeText(requireContext(), "For now, click event was passed", Toast.LENGTH_SHORT).show()
    }


//    private fun listenForInvites(currentUser: FirebaseUser?) {
//        mFirebaseDataBaseInstance!!.collection("users").document("${currentUser?.uid}")
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    Toast.makeText(
//                        requireContext(),
//                        "Error updating information",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@addSnapshotListener
//                }
//                checkForInvites(snapshot, currentUser)
//            }
//    }
//
//    private fun checkForInvites(
//        snapshot: DocumentSnapshot?,
//        currentUser: FirebaseUser?
//    ) {
//        if (snapshot != null && snapshot.exists()) {
//
//            Timber.d("Snapshot data: ${snapshot.data}")
//            val userRef = mFirebaseDataBaseInstance!!.collection("users")
//                .document(auth.currentUser!!.uid)
//
//            if (snapshot.data?.get("invites") != null) {
//                if (snapshot.data?.get("invites")!!.equals(true)) {
//                    //getting all of the invites for the current user, that have not been accepted
//                    val invites = userRef.collection("invitesReceived")
//                        .get()
//                    //iterate over them and delete later if declined, turn true and create pool on both ends if accepted
//                    invites.addOnSuccessListener { documents ->
//                        Timber.i("Result of checking for invites = ${documents.documents}")
//    //                                    val inviteReceivedHash = HashMap<String, Any>()
//    //                                    inviteReceivedHash["userWhoSentInvite"] = auth.currentUser!!.uid
//    //                                    inviteReceivedHash["accepted"] = false
//    //                                    inviteReceivedHash["userWhoSentInviteName"] = auth.currentUser!!.displayName!!
//    //                                    inviteReceivedHash["userWhoSentInviteEmail"] = auth.currentUser!!.email!!
//                        val listForRecycler = ArrayList<Invite>()
//                        for (document in documents) {
//                            val data = document.data
//                            Timber.i("DATA = $data")
//                            val email = data["userWhoSentInviteEmail"].toString()
//                            val name = data["userWhoSentInviteName"].toString()
//                            val poolId = data["poolId"].toString()
//                            val acceptBoolean = data["isAccepted"].toString()
//                            val senderId = data["userWhoSentInvite"].toString()
//                            val inviteId = data["documentId"].toString()
//
//                            val inviteForList = Invite(
//                                name,
//                                currentUser?.displayName!!,
//                                poolId,
//                                senderId,
//                                email,
//                                inviteId
//                            )
//                            //add invite object to list for recycler
//                            listForRecycler.add(inviteForList)
//                        }
//                        Timber.i("${listForRecycler.size}")
//                        //shows a list of current invitations
//                        generateDialogOfInvites(listForRecycler)
//                    }
//
//                }
//            }
//        }
//    }



//    private fun setDialogRecycler(
//        inviteDialog: Dialog,
//        listForRecycler: ArrayList<Invite>
//    ) {
//        inviteClickListener = InvitesClickListener { invite, position, delete ->
//            when (delete) {
//                1 -> { //accept invite
//                    invite.poolId?.let { poolViewModel.acceptInvite(it, auth, mFirebaseDataBaseInstance!!) }
//                    inviteDialog.dismiss()
//                }
//                2 -> { //decline and delete invite
//                    invite.inviteId?.let { it1 ->
//                        invite.senderId?.let {
//                            deleteInvite(
//                                it1,
//                                it
//                            )
//                        }
//                    }
//
//                    inviteDialog.dismiss()
//                }
//            }
//        }
//        val recycler = this.rv_invites
//        val inviteAdapter =
//            InviteRecyclerAdapter(requireContext(), listForRecycler, inviteClickListener)
//        val inviteLayoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        recycler.apply {
//            adapter = inviteAdapter
//            layoutManager = inviteLayoutManager
//        }
//    }
//
//
//    private fun createPool(): String {
//        val user = auth.currentUser!!
//        //change the fields of the user (recipient)
//        val userRef = mFirebaseDataBaseInstance!!
//            .collection("users")
//            .document(auth.currentUser!!.uid)
//        val poolData = HashMap<String, Any>()
//        poolData["owner"] = user.uid
//        poolData["ownerName"] = user.displayName.toString()
//        poolData["players"] = emptyList<Pair<String, String>>()
//        userRef.collection("pools").add(poolData).addOnSuccessListener {
//            //add reference to the document inside the document after it is created
//            it.id.let { it1 -> userRef.collection("pools").document(it1).update("documentId", it1) }
//        }
//        val id = userRef.collection("pools").document().id
//        return id
//    }

//    private fun getUserPools(userId: String){
//        mFirebaseDataBaseInstance!!.collection("users")
//            .document(userId)
//            .collection("pools")
//            .addSnapshotListener { snapshots, error ->
//                if(error != null){
//                    return@addSnapshotListener
//                }
//                val list = ArrayList<Pool>()
//                if(snapshots!=null){
//                    val pools = snapshots.documents
//                    for(pool in pools){
//                        val id = pool.get("documentId").toString()
//                        val owner = pool.get("owner").toString()
//                        val players = pool.get("players") as List<*>
//
//                        val poolToAdd = auth.currentUser?.uid?.let {
//                            Pool("test name", owner,
//                                it, id )
//                        }
//
////                        poolToAdd?.setPlayersFromList(players)
//                        if (poolToAdd != null) {
//                            list.add(poolToAdd)
//                        }
//
//                    }
//                    setUpPoolRecycler(list)
//                }
//            }
//    }


//
//    private fun deleteInvite(inviteId: String, userWhoSentInviteId: String) {
//        //get the document by its id, and delete it on the receiving end
//        mFirebaseDataBaseInstance!!.collection("users").document(auth.currentUser!!.uid)
//            .collection("invitesReceived").document(inviteId).delete()
//            .addOnSuccessListener {
//                Toast.makeText(requireContext(), "Invite declined", Toast.LENGTH_SHORT).show()
//               // resetInvitesBoolean()
//            }
//            .addOnFailureListener{ e -> Timber.d("$e")}
//        //notifiy sender that the invite was declined (at least update their fields)
//    }
//
//    private fun resetInvitesBoolean() {
//        mFirebaseDataBaseInstance!!.collection("users")
//            .document(auth.currentUser!!.uid)
//            .update("invites", false)
//    }

    /**
     * NOT CURRENTLY ACCEPTING THE INVITE AND ADDING A MATCHING POOL COLLECTION
     */
//    private fun acceptInvite(poolId: String) {
//        val user = auth.currentUser!!
//        //change the fields of the user (recipient)
//        val userRef = mFirebaseDataBaseInstance!!
//            .collection("users")
//            .document(auth.currentUser!!.uid)
//        //get the recieved invitations
//        val invitesReceived = userRef.collection("invitesRecieved").get()
//        invitesReceived.addOnSuccessListener {documents ->
//            //iterate over the recieved invitations
//            for(document in documents){
//                if(document.get("poolId")?.equals(poolId)!!){
//                    //get the pool id from the invite
//                    val poolIdRetrieved = document.get("poolId")
//                    val documentToPullFrom = userRef.collection("invitesRecieved").document(document.id).get()
//                    //empty variable for the owner/sender of invite
//                    var ownerName = ""
//                    //assign name to variable
//                    documentToPullFrom.addOnSuccessListener {
//                        ownerName =  it.get("userWhoSentInviteName").toString()
//                    }
//                    //create list of owner and current user to fill in fields on users pool document
//                    val poolMembers = listOf(ownerName, user.displayName)
//                    //get the pool reference from the sender, and copy it to the user
//                    val originalPoolDocument = mFirebaseDataBaseInstance!!.collection("users")
//                        .document(document["userWhoSentInvite"].toString())
//                        .collection("pools")
//                        .whereEqualTo("poolId", poolIdRetrieved)
//                        .get()
//                    val data = originalPoolDocument.result?.documents
//                    data?.forEach { it ->
//                        Timber.i("${it.data}")
//                        userRef.collection("pools").add(it)
//                    }
//
//                }
//            }
//
//        }
//
//
//        //change the fields on the sending end
//
//    }

//    private fun sendInvite(user: User, id: String) {
//        if (poolIdHolder == null) {
//            Toast.makeText(
//                requireContext(),
//                "You need to start a pool before sending invitations",
//                Toast.LENGTH_LONG
//            ).show()
//        } else { /////////////////////////////////////////////////////////
//            val playerRef = mFirebaseDataBaseInstance!!.collection("users").document(id)
//            playerRef.update("invites", true)
//            //create an invitation document on the receiving end
//            val inviteReceivedHash = HashMap<String, Any>()
//            inviteReceivedHash["userWhoSentInvite"] = auth.currentUser!!.uid
//            inviteReceivedHash["accepted"] = false
//            inviteReceivedHash["userWhoSentInviteName"] = auth.currentUser!!.displayName!!
//            inviteReceivedHash["userWhoSentInviteEmail"] = auth.currentUser!!.email!!
//
//            poolIdHolder?.let { it -> inviteReceivedHash["poolId"] = it }
//            //update the recieving end
//            mFirebaseDataBaseInstance!!.collection("users").document(id)
//                .collection("invitesReceived")
//                .add(inviteReceivedHash).addOnSuccessListener {
//                    //add a field to the new document that references its id
//                    it?.id?.let { it1 ->
//                        mFirebaseDataBaseInstance!!.collection("users")
//                            .document(id)
//                            .collection("invitesReceived")
//                            .document(it1).update("documentId", it1)
//                    }
//                }
//
//            //for the person sending the invite
//            val senderRef =
//                mFirebaseDataBaseInstance!!.collection("users").document(auth.currentUser!!.uid)
//            senderRef.update("numberOfInvitesSet", +1)
//            //creating a key value for collection of people who were sent invites
//            //there is an id mapped to "userWhoGotInvite" and a boolean set to false.  If they respond yes
//            //they will have to seek this boolean out and change it back, it will have to be listened too
//            val inviteHash = HashMap<String, Any>()
//            inviteHash["userWhoGotInvite"] = id
//            inviteHash["isAccepted"] = false
//            auth.currentUser?.uid?.let {
//                mFirebaseDataBaseInstance!!.collection("users").document(it)
//                    .collection("invitesSent").add(inviteHash).addOnSuccessListener {
//                        //add a field in new document to reference its own id
//                        it?.id?.let { it1 ->
//                            mFirebaseDataBaseInstance!!.collection("users")
//                                .document(auth.currentUser!!.uid)
//                                .collection("invitesSent").document(it1).update("documentId", it1)
//                        }
//                    }
//            }
//        }
////              VIEW MODEL FUNCTION DOES WORK KIND OF
////            poolIdHolder?.let{it ->
////                poolViewModel.sendInvitation(it, id).also {
////                    Toast.makeText(requireContext(), "Invite sent!", Toast.LENGTH_SHORT).show()
////                }
////                //  }
////            }
//    }

}