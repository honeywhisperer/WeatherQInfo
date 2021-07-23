package hr.trailovic.weatherqinfo.model

import java.util.concurrent.atomic.AtomicBoolean

class OneTimeEvent<T>(private val value: T) {
    var isConsumed = false
        private set

    internal fun getValue(): T? =
        if (!isConsumed) {
            isConsumed = true
            value
        } else {
            null
        }

    fun peekValue(): T = value
}

fun <T> T.toOneTimeEvent() = OneTimeEvent(this)

fun <T> OneTimeEvent<T>.consume(block: (T) -> Unit): T? = getValue()?.also { block(it) }