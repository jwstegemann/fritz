package io.fritz2.binding

import io.fritz2.optics.withId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/**
 * [Patch] contains information about an update on a [List].
 * Therefore it needs a position [from], a list with new elements [that] and
 * a number [replaced] of how many elements are replaced by this patch.
 */
data class Patch<out T>(val from: Int, val that: List<T>, val replaced: Int)

/**
 * [Seq] handles a [Flow] of [Patch] and offers some transformation methods.
 * Its created with  [each] method on a [Flow] of [List].
 */
class Seq<T>(val data: Flow<Patch<T>>) {
    /**
     * Maps the internal value of a [Seq].
     */
    inline fun <X> map(crossinline mapper: (T) -> X): Seq<X> {
        return Seq(data.map {
            Patch(it.from, it.that.map(mapper), it.replaced)
        })
    }

    /**
     * Maps and flattens he internal value of a [Seq].
     */
    inline fun <X> flatMap(crossinline mapper: (T) -> List<X>): Seq<X> {
        return Seq(data.map {
            Patch(it.from, it.that.flatMap(mapper), it.replaced)
        })
    }

    /**
     * Filters the internal value of a [Seq].
     */
    inline fun filter(crossinline filter: (T) -> Boolean): Seq<T> {
        return Seq(data.map {
            Patch(it.from, it.that.filter(filter), it.replaced)
        })
    }
}

@ExperimentalCoroutinesApi
private inline fun <T> compare(crossinline different: (T, T) -> Boolean): suspend (Pair<List<T>, List<T>>) -> Flow<Patch<T>> =
    { oldAndNew: Pair<List<T>, List<T>> ->
        channelFlow {
            val (oldValue, newValue) = oldAndNew
            //console.log("old = $oldValue; new = $newValue")
            val size2Compare = if (oldValue.size < newValue.size) {
                //console.log("### Seq: append " + (newValue.size - oldValue.size) + " from " + (oldValue.size))
                channel.send(Patch(oldValue.size, newValue.takeLast(newValue.size - oldValue.size), 0))
                oldValue.size
            } else {
                if (newValue.size < oldValue.size) {
                    //console.log("### Seq: delete " + (oldValue.size - newValue.size) + " from " + (newValue.size -1))
                    channel.send(Patch(newValue.size, emptyList<T>(), oldValue.size - newValue.size))
                }
                newValue.size
            }
            //console.log("### Seq: size to compare: $size2Compare")

            //FIXME: better performance without range?
            for (i in 0 until size2Compare) {
                //TODO: batch changed items in a row to one patch
                //console.log("### Seq: inner comparing: $i -> ${oldValue[i]} to ${newValue[i]}")
                if (different(oldValue[i], newValue[i])) channel.send(Patch(i, listOf(newValue[i]), 1))
            }
        }
    }

private suspend inline fun <T> accumulate(accumulator: Pair<List<T>, List<T>>, newValue: List<T>) =
    Pair(accumulator.second, newValue)

/**
 * Creates a [Seq] out of a [Flow] of [List].
 */
@ExperimentalCoroutinesApi
@FlowPreview
fun <T: withId> Flow<List<T>>.each(): Seq<T> =
    Seq(this.scan(Pair(emptyList<T>(), emptyList<T>()), ::accumulate).flatMapConcat(compare {a,b ->
        a.id != b.id
    }))

/**
 * Creates a [Seq] out of a [Flow] of [List].
 */
@ExperimentalCoroutinesApi
@FlowPreview
fun <T> Flow<List<T>>.each(): Seq<T> =
    Seq(this.scan(Pair(emptyList<T>(), emptyList<T>()), ::accumulate).flatMapConcat(compare {a,b ->
        a != b
    }))
