package com.androidengibeering.ondemandrxwrapper.internal.rx

import com.google.android.play.core.splitinstall.SplitInstallManager
import io.reactivex.Completable
import io.reactivex.CompletableObserver

class DeferredUninstallCompletable(private val modules: Set<String>,
                                 private val splitInstallManager: SplitInstallManager): Completable() {

  override fun subscribeActual(s: CompletableObserver) {
    val task = splitInstallManager.deferredUninstall(modules.toMutableList())
    val listener = VoidTaskCompletableListener(task, s)
    s.onSubscribe(listener)
  }

}