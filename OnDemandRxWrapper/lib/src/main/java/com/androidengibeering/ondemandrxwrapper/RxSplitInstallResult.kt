package com.androidengibeering.ondemandrxwrapper

import android.app.PendingIntent

sealed class RxSplitInstallResult {

  abstract val modules: Set<String>

  sealed class CreateSession : RxSplitInstallResult() {
    data class Created(val playCoreSessionId: Int, override val modules: Set<String>) : CreateSession()
    data class Failed(val exception: Exception, override val modules: Set<String>) : CreateSession()
  }

  sealed class ActiveSession : RxSplitInstallResult() {

    abstract val playCoreSessionId: Int

    data class Pending(
      override val playCoreSessionId: Int,
      override val modules: Set<String>
    ) : ActiveSession()

    data class Downloading(
      override val playCoreSessionId: Int,
      override val modules: Set<String>,
      val bytesDownloaded: Boolean,
      val totalBytesToDownload: Boolean,
      val pendingInstall: Boolean
    ) : ActiveSession()

    data class Installing(
      override val playCoreSessionId: Int,
      override val modules: Set<String>
    ) : ActiveSession()

    data class Canceling(
      override val playCoreSessionId: Int,
      override val modules: Set<String>,
      val finished: Boolean
    ) : ActiveSession()

  }

  sealed class Finished : RxSplitInstallResult() {

    data class Failure(
      val playCoreSessionId: Int,
      override val modules: Set<String>,
      val errorCode: Int?
    ) : Finished()

    data class Success(
      val playCoreSessionId: Int,
      override val modules: Set<String>
      ) : Finished()

    data class RequiresUserConfirmation(
      val playCoreSessionId: Int,
      override val modules: Set<String>,
      val pendingIntent: PendingIntent
    ) : Finished()

    data class AlreadyInstalled(
      override val modules: Set<String>
    ) : Finished()

  }

}
