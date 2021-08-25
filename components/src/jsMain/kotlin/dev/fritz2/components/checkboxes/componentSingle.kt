package dev.fritz2.components.checkboxes

import dev.fritz2.binding.Store
import dev.fritz2.components.*
import dev.fritz2.components.foundations.*
import dev.fritz2.dom.html.Input
import dev.fritz2.dom.html.Label
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.dom.states
import dev.fritz2.styling.*
import dev.fritz2.styling.params.BasicParams
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.params.Style
import dev.fritz2.styling.theme.FormSizesStyles
import dev.fritz2.styling.theme.IconDefinition
import dev.fritz2.styling.theme.Icons
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.w3c.dom.HTMLInputElement

/**
 * This class combines the _configuration_ and the core styling of a checkbox.
 *
 * This class offers the following _configuration_ features:
 *  - the label(mapping) static or dynamic via a [Flow<String>] or customized content see the examples below
 *  - some predefined styling variants (size)
 *  - the style of the checkbox
 *  - the style checked state
 *  - the style of the label
 *  - the checked icon (use our icon library of our theme)
 *  - link an external boolean flow to set the checked state of the box
 *  - link an external boolean flow to set the disabled state of the box
 *  - link events of the checkbox like `changes` with external handlers
 *
 *  This can be done within a functional expression that is the last parameter of the factory function, called
 *  `build`. It offers an initialized instance of this [CheckboxComponent] class as receiver, so every mutating
 *  method can be called for configuring the desired state for rendering the checkbox.
 *
 * Example usage
 * ```
 * // simple, store based:
 * val cheeseStore = storeOf(false)
 * checkbox(value = cheeseStore) {
 *      label("with extra cheese") // set the label
 * }
 *
 * // with manual event handling and further options
 * val cheeseStore = storeOf(false)
 * checkbox {
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
open class CheckboxComponent(protected val value: Store<Boolean>?) :
    Component<Label>,
    EventProperties<HTMLInputElement> by EventMixin(),
    ElementProperties<Input> by ElementMixin(),
    InputFormProperties by InputFormMixin(),
    SeverityProperties by SeverityMixin() {

    companion object {
        val checkboxInputStaticCss = staticStyle(
            "checkbox",
            """
            position: absolute;
            border: 0px;
            clip: rect(0px, 0px, 0px, 0px);
            height: 0px;
            width: 0px;
            overflow: hidden;
            white-space: nowrap;
            outline: none;
            &:focus{
                outline: none;
            }           
            """
        )
    }

    val size = ComponentProperty<FormSizesStyles.() -> Style<BasicParams>> { Theme().checkbox.sizes.normal }
    val icon = ComponentProperty<Icons.() -> IconDefinition> { Theme().icons.check }

    private var label: (RenderContext.() -> Unit)? = null
    fun label(value: String) {
        label = {
            span { +value }
        }
    }

    fun label(value: Flow<String>) {
        label = {
            span { value.asText() }
        }
    }

    fun label(value: (RenderContext.() -> Unit)) {
        label = value
    }

    val labelStyle = ComponentProperty(Theme().checkbox.label)
    val checked = DynamicComponentProperty(flowOf(false))
    var checkedStyle = ComponentProperty(Theme().checkbox.checked)

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ): Label = with(context) {
        return label({
            display { inlineFlex }
            alignItems { center }
            this@CheckboxComponent.size.value.invoke(Theme().checkbox.sizes)()
        }, baseClass, prefix = prefix) {
            input({
                Theme().checkbox.input()
                children("&[checked] + div") {
                    this@CheckboxComponent.checkedStyle.value()
                }
            }, checkboxInputStaticCss, prefix = prefix) {
                disabled(this@CheckboxComponent.disabled.values)
                readOnly(this@CheckboxComponent.readonly.values)
                type("checkbox")
                checked(this@CheckboxComponent.value?.data ?: this@CheckboxComponent.checked.values)
                className(this@CheckboxComponent.severityClassOf(Theme().checkbox.severity).name)
                this@CheckboxComponent.value?.let { changes.states() handledBy it.update }
                this@CheckboxComponent.events.value.invoke(this)
                this@CheckboxComponent.element.value.invoke(this)
            }

            div({
                Theme().checkbox.default()
                styling()
            }) {
                icon({
                    Theme().checkbox.icon()
                }) {
                    def(this@CheckboxComponent.icon.value(Theme().icons))
                }
            }

            this@CheckboxComponent.label?.let {
                div({
                    this@CheckboxComponent.labelStyle.value()
                }) {
                    it(this)
                }
            }
        }
    }
}