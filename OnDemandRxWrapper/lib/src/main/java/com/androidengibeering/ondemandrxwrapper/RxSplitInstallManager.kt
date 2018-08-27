package com.androidengibeering.ondemandrxwrapper

interface RxSplitInstallManager {
  fun install(vararg modules: String): RxSplitInstall
  fun uninstall(vararg modules: String): RxSplitUninstall
  fun check(): RxSplitChecker
}
