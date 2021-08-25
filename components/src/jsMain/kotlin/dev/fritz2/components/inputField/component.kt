package dev.fritz2.components.inputField

import dev.fritz2.binding.Store
import dev.fritz2.components.foundations.*
import dev.fritz2.dom.html.Input
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.dom.values
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.input
import dev.fritz2.styling.name
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.staticStyle
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.flowOf
import org.w3c.dom.HTMLInputElement

/**
 * This class deals with the configuration and rendering of an input element.
 *
 * The inputField can be configured for the following aspects:
 *  - the size of the element
 *  - some predefined styling variants
 *  - the element options of the HTML input element can be set.
 *    [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Attributes)
 *
 * You can optionally pass in a store in order to set the value and react to updates _automatically_.
 *
 * Example usages:
 * ```
 * inputField(value = dataStore /* inject a store so all user inputs are automatically reflected! */) {
 *     placeholder("Placeholder") // render a placeholder text for empty field
 * }
 *
 * // all state management can also be done manually if needed:
 * val someStore = storeOf("")
 * inputField {
 *     placeholder("Enter text")
 *     value(someStore.data) // connect a flow to the component for setting its value
 *     events {
 *         changes.values() handledBy someStore.update // connect an handler for emitting the user input made
 *     }
 *     element {
 *         // exposes the underlying HTML input element for direct access. Use with caution!
 *     }
 * }
 *
 * // apply predefined size and variant
 * inputField(value = dataStore) {
 *      size { small } // render a smaller input
 *      variant { filled } // fill the background with ``light`` color
 *      placeholder("Placeholder") // render a placeholder text for empty field
 * }
 *
 * // Of course you can apply custom styling as well
 * inputField({ // just use the ``styling`` parameter!
 *      background {
 *          color { dark }
 *      }
 *      radius { "1rem" }
 * },
 * value = dataStore) {
 *      size { small } // render a smaller input
 *      placeholder("Placeholder") // render a placeholder text for empty field
 * }
 * ```
 */
open class InputFieldComponent(protected val valueStore: Store<String>?) :
    Component<Input>,
    EventProperties<HTMLInputElement> by EventMixin(),
    ElementProperties<Input> by ElementMixin(),
    InputFormProperties by InputFormMixin(),
    SeverityProperties by SeverityMixin(),
    InputFieldProperties by InputFieldMixin() {

    companion object {
        val staticCss = staticStyle(
            "inputBox",
            """
                display: inline-flex;
                position: relative;
                vertical-align: middle;
                height: 2.5rem;
                appearance: none;
                align-items : center;
                justify-content: center;
                transition: all 250ms;
                white-space: nowrap;
                outline: none;
                width: 100%
            """
        )
    }

    val value = DynamicComponentProperty<String>()
    val type = DynamicComponentProperty(flowOf("text"))
    val step = DynamicComponentProperty<String>()

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ): Input {
        return with(context) {
            input({
                this@InputFieldComponent.size.value.invoke(Theme().input.sizes)()
                this@InputFieldComponent.variant.value.invoke(Theme().input.variants)()
            }, styling, baseClass + staticCss, id, prefix) {
                disabled(this@InputFieldComponent.disabled.values)
                readOnly(this@InputFieldComponent.readonly.values)
                placeholder(this@InputFieldComponent.placeholder.values)
                value(this@InputFieldComponent.value.values)
                type(this@InputFieldComponent.type.values)
                step(this@InputFieldComponent.step.values)
                className(this@InputFieldComponent.severityClassOf(Theme().input.severity).name)
                this@InputFieldComponent.valueStore?.let {
                    value(it.data)
                    changes.values() handledBy it.update
                }
                this@InputFieldComponent.events.value.invoke(this)
                this@InputFieldComponent.element.value.invoke(this)
            }
        }
    }
}