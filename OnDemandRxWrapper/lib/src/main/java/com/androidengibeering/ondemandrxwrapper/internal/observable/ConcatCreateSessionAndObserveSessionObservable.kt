package com.androidengibeering.ondemandrxwrapper.internal.observable

import com.androidengibeering.ondemandrxwrapper.RxSplitInstallResult
import com.google.android.play.core.splitinstall.SplitInstallManager
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class ConcatCreateSessionAndObserveSessionObservable(
  private val createSessionSource: Single<RxSplitInstallResult.CreateSession>,
  private val splitInstallManager: SplitInstallManager
) : Observable<RxSplitInstallResult>() {

  override fun subscribeActual(finalObserver: Observer<in RxSplitInstallResult>) {
    createSessionSource.subscribe(InternalObserver(finalObserver, splitInstallManager))
  }

  class InternalObserver(
    private val finalObserver: Observer<in RxSplitInstallResult>,
    private val manager: SplitInstallManager
  ) : SingleObserver<RxSplitInstallResult.CreateSession>, Observer<RxSplitInstallResult>, Disposable {

    private var sessionId: Int? = null
    private var currentDisposable: Disposable? = null

    override fun onSubscribe(d: Disposable) {
      val firstSubscribe = currentDisposable == null
      currentDisposable = d
      if (firstSubscribe) finalObserver.onSubscribe(this)
    }

    override fun onSuccess(t: RxSplitInstallResult.CreateSession) {
      if (isDisposed) return
      when (t) {
        is RxSplitInstallResult.CreateSession.Created -> {
          sessionId = t.playCoreSessionId
          finalObserver.onNext(t)
          ObserveSessionObservable(sessionId!!, manager)
            .subscribe(this)
        }
        is RxSplitInstallResult.CreateSession.Failed -> {
          finalObserver.onNext(t)
          finalObserver.onComplete()
        }
      }
    }

    override fun isDisposed() = currentDisposable!!.isDisposed

    override fun dispose() {
      currentDisposable!!.dispose()
    }

    override fun onComplete() {
      finalObserver.onComplete()
    }

    override fun onNext(t: RxSplitInstallResult) {
      finalObserver.onNext(t)
    }

    override fun onError(e: Throwable) {
      finalObserver.onError(e)
    }

  }

}
