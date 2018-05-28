package com.androidengibeering.ondemandrxwrapper.internal.rx

import com.androidengibeering.ondemandrxwrapper.api.RxSplitInstallResult
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.tasks.OnCompleteListener
import com.google.android.play.core.tasks.Task
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class InitializeSplitObservable(private val modules: Set<String>,
                                private val installManager: SplitInstallManager)
  : Observable<RxSplitInstallResult.Initial>() {

  override fun subscribeActual(observer: Observer<in RxSplitInstallResult.Initial>) {
    val listener = Listener(modules, installManager, observer)
    observer.onSubscribe(listener)
    val request = createRequest(modules, observer)
    if (request != null) {
      observer.onNext(RxSplitInstallResult.Initial.Started(modules))
      installManager.startInstall(request)
          .addOnCompleteListener(listener)
    }
  }

  private fun createRequest(modules: Set<String>, observer: Observer<in RxSplitInstallResult.Initial>): SplitInstallRequest? {
    return when (modules.isEmpty()) {
      true -> {
        observer.onError(Exception("Set of modules to install is empty"))
        null
      }
      false -> {
        val builder =  SplitInstallRequest.newBuilder()
        for (module in modules) {
          builder.addModule(module)
        }
        builder.build()
      }
    }
  }

  class Listener(private val modules: Set<String>,
                 private val installManager: SplitInstallManager,
                 private val observer: Observer<in RxSplitInstallResult.Initial>)
    : Disposable, OnCompleteListener<Int> {

    @Volatile
    private var disposed = false

    override fun isDisposed(): Boolean {
      return disposed
    }

    override fun dispose() {
      disposed = true
    }

    override fun onComplete(completedTask: Task<Int>) {
      when (disposed) {
        true -> if (completedTask.isSuccessful) installManager.cancelInstall(completedTask.result)
        false -> {
          when (completedTask.isSuccessful) {
            true -> {
              observer.onNext(RxSplitInstallResult.Initial.Created(completedTask.result, modules))
              observer.onComplete()
            }
            false -> {
              observer.onNext(RxSplitInstallResult.Initial.Failed(completedTask.exception, modules))
              observer.onComplete()
            }
          }
        }
      }
    }

  }

}
