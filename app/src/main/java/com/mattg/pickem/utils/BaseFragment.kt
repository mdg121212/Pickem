package com.mattg.pickem.utils

import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.mattg.pickem.ui.home.viewModels.HomeViewModel

open class BaseFragment: Fragment() {
    private var mFirebaseDatabase: FirebaseFirestore ?= null
    lateinit var viewModel: HomeViewModel
}