package dev.fritz2.dom

import dev.fritz2.dom.html.RenderContext
import kotlinx.browser.document
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.w3c.dom.Node

/**
 * Provides functionality to handle comments.
 */
interface WithComment<out T : Node> : WithDomNode<T>, RenderContext {

    /**
     * Adds comment-content of a [Flow] at this position
     *
     * @receiver comment-content
     */
    fun Flow<String>.asComment() {
        mountDomNode(job, domNode) { childJob ->
            childJob.cancelChildren()
            this.map { TextNode(it) }
        }
    }

    /**
     * Adds comment-content of a [Flow] at this position
     *
     * @receiver comment-content
     */
    fun <T> Flow<T>.asComment() {
        mountDomNode(job, domNode) { childJob ->
            childJob.cancelChildren()
            this.map { TextNode(it.toString()) }
        }
    }

    /**
     * Adds a comment in your HTML by using !"Comment Text".
     *
     * @receiver comment-content
     */
    operator fun String.not(): Node = domNode.appendChild(document.createComment(this))
}