package com.axalocklibs.axainterface

interface IAPIResponse {
    fun onSuccess(response: String, tag: String)
    fun onFailure(response: String, tag: String)

}