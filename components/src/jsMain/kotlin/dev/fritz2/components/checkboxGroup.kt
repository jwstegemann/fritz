package dev.fritz2.components

import dev.fritz2.binding.Store
import dev.fritz2.components.checkboxes.CheckboxGroupComponent
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.BasicParams


/**
 * This component generates a *group* of checkboxes.
 * So this component should be used if a user should be able to choose multiple values from a list of options.
 *
 * The function requires a list of items `T` as mandatory parameter, representing the pool of choices.
 * Clients should also consider to pass a [Store<List<T>>] too, in order to let the component manage the selected
 * items and also use this for preselection.
 *
 * Example usage
 * ```
 * // simple use case showing the core functionality
 * val options = listOf("A", "B", "C")
 * val myStore = storeOf<List<String>>(emptyList())
 * checkboxGroup(items = options, values = myStore) {
 * }
 * ```
 *
 * @see CheckboxGroupComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param items a list of all available options
 * @param values a store of List<T>
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form `$prefix-$hash`
 * @param build a lambda expression for setting up the component itself. Details in [CheckboxGroupComponent]
 */
fun <T> RenderContext.checkboxGroup(
    styling: BasicParams.() -> Unit = {},
    items: List<T>,
    values: Store<List<T>>? = null,
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "checkboxGroupComponent",
    build: CheckboxGroupComponent<T>.() -> Unit = {}
) = CheckboxGroupComponent(items, values).apply(build).render(this, styling, baseClass, id, prefix)

