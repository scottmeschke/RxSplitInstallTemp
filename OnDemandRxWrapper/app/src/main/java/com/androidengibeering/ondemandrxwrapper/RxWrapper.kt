package com.androidengibeering.ondemandrxwrapper

import android.content.Context
import android.content.IntentSender
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

fun stuff() {
  val ctx: Context = null
  val manager = SplitInstallManagerFactory.create(ctx)
  manager.getSessionState()
}

interface RxSplitInstallManager {

  fun installer(): RxSplitInstaller
  fun deferredInstaller(): RxDeferredSplitInstaller
  fun canceler(): RxSplitInstallationCanceler
  fun sessionsObserver(): RxSplitInstallationSessionsObserver
  fun modulesObserver(): RxSplitModulesObserver

}

interface RxSplitModulesObserver {
  fun observeInstalledModules(): Observable<Set<String>>
  fun getInstalledModules(): Set<String>
}

interface RxSplitInstallationSessionsObserver {
  fun observeState(sessionId: Int): Observable<SplitInstallSessionState>
  fun observeStates(): Observable<List<SplitInstallSessionState>>
  fun getSessionState(sessionId: Int): SplitInstallSessionState
  fun getSessionStates(): List<SplitInstallSessionState>
}

interface RxSplitInstallationCanceler {

  //emits all onNext(), noisy
  fun cancelWithProgressUpdates(sessionId: Int): Observable<SplitInstallCancelResult>

  //only emits final states
  fun cancel(sessionId: Int): Single<SplitInstallCancelResult>

  //pushes failures into onError
  fun cancelOrError(sessionId: Int): Completable

  data class SplitInstallCancelResult(val sessionId: Int, val hasCanceled: Boolean)
}

interface RxDeferredSplitInstaller {
  fun deferredInstall(modules: Set<String>): Completable
  fun deferredUninstall(modules: Set<String>): Completable
}

interface RxSplitInstaller {

  //emits updates as the install process happens, with all terminal events (including failures) coming downstream
  fun installWithProgressUpdates(req: SplitInstallRequest): Observable<SplitInstallResult>

  //only emits final states (require conf, failure, or complete)
  fun install(req: SplitInstallRequest): Single<SplitInstallResult>

  //errors or pause (user required conf) go down onError stream
  fun installOrError(req: SplitInstallRequest): Completable
}

data class SplitInstallError(val state: SplitInstallSessionState): Exception()

sealed class SplitInstallResult {
  abstract val sessionId: Int

  data class Pending(override val sessionId: Int): SplitInstallResult()

  data class AwaitingUserConfirmation(override val sessionId: Int, val totalBytes: Long,
                                      val intentSender: IntentSender): SplitInstallResult()

  sealed class Active: SplitInstallResult() {
    data class Downloading(val bytesDownloaded: Long, val totalBytes: Long, val completed: Boolean, override val sessionId: Int): Active()
    data class Installing(override val sessionId: Int): Active()
  }

  sealed class Complete(override val sessionId: Int): SplitInstallResult()

  data class Failed(val errorCode: SplitInstallErrorCode, override val sessionId: Int): SplitInstallResult()

  fun stuff() {

    /*
     int NO_ERROR = 0;
    int ACTIVE_SESSIONS_LIMIT_EXCEEDED = -1;
    int MODULE_UNAVAILABLE = -2;
    int INVALID_REQUEST = -3;
    int SESSION_NOT_FOUND = -4;
    int API_NOT_AVAILABLE = -5;
    int NETWORK_ERROR = -6;
    int ACCESS_DENIED = -7;
    int INCOMPATIBLE_WITH_EXISTING_SESSION = -8;
    int SERVICE_DIED = -9;
    int INTERNAL_ERROR = -100;
     */


    /*
    int UNKNOWN = 0;
    int PENDING = 1;
    int REQUIRES_USER_CONFIRMATION = 8;
    int DOWNLOADING = 2;
    int DOWNLOADED = 3;
    int INSTALLING = 4;
    int INSTALLED = 5;
    int FAILED = 6;
    int CANCELING = 9;
    int CANCELED = 7;
     */
  }
}

