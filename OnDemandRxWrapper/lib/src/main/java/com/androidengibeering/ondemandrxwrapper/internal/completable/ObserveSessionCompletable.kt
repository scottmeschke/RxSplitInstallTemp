package com.androidengibeering.ondemandrxwrapper.internal.completable

import com.androidengibeering.ondemandrxwrapper.RxSplitInstallResult
import com.androidengibeering.ondemandrxwrapper.internal.isError
import com.androidengibeering.ondemandrxwrapper.internal.isTerminal
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class ObserveSessionCompletable(private val upstream: Observable<RxSplitInstallResult>) :
  Completable() {

  override fun subscribeActual(s: CompletableObserver) {
    upstream.subscribe(ObserveSessionInstallingObserver(s))
  }

  class ObserveSessionInstallingObserver(
    private val finalObserver: CompletableObserver
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
        when (t.isError) {
          true -> finalObserver.onError(Exception("$t"))
          false -> finalObserver.onComplete()
        }
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
