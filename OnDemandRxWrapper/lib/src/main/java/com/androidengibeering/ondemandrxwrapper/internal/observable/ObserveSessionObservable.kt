package com.androidengibeering.ondemandrxwrapper.internal.observable

import com.androidengibeering.ondemandrxwrapper.RxSplitInstallResult
import com.androidengibeering.ondemandrxwrapper.internal.isTerminal
import com.androidengibeering.ondemandrxwrapper.internal.transformSplitInstallSessionStateToResult
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class ObserveSessionObservable(
  private val sessionId: Int,
  private val splitInstallManager: SplitInstallManager
) : Observable<RxSplitInstallResult>() {

  override fun subscribeActual(observer: Observer<in RxSplitInstallResult>) {
    val listener = Listener(sessionId, splitInstallManager, observer)
    observer.onSubscribe(listener)
    listener.pushInitialState()
    splitInstallManager.registerListener(listener)
  }

  class Listener(
    private val sessionId: Int,
    private val splitInstallManager: SplitInstallManager,
    private val observer: Observer<in RxSplitInstallResult>
  ) :
    Disposable, SplitInstallStateUpdatedListener {

    @Volatile
    private var disposed: Boolean = false
    @Volatile
    private var emittedTerminal: Boolean = false

    fun pushInitialState() {
      val currentResult =
        splitInstallManager.getSessionState(sessionId).result?.let {
        transformSplitInstallSessionStateToResult(it)
      }
      when (currentResult) {
        null -> {
          splitInstallManager.unregisterListener(this)
          emittedTerminal = true
          observer.onError(Exception("No session with id: $sessionId found."))
        }
        else -> {
          observer.onNext(currentResult)
          if (currentResult.isTerminal) {
            splitInstallManager.unregisterListener(this)
            emittedTerminal = true
            observer.onComplete()
          }
        }
      }
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
      if (disposed || emittedTerminal) return
      if (state.sessionId() != sessionId) return
      val rxState = transformSplitInstallSessionStateToResult(state)
      observer.onNext(rxState)
      if (rxState.isTerminal) {
        emittedTerminal = true
        splitInstallManager.unregisterListener(this)
        observer.onComplete()
      }
    }

    override fun isDisposed(): Boolean = disposed

    override fun dispose() {
      if (!emittedTerminal) {
        splitInstallManager.cancelInstall(sessionId)
        splitInstallManager.unregisterListener(this)
      }
      disposed = true
    }
  }
}
