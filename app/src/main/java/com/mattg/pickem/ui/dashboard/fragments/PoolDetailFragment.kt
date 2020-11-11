package com.mattg.pickem.ui.dashboard.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattg.pickem.R
import com.mattg.pickem.models.firebase.User
import com.mattg.pickem.ui.dashboard.PoolViewModel
import com.mattg.pickem.ui.dashboard.adapters.PoolPlayerListAdapter
import com.mattg.pickem.ui.home.adapters.PoolPlayerListClickListener
import kotlinx.android.synthetic.main.create_pool_dialog.*
import kotlinx.android.synthetic.main.fragment_pool_detail.*
import timber.log.Timber


class PoolDetailFragment : Fragment() {

    private lateinit var poolViewModel: PoolViewModel
    private lateinit var clickListener: PoolPlayerListClickListener
    private lateinit var args: NavArgs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        poolViewModel = ViewModelProvider(requireActivity()).get(PoolViewModel::class.java)



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pool_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPoolDetails()
        observeViewModel()
    }

    private fun getPoolDetails() {
        //set title
       // tv_pool_detail_title.text = poolViewModel.currentPoolName.value
        //get week

        //use id to get list of players
        val currentId = poolViewModel.currentPool.value
        val players = ArrayList<User>()
        if (currentId != null) {
            poolViewModel.getPoolPlayers(currentId)
        }

    }

    fun observeViewModel(){
        poolViewModel.currentPoolPlayers.observe(viewLifecycleOwner){
            Timber.i("TESTING, list of players value = ${it.size}")
            setupPoolPlayerListRecycler(it)
        }
        poolViewModel.currentPoolName.observe(viewLifecycleOwner){
            tv_pool_detail_title.text = it
        }
    }
    fun setupPoolPlayerListRecycler(list: ArrayList<User>) {
        val recycler = rv_pool_players
        clickListener = PoolPlayerListClickListener { user, position, int ->
            when(int){
                1  -> {
                    showPicksDialog(user)
                }
            }
        }
        val poolPlayersAdapter = PoolPlayerListAdapter(requireContext(), list, clickListener)
        val poolPlayersLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.apply{
            layoutManager = poolPlayersLayoutManager
            adapter = poolPlayersAdapter
        }

    }
    private fun showSubmitPicksDialog(){

    }

    private fun showPicksDialog(user: User) {
        Toast.makeText(requireContext(), "Picks will be here", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.pool_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
        R.id.mnu_submit_picks -> {
            showSubmitPicksDialog()
                 }
            }
        return true
    }

}