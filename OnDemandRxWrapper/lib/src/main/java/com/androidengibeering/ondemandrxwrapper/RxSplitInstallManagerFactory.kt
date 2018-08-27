package com.androidengibeering.ondemandrxwrapper

import android.content.Context
import com.androidengibeering.ondemandrxwrapper.internal.RxSplitManagerImpl
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory

object RxSplitInstallManagerFactory {

  fun create(context: Context): RxSplitInstallManager {
    return RxSplitManagerImpl(SplitInstallManagerFactory.create(context))
  }

}
