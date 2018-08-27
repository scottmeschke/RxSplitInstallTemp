package com.androidengibeering.ondemandrxwrapper.internal.completable

import com.androidengibeering.ondemandrxwrapper.RxSplitInstallResult
import com.androidengibeering.ondemandrxwrapper.internal.isError
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class ConcatCreateSessionAndObserveSessionCompletable(private val createAndObserveUpstream: Observable<RxSplitInstallResult>) : Completable() {

  override fun subscribeActual(observer: CompletableObserver) {
    createAndObserveUpstream.subscribe(InternalObserver(observer))
  }

  class InternalObserver(private val finalObserver: CompletableObserver) : Observer<RxSplitInstallResult> {

    private var pendingResult: RxSplitInstallResult? = null

    override fun onSubscribe(d: Disposable) {
      finalObserver.onSubscribe(d)
    }

    override fun onNext(t: RxSplitInstallResult) {
      pendingResult = t
    }

    override fun onComplete() {
      when (pendingResult) {
        null -> finalObserver.onError(Exception("Upstream never emitted to emitted."))
        else -> {
          when (pendingResult!!.isError) {
            true -> {
              TODO()
              //finalObserver.onError(RxSplitExceptions(pendingResult!!))
            }
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
