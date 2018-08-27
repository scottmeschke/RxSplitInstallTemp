package com.todostuff

import android.app.Application
import android.content.Context
import com.androidengibeering.ondemandrxwrapper.RxSplitInstallManager
import com.androidengibeering.ondemandrxwrapper.RxSplitInstallManagerFactory
import com.google.android.play.core.splitcompat.SplitCompat

class TodoStuffApplication : Application() {

  lateinit var rxSplitInstallManager: RxSplitInstallManager
    private set

  override fun onCreate() {
    super.onCreate()
    rxSplitInstallManager = RxSplitInstallManagerFactory.create(this)
  }

  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    SplitCompat.install(base)
  }
}
