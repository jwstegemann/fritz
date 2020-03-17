package io.fritz2.dom.html

import io.fritz2.dom.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.w3c.dom.Element

@ExperimentalCoroutinesApi
@FlowPreview
fun <E : Element> tags(content: HtmlElements.() -> Tag<E>): Tag<E> =
    content(object : HtmlElements {

        var alreadyRegistered: Boolean = false

        override fun <X : Element, T : Tag<X>> register(element: T, content: (T) -> Unit): T {
            if (alreadyRegistered) {
                throw MultipleRootElementsException(
                    "You can have only one root-tag per html-context!"
                )
            } else {
                content(element)
                alreadyRegistered = true
                return element
            }
        }
    })

internal class MultipleRootElementsException(message: String) : RuntimeException(message)