package com.mattg.pickem.ui.home.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattg.pickem.R
import com.mattg.pickem.db.Pick
import com.mattg.pickem.ui.pools.viewModel.PoolViewModel
import com.mattg.pickem.utils.PicksClickListener
import com.mattg.pickem.ui.home.adapters.SavedPicksAdapter
import com.mattg.pickem.ui.home.viewModels.HomeViewModel
import com.mattg.pickem.utils.BaseFragment
import com.mattg.pickem.utils.shortToast
import kotlinx.android.synthetic.main.fragment_current_list.*

class CurrentList : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var clickListener: PicksClickListener
    private lateinit var poolViewModel: PoolViewModel
    val args : CurrentListArgs by navArgs()
    private var wasFromDetail : Boolean ?= null
    private var week: String ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        poolViewModel = ViewModelProvider(requireActivity()).get(PoolViewModel::class.java)
        return inflater.inflate(R.layout.fragment_current_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         week = args.week

        if(week != "null"){
            getPicks(true)

        } else {
            getPicks(false)

        }//call database
        wasFromDetail = args.wasFromPoolDetail
        observeViewModel() //observe database / view model data
    }

    private fun getPicks(forSubmit: Boolean){
        when(forSubmit){
            true -> {

                 homeViewModel.retrievePicksFromDatabaseForSubmit(week!!)
            }
            false -> {

                homeViewModel.retrievePicksFromDatabase()
            }
        }

    }

    private fun observeViewModel(){
        homeViewModel.picksString.observe(viewLifecycleOwner){
            tv_current_list_title.text = if(it.isNullOrBlank()) "No data" else it
        }
        homeViewModel.picksFromDatabase.observe(viewLifecycleOwner){
            setUpRecycler(it)
        }


    }

    private fun setUpRecycler(picks: List<Pick>?) {
        val recyler = rv_saved_picks
        clickListener = PicksClickListener { pick, _, option ->
            when(option){
                1 -> showPicksOptionsDialog(pick)
            }
        }
        recyler.adapter = picks?.let { SavedPicksAdapter(requireContext(), it, clickListener) }
        recyler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun showPicksOptionsDialog(picks: Pick){
        when(wasFromDetail) {
            true -> {
                AlertDialog.Builder(requireContext()).setTitle("Options")
                        .setPositiveButton("Choose picks"){_, _ ->
                            poolViewModel.setPicks(picks)
                            val action = CurrentListDirections.actionCurrentListToPoolDetailFragment(null,args.poolName, true)
                            findNavController().navigate(action)
                        }
                        .setNeutralButton("Cancel"){dialog, _ ->
                            dialog.dismiss()

                        }
                        .show()
            }
                false -> {

                    AlertDialog.Builder(requireContext()).setTitle("Options")
                            .setPositiveButton("Send Picks") { _, _ ->
                               requireContext().shortToast("ADD EMAIL/TEXT SEND CAPABILITY")
                            }
                            .setNeutralButton("Cancel") { dialog , _ ->
                                dialog.dismiss()
                            }
                            .setNegativeButton("Delete Picks") { _ , _ ->
                                deletePicks(picks.id)
                                homeViewModel.retrievePicksFromDatabase()
                            }
                            .show()
                }
            else -> {
                Toast.makeText(requireContext(), "Error getting your picks, try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deletePicks(id: Int) {
        homeViewModel.deletePicksFromDatabase(id)

    }


}