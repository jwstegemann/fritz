package dev.fritz2.binding

import dev.fritz2.identification.RootInspector
import dev.fritz2.dom.html.WithJob
import dev.fritz2.lenses.Lens
import dev.fritz2.lenses.Lenses
import dev.fritz2.remote.Socket
import dev.fritz2.remote.body
import dev.fritz2.resource.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Defines a type for transforming one value into the next
 */
typealias Update<T> = suspend (T) -> T

/**
 * Defines a type for handling errors in updates
 */
typealias ErrorHandler<T> = (Throwable, T) -> T

/**
 * Type of elements in the update-queue of a [Store]
 *
 * @property update function describing the step from the old value to the new
 * @property errorHandler describes the handling of errors during an update and the new value in case of error
 */
class QueuedUpdate<T>(
    inline val update: Update<T>,
    inline val errorHandler: (Throwable, T) -> T
)


/**
 * The [Store] is the main type for all data binding activities. It the base class of all concrete Stores like [RootStore], [SubStore], etc.
 */
interface Store<T> : WithJob {

    /**
     * Default error handler printing the error to console and keeping the previous value.
     *
     * @param exception Exception to handle
     * @param oldValue previous value of the [Store]
     */
    fun errorHandler(exception: Throwable, oldValue: T): T {
        console.error("ERROR[$id]: ${exception.message}", exception)
        return oldValue
    }

    /**
     * Factory method to create a [SimpleHandler] mapping the actual value of the [Store] and a given Action to a new value.
     *
     * @param execute lambda that is executed whenever a new action-value appears on the connected event-[Flow].
     */
    fun <A> handle(
        errorHandler: ErrorHandler<T> = ::errorHandler,
        execute: suspend (T, A) -> T
    ) = SimpleHandler<A> { flow, job ->
        flow.onEach { enqueue(QueuedUpdate({ t -> execute(t, it) }, errorHandler)) }
            .launchIn(MainScope() + job)
    }

    /**
     * Factory method to create a [SimpleHandler] that does not take an Action
     *
     * @param errorHandler handles error during update
     * @param execute lambda that is execute for each event on the connected [Flow]
     */
    fun handle(
        errorHandler: ErrorHandler<T> = ::errorHandler,
        execute: suspend (T) -> T
    ) = SimpleHandler<Unit> { flow, job ->
        flow.onEach { enqueue(QueuedUpdate({ t -> execute(t) }, errorHandler)) }
            .launchIn(MainScope() + job)
    }

    /**
     * Factory method to create a [EmittingHandler] taking an action-value and the current store value to derive the new value.
     * An [EmittingHandler] is a [Flow] by itself and can therefore be connected to other [SimpleHandler]s even in other [Store]s.
     *
     * @param errorHandler handles error during update
     * @param execute lambda that is executed for each action-value on the connected [Flow]. You can emit values from this lambda.
     */
    fun <A, E> handleAndEmit(
        errorHandler: ErrorHandler<T> = ::errorHandler,
        execute: suspend FlowCollector<E>.(T, A) -> T
    ) =
        EmittingHandler<A, E>({ inFlow, outFlow, job ->
            inFlow.onEach { enqueue(QueuedUpdate({ t -> outFlow.execute(t, it) }, errorHandler)) }
                .launchIn(MainScope() + job)
        })

    /**
     * factory method to create an [EmittingHandler] that does not take an action in it's [execute]-lambda.
     *
     * @param errorHandler handles error during update
     * @param execute lambda that is executed for each event on the connected [Flow]. You can emit values from this lambda.
     */
    fun <E> handleAndEmit(
        errorHandler: ErrorHandler<T> = ::errorHandler,
        execute: suspend FlowCollector<E>.(T) -> T
    ) =
        EmittingHandler<Unit, E>({ inFlow, outFlow, job ->
            inFlow.onEach { enqueue(QueuedUpdate({ t -> outFlow.execute(t) }, errorHandler)) }
                .launchIn(MainScope() + job)
        })

    /**
     * abstract method defining, how this [Store] handles an [Update]
     *
     * @param update the [Update] to handle
     */
    suspend fun enqueue(update: QueuedUpdate<T>)

    /**
     * base-id of this [Store]. ids of depending [Store]s are concatenated separated by a dot.
     */
    val id: String?

    /**
     * the [Flow] representing the current value of the [Store]. Use this to bind it to ui-elements or derive calculated values by using [map] for example.
     */
    val data: Flow<T>

    /**
     * represents the current value of the [Store]
     */
    val current: T

    /**
     * a simple [SimpleHandler] that just takes the given action-value as the new value for the [Store].
     */
    val update: Handler<T>

    /**
     * calls a handler on each new value of the [Store]
     */
    fun syncBy(handler: Handler<Unit>) {
        data.drop(1).map { } handledBy handler
    }

    /**
     * calls a handler on each new value of the [Store]
     */
    fun syncBy(handler: Handler<T>) {
        data.drop(1) handledBy handler
    }

    fun <I> syncWith(socket: Socket, resource: Resource<T, I>) {
        val session = socket.connect()
        var last: T? = null
        session.messages.body.map {
            val received = resource.deserialize(it)
            last = received
            received
        } handledBy update

        data.drop(1).onEach {
            if (last != it) session.send(resource.serialize(it))
        }.watch()
    }

    /**
     * create a [SubStore] that represents a certain part of your data model.
     *
     * @param lens: a [Lens] describing, which part of your data model you will create [SubStore] for.
     * Use @[Lenses] annotation to let your compiler
     * create the lenses for you or use the buildLens-factory-method.
     */
    fun <X> sub(lens: Lens<T, X>): SubStore<T, X> =
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

    data.drop(1).onEach {
        if (last != it) session.send(resource.serializeList(it))
    }.watch()
}

/**
 * A [Store] can be initialized with a given value.
 * Use a [RootStore] to "store" your model and create [SubStore]s from here.
 *
 * @param initialData: the first current value of this [Store]
 * @param id: the id of this store. ids of [SubStore]s will be concatenated.
 */
open class RootStore<T>(
    initialData: T,
    override val id: String? = null
) : Store<T> {

    private val state: MutableStateFlow<T> = MutableStateFlow(initialData)
    private val mutex = Mutex()

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
    override val data: Flow<T> = state.asStateFlow()

    /**
     * Represents the current data of this [RootStore].
     */
    override val current: T
        get() = state.value

    /**
     * in a [RootStore] an [Update] is handled by applying it to the internal [StateFlow].
     */
    override suspend fun enqueue(update: QueuedUpdate<T>) {
        try {
            mutex.withLock {
                state.value = update.update(state.value)
            }
        } catch (e: Throwable) {
            update.errorHandler(e, state.value)
        }
    }

    /**
     * a simple [SimpleHandler] that just takes the given action-value as the new value for the [Store].
     */
    override val update = this.handle<T> { _, newValue -> newValue }
}

/**
 * convenience method to create a simple [RootStore] without any handlers, etc.
 *
 * @param initialData the first current value of this [Store]
 * @param id the id of this store. ids of [SubStore]s will be concatenated.
 */
fun <T> storeOf(initialData: T, id: String? = null) = RootStore(initialData, id)

fun <T> Store<T>.inspect(data: T) = RootInspector(data, id)