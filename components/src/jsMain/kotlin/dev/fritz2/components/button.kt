package dev.fritz2.components

import dev.fritz2.dom.DomListener
import dev.fritz2.dom.Listener
import dev.fritz2.dom.html.Button
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.*
import dev.fritz2.styling.staticStyle
import dev.fritz2.styling.theme.Colors
import dev.fritz2.styling.theme.FormSizes
import dev.fritz2.styling.theme.PushButtonVariants
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.Flow
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.events.MouseEvent

/**
 * This class combines the _configuration_ and the core rendering of a button.
 *
 * The rendering functions are used by the component factory functions [pushButton] and [clickButton], so they are
 * not meant to be called directly unless you plan to implement your own button.
 * If not, just use those functions for stetting up a button!
 *
 * Much more important are the _configuration_ functions. You can configure the following aspects:
 *  - the background color
 *  - the label text
 *  - the icon including its position (left or right)
 *  - a state called ``loading`` for visualizing a longer background task
 *  - an additional label during the loading state
 *  - some predefined styling variants
 *  - link events of the button like ``clicks`` with external handlers
 *
 *  This can be done within a functional expression that is the last parameter of the two button functions, called
 *  ``build``. It offers an initialized instance of this [PushButtonComponent] class as receiver, so every mutating
 *  method can be called for configuring the desired state for rendering the button.
 *
 *  The following example shows the usage:
 *  ```
 *  pushButton { /* this == PushButtonComponent() */
 *      icon { fromTheme { check } } // set up an icon
 *      iconPlacement { right } // place the icon on the right side (``left`` is the default)
 *      loading(someStore.loading) // pass in some [Flow<Boolean>] that shows a spinner if ``true`` is passed
 *      loadingText("saving") // show an _alternate_ label, if store sends ``true``
 *      text("save") // define the default label
 *      disabled(true) // disable the button; could also be a ``Flow<Boolean>`` for dynamic disabling
 *      events { // open inner context with all DOM-element events
 *          clicks handledBy someStore.update // react to click event
 *      }
 *      element {
 *          // exposes the underlying HTML button element for direct access. Use with caution!
 *      }
 *  }
 *  ```
 */
@ComponentMarker
open class PushButtonComponent :
    Component<Unit>,
    EventProperties<HTMLButtonElement> by EventMixin(),
    ElementProperties<Button> by ElementMixin(),
    FormProperties by FormMixin() {
    companion object {
        val staticCss = staticStyle(
            "button",
            """
                appearance: none;
                display: inline-flex;
                align-items : center;
                justify-content: center;
                transition: all 250ms;
                user-select: none;
                white-space: nowrap;
                vertical-align: middle;
                outline: none;
                text-overflow: ellipsis;
                
                &:disabled {
                    opacity: 0.4;
                    cursor: not-allowed;
                    boxShadow: none;
                }
            """
        )

        internal val hidden = staticStyle(
            "hidden",
            "visibility: hidden;"
        )

        val iconPlacementContext = IconPlacementContext()
    }

    private val iconSize = "1.15em"
    private val marginToText = "0.35rem"
    private val marginToBorder = "-0.2rem"

    val centerIconStyle: Style<BasicParams> = {
        width { "1.5em" }
        height { "1.5em" }
    }

    val centerSpinnerStyle: Style<BasicParams> = {
        width { iconSize }
        height { iconSize }
    }

    val leftSpinnerStyle: Style<BasicParams> = {
        width { "1.0em" }
        height { "1.0em" }
        margins {
            left { marginToBorder }
            right { marginToText }
        }
    }

    val rightSpinnerStyle: Style<BasicParams> = {
        width { "1.0em" }
        height { "1.0em" }
        margins {
            left { marginToText }
            right { marginToBorder }
        }
    }

    val leftIconStyle: Style<BasicParams> = {
        width { iconSize }
        height { iconSize }
        margins {
            left { marginToBorder }
            right { marginToText }
        }
    }

    val rightIconStyle: Style<BasicParams> = {
        width { iconSize }
        height { iconSize }
        margins {
            right { marginToBorder }
            left { marginToText }
        }
    }

    private fun buildColor(value: ColorProperty): Style<BasicParams> = { css("--main-color: $value;") }

    private var colorField: Style<BasicParams> = buildColor(Theme().colors.primary.base)

    fun color(value: Colors.() -> ColorProperty) {
        colorField = buildColor(Theme().colors.value())
    }

    val variant = ComponentProperty<PushButtonVariants.() -> Style<BasicParams>> { Theme().button.variants.solid }
    val size = ComponentProperty<FormSizes.() -> Style<BasicParams>> { Theme().button.sizes.normal }

    private var text: (RenderContext.(hide: Boolean) -> Unit)? = null

    fun text(value: String) {
        text = { hide -> span { +value
                if(hide) className(hidden.name)
            }
        }
    }

    fun text(value: Flow<String>) {
        text = { hide -> span { value.asText()
                if(hide) className(hidden.name)
            }
        }
    }

    private var loadingText: (RenderContext.() -> Unit)? = null

    fun loadingText(value: String) {
        loadingText = { span { +value } }
    }

    fun loadingText(value: Flow<String>) {
        loadingText = { span { value.asText() } }
    }

    private var loading: Flow<Boolean>? = null

    fun loading(value: Flow<Boolean>) {
        loading = value
    }

    private var icon: ((RenderContext, Style<BasicParams>) -> Unit)? = null

    fun icon(
        styling: BasicParams.() -> Unit = {},
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = IconComponent.prefix,
        build: IconComponent.() -> Unit = {}
    ) {
        icon = { context, iconStyle ->
            context.icon(styling + iconStyle, baseClass, id, prefix, build)
        }
    }

    enum class IconPlacement {
        Right,
        Left
    }

    class IconPlacementContext {
        val right: IconPlacement = IconPlacement.Right
        val left: IconPlacement = IconPlacement.Left
    }

    val iconPlacement = ComponentProperty<IconPlacementContext.() -> IconPlacement> { IconPlacement.Left }

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ) {
        context.apply {
            (::button.styled(styling, baseClass + staticCss, id, prefix) {
                colorField()
                variant.value.invoke(Theme().button.variants)()
                size.value.invoke(Theme().button.sizes)()
            }) {
                disabled(disabled.values)
                if (text == null) {
                    renderIcon(this, centerIconStyle, centerSpinnerStyle)
                } else {
                    if (icon != null && iconPlacement.value(iconPlacementContext) == IconPlacement.Left) {
                        renderIcon(this, leftIconStyle, leftSpinnerStyle)
                    }
                    renderText(this)
                    if (icon != null && iconPlacement.value(iconPlacementContext) == IconPlacement.Right) {
                        renderIcon(this, rightIconStyle, rightSpinnerStyle)
                    }
                }
                events.value.invoke(this)
                element.value.invoke(this)
            }
        }
    }

    private fun renderIcon(renderContext: Button, iconStyle: Style<BasicParams>, spinnerStyle: Style<BasicParams>) {
        if (loading == null) {
            icon?.invoke(renderContext, iconStyle)
        } else {
            renderContext.apply {
                loading?.render { running ->
                    if (running) {
                        spinner(spinnerStyle) {}
                    } else {
                        icon?.invoke(this, iconStyle)
                    }
                }
            }
        }
    }

    private fun renderText(renderContext: Button) {
        if (loading == null || icon != null) {
            text?.invoke(renderContext, false)
        } else {
            renderContext.apply {
                loading?.render { running ->
                    if (running) {
                        spinner({
                            if (loadingText == null) {
                                css("position: absolute;")
                                centerSpinnerStyle()
                            } else leftSpinnerStyle()
                        }) {}
                        if (loadingText != null) {
                            loadingText!!.invoke(this)
                        } else {
                            text?.invoke(this, true)
                        }
                    } else {
                        text?.invoke(this, false)
                    }
                }
            }
        }
    }
}

/**
 * This component generates a simple button.
 *
 * You can set the label, an icon, the position of the icon and access its events.
 * For a detailed overview about the possible properties of the component object itself, have a look at
 * [PushButtonComponent]
 *
 * In contrast to the [clickButton] component, this one does not return a [Listener] (basically a [Flow]) and so
 * the event handling has to be done manually!
 *
 * @see PushButtonComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [PushButtonComponent]
 */
fun RenderContext.pushButton(
    styling: BasicParams.() -> Unit = {},
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "push-button",
    build: PushButtonComponent.() -> Unit = {}
) {
    PushButtonComponent().apply(build).render(this, styling, baseClass, id, prefix)
}

/**
 * This component generates a simple button.
 *
 * You can set the label, an icon, the position of the icon and access its events.
 * For a detailed overview about the possible properties of the component object itself, have a look at
 * [PushButtonComponent]
 *
 * In contrast to the [pushButton] component, this variant returns a [Listener] (basically a [Flow]) in order
 * to combine the button declaration directly to a fitting _handler_. Some other components
 * offer such a handler btw, so for example you can combine such a [clickButton] with a [modal] like this:
 * ```
 * clickButton { text("save") } handledBy modal {
 *      items { p {+"foo"} }
 * }
 * ```
 *
 * @see PushButtonComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [PushButtonComponent]
 * @return a listener (think of a flow!) that offers the clicks of the button
 */
fun RenderContext.clickButton(
    styling: BasicParams.() -> Unit = {},
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "push-button",
    build: PushButtonComponent.() -> Unit = {}
): DomListener<MouseEvent, HTMLButtonElement> {
    var clickEvents: DomListener<MouseEvent, HTMLButtonElement>? = null
    pushButton(styling, baseClass, id, prefix) {
        build()
        events {
            clickEvents = clicks
        }
    }
    return clickEvents!!
}
