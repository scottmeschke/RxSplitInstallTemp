package com.androidengibeering.ondemandrxwrapper.internal

import com.androidengibeering.ondemandrxwrapper.*
import com.google.android.play.core.splitinstall.SplitInstallManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class RxSplitManagerImpl(private val installManager: SplitInstallManager) :
  RxSplitInstallManager, RxSplitChecker {

  override fun allInstalled(): Set<String> = installManager.installedModules ?: emptySet()

  override fun isInstalled(vararg modules: String) =
    installManager.installedModules?.containsAll(modules.toList()) ?: modules.isNotEmpty()

  override fun install(vararg modules: String): RxSplitInstall {
    return RxSplitInstallImpl(modules.toSet(), installManager)
  }

  override fun uninstall(vararg modules: String): RxSplitUninstall {
    return RxSplitUninstallImpl(modules.toSet(), installManager)
  }

  override fun check(): RxSplitChecker = this

}


class RxSplitInstallImpl(private val modules: Set<String>, private val manager: SplitInstallManager) : RxSplitInstall {

  init {
    require(modules.isNotEmpty())
  }

  override fun deferred() {
    manager.deferredInstall(modules.toList())
  }

  override fun asSingle(): Single<RxSplitInstallResult> {
    TODO()
  }

  override fun asObservable(): Observable<RxSplitInstallResult> {
    TODO()
  }

  override fun asCompletable(): Completable {
    TODO()
  }

}

class RxSplitUninstallImpl(private val modules: Set<String>, private val manager: SplitInstallManager) :
  RxSplitUninstall {

  init {
    require(modules.isNotEmpty())
  }

  override fun deferred() {
    TODO()
  }

  override fun asCompletable(): Completable {
    TODO()
  }

  override fun asSingle(): Single<RxSplitUninstallResult> {
    TODO()
  }

}