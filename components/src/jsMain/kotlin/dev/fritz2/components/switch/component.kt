package dev.fritz2.components.switch

import dev.fritz2.binding.Store
import dev.fritz2.components.foundations.*
import dev.fritz2.dom.html.Div
import dev.fritz2.dom.html.Input
import dev.fritz2.dom.html.Label
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.dom.states
import dev.fritz2.styling.*
import dev.fritz2.styling.params.BasicParams
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.params.Style
import dev.fritz2.styling.theme.FormSizesStyles
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.w3c.dom.HTMLInputElement

/**
 * This class combines the _configuration_ and the core styling of a switch.
 *
 * This class offers the following _configuration_ features:
 *  - the optional label of a switch (static, dynamic via a [Flow<String>] or customized content of a Div.RenderContext )
 *  - some predefined styling variants (size)
 *  - the style of the switch
 *  - the style checked state
 *  - the style of the dot
 *  - the style of the label
 *  - link an external boolean flow to set the checked state of the box
 *  - link an external boolean flow to set the disabled state of the box
 *  - link events of the switch like ``changes`` with external handlers
 *
 *  This can be done within a functional expression that is the last parameter of the factory function, called
 *  ``build``. It offers an initialized instance of this [SwitchComponent] class as receiver, so every mutating
 *  method can be called for configuring the desired state for rendering the switch.
 *
 * Example usage
 * ```
 * val cheeseStore = storeOf(false)
 * switch(value = cheeseStore) {
 *      label("with extra cheese") // set the label
 *      size { normal } // choose a predefined size
 * }
 *
 * // one can handle the events and preselect the control also manually if needed:
 * switch {
 *      label("with extra cheese") // set the label
 *      size { normal } // choose a predefined size
 *      checked(cheeseStore.data) // link a [Flow<Boolean>] in order to visualize the checked state
 *      events { // open inner context with all DOM-element events
 *          changes.states() handledBy cheeseStore.update // connect the changes event with the state store
 *      }
 *      element {
 *          // exposes the underlying HTML input element for direct access. Use with caution!
 *      }
 * }
 * ```
 */
open class SwitchComponent(protected val value: Store<Boolean>? = null) :
    Component<Label>,
    EventProperties<HTMLInputElement> by EventMixin(),
    ElementProperties<Input> by ElementMixin(),
    InputFormProperties by InputFormMixin(),
    SeverityProperties by SeverityMixin() {

    companion object {
        val switchInputStaticCss = staticStyle(
            "switch",
            """
            position: absolute;
            border: 0px;
            clip: rect(0px, 0px, 0px, 0px);
            height: 0px;
            width: 0px;
            white-space: nowrap;
            overflow: hidden;
            outline: none;
            &:focus{
                outline: none;
            }           
            """
        )
    }

    val size = ComponentProperty<FormSizesStyles.() -> Style<BasicParams>> { Theme().switch.sizes.normal }

    private var label: (Div.() -> Unit)? = null
    fun label(value: String) {
        label = {
            +value
        }
    }

    fun label(value: Flow<String>) {
        label = {
            value.asText()
        }
    }

    fun label(value: (Div.() -> Unit)) {
        label = value
    }

    val labelStyle = ComponentProperty(Theme().switch.label)
    val dotStyle = ComponentProperty<Style<BasicParams>> {}
    var checkedStyle = ComponentProperty(Theme().switch.checked)
    val checked = DynamicComponentProperty(flowOf(false))

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ): Label {
        return with(context) {
            label({
                display { inlineFlex }
                alignItems { center }
                this@SwitchComponent.size.value.invoke(Theme().switch.sizes)()
            }, baseClass, prefix = prefix) {
                input({
                    Theme().switch.input()
                    children("&[checked] + div") {
                        this@SwitchComponent.checkedStyle.value()
                    }
                }, switchInputStaticCss, id, prefix) {
                    disabled(this@SwitchComponent.disabled.values)
                    readOnly(this@SwitchComponent.readonly.values)
                    type("checkbox")
                    checked(this@SwitchComponent.value?.data ?: this@SwitchComponent.checked.values)
                    this@SwitchComponent.events.value.invoke(this)
                    this@SwitchComponent.value?.let { changes.states() handledBy it.update }
                    className(this@SwitchComponent.severityClassOf(Theme().switch.severity).name)
                    this@SwitchComponent.element.value.invoke(this)
                }

                div({
                    Theme().switch.default()
                    styling()
                }) {
                    div({
                        Theme().switch.dot()
                        this@SwitchComponent.dotStyle.value()
                    }) {}
                }

                this@SwitchComponent.label?.let {
                    div({
                        this@SwitchComponent.labelStyle.value()
                    }) {
                        it(this)
                    }
                }
            }
        }
    }
}