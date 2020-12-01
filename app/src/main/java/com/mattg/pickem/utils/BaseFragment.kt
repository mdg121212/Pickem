package com.mattg.pickem.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.FirebaseFirestore
import com.mattg.pickem.LoginActivity
import com.mattg.pickem.MainActivity
import com.mattg.pickem.ui.home.viewModels.HomeViewModel

open class BaseFragment: Fragment() {

    fun logout(){
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