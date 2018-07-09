package com.moviegetter.api

/**
 *Created by Aramis
 *Date:2018/6/29
 *Description:
 */
object Api {
    private const val baseUrl = "http://192.168.40.17:5001/"
//    private const val baseUrl = "http://192.168.40.17:5001/"

    const val index = baseUrl + "index"
    const val createUser = baseUrl + "create_user"
    const val getRole = baseUrl + "get_role"
    const val markIn = baseUrl + "mark_in"
    const val markOut = baseUrl + "mark_out"
}