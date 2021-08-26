package com.mattg.pickem.parsebackend

import androidx.lifecycle.MutableLiveData
import com.mattg.pickem.db.Pick
import com.mattg.pickem.parsebackend.models.ParsePick
import com.parse.*
import timber.log.Timber


class ParsePickRepository {

    val currentPick = MutableLiveData<ParsePick>()
    val justSavedPickId = MutableLiveData<String>()

    fun createPick(input: Pick, user: ParseUser): String? {
        val pick = ParseObject("Pick")
        user.email?.let { pick.put("ownerEmail", it) }
        pick.put("picks", input.picksGamesOnly)
        pick.put("finalPoints", input.finalPoints)
        pick.put("week", input.week)
        pick.put("ownerName", user.username)

        return try {
            pick.save()
            pick.objectId
            //            pick.saveInBackground{ e ->
            //                if(e == null){
            //                    //pick saved with no errors
            //                    justSavedPickId.value = pick.objectId
            //
            //                } else
            //                    Timber.i("******parse error saving pick : code - ${e.code} cause - ${e.cause} message - ${e.message} ")
            //            }
        } catch (e: ParseException) {
            null

        }

    }

    fun getPickById(objectId: String): ParsePick? {
        val query = ParseQuery.getQuery<ParseObject>("Pick")
            .whereEqualTo("objectId", objectId)
        try {
            val results = query.find()
            if (!results.isEmpty()) {
                val pick = results[0]
                val returnPick = ParsePick(
                    finalPoints = pick.getString("finalPoints"),
                    picks = pick.getString("picks"),
                    week = pick.getString("week"),
                    ownerEmail = pick.getString("ownerEmail"),
                    ownerName = pick.getString("ownerName"),
                    objectId = pick.objectId
                )
                return returnPick
            } else
                return null


        } catch (e: ParseException) {
            return null
        }

    }

    fun updatePick(objectId: String, updateField: String, updateData: String) {
        val query = ParseQuery.getQuery<ParseObject>("Pick")
        query.getInBackground(objectId, GetCallback { pick, e ->
            if (e == null) {
                pick.put(updateField, updateData)
                pick.saveInBackground()
            } else {
                Timber.i("******parse error updating pick : code - ${e.code} cause - ${e.cause} message - ${e.message} ")

            }
        })
    }

    fun deletePickById(objectId: String) {
        val query = ParseQuery.getQuery<ParseObject>("Pick")
        query.getInBackground(objectId, GetCallback { pick, e ->
            if (e == null) {
                pick.deleteInBackground()
            } else
                Timber.i("******parse error deleting pick : code - ${e.code} cause - ${e.cause} message - ${e.message} ")

        })
    }

    /**
     *
     * Insert Picks into users parse db
     * @param pickId String: id of the pick
     * @param objectId String(Nullable): object id for the "Pool" these picks
     * are associated with
     */
    fun addPicksToPool(pickId: String, objectId: String?) {
        val query = ParseQuery.getQuery<ParseObject>("Pool")
        query.whereEqualTo("objectId", objectId)
        try {
            val result = query.find()
            val pool = result[0]
            val picks = pool.getList<String>("picks")
            picks?.add(pickId)
            if (picks != null) {
                pool.put("picks", picks)
            }
            pool.save()
        } catch (e: ParseException) {
            Timber.i("******trying to update pool with picks failed $e ${e.message}")
        }


    }
}