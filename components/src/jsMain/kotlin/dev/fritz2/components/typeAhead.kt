package dev.fritz2.components

import dev.fritz2.binding.Store
import dev.fritz2.components.typeAhead.Proposal
import dev.fritz2.components.typeAhead.TypeAheadComponent
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.BasicParams

/**
 * The [typeAhead] factory function creates a [TypeAheadComponent].
 *
 * It offers the possibility to input some [String] and get some list of proposals to choose from.
 * Internally this is achieved by adding some
 * [datalist](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/datalist)
 * to the input field.
 *
 * The typical (and minimal) usage might look like this:
 * ```
 * val proposals = listOf("Kotlin", "Scala", "Java", "OCaml", "Haskell").asProposals()
 * val choice = storeOf("")
 * typeAhead(value = choice, items = proposals) { }
 * ```
 *
 * @see TypeAheadComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param value optional [Store] that holds the data of the result
 * @param items
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [TypeAheadComponent]
 */
fun RenderContext.typeAhead(
    styling: BasicParams.() -> Unit = {},
    value: Store<String>? = null,
    items: Proposal,
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "typeAhead",
    build: TypeAheadComponent.() -> Unit = {}
) = TypeAheadComponent(value, items).apply(build).render(this, styling, baseClass, id, prefix)
