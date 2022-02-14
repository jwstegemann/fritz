package dev.fritz2.dom

import dev.fritz2.binding.mountSimple
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.dom.html.Scope
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.dom.clear
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.Event

/**
 * A marker to separate the layers of calls in the type-safe-builder pattern.
 */
@DslMarker
annotation class HtmlTagMarker

/**
 * Represents a tag.
 * Sorry for the name, but we needed to delimit it from the [Element] it is wrapping.
 */
interface Tag<out E : Element> : RenderContext, WithDomNode<E>, WithEvents<E> {

    /**
     * id of this [Tag]
     */
    val id: String?

    /**
     * constant css-classes of this [Tag]
     */
    val baseClass: String?

    /**
     * Sets an attribute.
     *
     * @param name to use
     * @param value to use
     */
    fun attr(name: String, value: String) {
        domNode.setAttribute(name, value)
    }

    /**
     * Sets an attribute only if its [value] is not null.
     *
     * @param name to use
     * @param value to use
     */
    fun attr(name: String, value: String?) {
        value?.let { domNode.setAttribute(name, it) }
    }

    /**
     * Sets an attribute.
     *
     * @param name to use
     * @param value to use
     */
    fun attr(name: String, value: Flow<String>) {
        mountSimple(job, value) { v -> attr(name, v) }
    }

    /**
     * Sets an attribute only for all none null values of the flow.
     *
     * @param name to use
     * @param value to use
     */
    fun attr(name: String, value: Flow<String?>) {
        mountSimple(job, value) { v ->
            if (v != null) attr(name, v)
            else domNode.removeAttribute(name)
        }
    }

    /**
     * Sets an attribute.
     *
     * @param name to use
     * @param value to use
     */
    fun <T> attr(name: String, value: T) {
        value?.let { domNode.setAttribute(name, it.toString()) }
    }

    /**
     * Sets an attribute.
     *
     * @param name to use
     * @param value to use
     */
    fun <T> attr(name: String, value: Flow<T>) {
        mountSimple(job, value.map { it?.toString() }) { v ->
            if (v != null) attr(name, v)
            else domNode.removeAttribute(name)
        }
    }

    /**
     * Sets an attribute when [value] is true otherwise removes it.
     *
     * @param name to use
     * @param value for decision
     * @param trueValue value to use if attribute is set (default "")
     */
    fun attr(name: String, value: Boolean, trueValue: String = "") {
        if (value) domNode.setAttribute(name, trueValue)
        else domNode.removeAttribute(name)
    }

    /**
     * Sets an attribute when [value] is true otherwise removes it.
     *
     * @param name to use
     * @param value for decision
     * @param trueValue value to use if attribute is set (default "")
     */
    fun attr(name: String, value: Boolean?, trueValue: String = "") {
        value?.let {
            if (it) domNode.setAttribute(name, trueValue)
            else domNode.removeAttribute(name)
        }
    }

    /**
     * Sets an attribute when [value] is true otherwise removes it.
     *
     * @param name to use
     * @param value for decision
     * @param trueValue value to use if attribute is set (default "")
     */
    fun attr(name: String, value: Flow<Boolean>, trueValue: String = "") {
        mountSimple(job, value) { v -> attr(name, v, trueValue) }
    }

    /**
     * Sets an attribute when [value] is true otherwise removes it.
     *
     * @param name to use
     * @param value for decision
     * @param trueValue value to use if attribute is set (default "")
     */
    fun attr(name: String, value: Flow<Boolean?>, trueValue: String = "") {
        mountSimple(job, value) { v -> attr(name, v, trueValue) }
    }

    /**
     * adds a [String] of class names to the classes attribute of this [Tag]
     */
    fun addToClasses(classesToAdd: String)

    /**
     * adds a [Flow] of class names to the classes attribute of this [Tag]
     */
    fun addToClasses(classesToAdd: Flow<String>)

    /**
     * Sets the *class* attribute.
     *
     * @param value as [String]
     */
    fun className(value: String) {
        addToClasses(value)
    }

    /**
     * Sets the *class* attribute.
     *
     * @param value [Flow] with [String]
     */
    fun className(value: Flow<String>) {
        addToClasses(value)
    }

    /**
     * Sets the *class* attribute from a [List] of [String]s.
     *
     * @param values as [List] of [String]s
     */
    fun classList(values: List<String>) {
        addToClasses(values.joinToString(" "))
    }

    /**
     * Sets the *class* attribute from a [List] of [String]s.
     *
     * @param values [Flow] with [List] of [String]s
     */
    fun classList(values: Flow<List<String>>) {
        addToClasses(values.map { it.joinToString(" ") })
    }

