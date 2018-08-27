package com.androidengibeering.ondemandrxwrapper.internal.completable

import com.androidengibeering.ondemandrxwrapper.internal.listener.VoidTaskCompletableListener
import com.google.android.play.core.splitinstall.SplitInstallManager
import io.reactivex.Completable
import io.reactivex.CompletableObserver

class DeferredInstallModulesCompletable(
  private val modules: Set<String>,
  private val splitInstallManager: SplitInstallManager
) : Completable() {

  override fun subscribeActual(s: CompletableObserver) {
    val task = splitInstallManager.deferredInstall(modules.toList())
    val listener = VoidTaskCompletableListener(task, s)
    s.onSubscribe(listener)
    listener.init()
  }

}
