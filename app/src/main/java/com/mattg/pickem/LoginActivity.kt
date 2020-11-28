package com.mattg.pickem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.mattg.pickem.models.firebase.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val FIREBASE_LOGIN_REQUEST_CODE = 1234

class LoginActivity : AppCompatActivity() {

    private var isFirebaseLaunched = false
    private var mFirebaseDatabaseInstance: FirebaseFirestore? = null
    private var mFirebaseRealTimeDatabaseInstance: FirebaseDatabase? = null
    private var refUserDatabase: DatabaseReference? = null
    private var firebaseUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mFirebaseDatabaseInstance = FirebaseFirestore.getInstance()


        setOnClickListeners()

    }

    override fun onStart() {
        super.onStart()
        //check if user is signed in
        val auth = getInstance()

        //app launch intent
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK )
        if (auth.currentUser != null) {
            val userToSave = getInstance().currentUser
            val userName = userToSave?.displayName
            val userEmail = userToSave?.email
            val userId = userToSave?.uid
            val userToAdd = User(userName!!, userEmail!!, userId!!, null, null)

            //checking for user document in database.  will create if it doesn't exist
            mFirebaseDatabaseInstance!!.collection("users").document(userId).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val document = it.result
                        if (document != null) {
                            if (document.exists()) {
                                //storing uid in variable to add to database as well
                                firebaseUserId = auth.currentUser!!.uid
                                refUserDatabase = FirebaseDatabase.getInstance().reference.child("users").child(
                                    firebaseUserId!!
                                )   //add to database with child node of id

                                val userHashMap = HashMap<String, Any>()
                                userHashMap["uid"] = firebaseUserId!!
                                userHashMap["username"] = userName
                                userHashMap["search"] = userName.toLowerCase()
                                userHashMap["searchEmail"] = userEmail.toLowerCase()
                                userHashMap["pools"] = emptyList<String>()

                                refUserDatabase!!.updateChildren(userHashMap)
                                    .addOnCompleteListener {
                                        if(it.isSuccessful){
                                            startActivity(intent)


                                        } else {
                                            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
                                        }
                                    }


                            } else {
                                mFirebaseDatabaseInstance!!.collection("users").document(userId)
                                    .set(userToAdd)
                                firebaseUserId = auth.currentUser!!.uid
                            }
                        }
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FIREBASE_LOGIN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            doOnResult(resultCode)
        } else {
            Toast.makeText(this, "Need to log in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun doOnResult(resultCode: Int) {
        try {
            //successfully logged in
            if (resultCode == RESULT_OK) {
                getInstance().currentUser
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sign in failed...please try again", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Error) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun onSignInButtonClicked() {
        isFirebaseLaunched = true
        //list of providers for firebase
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        //create and launch intent
        val instance = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
        try {
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                startActivityForResult(
                    instance,
                    FIREBASE_LOGIN_REQUEST_CODE,
                    null
                )
            }
        } catch (ex: FirebaseAuthException) {
            Log.i("FIREBASE", "Exception = ${ex.message}")
            Toast.makeText(this, "Error signing in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSignOutClickedButton() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                //user is now signed out
                Toast.makeText(applicationContext, "User has signed out", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setOnClickListeners() {
        btn_login.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { onSignInButtonClicked() }
        }
        btn_logout.setOnClickListener {
            onSignOutClickedButton()
        }
    }


}