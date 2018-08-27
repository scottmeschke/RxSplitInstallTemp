package com.androidengibeering.ondemandrxwrapper.internal.listener

import com.google.android.play.core.tasks.OnCompleteListener
import com.google.android.play.core.tasks.Task
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable

class VoidTaskCompletableListener(private val task: Task<Void>, private val observer: CompletableObserver) :
  OnCompleteListener<Void>, Disposable {

  private var disposed = false

  override fun onComplete(completedTask: Task<Void>) {
    if (!isDisposed) {
      when (completedTask.isSuccessful) {
        true -> observer.onComplete()
        false -> observer.onError(completedTask.exception)
      }
    }
  }

  override fun isDisposed() = disposed

  override fun dispose() {
    disposed = true
  }

  fun init() {
    if (disposed) return
    when {
      task.isComplete -> onComplete(task)
      else -> task.addOnCompleteListener(this)
    }
  }
}
