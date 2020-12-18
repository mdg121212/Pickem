package com.mattg.pickem.utils


import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseFragment: Fragment() {

    val fragmentScope = CoroutineScope(Dispatchers.IO)

    fun logout() {
//        AuthUI.getInstance()
//            .signOut(requireContext())
//            .addOnCompleteListener {
//                //user is now signed out
//                Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
//                val intent = Intent(requireContext(), LoginActivity::class.java)
//                startActivity(intent)
//            }
    }
}