package dev.fritz2.headless.foundation

import dev.fritz2.core.*
import kotlinx.coroutines.flow.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.events.Event

/**
 * Base class that provides all functionality needed for components, that have some "open" and "close" state of
 * representation.
 *
 * Just extend from this class to gain and provide access to the basic data binding [openState] that holds the
 * central state, the [opened] data-flow and expressive handler like [close] or [open] to set the state.
 *
 * Typical examples of [OpenClose] based components are modal dialogs or all popup-components, that appear and
 * disappear based upon user interaction.
 */
abstract class OpenClose : WithJob {

    val openState = DatabindingProperty<Boolean>()

    val opened: Flow<Boolean> by lazy { openState.data }

    val close by lazy {
        SimpleHandler<Unit> { data, _ ->
            openState.handler?.invoke(this, data.map { false })
        }
    }

    val open by lazy {
        SimpleHandler<Unit> { data, _ ->
            openState.handler?.invoke(this, data.map { true })
        }
    }

    val toggle by lazy {
        SimpleHandler<Unit> { data, _ ->
            openState.handler?.invoke(this, data.map { !opened.first() })
        }
    }

    /**
     * Combines all events relevant for toggling an element that implements the [OpenClose] behavior. By default, these
     * events are clicking the left mouse button or pressing the Enter or Space keys.
     *
     * @param init pass an optional lambda to execute event handling manipulations, like calling
     * `stopPropagation` or alike
     */
    internal fun Tag<*>.activations(init: Event.() -> Unit = {}): Flow<Event> =
        // If the wrapped element is a button, click events are already triggered by the Enter and Space keys.
        if (domNode is HTMLButtonElement) {
            clicks { init() }
        } else {
            merge(
                clicks { init() },
                keydownsIf {
                    (shortcutOf(this) in setOf(Keys.Space, Keys.Enter)).also {
                        if(it) init()
                    }
                })
        }

}
