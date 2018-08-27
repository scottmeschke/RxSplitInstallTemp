package com.androidengibeering.ondemandrxwrapper.internal.single

import com.androidengibeering.ondemandrxwrapper.RxSplitInstallResult
import com.androidengibeering.ondemandrxwrapper.internal.isTerminal
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class ObserverSessionSingle(private val upstream: Observable<RxSplitInstallResult>) :
  Single<RxSplitInstallResult>() {

  override fun subscribeActual(observer: SingleObserver<in RxSplitInstallResult>) {
    upstream.subscribe(ObserveSessionInstallingSingleObserver(observer))
  }

  class ObserveSessionInstallingSingleObserver(
    private val finalObserver: SingleObserver<in RxSplitInstallResult>
  ) :
    Observer<RxSplitInstallResult> {

    private var emittedTerminal = false

    override fun onComplete() {
      /* no-op */
    }

    override fun onSubscribe(d: Disposable) {
      finalObserver.onSubscribe(d)
    }

    override fun onNext(t: RxSplitInstallResult) {
      if (t.isTerminal && !emittedTerminal) {
        emittedTerminal = true
        finalObserver.onSuccess(t)
      }
    }

    override fun onError(e: Throwable) {
      if (!emittedTerminal) {
        emittedTerminal = true
        finalObserver.onError(e)
      }
    }

  }

}
