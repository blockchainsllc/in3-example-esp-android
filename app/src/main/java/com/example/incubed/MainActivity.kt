/*******************************************************************************
 * This file is part of the Incubed project.
 * Sources: https://github.com/slockit/in3
 *
 * Copyright (C) 2018-2019 slock.it GmbH, Blockchains LLC
 *
 *
 * COMMERCIAL LICENSE USAGE
 *
 * Licensees holding a valid commercial license may use this file in accordance
 * with the commercial license agreement provided with the Software or, alternatively,
 * in accordance with the terms contained in a written agreement between you and
 * slock.it GmbH/Blockchains LLC. For licensing terms and conditions or further
 * information please contact slock.it at in3@slock.it.
 *
 * Alternatively, this file may be used under the AGPL license as follows:
 *
 * AGPL LICENSE USAGE
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * [Permissions of this strong copyleft license are conditioned on making available
 * complete source code of licensed works and modifications, which include larger
 * works using a licensed work, under the same license. Copyright and license notices
 * must be preserved. Contributors provide an express grant of patent rights.]
 * You should have received a copy of the GNU Affero General Public License along
 * with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

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