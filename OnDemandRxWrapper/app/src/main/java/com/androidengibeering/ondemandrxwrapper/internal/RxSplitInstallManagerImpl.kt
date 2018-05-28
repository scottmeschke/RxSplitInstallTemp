package com.androidengibeering.ondemandrxwrapper.internal

import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallManager
import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallResult
import com.androidengibeering.ondemandrxwrapper.internal.rx.*
import com.google.android.play.core.splitinstall.SplitInstallManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class RxSplitInstallManagerImpl(private val installManager: SplitInstallManager) : RxSplitInstallManager {

  override fun deferredInstall(modules: Set<String>): Completable {
    return DeferredInstallCompletable(modules, installManager)
  }

  override fun deferredUninstall(modules: Set<String>): Completable {
    return DeferredUninstallCompletable(modules, installManager)
  }

  override fun deferredInstall(module: String): Completable {
    return deferredInstall(setOf(module))
  }

  override fun deferredUninstall(module: String): Completable {
    return deferredUninstall(setOf(module))
  }

  override fun detailedInstall(modules: Set<String>): Observable<RxSplitInstallResult> {
    return MergedInitializeAndInstallSplitObservable(initializeObservable(modules), installManager)
  }

  override fun install(modules: Set<String>): Single<RxSplitInstallResult> {
    return MergedInitializeAndInstallSplitSingle(detailedInstall(modules))
  }

  override fun installOrError(modules: Set<String>): Completable {
    return MergedInitializeAndInstallCompletable(detailedInstall(modules))
  }

  override fun detailedInstall(module: String): Observable<RxSplitInstallResult> {
    return detailedInstall(setOf(module))
  }

  override fun install(module: String): Single<RxSplitInstallResult> {
    return MergedInitializeAndInstallSplitSingle(detailedInstall(module))
  }

  override fun installOrError(module: String): Completable {
    return MergedInitializeAndInstallCompletable(detailedInstall(module))
  }

  override fun moduleInstalled(module: String) = installManager.installedModules.contains(module)

  override fun modulesInstalled(modules: Set<String>): Boolean = installManager.installedModules.containsAll(modules)

  private fun initializeObservable(modules: Set<String>): InitializeSplitObservable {
    return InitializeSplitObservable(modules, installManager)
  }
}