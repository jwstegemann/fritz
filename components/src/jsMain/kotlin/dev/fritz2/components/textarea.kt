package dev.fritz2.components

import dev.fritz2.binding.Store
import dev.fritz2.components.textarea.TextAreaComponent
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.BasicParams

/**
 * This component generates a TextArea.
 *
 * You can optionally pass a store in order to set the value and react to updates automatically.
 *
 * Basic usage
 * ```
 * textArea(value = dataStore) {
 *     placeholder { "My placeholder" }
 * }
 * ```
 *
 * @see TextAreaComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param value optional [Store] that holds the data of the textarea
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [TextAreaComponent]
 */
fun RenderContext.textArea(
    styling: BasicParams.() -> Unit = {},
    value: Store<String>? = null,
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "textArea",
    build: TextAreaComponent.() -> Unit
) = TextAreaComponent(value).apply(build).render(this, styling, baseClass, id, prefix)