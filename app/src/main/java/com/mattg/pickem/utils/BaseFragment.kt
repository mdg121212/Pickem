package com.mattg.pickem.utils


import android.content.Intent
import androidx.fragment.app.Fragment
import com.mattg.pickem.LoginActivityParse
import com.parse.ParseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseFragment: Fragment() {

    val fragmentScope = CoroutineScope(Dispatchers.IO)

    fun logout() {
        ParseUser.logOut()
        SharedPrefHelper.nukeUserData(requireContext())
        val intent = Intent(requireContext(), LoginActivityParse::class.java)
        intent.putExtra("logout", true)
        startActivity(intent)
    }
}