package com.todostuff

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.androidengibeering.ondemandrxwrapper.R
import com.androidengibeering.ondemandrxwrapper.RxSplitInstallManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {

  private lateinit var rxInstallManager: RxSplitInstallManager


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)
    rxInstallManager = (application as TodoStuffApplication).rxSplitInstallManager
    Log.d("MainActivity", "trying to install feature Z")

    rxInstallManager
      .install("Feature-Z")
      .asObservable()
      .subscribe {
        Log.d("MainActivity", Thread.currentThread().toString())
        Log.d("MainActivity", it.toString())
      }

    initViews()

    val avail = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

    Log.d("MainActivity", "google play services is available: ${avail == ConnectionResult.SUCCESS}")
  }

  private fun initViews() {

  }


}