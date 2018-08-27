package com.androidengibeering.ondemandrxwrapper

interface RxSplitChecker {
  fun allInstalled(): Set<String>
  fun isInstalled(vararg modules: String): Boolean
}