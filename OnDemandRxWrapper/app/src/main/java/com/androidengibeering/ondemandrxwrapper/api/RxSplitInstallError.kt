package com.androidengibeering.ondemandrxwrapper.api

import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallResult

class RxSplitInstallError(private val rxSplitInstallResult: RxSplitInstallResult): Exception() {
  override val message: String?
    get() = "Split failed to install: $rxSplitInstallResult"
}