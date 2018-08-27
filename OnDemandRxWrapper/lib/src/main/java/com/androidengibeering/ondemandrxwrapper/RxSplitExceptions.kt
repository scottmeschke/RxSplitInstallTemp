package com.androidengibeering.ondemandrxwrapper

class RxSplitInstallFinishedWithFailureException(
  private val rxSplitInstallResult: RxSplitInstallResult.Finished.Failure
) :
  Exception() {
  override val message: String?
    get() = "Split failed to install: $rxSplitInstallResult"
}


