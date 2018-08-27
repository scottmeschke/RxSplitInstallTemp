package com.androidengibeering.ondemandrxwrapper.internal.single

import com.androidengibeering.ondemandrxwrapper.RxSplitInstallResult
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class ConcatCreateSessionAndObserveSessionSingle(private val createAndObserveUpstream: Observable<RxSplitInstallResult>) :
  Single<RxSplitInstallResult>() {

  override fun subscribeActual(observer: io.reactivex.SingleObserver<in RxSplitInstallResult>) {
    createAndObserveUpstream.subscribe(InternalObserver(observer))
  }

  class InternalObserver(private val finalObserver: SingleObserver<in RxSplitInstallResult>) :
    Observer<RxSplitInstallResult> {

    private var pendingResult: RxSplitInstallResult? = null

    override fun onSubscribe(d: Disposable) {
      finalObserver.onSubscribe(d)
    }

    override fun onNext(t: RxSplitInstallResult) {
      pendingResult = t
    }

    override fun onComplete() {
      when (pendingResult) {
        null -> finalObserver.onError(Exception("Upstream observable never emitted a next."))
        else -> finalObserver.onSuccess(pendingResult!!)
      }
    }

    override fun onError(e: Throwable) {
      finalObserver.onError(e)
    }

  }
}
