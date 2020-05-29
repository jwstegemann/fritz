package io.fritz2.dom

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.w3c.dom.Element
import kotlin.reflect.KProperty


//TODO: different datatypes for attributes (List of String for classes, etc.)
internal object AttributeDelegate {
    operator fun <X : Element, T : Any> getValue(thisRef: Tag<X>, property: KProperty<*>): Flow<T> =
        throw NotImplementedError()

    operator fun <X : Element, T : Any> setValue(thisRef: Tag<X>, property: KProperty<*>, values: Flow<T>) {
        thisRef.apply { values.map { s -> s.toString() }.bindAttr(property.name) }
    }
}

/**
 * [ValueAttributeDelegate] is a special attribute delegate for
 * the html value attribute.
 */
internal object ValueAttributeDelegate {
    operator fun <X : Element> getValue(thisRef: Tag<X>, property: KProperty<*>): Flow<String> =
        throw NotImplementedError()

    operator fun <X : Element> setValue(thisRef: Tag<X>, property: KProperty<*>, values: Flow<String>) {
        ValueAttributeMountPoint(values, thisRef.domNode)
    }
}

/**
 * This interface allows instances of implementing classes to set and bind DOM-attributes.
 */
interface WithAttributes<out T : Element> : WithDomNode<T> {

    /**
     * set a value of an attribute to a constant value
     *
     * @param name of the attribute
     * @param value to set
     */
    fun attr(name: String, value: String) = domNode.setAttribute(name, value)

    /**
     * set a value of an attribute to a constant [List] of values
     *
     * @param name of the attribute
     * @param values [List] of values to set (concatenated by empty space)
     */
    fun attr(name: String, values: List<String>) = domNode.setAttribute(name, values.joinToString(separator = " "))

    /**
     * bind a [Flow] to an attribute. The attribute will be updated in the DOM whenever a new value appears on this [Flow].
     *
     * @param name of the attribute
     * @receiver the [Flow] to bind
     */
    fun Flow<String>.bindAttr(name: String) = AttributeMountPoint(name, this, domNode)

    /**
     * bind a [Flow] of [List]s to an attribute. The attribute will be updated in the DOM whenever a new value appears on this [Flow].
     * The elements of the list will be concatenated by empty space.
     *
     * @param name of the attribute
     * @receiver the [Flow] to bind
     */
    fun Flow<List<String>>.bindAttr(name: String) =
        AttributeMountPoint(name, this.map { l -> l.joinToString(separator = " ") }, domNode)

    /**
     * bind a [Flow] of [Map]s to an attribute. The attribute will be updated in the DOM whenever a new value appears on this [Flow].
     * The value will be set to the keys of the map that are mapped to true separated by empty space.
     * Use it to build dynamic lists of style-classes, i.e.
     *
     * @param name of the attribute
     * @receiver the [Flow] to bind
     */
    fun Flow<Map<String, Boolean>>.bindAttr(name: String) = AttributeMountPoint(name, this.map { m ->
        m.filter { it.value }.keys.joinToString(" ")
    }, domNode)
}