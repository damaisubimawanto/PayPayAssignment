package com.damai.base.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.damai.base.utils.Event
import com.damai.base.utils.EventObserver

/**
 * Created by damai007 on 30/October/2023
 */

fun <T> Fragment.observe(
    liveData: LiveData<T>,
    action: (t: T) -> Unit
) {
    with(viewLifecycleOwner) {
        liveData.observe(this) { it?.let { t -> action(t) } }
    }
}

@JvmName("observeEvent")
fun <T> Fragment.observe(
    liveData: LiveData<Event<T>>,
    observer: EventObserver<T>
) {
    with(viewLifecycleOwner) { liveData.observe(this, observer) }
}