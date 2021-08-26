package com.mattg.pickem.ui.pools.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mattg.pickem.R
import com.mattg.pickem.parsebackend.models.ParsePick
import com.mattg.pickem.ui.pools.adapters.ParsePoolPicksAdapter
import com.mattg.pickem.ui.pools.viewModel.PoolViewModel
import com.mattg.pickem.utils.BaseFragment
import com.mattg.pickem.utils.DisplayPicksClickListener
import com.mattg.pickem.utils.SharedPrefHelper
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_pool_players_picks.*
import timber.log.Timber


class PoolPlayersPicksFragment : BaseFragment() {

    private lateinit var poolViewModel: PoolViewModel
    private lateinit var clickListener: DisplayPicksClickListener
    private var currentWeek: String? = null
    private var lastWeek: String? = null
    private val args: PoolPlayersPicksFragmentArgs by navArgs()
    private var action: Int = 0
    private var isOwner = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        poolViewModel = ViewModelProvider(requireActivity()).get(PoolViewModel::class.java)
        //filter based on current week
        action = args.action

        currentWeek = SharedPrefHelper.getWeekFromPrefs(requireContext())

        lastWeek = SharedPrefHelper.getLastOrCurrentWeekFromPrefs(requireContext())

        checkOwner()

        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_pool_players_picks, container, false)
    }

    private fun checkOwner() {
        //getting picks from pool from parse here for now
        poolViewModel.getParsePoolPicks()
        observeViewModelParse()
    }

    private fun observeViewModelParse() {
        poolViewModel.parseRetrievedPoolPicks.observe(viewLifecycleOwner) {
            Timber.i("%%%%observing pool picks from parse, value is $it")
            if (it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No picks submitted yet", Toast.LENGTH_SHORT)
                    .show()

            } else

                setupParsePicksRecycler(it)
        }
    }

    private fun setupParsePicksRecycler(list: ArrayList<ParsePick>) {
        val recycler = rv_player_picks_list
        val picksLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        clickListener = DisplayPicksClickListener { pick, position, actionInt ->
            when (actionInt) {
                1 -> {
                    showParseDeleteChangeDialog(pick, position, recycler)
                    list.remove(pick)
                }
            }
        }
        val picksAdapter = ParsePoolPicksAdapter(
            requireContext(),
            list,
            clickListener,
            ParseUser.getCurrentUser(),
            isOwner
        )



        recycler.apply {
            layoutManager = picksLayoutManager
            adapter = picksAdapter

        }


    }


    private fun showChooseWeekDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_choose_week)
        val cancelButton = dialog.findViewById(R.id.btn_cancel) as Button
        val submitButton = dialog.findViewById(R.id.btn_search_week) as Button
        val editText = dialog.findViewById(R.id.et_week) as EditText
        submitButton.setOnClickListener {
            val week = editText.text
            if (week.toString().toInt() < 1 || week.toString().toInt() > 18) {
                Toast.makeText(requireContext(), "Enter a valid week", Toast.LENGTH_SHORT).show()
            } else {
                val weekString = "Week $week"
                //              poolViewModel.filteredGetPicks(poolViewModel.currentPool.value!!, weekString)
                //   observeViewModel()
                dialog.dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showParseDeleteChangeDialog(
        pick: ParsePick,
        position: Int,
        recycler: RecyclerView
    ) {
        Toast.makeText(requireContext(), "Will port dialog here", Toast.LENGTH_SHORT).show()
        MaterialAlertDialogBuilder(requireContext()).setTitle("Delete Picks?")
            .setNegativeButton("Yes") { dialog, _ ->

                poolViewModel.deleteParsePickFromPool(pick)
                recycler.layoutManager?.onItemsRemoved(recycler, position, 1)


                Toast.makeText(requireContext(), "Picks Removed!", Toast.LENGTH_SHORT).show()
                observeViewModelParse()
                recycler.adapter?.notifyDataSetChanged()
                findNavController().navigate(R.id.action_poolPlayersPicksFragment_self)
                findNavController().popBackStack()

                dialog.dismiss()
            }

            .setNeutralButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.picks_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnu_sort_by_week -> {
                showChooseWeekDialog()
            }
            R.id.mnu_picks_detail_logout -> logout()
            R.id.homeAsUp -> poolViewModel.resetParsePicksList()
        }
        return true
    }

    override fun onDestroy() {
        poolViewModel.resetParsePicksList()
        super.onDestroy()
    }

}