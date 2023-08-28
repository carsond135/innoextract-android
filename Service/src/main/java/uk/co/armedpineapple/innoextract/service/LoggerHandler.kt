package uk.co.armedpineapple.innoextract.service

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log

internal class LoggerHandler(looper: Looper, val callback: ExtractCallback) : Handler(looper) {

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            LogFileDescriptors.STDOUT.fd -> parseOut(msg.obj as String)
            LogFileDescriptors.STDERR.fd -> parseErr(msg.obj as String)
            else -> run {
                Log.w(LOG_TAG, "Unknown message")
                parseErr(msg.obj as String)
            }
        }
    }

    private fun parseOut(line: String) {
        if (line.isEmpty()) return
        Log.i(LOG_TAG, line)
    }

    private fun parseErr(line: String) {
        if (line.isEmpty()) return
        Log.e(LOG_TAG, line)
    }

    companion object {
        private const val LOG_TAG = "LoggerHandler"
    }
}