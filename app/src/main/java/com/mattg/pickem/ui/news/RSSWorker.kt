package com.mattg.pickem.ui.news

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class RSSWorker(context: Context, workParams: WorkerParameters) : Worker(context, workParams) {

    fun downloadXml(urlPath: String): String {
        val xmlResult = StringBuilder()
        try {
            val url = URL(urlPath)
            val connection: HttpURLConnection = url.openConnection() as HttpsURLConnection
            val response = connection.responseCode
            Timber.i("*********response code $response")
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val inputBuffer = CharArray(1000)

            var charsRead = 0
            //continues through the end of the input stream
            while (charsRead >= 0) { //only loops while there is over 0 things to read
                charsRead = reader.read(inputBuffer)
                if (charsRead > 0) { //coded defensively, could be a zero but likely wont be
                    xmlResult.append(
                        String(
                            inputBuffer,
                            0,
                            charsRead
                        )
                    ) //adding char for char to result
                }
            }
            reader.close() //once done close the reader
            Timber.i("****** recieved: ${xmlResult.length} bytes, and ${xmlResult}")
            return xmlResult.toString()
        } catch (ex: MalformedURLException) {
            Timber.e("***** invalid url exception ${ex.message}")
        } catch (ex: IOException) {
            Timber.e("******** IO exception : ${ex.message}")
        } catch (ex: Exception) {
            Timber.e("******** unknown exception ${ex.printStackTrace()}")
        }

        return "" //if it returns empty string, it is due to error

    }

    override fun doWork(): Result {
        val stringToGet = inputData.getString("url").toString()
        downloadXml(urlPath = stringToGet)
        return Result.success()
    }
}