    /**
     * Sets the *class* attribute from a [Map] of [String] to [Boolean].
     * If the value of the [Map]-entry is true, the key will be used inside the resulting [String].
     *
     * @param values as [Map] with key to set and corresponding values to decide
     */
    fun classMap(values: Map<String, Boolean>) {
        addToClasses(values.filter { it.value }.keys.joinToString(" "))
    }

    /**
     * Sets the *class* attribute from a [Map] of [String] to [Boolean].
     * If the value of the [Map]-entry is true, the key will be used inside the resulting [String].
     *
     * @param values [Flow] of [Map] with key to set and corresponding values to decide
     */
    fun classMap(values: Flow<Map<String, Boolean>>) {
        addToClasses(values.map { map -> map.filter { it.value }.keys.joinToString(" ") })
    }

    /**
     * Sets the *style* attribute.
     *
     * @param value [String] to set
     */
    fun inlineStyle(value: String) {
        attr("style", value)
    }

    /**
     * Sets the *style* attribute.
     *
     * @param value [Flow] with [String]
     */
    fun inlineStyle(value: Flow<String>) {
        attr("style", value)
    }

    /**
     * Sets all scope-entries as data-attributes to the element.
     */
    fun Scope.asDataAttr() {
        for ((k, v) in this) {
            attr("data-${k.name}", v.toString())
        }
    }

    /**
     * Creates an [Listener] for the given event [name].
     *
     * @param name of the [Event] to listen for
     */
    override fun <X : Event> subscribe(name: String): Listener<X, E> = Listener(callbackFlow {
        val listener: (Event) -> Unit = {
            try {
                trySend(it.unsafeCast<X>())
            } catch (e: Exception) {
                console.error("Unexpected type while listening for `$name` events in Window object", e)
            }
        }
        domNode.addEventListener(name, listener)

        awaitClose { domNode.removeEventListener(name, listener) }
    })

    /**
     * Adds text-content of a [Flow] at this position
     *
     * @param into target to render text-content to
     * @receiver text-content
     */
    fun Flow<String>.renderText(into: Tag<*>? = null) {
        val target = into ?: span {}

        mountSimple(job, this) { content ->
            target.domNode.clear()
            target.domNode.appendChild(window.document.createTextNode(content))
        }
    }

    /**
     * Adds text-content of a [Flow] at this position
     *
     * @param into target to render text-content to
     * @receiver text-content
     */
    fun <T> Flow<T>.renderText(into: Tag<*>? = null) = this.map { it.toString() }.renderText(into)

    /**
     * Adds static text-content at this position
     *
     * @receiver text-content
     */
    operator fun String.unaryPlus(): Node = domNode.appendChild(document.createTextNode(this))

    /**
     * Adds a comment in your HTML by using !"Comment Text".
     *
     * @receiver comment-content
     */
    operator fun String.not(): Node = domNode.appendChild(document.createComment(this))

    /**
     * Sets scope-entry for the given [key] as data-attribute to the element
     * when available.
     *
     * @param key key of scope-entry to look for in scope
     */
    fun <T : Any> Scope.asDataAttr(key: Scope.Key<T>) {
        this[key]?.let {
            attr("data-${key.name}", it.toString())
        }
    }

    /**
     * This extension method takes a boolean [Flow] that controls the forwarding of the initial value:
     * If it is `true` the value will be passed further on the result flow, if it is `false` a `null` will appear instead.
     *
     * This is especially useful for DOM node attributes, that should only appear if a certain condition is true.
     *
     * Take the `aria-controls` attribute as example. This should only be set, if there is an area active / visible
     * to control. Within a dynamic component - like some disclosure based one - the latter is only shown, if a state-flow
     * is `true`:
     * ```kotlin
     * // `open`: Flow<Boolean>
     * button.attr("aria-controls", "panelId".whenever(open))
     * //                                     ^^^^^^^^^^^^^^
     * //                                     if open == true -> result flow provides "panelId" String
     * //                                     if open == false -> result flow provides `null` -> whole attribute is removed
     * ```
     *
     *  @param condition the boolean flow that decides whether to forward [T] or `null`
     */
    fun <T> T.whenever(condition: Flow<Boolean>): Flow<T?> = condition.map { if (it) this else null }

    /**
     * This extension method takes a boolean [Flow] that controls the forwarding of an initial flow:
     * If it is `true` the current value will be passed further on the result flow, if it is `false` a `null` will appear
     * instead.
     *
     * @see whenever
     */
    fun <T> Flow<T>.whenever(condition: Flow<Boolean>): Flow<T?> =
        condition.flatMapLatest { cond -> this.map { value -> if (cond) value else null } }

    /**
     * provides [RenderContext] next to this [Tag] on the same DOM-level.
     */
    val annex: RenderContext
}
