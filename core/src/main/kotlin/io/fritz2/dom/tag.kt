package io.fritz2.dom

import io.fritz2.binding.Handler
import io.fritz2.binding.MultiMountPoint
import io.fritz2.binding.Seq
import io.fritz2.binding.SingleMountPoint
import io.fritz2.dom.html.HtmlElements
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import kotlin.browser.window

@DslMarker
annotation class HtmlTagMarker

//TODO: remove unnecassary default-arguments

@FlowPreview
@HtmlTagMarker
abstract class Tag<T : Element>(tagName: String, val id: String? = null, val baseClass: String? = null, override val domNode: T = createDomElement(tagName, id, baseClass).unsafeCast<T>())
    : WithDomNode<T>, WithAttributes<T>, WithEvents<T>(), HtmlElements {

    override fun <X : Element, T : Tag<X>> register(element: T, content: (T) -> Unit): T {
        content(element)
        domNode.appendChild(element.domNode)
        return element
    }

    fun <X : Element> Flow<Tag<X>>.bind(): SingleMountPoint<WithDomNode<Element>> = DomMountPoint(this, domNode)

    fun <X : Element> Seq<Tag<X>>.bind(): MultiMountPoint<WithDomNode<Element>> = DomMultiMountPoint(this.data, domNode)

    operator fun <E: Event, X: Element> Handler<Unit>.compareTo(listener: Listener<E, X>): Int {
        execute(listener.events.map { Unit })
        return 0
    }

    var className: Flow<String>
        get() {throw NotImplementedError()}
        set(value) {
            //TODO: better elvis?
            (if (baseClass != null) value.map { "$baseClass $it" } else value).bindAttr("class")
        }

    var classList: Flow<List<String>>
        get() {throw NotImplementedError()}
        set(values) {
            (if (baseClass != null) values.map { it + baseClass} else values).bindAttr("class")
        }

    var classMap: Flow<Map<String, Boolean>>
        get() {throw NotImplementedError()}
        set(values) {
            (if (baseClass != null) values.map { it + (baseClass to true)} else values).bindAttr("class")
        }
}

internal fun createDomElement(tagName: String, id: String?, baseClass: String?): Element =
    window.document.createElement(tagName).also {element ->
        id?.let {
            element.setAttribute("id",it)
        }
        baseClass?.let {
            element.setAttribute("class", it)
        }
    }
