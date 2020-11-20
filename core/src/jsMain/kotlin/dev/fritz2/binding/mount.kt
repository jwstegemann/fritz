package dev.fritz2.binding

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.scan

/**
 * collects the values of a given [Flow] one by one.
 * Use this for data-types that represent a single (simple or complex) value.
 *
 * @param parentJob parent Job for starting a new coroutine
 * @param upstream returns the Flow that should be mounted at this point
 * @param set function which getting called when values are changing (rerender)
 */
fun <T> mountSingle(parentJob: Job, upstream: Flow<T>, set: suspend (T, T?) -> Unit) {
    (MainScope() + parentJob).launch {
        upstream.scan(null) { last: T?, value: T ->
            set(value, last)
            value
        }.catch {
            cancel("error mounting", it)
        }.collect()
    }
}