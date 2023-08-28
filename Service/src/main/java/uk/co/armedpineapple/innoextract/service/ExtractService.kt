package uk.co.armedpineapple.innoextract.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.Keep
import androidx.core.app.NotificationCompat
import com.lazygeniouz.dfc.file.DocumentFileCompat
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ExtractService : Service(), IExtractService {

    private lateinit var temporaryRoot: File
    private var extractDocumentRoot: DocumentFileCompat? = null
    private var extractRoot: File? = null
    private var documentCache: DocumentFileCache? = null
    private var isBusy = false
    private var callback: ExtractCallback? = null
    private var configuration: Configuration? = null
    private var currentFile: String? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var finalNotificationBuilder: NotificationCompat.Builder

    private var loggingThread: LoggingThread? = null
    private var serviceBinder = ServiceBinder()

    override val isExtracting get() = isBusy

    private external fun nativePrepare()
    private external fun nativeExtract(sourceFd: Int, extractDir: String): Int
    private external fun nativeCheck(sourceFd: Int): InnoValidationResult

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int = START_NOT_STICKY

    private fun createNotificationChannel() {
        val progressChannel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        progressChannel.description = getString(R.string.notification_channel_description)
        progressChannel.enableLights(false)
        progressChannel.enableVibration(false)
        progressChannel.setSound(null, null)
        notificationManager.createNotificationChannel(progressChannel)
    }

    override fun onCreate() {
        super.onCreate()

        temporaryRoot = File(noBackupFilesDir, "extract")
        temporaryRoot.mkdirs()
        temporaryRoot.deleteOnExit()

        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
    }


}