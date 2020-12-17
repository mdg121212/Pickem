package com.mattg.pickem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mattg.pickem.parsebackend.ParseUserRepository
import com.mattg.pickem.utils.SharedPrefHelper
import kotlinx.android.synthetic.main.activity_sign_up_parse.*
import timber.log.Timber

class SignUpActivityParse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_parse)

        btn_sign_up.setOnClickListener {
            signUpOnClick()
        }
        btn_signUpCancel.setOnClickListener {
            cancelClick()
        }
    }

    private fun cancelClick() {
        TODO("Not yet implemented")
    }

    private fun signUpOnClick() {
        Timber.i("**********signUpOnClick called")
        val userEmailString = et_email.text.toString().trim()
        val userNameString = et_userName.text.toString().trim()
        val passwordString = et_password.text.toString().trim()

        if (userEmailString.isNotEmpty() && userNameString.isNotEmpty() && passwordString.isNotEmpty()) {
            //fields were filled in create user
            ParseUserRepository().createUser(userNameString, passwordString, userEmailString, this)
            val createdUser = SharedPrefHelper.getParseUser(this)
            val wasSuccess = SharedPrefHelper.getSignUpSuccess(this)
            Timber.i("***********was success from login == $wasSuccess")

            if (wasSuccess) {
                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error creating account, please try again", Toast.LENGTH_SHORT)
                    .show()
                et_password.text.clear()
                et_userName.text.clear()
                et_email.text.clear()
            }
        }
    }
}