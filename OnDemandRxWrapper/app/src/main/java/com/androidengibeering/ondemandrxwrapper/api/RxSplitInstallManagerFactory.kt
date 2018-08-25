package com.androidengibeering.ondemandrxwrapper.api

import android.content.Context
import com.androidengibeering.ondemandrxwrapper.internal.RxSplitInstallManagerImpl
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory

object RxSplitInstallManagerFactory {

  fun create(context: Context): RxSplitInstallManager {
    return RxSplitInstallManagerImpl(SplitInstallManagerFactory.create(context))
  }

}