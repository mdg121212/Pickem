package com.mattg.pickem.detailui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattg.pickem.R
import com.mattg.pickem.db.Pick
import com.mattg.pickem.utils.PicksClickListener
import com.mattg.pickem.ui.home.viewModels.HomeViewModel
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


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_picks, container, false)
    }


    private fun initRecycler(list: ArrayList<Pick>) {
        clickListener = PicksClickListener{pick, position, option ->
            when(option){
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