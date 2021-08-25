package dev.fritz2.components.radios

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
 * This class combines the _configuration_ and the core styling of a radio button.
 *
 * This class offers the following _configuration_ features:
 *  - the label(mapping) static or dynamic via a [Flow<String>] or customized content see the examples below
 *  - some predefined styling variants (size)
 *  - the style of the selected state
 *  - the style of the radio
 *  - the style of the label
 *  - the groupname
 *  - link an external boolean flow to set the selected state of the box
 *  - link an external boolean flow to set the disabled state of the box
 *  - link events of the switch like ``changes`` with external handlers
 *
 *  This can be done within a functional expression that is the last parameter of the factory function, called
 *  `build`. It offers an initialized instance of this [RadioComponent] class as receiver, so every mutating
 *  method can be called for configuring the desired state for rendering the radio.
 *
 * Example usage
 * ```
 * // simple use case
 * val cheeseStore = storeOf(false)
 * radio(value = cheeseStore) {
 *      label("with extra cheese") // set the label
 * }
 *
 * // manual event handling and styling options
 * radio {
 *      label("with extra cheese") // set the label
 *      size { normal } // choose a predefined size
 *      selected(cheeseStore.data) // link a [Flow<Boolean>] in order to visualize the checked state
 *      events { // open inner context with all DOM-element events
 *          changes.states() handledBy cheeseStore.update // connect the changes event with the state store
 *      }
 *      element {
 *          // exposes the underlying HTML input element for direct access. Use with caution!
 *      }
 * }
 * ```
 */
open class RadioComponent(protected val value: Store<Boolean>? = null) :
    Component<Label>,
    EventProperties<HTMLInputElement> by EventMixin(),
    ElementProperties<Input> by ElementMixin(),
    InputFormProperties by InputFormMixin(),
    SeverityProperties by SeverityMixin() {

    private val radioInputStaticCss = style(
        """
            position: absolute;
            border: 0px;
            clip: rect(0px, 0px, 0px, 0px);
            height: 0px;
            width: 0px;
            overflow: hidden;
            white-space: nowrap;
            &:focus{
                outline: none;
            }
            &:focus + label::before {
                box-shadow: 0 0 1px ${Theme().colors.gray700};
            }
            &:disabled + label {
                color: ${Theme().colors.disabled};
                cursor: not-allowed;
            }
            &:disabled + label::before {
                opacity: 0.3;
                cursor: not-allowed;
                boxShadow: none;
                color: ${Theme().colors.disabled};
            }
        """.trimIndent(),
        prefix = "radioInput"
    )

    val size = ComponentProperty<FormSizesStyles.() -> Style<BasicParams>> { Theme().radio.sizes.normal }

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

    val labelStyle = ComponentProperty(Theme().radio.label)
    val selectedStyle = ComponentProperty(Theme().radio.selected)
    val selected = DynamicComponentProperty(flowOf(false))
    val groupName = ComponentProperty<String?>(null)

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
                verticalAlign { top }
                css("-webkit-box-align: center;")
                this@RadioComponent.size.value.invoke(Theme().radio.sizes)()
            }, baseClass, prefix = prefix) {
                input({
                    Theme().radio.input()
                    children("&[checked] + div") {
                        this@RadioComponent.selectedStyle.value()
                    }
                }, this@RadioComponent.radioInputStaticCss, prefix = prefix) {
                    disabled(this@RadioComponent.disabled.values)
                    readOnly(this@RadioComponent.readonly.values)
                    type("radio")
                    this@RadioComponent.groupName.value?.let { name(it) }
                    checked(this@RadioComponent.value?.data ?: this@RadioComponent.selected.values)
                    value("X")
                    className(this@RadioComponent.severityClassOf(Theme().radio.severity).name)
                    this@RadioComponent.value?.let { changes.states() handledBy it.update }
                    this@RadioComponent.events.value.invoke(this)
                    this@RadioComponent.element.value.invoke(this)
                }

                div({
                    Theme().radio.default()
                    styling()
                }) { }

                this@RadioComponent.label?.let {
                    div({
                        this@RadioComponent.labelStyle.value()
                    }) {
                        it(this)
                    }
                }
            }
        }
    }
}