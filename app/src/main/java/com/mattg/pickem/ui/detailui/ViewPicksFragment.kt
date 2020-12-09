package com.mattg.pickem.ui.detailui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattg.pickem.R
import com.mattg.pickem.db.Pick
import com.mattg.pickem.ui.home.viewModels.HomeViewModel
import com.mattg.pickem.utils.PicksClickListener
import kotlinx.android.synthetic.main.fragment_view_picks.*

class ViewPicksFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var clickListener: PicksClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_view_picks, container, false)
    }


    private fun initRecycler(list: ArrayList<Pick>) {
        clickListener = PicksClickListener { _, _, option ->
            when (option) {
                1 -> {
                    showDeletePickDialog()
                }
                2 -> {
                    showDuplicatePickDialog()
                }
            }
        }

        val viewPicksAdapter = ViewPicksAdapter(requireContext(), list, clickListener)
        val viewPicksLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        rv_view_picks.apply {
            adapter = viewPicksAdapter
            layoutManager = viewPicksLayoutManager
        }
    }

    private fun showDuplicatePickDialog() {
        TODO("Not yet implemented")
    }

    private fun showDeletePickDialog() {
        TODO("Not yet implemented")
    }
}