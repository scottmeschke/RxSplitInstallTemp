package com.androidengibeering.ondemandrxwrapper.internal.rx

import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallResult
import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallError
import com.androidengibeering.ondemandrxwrapper.internal.RxSplitInstallResultUtils
import io.reactivex.*
import io.reactivex.disposables.Disposable

class MergedInitializeAndInstallCompletable(private val upstream: Observable<RxSplitInstallResult>) : Completable() {

  override fun subscribeActual(observer: CompletableObserver) {
    upstream.subscribe(CompletableSimplifiedObserver(observer))
  }

  //todo rename
  class CompletableSimplifiedObserver(private val finalObserver: CompletableObserver)
    : Observer<RxSplitInstallResult> {

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
        else -> {
          when (RxSplitInstallResultUtils.isConsideredError(pendingResult!!)) {
            true -> finalObserver.onError(RxSplitInstallError(pendingResult!!))
            false -> finalObserver.onComplete()
          }
        }
      }
    }

    override fun onError(e: Throwable) {
      finalObserver.onError(e)
    }

  }

}