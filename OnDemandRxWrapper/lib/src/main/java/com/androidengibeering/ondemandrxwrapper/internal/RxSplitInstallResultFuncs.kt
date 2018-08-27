package com.androidengibeering.ondemandrxwrapper.internal

import com.androidengibeering.ondemandrxwrapper.RxSplitInstallResult
import com.google.android.play.core.splitinstall.SplitInstallSessionState

internal val RxSplitInstallResult.isTerminal: Boolean
  get() {
    TODO()
  }

internal val RxSplitInstallResult.isError: Boolean
  get() {
    TODO()
  }


internal fun transformSplitInstallSessionStateToResult(state: SplitInstallSessionState): RxSplitInstallResult {
  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}