package dev.fritz2.dom

import dev.fritz2.binding.mountSimple
import dev.fritz2.dom.html.RenderContext
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.dom.clear
import org.w3c.dom.Node
import org.w3c.dom.Text

/**
 * Interface providing functionality to handle text-content
 */
interface WithText<N : Node> : WithDomNode<N>, RenderContext {

    /**
     * Mounts a [Flow] of [String]s to a [WithDomNode]
     *
     * @param target element to mount the [Flow] to
     * @param upstream [Flow] to mount to [target]
     */
    private fun mountTextNode(target: WithDomNode<*>, upstream: Flow<String>) {
        mountSimple(job, upstream) {
            target.domNode.clear()
            target.domNode.appendChild(TextNode(it).domNode)
        }
    }

    /**
     * Adds text-content of a [Flow] at this position
     *
     * @param classes css-classes to apply to the generated span-element
     * @param id id of the generated span-element
     * @receiver text-content
     */
    fun Flow<String>.asText(classes: String? = null, id: String? = null) =
        span(classes, id) {
            mountTextNode(this, this@asText)
        }

    /**
     * Adds text-content of a [Flow] at this position
     *
     * @param classes css-classes to apply to the generated span-element
     * @param id id of the generated span-element
     * @receiver text-content
     */
    fun <T> Flow<T>.asText(classes: String? = null, id: String? = null) =
        this.map { it.toString() }.asText(classes, id)

    /**
     * Adds static text-content at this position
     *
     * @receiver text-content
     */
    operator fun String.unaryPlus(): Node = domNode.appendChild(document.createTextNode(this))
}

/**
 * Represents a DOM-TextNode
 *
 * @param content text-content
 * @param domNode wrapped domNode (created by default)
 */
class TextNode(private val content: String, override val domNode: Text = window.document.createTextNode(content)) :
    WithDomNode<Text>
