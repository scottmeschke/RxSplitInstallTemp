package com.androidengibeering.ondemandrxwrapper.api

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface RxSplitInstallManager {
  fun moduleInstalled(module: String): Boolean
  fun modulesInstalled(modules: Set<String>): Boolean
  fun deferredInstall(modules: Set<String>): Completable
  fun deferredUninstall(modules: Set<String>): Completable
  fun deferredInstall(module: String): Completable
  fun deferredUninstall(module: String): Completable


  //emits updates as the install process happens, with all terminal events (including failures) coming downstream
  fun detailedInstall(modules: Set<String>): Observable<RxSplitInstallResult>

  //only emits final states (require conf, failure, or complete)
  fun install(modules: Set<String>): Single<RxSplitInstallResult>

  //errors or pause (user required conf) go down onError stream
  fun installOrError(modules: Set<String>): Completable

  //emits updates as the install process happens, with all terminal events (including failures) coming downstream
  fun detailedInstall(module: String): Observable<RxSplitInstallResult>

  //only emits final states (require conf, failure, or complete)
  fun install(module: String): Single<RxSplitInstallResult>

  //errors or pause (user required conf) go down onError stream
  fun installOrError(module: String): Completable

}