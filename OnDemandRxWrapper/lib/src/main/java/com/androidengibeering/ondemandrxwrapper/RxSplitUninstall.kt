package com.androidengibeering.ondemandrxwrapper

import io.reactivex.Completable
import io.reactivex.Single

interface RxSplitUninstall {
  fun deferred()
  fun asCompletable(): Completable
  fun asSingle(): Single<RxSplitUninstallResult>
}