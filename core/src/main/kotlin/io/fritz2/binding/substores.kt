package io.fritz2.binding

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface Lens<P,T> {
    fun get(parent: P): T
    fun set(parent: P, value: T): P
    fun map(parent: P, mapper: (T) -> T): P = set(parent, mapper(get(parent)))


    operator fun <X> plus(other: Lens<T,X>): Lens<P,X> = object : Lens<P,X> {
        override fun get(parent: P): X = other.get(this@Lens.get(parent))
        override fun set(parent: P, value: X): P = this@Lens.set(parent, other.set(this@Lens.get(parent), value))
    }
}



@FlowPreview
@ExperimentalCoroutinesApi
class SubStore<R, P, T>(private val parent: Store<P>, private val lens: Lens<P,T>, val rootStore: RootStore<R>, val rootLens: Lens<R,T>) : Store<T> {

    override fun enqueue(update: Update<T>) {
        rootStore.enqueue {
            rootLens.map(it, update)
        }
    }

    override val data: Flow<T> = parent.data.map {
        lens.get(it)
    }.distinctUntilChanged()

    override fun <X> sub(lens: Lens<T,X>): SubStore<R,T,X> = SubStore(this, lens, rootStore, this.rootLens + lens)

}