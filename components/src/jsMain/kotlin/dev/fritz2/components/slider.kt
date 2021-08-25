package dev.fritz2.components

import dev.fritz2.binding.Store
import dev.fritz2.components.slider.SliderComponent
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.BasicParams

/**
 * This component generates a slider.
 *
 * @see SliderComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param value a store for holding the value of the slider
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [SliderComponent]
 */
fun RenderContext.slider(
    styling: BasicParams.() -> Unit = {},
    value: Store<Int>? = null,
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "slider",
    build: SliderComponent.() -> Unit = {}
) = SliderComponent(value).apply(build).render(this, styling, baseClass, id, prefix)

/**
 * This component generates a slider.
 *
 * @see SliderComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param value an initial value
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [SliderComponent]
 */
fun RenderContext.slider(
    styling: BasicParams.() -> Unit = {},
    value: Int,
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "slider",
    build: SliderComponent.() -> Unit = {}
) = SliderComponent().apply {
    build()
    value(value)
}.render(this, styling, baseClass, id, prefix)
