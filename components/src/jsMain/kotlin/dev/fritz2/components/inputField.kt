package dev.fritz2.components


import dev.fritz2.binding.Store
import dev.fritz2.components.foundations.ElementMixin
import dev.fritz2.components.inputField.InputFieldComponent
import dev.fritz2.dom.html.Input
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.BasicParams


/**
 * This component generates a text based input field.
 *
 * You can optionally pass in a store in order to set the value and react to updates _automatically_.
 *
 * There are options to choose from predefined sizes and some variants from the Theme.
 *
 * To enable or disable it or to make it readOnly just use the well known attributes of the HTML
 * [input element](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/Input). To manually set the value or
 * react to a change refer also to its event's. All that can be achieved via the [ElementMixin.element] property!
 *
 * Basic usage
 * ```
 * val text = storeOf("")
 * inputField(value = text) {
 *     placeholder("Enter some text")
 * }
 * ```
 *
 * @see InputFieldComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param value optional [Store] that holds the data of the input
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [InputFieldComponent]
 */
fun RenderContext.inputField(
    styling: BasicParams.() -> Unit = {},
    value: Store<String>? = null,
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "inputField",
    build: InputFieldComponent.() -> Unit = {}
): Input = InputFieldComponent(value).apply(build).render(this, styling, baseClass, id, prefix)
