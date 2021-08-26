package com.mattg.pickem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mattg.pickem.parsebackend.ParseUserRepository
import com.mattg.pickem.utils.SharedPrefHelper
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_parse_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class LoginActivityParse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parse_login)

        val wasLoggedOut = intent.getBooleanExtra("logout", false)
        Timber.i("was logged out = $wasLoggedOut")
        if (wasLoggedOut) {
            SharedPrefHelper.setDontRememberParse(this)
            checkBox_remember.isChecked = false
            et_userName.text.clear()
            et_password.text.clear()
        }
        setOnClickListeners()
        val isRemember = SharedPrefHelper.getRememberMe(this)
        if (isRemember) {
            checkBox_remember.isChecked = true
            val user = SharedPrefHelper.getParseUser(this)
            user.first?.let { user.second?.let { it1 -> parseLogin(it, it1) } }
        }

    }

    private fun setOnClickListeners() {
        btn_signIn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { onSignInButtonClicked() }
        }
        btn_logOut.setOnClickListener {
            onSignOutClickedButton()
        }
        btn_signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivityParse::class.java)
            startActivity(intent)
        }
    }

    private fun onSignInButtonClicked() {
        if (et_password.text.isNullOrEmpty() || et_userName.text.isNullOrEmpty()) {
            runOnUiThread {
                Toast.makeText(this, "Please enter your user name and password", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {

            if (checkBox_remember.isChecked) {
                SharedPrefHelper.setRememberMeParse(this)
            }

            val userName = et_userName.text.toString().trim()
            val userPassword = et_password.text.toString().trim()
            parseLogin(userName, userPassword)
        }


    }

    private fun parseLogin(userName: String, userPassword: String) {
        ParseUserRepository().loginUser(userName, userPassword, this)

        val wasSuccess = SharedPrefHelper.getLoginResultParse(this)
        Timber.i("***********was success is $wasSuccess")
        if (wasSuccess) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            SharedPrefHelper.nukeUserData(this)

            runOnUiThread {
                checkBox_remember.isChecked = false
                Toast.makeText(this, "Error signing in.  Please try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun onSignOutClickedButton() {
        ParseUser.logOut()
    }

}