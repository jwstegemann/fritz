package dev.fritz2.components

import dev.fritz2.binding.Store
import dev.fritz2.components.selectField.SelectFieldComponent
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.BasicParams

/**
 * This function generates a [selectField] element.
 * So this supports the use case to choose one item from a list of options.
 * If there is enough vertical or horizontal space consider a [radioGroup] as alternative.
 * If there are lots of option, consider using a [typeAhead] instead.
 *
 * You have to pass a store as value in order to handle the selected value,
 * and the events will be connected automatically.
 *
 * Basic usage:
 * ```
 * val myOptions = listOf("black", "red", "yellow")
 * val selectedItem = storeOf(myOptions[0]) // preselect "red"
 * selectField (items = myOptions, value = selectedItem) {
 * }
 * ```
 *
 * @see SelectFieldComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param items a list of all available options
 * @param value for backing up the preselected item and reflecting the selection automatically.
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form `$prefix-$hash`
 * @param build a lambda expression for setting up the component itself. Details in [SelectFieldComponent]
 */
fun <T> RenderContext.selectField(
    styling: BasicParams.() -> Unit = {},
    items: List<T>,
    value: Store<T>? = null,
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "selectField",
    build: SelectFieldComponent<T>.() -> Unit,
) = SelectFieldComponent(items, value).apply(build).render(this, styling, baseClass, id, prefix)
