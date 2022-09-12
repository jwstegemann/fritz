package dev.fritz2.core

/**
 * Used by the fritz2 gradle-plugin to identify data classes it should generate [Lens]es for.
 */
@Target(AnnotationTarget.CLASS)
annotation class Lenses

/**
 * Describes a focus point into a data structure, i.e. a property of a given complex entity
 *
 * @property id identifies the focus of this lens
 */
interface Lens<P, T> {
    val id: String

    /**
     * gets the value of the focus target
     *
     * @param parent concrete instance to apply the focus tos
     */
    fun get(parent: P): T

    /**
     * sets the value of the focus target
     *
     * @param parent concrete instance to apply the focus to
     * @param value the new value of the focus target
     */
    fun set(parent: P, value: T): P

    /**
     * manipulates the focus target's value inside the [parent]
     *
     * @param parent concrete instance to apply the focus to
     * @param mapper function defining the manipulation
     */
    suspend fun apply(parent: P, mapper: suspend (T) -> T): P = set(parent, mapper(get(parent)))


    /**
     * appends to [Lens]es so that the resulting [Lens] points from the parent of the [Lens] this is called on to the target of [other]
     *
     * @param other [Lens] to append to this one
     */
    operator fun <X> plus(other: Lens<T, X>): Lens<P, X> = object :
        Lens<P, X> {
        override val id = "${this@Lens.id}.${other.id}".trimEnd('.')
        override fun get(parent: P): X = other.get(this@Lens.get(parent))
        override fun set(parent: P, value: X): P = this@Lens.set(parent, other.set(this@Lens.get(parent), value))
    }
}

/**
 * convenience function to create a [Lens]
 *
 * @param id of the [Lens]
 * @param getter of the [Lens]
 * @param setter of the [Lens]
 */
inline fun <P, T> lens(id: String, crossinline getter: (P) -> T, crossinline setter: (P, T) -> P): Lens<P, T> =
    object : Lens<P, T> {
        override val id: String = id
        override fun get(parent: P): T = getter(parent)
        override fun set(parent: P, value: T): P = setter(parent, value)
    }

/**
 * creates a [Lens] converting [P] to and from a [String]
 *
 * @param parse function for parsing a [String] to [P]
 * @param format function for formatting a [P] to [String]
 */
inline fun <P> format(crossinline parse: (String) -> P, crossinline format: (P) -> String): Lens<P, String> =
    object : Lens<P, String> {
        override val id: String = ""
        override fun get(parent: P): String = format(parent)
        override fun set(parent: P, value: String): P = parse(value)
    }

/**
 * function to derive a valid id for a given instance that does not change over time.
 */
typealias IdProvider<T, I> = (T) -> I

/**
 * Occurs when [Lens] points to non-existing element.
 */
class LensException: Exception() // is needed to cancel the coroutine correctly

/**
 * creates a [Lens] pointing to a certain element in a [List]
 *
 * @param element current instance of the element to focus on
 * @param idProvider to identify the element in the list (i.e. when it's content changes over time)
 */
fun <T, I> lensOf(element: T, idProvider: IdProvider<T, I>): Lens<List<T>, T> = object : Lens<List<T>, T> {
    override val id: String = idProvider(element).toString()

    override fun get(parent: List<T>): T = parent.find {
        idProvider(it) == idProvider(element)
    } ?: throw LensException()

    override fun set(parent: List<T>, value: T): List<T> = parent.map {
        if (idProvider(it) == idProvider(value)) value else it
    }
}

/**
 * creates a [Lens] pointing to a certain [index] in a list
 *
 * @param index position to focus on
 */
fun <T> lensOf(index: Int): Lens<List<T>, T> = object : Lens<List<T>, T> {
    override val id: String = index.toString()

    override fun get(parent: List<T>): T =
        parent.getOrNull(index) ?: throw LensException()

    override fun set(parent: List<T>, value: T): List<T> =
        parent.mapIndexed { i, it -> if(i == index) value else it }
}

/**
 * creates a [Lens] pointing to a certain element in a [Map]
 *
 * @param key of the entry to focus on
 */
fun <K, V> lensOf(key: K): Lens<Map<K, V>, V> = object : Lens<Map<K, V>, V> {
    override val id: String = key.toString()

    override fun get(parent: Map<K, V>): V =
        parent[key] ?: throw LensException()

    override fun set(parent: Map<K, V>, value: V): Map<K, V> = parent.mapValues {
        if(it.key == key) value else it.value
    }
}
