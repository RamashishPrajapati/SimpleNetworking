package com.ram.simplenetworking

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_URL = "https://api.github.com/search/repositories"
        private const val SEARCH = "q=language:kotlin&sort=stars&order=desc&per_page=50"
        private const val COMPLETE_URL = "$REQUEST_URL?$SEARCH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectToServer()
    }

    private fun connectToServer() {

        if (isNetworkConnected())
            GlobalScope.launch(Dispatchers.IO) {
                val repoListJsonStr = URL(COMPLETE_URL).readText()
                parsJson(repoListJsonStr)
                Log.d(javaClass.simpleName, repoListJsonStr)
            }
        else {
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }
    }

    /*Parsing the response string to respactive JSON type*/
    private fun parsJson(repoListJsonStr: String) {
        try {
            /*as it was JSONObject type so parsing in that*/
            val jsonObject = JSONObject(repoListJsonStr)
            /*for JSONArray type use below method*/
            //val jsonArray = JSONArray(repoListJsonStr)
            Log.d(javaClass.simpleName, "$jsonObject")
        } catch (e: Exception) {
            Log.e("Buffer Error", "Error converting result $e")
        }
    }

    /*To check the internet connection is available or not */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}