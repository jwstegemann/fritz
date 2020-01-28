package io.fritz2.dom.html

import io.fritz2.dom.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.w3c.dom.Element

@ExperimentalCoroutinesApi
@FlowPreview
fun <E : Element> html(content: HtmlElements.() -> Tag<E>) =
    content(object : HtmlElements {
        override fun <X : Element, T : Tag<X>> register(element: T, content: (T) -> Unit): T {
            content(element)
            return element
        }
    })
