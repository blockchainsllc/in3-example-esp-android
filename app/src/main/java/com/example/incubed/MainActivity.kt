package com.example.incubed

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import java.util.*
import kotlin.concurrent.schedule

//Setup your local ip address.
private const val ipaddress = "http://192.168.178.64"
private const val localdns = "http://slock.local"
private const val slockitUrl = "%s/api/%s"
// slockit endpoints for verification rest server.
private const val hasAccess = "access"
private const val retrieve = "retrieve"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        retrieveButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                in3_exec(hasAccess)
                // retrieve the results after some seconds, the request inside the esp is async.
                // so results will be returned after a while.
                Timer("retrieve", false).schedule(1000 * 5) {
                    in3_exec("retrieve")
                }
            }
        }
    }
    /**
     * Http requests to local http server inside esp32
     * @param String path the get url path
     */

    fun in3_exec(path: String) {

        slockitUrl.format(ipaddress,path).httpGet().responseString { request, _, result ->
            //do something with response
            when (result) {
                is Result.Failure -> {
                    val error = result.toString()
                    println(error)
                }
                is Result.Success -> {
                    var data = result.toString()
                    GlobalScope.launch(Dispatchers.Main) {
                        outText.text = withContext(Dispatchers.Default) {
                            data
                        }
                    }
                }

            }
        }


    }


}