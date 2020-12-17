package com.mattg.pickem.utils

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.mattg.pickem.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseFragment: Fragment() {

    val fragmentScope = CoroutineScope(Dispatchers.IO)

    fun logout() {
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