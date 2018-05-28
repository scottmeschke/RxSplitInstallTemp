package com.androidengibeering.ondemandrxwrapper.api

import android.app.PendingIntent

sealed class RxSplitInstallResult {

  sealed class Initial : RxSplitInstallResult() {

    abstract val modules: Set<String>

    data class Started(override val modules: Set<String>) : Initial()
    data class Created(val sessionId: Int, override val modules: Set<String>) : Initial()
    data class Failed(val exception: Exception, override val modules: Set<String>) : Initial()
  }

  sealed class Active : RxSplitInstallResult() {

    abstract val sessionId: Int
    abstract val modules: Set<String>

    data class Pending(override val sessionId: Int,
                       override val modules: Set<String>) : Active()

    data class RequiresUserConfirmation(override val sessionId: Int,
                                        override val modules: Set<String>,
                                        val pendingIntent: PendingIntent) : Active()

    data class Downloading(override val sessionId: Int,
                           override val modules: Set<String>,
                           val bytesDownloaded: Boolean,
                           val totalBytesToDownload: Boolean,
                           val pendingInstall: Boolean): Active()

    data class Installing(override val sessionId: Int,
                          override val modules: Set<String>): Active()

    data class Completed(override val sessionId: Int,
                         override val modules: Set<String>): Active()

    data class Canceling(override val sessionId: Int,
                         override val modules: Set<String>,
                         val finished: Boolean): Active()

    data class Failed(override val sessionId: Int,
                      override val modules: Set<String>,
                      val errorCode: Int?): Active()

  }

}
