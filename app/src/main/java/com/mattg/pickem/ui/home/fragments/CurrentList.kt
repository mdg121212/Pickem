package com.mattg.pickem.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mattg.pickem.R
import com.mattg.pickem.ui.home.viewModels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_current_list.*

class CurrentList : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        observeViewModel()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_list, container, false)
    }

    fun observeViewModel(){
        homeViewModel.picksString.observe(viewLifecycleOwner){
            tv_current_list_title.text = if(it.isNullOrBlank()) "No data" else it
        }
    }
}