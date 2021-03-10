package dev.fritz2.components

import dev.fritz2.dom.html.Div
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.FlexParams
import dev.fritz2.styling.params.GridParams
import dev.fritz2.styling.params.styled

/**
 * This component represents the simplest layout component possible: A simple ``div`` that acts as a container
 * for arbitrary content.
 *
 * In fact it is more or less a shorthand for styling a ``div`` manually and so to avoid the cumbersome syntax
 * of ``BasicComponent.styled``
 *
 * Example usage:
 * ```
 * box({
 *      /* styling expressions */
 *      border {
 *          color { dark }
 *          size { normal }
 *          style { solid }
 *      }
 * }) {
 *      p { +"Some content in the box" }
 * }
 * ```
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param init a lambda expression for setting up the content and events of the ``div`` element itself
 */
fun RenderContext.box(
    styling: FlexParams.() -> Unit = {},
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "box",
    init: Div.() -> Unit
): Div =
    ::div.styled(styling, baseClass, id, prefix) {
    }(init)


/**
 * This component represents a layout component with *flex* property.
 * That is the ``display`` property is set to ``flex``. Besides that is totally resembles the [box] component
 *
 * Example usage:
 * ```
 * flexBox({
 *      /* you can use flex styling properties for the _parent_ */
 *      justifyContent { center }
 *      alignItems { stretch }
 *      /* further styles */
 *      border {
 *          color { dark }
 *          size { normal }
 *          style { solid }
 *      }
 * }) {
 *      p { +"Some content in the box" }
 * }
 * ```
 * @see [dev.fritz2.styling.params.Flexbox] for a detailed overview about how to define flexBox layouts
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param init a lambda expression for setting up the content and events of the ``div`` element itself
 */
fun RenderContext.flexBox(
    styling: FlexParams.() -> Unit = {},
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "flex-box",
    init: Div.() -> Unit
): Div =
    ::div.styled(styling, baseClass, id, prefix) {
        css("display: flex;")
    }(init)


/**
 * This component represents a layout component with *grid* property.
 * That is the ``display`` property is set to ``grid``. Besides that is totally resembles the [box] component
 *
 * Example usage:
 * ```
 * gridBox({
 *      /* you can use grid styling properties for the _parent_ */
 *      columns {
 *          // create a grid with always seven cells per row, each one fraction of the container size
 *           repeat(7) { "1fr" }
 *      }
 *      gap { normal }
 *      /* further styles */
 *      border {
 *          color { dark }
 *          size { normal }
 *          style { solid }
 *      }
 * }) {
 *      p { +"Some content in the box" }
 * }
 * ```
 *
 * @see [dev.fritz2.styling.params.GridLayout] for a detailed overview about how to define grid layouts
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param init a lambda expression for setting up the content and events of the ``div`` element itself
 */
fun RenderContext.gridBox(
    styling: GridParams.() -> Unit = {},
    baseClass: StyleClass = StyleClass.None,
    id: String? = null,
    prefix: String = "grid-box",
    init: Div.() -> Unit
): Div =
    ::div.styled(styling, baseClass, id, prefix) {
        css("display: grid;")
    }(init)

