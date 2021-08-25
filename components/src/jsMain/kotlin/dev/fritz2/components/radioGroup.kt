package dev.fritz2.components

import dev.fritz2.binding.Store
import dev.fritz2.components.radios.RadioGroupComponent
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.BasicParams


/**
 * This component generates a *group* of radio buttons.
 * So this supports the use case to choose one item from a list of options.
 * If there is not much vertical or horizontal space consider a [selectField] as alternative.
 * If there are lots of option, consider using a [typeAhead] instead.
 *
 * You can set different kind of properties like the labeltext or different styling aspects like the colors of the
 * background, the label or the selected item.
 *
 * Example usage
 * ```
 * val options = listOf("A", "B", "C")
 * radioGroup(items = options, value = selectedItemStore) {
 *     selectedItem(options[1]) // pre select "B", or "null" (default) to select nothing
 * }
 * ```
 *
 * @see RadioGroupComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param items a list of all available options
 * @param value for backing up the preselected item and reflecting the selection automatically.
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [RadioGroupComponent]
 * @return a flow of the _selected_ item
 */
fun <T> RenderContext.radioGroup(
    styling: BasicParams.() -> Unit = {},
    items: List<T>,
    value: Store<T>? = null,
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "radioGroupComponent",
    build: RadioGroupComponent<T>.() -> Unit = {}
) = RadioGroupComponent(items, value).apply(build).render(this, styling, baseClass, id, prefix)