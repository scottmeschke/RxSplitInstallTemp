package com.androidengibeering.ondemandrxwrapper.internal.rx

import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallResult
import com.google.android.play.core.splitinstall.SplitInstallManager
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MergedInitializeAndInstallSplitObservable(private val initialSource: Observable<RxSplitInstallResult.Initial>,
                                                private val splitInstallManager: SplitInstallManager)
  : Observable<RxSplitInstallResult>() {

  override fun subscribeActual(observer: Observer<in RxSplitInstallResult>) {
    initialSource.subscribe(MergeInitializeAndInstallSplitObserver(observer, splitInstallManager))
  }

  class MergeInitializeAndInstallSplitObserver(private val finalObserver: Observer<in RxSplitInstallResult>,
                                               private val manager: SplitInstallManager)
    : Observer<RxSplitInstallResult.Initial>, Disposable {

    private var sessionId: Int? = null
    private var disposable: Disposable? = null

    override fun onSubscribe(d: Disposable) {
      disposable = d
    }

    override fun onNext(t: RxSplitInstallResult.Initial) {
      if (!isDisposed) {
        if (t is RxSplitInstallResult.Initial.Created) sessionId = t.sessionId
        finalObserver.onNext(t)
      }
    }

    override fun onComplete() {
      if (!isDisposed && sessionId != null) {
        val sessionObservable = InstallSplitSessionStateObservable(sessionId!!, manager)
        sessionObservable.subscribe(finalObserver)
      }
    }

    override fun onError(e: Throwable) {
      if (!isDisposed) {
        finalObserver.onError(e)
      }
    }

    override fun isDisposed(): Boolean {
      return disposable!!.isDisposed
    }

    override fun dispose() {
      return disposable!!.dispose()
    }

  }

}