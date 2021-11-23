package com.axalocklibs.webservice

import android.app.Activity
import android.util.Log
import com.androidnetworking.common.Priority
import com.axalocklibs.`interface`.IAPIResponse
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.TimeUnit

object ApiRequest {
    private var response = ""
    fun callPOSTAPI(
        activity: Activity?,
        url: String,
        params: HashMap<String, Any>,
        tag: String,
        apiResponse: IAPIResponse,
        appVersion: String,
        authorizationHeader: String,
        bearerAuthorization: String
    ) {
        Log.e("WS_PARAM_", "$tag  : $params")
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build()
        Rx2AndroidNetworking.post(url)
            .setOkHttpClient(okHttpClient)
            .addBodyParameter("android_version", appVersion)
            .addHeaders("Authorization", authorizationHeader)
            .addBodyParameter(params)
            .setPriority(Priority.MEDIUM)
            .build()
            .stringObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(jsonObject: String) {
                    response = jsonObject + ""
                    Log.e("WS_RESP_", tag + " : " + response)
                }

                override fun onError(e: Throwable) {
                    try {
                        e.printStackTrace()
                        apiResponse.onFailure(e.localizedMessage, tag)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }

                override fun onComplete() {
                    try {
                        apiResponse.onSuccess(response, tag)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        apiResponse.onFailure(e.localizedMessage, tag)
                    }
                }
            })
    }
}