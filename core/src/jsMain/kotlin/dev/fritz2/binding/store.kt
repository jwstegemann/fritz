package dev.fritz2.binding

import dev.fritz2.dom.html.WithJob
import dev.fritz2.identification.Id
import dev.fritz2.lenses.Lens
import dev.fritz2.lenses.Lenses
import dev.fritz2.remote.Socket
import dev.fritz2.remote.body
import dev.fritz2.resource.Resource
import dev.fritz2.validation.Validation
import dev.fritz2.validation.ValidationMessage
import dev.fritz2.validation.valid
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Defines a type for transforming one value into the next
 */
typealias Update<D> = suspend (D) -> D

/**
 * The [Store] is the main type for all data binding activities. It the base class of all concrete Stores like [RootStore], [SubStore], etc.
 */
interface Store<D> : WithJob {

    /**
     * Factory method to create a [SimpleHandler] mapping the actual value of the [Store] and a given Action to a new value.
     *
     * @param execute lambda that is executed whenever a new action-value appears on the connected event-[Flow].
     */
    fun <A> handle(
        execute: suspend (D, A) -> D
    ) = SimpleHandler<A> { flow, job ->
        flow.onEach { enqueue { d -> execute(d, it) } }
            .catch { d -> errorHandler(d) }
            .launchIn(MainScope() + job)
    }

    /**
     * Factory method to create a [SimpleHandler] that does not take an Action
     *
     * @param execute lambda that is execute for each event on the connected [Flow]
     */
    fun handle(
        execute: suspend (D) -> D
    ) = SimpleHandler<Unit> { flow, job ->
        flow.onEach { enqueue { d -> execute(d) } }
            .catch { d -> errorHandler(d) }
            .launchIn(MainScope() + job)
    }

    /**
     * Factory method to create a [EmittingHandler] taking an action-value and the current store value to derive the new value.
     * An [EmittingHandler] is a [Flow] by itself and can therefore be connected to other [SimpleHandler]s even in other [Store]s.
     *
     * @param execute lambda that is executed for each action-value on the connected [Flow]. You can emit values from this lambda.
     */
    fun <A, E> handleAndEmit(
        execute: suspend FlowCollector<E>.(D, A) -> D
    ) = EmittingHandler<A, E>({ inFlow, outFlow, job ->
            inFlow.onEach { enqueue { d -> outFlow.execute(d, it) } }
                .catch { d -> errorHandler(d) }
                .launchIn(MainScope() + job)
        })

    /**
     * factory method to create an [EmittingHandler] that does not take an action in it's [execute]-lambda.
     *
     * @param execute lambda that is executed for each event on the connected [Flow]. You can emit values from this lambda.
     */
    fun <E> handleAndEmit(
        execute: suspend FlowCollector<E>.(D) -> D
    ) =
        EmittingHandler<Unit, E>({ inFlow, outFlow, job ->
            inFlow.onEach { enqueue { d -> outFlow.execute(d) } }
                .catch { d -> errorHandler(d) }
                .launchIn(MainScope() + job)
        })

    /**
     * abstract method defining, how this [Store] handles an [Update]
     *
     * @param update the [Update] to handle
     */
    suspend fun enqueue(update: Update<D>)

    /**
     * [id] of this [Store].
     * ids of depending [Store]s are concatenated and separated by a dot.
     */
    val id: String

    /**
     * Path of this [Store] derived from the underlying model.
     * Paths of depending [Store]s are concatenated and separated by a dot.
     */
    val path: String

    /**
     * the [Flow] representing the current value of the [Store]. Use this to bind it to ui-elements or derive calculated values by using [map] for example.
     */
    val data: Flow<D>

    /**
     * represents the current value of the [Store]
     */
    val current: D

    /**
     * a simple [SimpleHandler] that just takes the given action-value as the new value for the [Store].
     */
    val update: Handler<D>

    /**
     * calls a handler on each new value of the [Store]
     */
    fun syncBy(handler: Handler<Unit>) {
        data.drop(1).map { } handledBy handler
    }

    /**
     * calls a handler on each new value of the [Store]
     */
    fun syncBy(handler: Handler<D>) {
        data.drop(1) handledBy handler
    }

    fun <I> syncWith(socket: Socket, resource: Resource<D, I>) {
        val session = socket.connect()
        var last: D? = null
        session.messages.body.map {
            val received = resource.deserialize(it)
            last = received
            received
        } handledBy update

        data.drop(1) handledBy {
            if (last != it) session.send(resource.serialize(it))
        }
    }

    /**
     * create a [SubStore] that represents a certain part of your data model.
     *
     * @param lens: a [Lens] describing, which part of your data model you will create [SubStore] for.
     * Use @[Lenses] annotation to let your compiler
     * create the lenses for you or use the buildLens-factory-method.
     */
    fun <X> sub(lens: Lens<D, X>): SubStore<D, X> =
        SubStore(this, lens)
}

/**
 * calls a handler on each new value of the [Store]
 */
inline fun <T, R> Store<T>.syncBy(handler: Handler<R>, crossinline mapper: suspend (T) -> R) {
    data.drop(1).map(mapper) handledBy handler
}

fun <T, I> Store<List<T>>.syncWith(socket: Socket, resource: Resource<T, I>) {
    val session = socket.connect()
    var last: List<T>? = null
    session.messages.body.map {
        val received = resource.deserializeList(it)
        last = received
        received
    } handledBy update

    data.drop(1) handledBy {
        if (last != it) session.send(resource.serializeList(it))
    }
}

/**
 * A [Store] can be initialized with a given value.
 * Use a [RootStore] to "store" your model and create [SubStore]s from here.
 *
 * @param initialData first current value of this [Store]
 * @param id id of this [Store]. Ids of [SubStore]s will be concatenated.
 */
open class RootStore<D>(
    initialData: D,
    override val id: String = Id.next()
) : Store<D> {

    private val state: MutableStateFlow<D> = MutableStateFlow(initialData)
    private val mutex = Mutex()

    override val path: String = ""

    /**
     * [Job] used as parent job on all coroutines started in [Handler]s in the scope of this [Store]
     */
    override val job: Job = Job()

    /**
     * Emits a [Flow] with the current data of this [RootStore].
     * The [Flow] internal data is only changed, when the value differs from the last one to avoid calculations
     * and updates that are not necessary.
     *
     * Actual data therefore is derived by applying the updates on the internal channel one by one to get the next value.
     */
    override val data: Flow<D> = state.asStateFlow()

    /**
     * Represents the current data of this [RootStore].
     */
    override val current: D
        get() = state.value

    /**
     * in a [RootStore] an [Update] is handled by applying it to the internal [StateFlow].
     */
    override suspend fun enqueue(update: Update<D>) {
        mutex.withLock {
            state.value = update(state.value)
        }
    }

    /**
     * a simple [SimpleHandler] that just takes the given action-value as the new value for the [Store].
     */
    override val update = this.handle<D> { _, newValue -> newValue }
}

/**
 * Convenience function to create a simple [RootStore] without any handlers, etc.
 *
 * @param initialData first current value of this [Store]
 * @param id id of this store. Ids of [SubStore]s will be concatenated.
 */
fun <D> storeOf(initialData: D, id: String = Id.next()) = RootStore(initialData, id)