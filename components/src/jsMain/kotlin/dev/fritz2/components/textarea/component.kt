package dev.fritz2.components.textarea

import dev.fritz2.binding.Store
import dev.fritz2.components.foundations.*
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.dom.html.TextArea
import dev.fritz2.dom.values
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.name
import dev.fritz2.styling.params.BasicParams
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.params.Style
import dev.fritz2.styling.staticStyle
import dev.fritz2.styling.textarea
import dev.fritz2.styling.theme.FormSizesStyles
import dev.fritz2.styling.theme.TextAreaResize
import dev.fritz2.styling.theme.TextAreaVariants
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.flowOf
import org.w3c.dom.HTMLTextAreaElement

/**
 * This class handles the configuration of a textarea element.
 *
 * Possible values to set are( default *) :
 *  - size : small | normal * | large
 *  - resizeBehavior : none | vertical *| horizontal
 *  - placeholder : String | Flow<String>
 *  - disable : Boolean | Flow<Boolean>
 *  - value -> maybe you want to set an initial value instead of a placeholder
 *  - events -> access the DOM events of the underlying HTML element
 *  - element -> basic properties of the textarea html element; use with caution!
 *  - the base options of the HTML input element can be set.
 *    [Attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/textarea#Attributes)
 *
 * Example usage
 * ```
 * // Basic useage
 * textArea(value = dataStore) {
 *     placeholder { "My placeholder" } // render a placeholder text for empty textarea
 * }
 *
 * // Styling and options
 * textArea({ // use the styling parameter
 *     background {
 *         color { dark }
 *     }
 *     color { light }
 *     radius { "1rem" }}, store = dataStore) {
 *     disable(true) // textarea is disabled
 *     resizeBehavior { none } // resizing is not possible
 *     size { large } // render a large textarea
 * }
 *
 * // disabled
 * textArea {
 *     value { dataStore.data } // value depends on value in store
 *     disable(true) // editing is disabled, but resizing still works
 * }
 *
 * // all state management can also be done manually if needed:
 * val someStore = storeOf("some initial text")
 * textArea {
 *     value(someStore.data)
 *     events {
 *         changes.values() handledBy someStore.update
 *     }
 * }
 * ```
 */
open class TextAreaComponent(protected val valueStore: Store<String>? = null) :
    Component<TextArea>,
    EventProperties<HTMLTextAreaElement> by EventMixin(),
    ElementProperties<TextArea> by ElementMixin(),
    InputFormProperties by InputFormMixin(),
    SeverityProperties by SeverityMixin() {

    companion object {
        val staticCss = staticStyle(
            "textAreaContainer",
            """
            outline: 0px;
            position: relative;
            appearance: none;
            transition: all 0.2s ease 0s;
            min-height: 80px;
            
            """
        )
    }

    val value = DynamicComponentProperty(flowOf(""))
    val variant = ComponentProperty<TextAreaVariants.() -> Style<BasicParams>> { basic }
    val placeholder = DynamicComponentProperty(flowOf(""))
    val resizeBehavior = ComponentProperty<TextAreaResize.() -> Style<BasicParams>> { Theme().textArea.resize.both }
    val size = ComponentProperty<FormSizesStyles.() -> Style<BasicParams>> { Theme().textArea.sizes.normal }

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ): TextArea {
        return with(context) {
            textarea({
                this@TextAreaComponent.resizeBehavior.value.invoke(Theme().textArea.resize)()
                this@TextAreaComponent.size.value.invoke(Theme().textArea.sizes)()
                this@TextAreaComponent.variant.value.invoke(Theme().textArea.variants)()
            }, styling, baseClass + staticCss, id, prefix) {
                disabled(this@TextAreaComponent.disabled.values)
                readOnly(this@TextAreaComponent.readonly.values)
                placeholder(this@TextAreaComponent.placeholder.values)
                value(this@TextAreaComponent.value.values)
                className(this@TextAreaComponent.severityClassOf(Theme().textArea.severity).name)
                this@TextAreaComponent.valueStore?.let {
                    value(it.data)
                    changes.values() handledBy it.update
                }
                this@TextAreaComponent.events.value.invoke(this)
                this@TextAreaComponent.element.value.invoke(this)
            }
        }
    }
}