package com.androidengibeering.ondemandrxwrapper.internal.rx

import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallResult
import com.androidengibeering.ondemandrxwrapper.internal.RxSplitInstallResultUtils
import com.androidengibeering.ondemandrxwrapper.internal.SplitInstallSessionStateTransformer
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class InstallSplitSessionStateObservable(private val sessionId: Int,
                                         private val splitInstallManager: SplitInstallManager) : Observable<RxSplitInstallResult>() {

  override fun subscribeActual(observer: Observer<in RxSplitInstallResult>) {
    val listener = Listener(sessionId, splitInstallManager, observer)
    observer.onSubscribe(listener)
    splitInstallManager.registerListener(listener)
  }

  class Listener(private val sessionId: Int,
                 private val splitInstallManager: SplitInstallManager,
                 private val observer: Observer<in RxSplitInstallResult>) :
      Disposable, SplitInstallStateUpdatedListener {

    @Volatile
    private var disposed: Boolean = false
    @Volatile
    private var emittedTerminal: Boolean = false

    init {
      pushInitialStateIfAvailable()
    }

    private fun pushInitialStateIfAvailable() {
      val result = splitInstallManager.getSessionState(sessionId).result
      if (result != null) {
        val rxState = SplitInstallSessionStateTransformer.transformState(result)
        observer.onNext(rxState)
        if (RxSplitInstallResultUtils.isTerminal(rxState)) {
          emittedTerminal = true
          splitInstallManager.unregisterListener(this)
          observer.onComplete()
        }
      }
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
      if (!disposed && !emittedTerminal) {
        if (state.sessionId() != sessionId) return
        val rxState = SplitInstallSessionStateTransformer.transformState(state)
        observer.onNext(rxState)
        if (RxSplitInstallResultUtils.isTerminal(rxState)) {
          emittedTerminal = true
          splitInstallManager.unregisterListener(this)
          observer.onComplete()
        }
      }
    }

    override fun isDisposed(): Boolean {
      return disposed
    }

    override fun dispose() {
      if (!emittedTerminal) {
        splitInstallManager.cancelInstall(sessionId)
        splitInstallManager.unregisterListener(this)
      }
      disposed = true
    }
  }
}