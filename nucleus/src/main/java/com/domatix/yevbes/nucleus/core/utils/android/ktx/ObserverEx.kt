package com.domatix.yevbes.nucleus.core.utils.android.ktx

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber

class ObserverEx<T : Any> : Observer<T> {

    private var subscribe: (disposable: Disposable) -> Unit = {
        Timber.d("onSubscribe() called")
    }

    fun onSubscribe(subscribe: (Disposable) -> Unit) {
        this.subscribe = subscribe
    }

    override fun onSubscribe(d: Disposable) {
        this.subscribe.invoke(d)
    }

    private var next: (t: T) -> Unit = {
        Timber.d("onNext() called: response is $it")
    }

    fun onNext(next: (T) -> Unit) {
        this.next = next
    }

    override fun onNext(t: T) {
        this.next.invoke(t)
    }

    private var error: (Throwable) -> Unit = {
        Timber.e(it, "onError() called: ${it::class.java.simpleName}: ${it.message}")
    }

    fun onError(error: (Throwable) -> Unit) {
        this.error = error
    }

    override fun onError(e: Throwable) {
        this.error.invoke(e)
    }

    private var complete: () -> Unit = {
        Timber.d("onComplete() called")
    }

    fun onComplete(complete: () -> Unit) {
        this.complete = complete
    }

    override fun onComplete() {
        this.complete.invoke()
    }
}
