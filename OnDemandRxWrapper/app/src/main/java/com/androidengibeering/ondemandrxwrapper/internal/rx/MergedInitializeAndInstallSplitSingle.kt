package com.androidengibeering.ondemandrxwrapper.internal.rx

import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallResult
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class MergedInitializeAndInstallSplitSingle(private val upstream: Observable<RxSplitInstallResult>) : Single<RxSplitInstallResult>() {

  override fun subscribeActual(observer: SingleObserver<in RxSplitInstallResult>) {
    upstream.subscribe(SingleSimplifiedObserver(observer))
  }

  //todo rename
  class SingleSimplifiedObserver(private val finalObserver: SingleObserver<in RxSplitInstallResult>) : Observer<RxSplitInstallResult> {

    private var pendingResult: RxSplitInstallResult? = null

    override fun onSubscribe(d: Disposable) {
      finalObserver.onSubscribe(d)
    }

    override fun onNext(t: RxSplitInstallResult) {
      pendingResult = t
    }

    override fun onComplete() {
      when (pendingResult) {
        null -> finalObserver.onError(Exception("upstream install stream never completed to emit a terminal result"))
        else -> finalObserver.onSuccess(pendingResult!!)
      }
    }

    override fun onError(e: Throwable) {
      finalObserver.onError(e)
    }

  }

}