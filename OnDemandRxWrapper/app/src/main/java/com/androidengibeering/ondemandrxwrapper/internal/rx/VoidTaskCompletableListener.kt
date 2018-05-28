package com.androidengibeering.ondemandrxwrapper.internal.rx

import com.google.android.play.core.tasks.OnCompleteListener
import com.google.android.play.core.tasks.Task
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable

class VoidTaskCompletableListener(private val task: Task<Void>, private val observer: CompletableObserver) :
    OnCompleteListener<Void>, Disposable {

  @Volatile private var disposed = false

  init {
    checkCompletion()
  }

  private fun checkCompletion() {
    if (task.isComplete) onComplete(task)
  }

  override fun onComplete(completedTask: Task<Void>) {
    if (!isDisposed) {
      when (completedTask.isSuccessful) {
        true -> observer.onComplete()
        false -> observer.onError(completedTask.exception)
      }
    }
  }

  override fun isDisposed(): Boolean {
    return disposed
  }

  override fun dispose() {
    disposed = true
  }

}
