package io.fritz2.dom

import io.fritz2.dom.html.EventType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.Element
import org.w3c.dom.events.Event

interface WithEvents<out T : Element> : WithDomNode<T> {

    //TODO: better syntax with infix like "handle EVENT by HANDLER"
    //TODO: rename to "on"
    fun event(type: String, handler: (Event) -> Unit) = domNode.addEventListener(type, handler)

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun <T> event(type: EventType<T>): Flow<T> = callbackFlow {
        val listener: (Event) -> Unit = {
            channel.offer(type.extract(it))
        }
        domNode.addEventListener(type.name, listener)

        awaitClose { domNode.removeEventListener(type.name, listener) }
    }
}
