package com.axalocklibs.axainterface

interface IAPIAxaResponse {
    fun onSuccess(response: String, tag: String)
    fun onFailure(response: String, tag: String)

}