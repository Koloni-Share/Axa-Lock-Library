package com.axalocklibs.axainterface

interface IAPIAxaLockCallback {

    fun onAxaConnected(eventType:String, lockValue: String)
    fun onAxaDisconnected(eventType:String, lockValue: String)
    fun onAxaLockUnLockSuccessfully(eventType:String, lockValue: String)
    fun onAxaDiscovered(eventType:String, lockValue: String)
    fun onAxaERLNotFound(eventType:String, lockValue: String)
    fun onAxaStartEkeyUpdate(eventType:String, lockValue: String)
    fun onAxaEkeyUpdatedSuccessfully(eventType:String, lockValue: String)
    fun onAxaEkeyUpdatedSuccessfully(eventType:String, lockValue: String, updatedJWTToken: String)
    fun onAxaConnecting(eventType:String, lockValue: String)
    fun onAxaStartLockUnlock(eventType:String, lockValue: String)
    fun onRetryInitService()

}