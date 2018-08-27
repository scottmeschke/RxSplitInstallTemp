package com.androidengibeering.ondemandrxwrapper.internal.single

import com.androidengibeering.ondemandrxwrapper.RxSplitInstallResult
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.tasks.OnCompleteListener
import com.google.android.play.core.tasks.Task
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class CreateSessionSingle(
  private val modules: Set<String>,
  private val installManager: SplitInstallManager
) : Single<RxSplitInstallResult.CreateSession>() {

  override fun subscribeActual(observer: SingleObserver<in RxSplitInstallResult.CreateSession>) {
    val listener = Listener(
      modules,
      installManager,
      observer
    )
    observer.onSubscribe(listener)
    installManager.startInstall(createRequest(modules))
      .addOnCompleteListener(listener)
  }

  private fun createRequest(
    modules: Set<String>
  ): SplitInstallRequest =
    SplitInstallRequest.newBuilder().run {
      for (module in modules) {
        addModule(module)
      }
      build()
    }

  class Listener(
    private val modules: Set<String>,
    private val installManager: SplitInstallManager,
    private val observer: SingleObserver<in RxSplitInstallResult.CreateSession>
  ) : Disposable, OnCompleteListener<Int> {

    @Volatile
    private var disposed = false

    override fun isDisposed(): Boolean = disposed

    override fun dispose() {
      disposed = true
    }

    override fun onComplete(completedTask: Task<Int>) {
      if (!disposed) emitResult(completedTask)
    }

    private fun cancelInstall(completedTask: Task<Int>) {
      if (completedTask.isSuccessful) {
        //unfortunately we can't cancel installation until we have a sessionId,
        //instead of cancelling at time of disposable
        installManager.cancelInstall(completedTask.result)
      }
    }

    private fun emitResult(completedTask: Task<Int>) {
      when (completedTask.isSuccessful) {
        true -> observer.onSuccess(RxSplitInstallResult.CreateSession.Created(completedTask.result, modules))
        false -> observer.onSuccess(RxSplitInstallResult.CreateSession.Failed(completedTask.exception, modules))
      }
    }

  }

}
