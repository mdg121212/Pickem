package com.mattg.pickem.parsebackend

import android.content.Context
import com.google.gson.GsonBuilder
import com.mattg.pickem.parsebackend.models.ParsePool
import com.mattg.pickem.parsebackend.models.ParsePoolPlayer
import com.mattg.pickem.utils.SharedPrefHelper
import com.parse.ParseException
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONArray
import timber.log.Timber


class ParseUserRepository {

    val gson = GsonBuilder().create()

    fun createUser(name: String, password: String, email: String, context: Context) {

        val currentUser = ParseUser.logOut()
        val user = ParseUser()
        user.setPassword(password)
        user.email = email
        user.username = name

        // Other fields can be set just like any other ParseObject,
        // using the "put" method, like this: user.put("attribute", "its value");
        // If this field does not exists, it will be automatically created
        user.signUpInBackground() { e ->
            if (e == null) {
                //can use the app now
                SharedPrefHelper.addParseUserToPrefs(context, name, password)
                SharedPrefHelper.setLoginSuccessParse(context)
                SharedPrefHelper.setSignUpSuccessParse(context)

            } else {
                //sign up failed
                SharedPrefHelper.setSignUpFailParse(context)
                Timber.i("******parse error with sign up : code - ${e.code} cause - ${e.cause} message - ${e.message} ")
            }
        }
    }

    fun loginUser(userName: String, password: String, context: Context) {
        ParseUser.logInInBackground(userName, password) { user, exception ->
            if (user != null) {
                //user is logged in
                SharedPrefHelper.setLoginSuccessParse(context)
            } else {
                //sign in failed look at exception
                SharedPrefHelper.setLoginFailParse(context)
                Timber.i("*******parse error logging in: code: ${exception.code} cause: ${exception.cause} message: ${exception.message}")
            }

        }
    }

    /**
     * ADD EMAIL VERIFICATION
     */

    fun getCurrentUser() {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser != null) {
            //user is good
        } else {
            //show that no user exists, need to sign in or sign up
        }
    }

    fun findUsers(queryField: String, query: String) {
        val query = ParseUser.getQuery()
        query.whereEqualTo(queryField, query)
        query.findInBackground { users, exception ->
            if (exception == null) {
                //query worked and returns matched users
                for (user in users) {
                    //iterate over found users and do something with the data
                    Timber.i("******querried users, found this user: $user")
                }
            } else {
                Timber.i("*******parse error querying users: code: ${exception.code} cause: ${exception.cause} message: ${exception.message}")

            }
        }
    }

    fun updateUser(email: String) {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser != null) {
            currentUser.put("email", email)
            currentUser.saveInBackground { exception ->
                if (exception != null) {
                    Timber.i("*******parse error updating user: code: ${exception.code} cause: ${exception.cause} message: ${exception.message}")

                }
            }
        }
    }

    fun deleteUser() {
        val currentUser = ParseUser.getCurrentUser()

        currentUser?.deleteInBackground {
            // Here you can handle errors, if thrown. Otherwise, "e" should be null
        }
    }

    suspend fun queryUsers(field: String): ArrayList<ParseUser>? = coroutineScope {
        val deferred = async(Dispatchers.IO) {
            val query = ParseUser.getQuery()
            query.whereContains("email", field.trim())
            query.whereContains("username", field.trim())

            try {
                val returnList = ArrayList<ParseUser>()
                val results = query.find()
                if (results.isNotEmpty()) {
                    Timber.i("*****FOUND USERS FOR INVITING, $results")
                    for (result in results) {

                        val email = result.email
                        val name = result.username
                        Timber.i("*********USER IN RESULTS, email is $email name is $name going to add to list")
                        returnList.add(result)
                    }
                    Timber.i("*********RETURNING USER LIST its value is $returnList")
                    return@async returnList
                } else return@async null

            } catch (e: ParseException) {
                Timber.i("*******tried to search for a user name or email to return, failed $e ${e.message}")
                return@async null
            }

        }
        return@coroutineScope deferred.await()


    }

    fun addPoolToCurrentUser(id: String) {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser != null) {
            //adds the id of the pool object to the users array, can be fetched this way
            currentUser.addUnique("pools", id)
            currentUser.saveInBackground() { e ->
                if (e == null) {
                    //saved fine
                } else
                    Timber.i("*******parse error updating user pool: code: ${e.code} cause: ${e.cause} message: ${e.message}")
            }
        }
    }

    fun removeUserPool(pool: ParsePool) {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser != null) {
            val pools = currentUser.get("pools").toString()
            val listFromPools = gson.fromJson(pools, Array<ParsePool>::class.java).toList()
            val arrayOfPools = ArrayList<ParsePool>()
            arrayOfPools.addAll(listFromPools)
            arrayOfPools.remove(pool)

            val newJsonPools = JSONArray(arrayOfPools)
            currentUser.put("pools", newJsonPools)
            currentUser.saveInBackground()
        }

    }

    fun getUserByName(name: String): ParsePoolPlayer? {
        val query = ParseUser.getQuery()
        query.whereEqualTo("username", name.trim())

        try {
            val results = query.find()
            val result = results[0]

            val name = result.getString("username").toString()
            val email = result.getString("email").toString()
            val id = result.objectId
            val returnUser = ParsePoolPlayer(
                name,
                email,
                id
            )

            return returnUser

        } catch (e: ParseException) {
            Timber.i("*****tried to get userbyname from repo, error was $e")
            return null
        }
    }


}