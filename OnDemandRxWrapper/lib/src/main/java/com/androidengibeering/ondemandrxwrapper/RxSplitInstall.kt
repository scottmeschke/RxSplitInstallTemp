package com.androidengibeering.ondemandrxwrapper

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface RxSplitInstall {
  fun deferred()
  fun asSingle(): Single<RxSplitInstallResult>
  fun asObservable(): Observable<RxSplitInstallResult>
  fun asCompletable(): Completable
}