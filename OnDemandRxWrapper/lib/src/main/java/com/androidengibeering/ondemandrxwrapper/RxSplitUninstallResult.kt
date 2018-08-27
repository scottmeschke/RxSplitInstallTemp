package com.androidengibeering.ondemandrxwrapper

sealed class RxSplitUninstallResult {

  abstract val modules: Set<String>

  data class Successful(override val modules: Set<String>): RxSplitUninstallResult()
  data class Failed(override val modules: Set<String>, val error: Exception): RxSplitUninstallResult()
}