package uk.co.armedpineapple.innoextract.service

import android.os.HandlerThread

internal class LoggingThread internal constructor(internal var callback: ExtractCallback) :
    HandlerThread("LoggingThread") {

    private lateinit var lineHandler: LoggerHandler

    fun PostLogMessage(streamNo: Int, text: String) {
        val msg = lineHandler.obtainMessage()
        msg.what = streamNo
        msg.obj = text
        lineHandler.sendMessage(msg)
    }

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        lineHandler = LoggerHandler(looper, callback)
    }
}