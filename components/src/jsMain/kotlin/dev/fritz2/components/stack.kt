package dev.fritz2.components

import dev.fritz2.dom.html.Div
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.StyleClass.Companion.plus
import dev.fritz2.styling.params.FlexParams
import dev.fritz2.styling.params.ScaledValueProperty
import dev.fritz2.styling.params.Style
import dev.fritz2.styling.staticStyle

/**
 * This base component class for stacking components offer some _configuration_ properties.
 *
 * It enables to configure the following features:
 *  - switching the default order of rendering (top -> bottom to bottom -> top for stackUps and left -> right to
 *    right -> left for lineUps)
 *  - defining the spacing between the items. For details have a look at [dev.fritz2.styling.theme.Theme.space]
 *  - adding arbitrary items like HTML elements or other components
 *
 *  You can combine both kind of stacking components to realize a simple layou for example:
 *   - ``lineUp`` for structure "menu" and "content" parts
 *   - ``stackUp`` for alignment of menu items
 *  ```
 *      <- lineUp                                  ->
 *   ^  +----------+--------------------------------+
 *   |  | Menu:    |  ** Item 2 **                  |
 *   S  | - Item1  |                                |
 *   t  | -*Item2* |  This is the content of Item 2 |
 *   a  | - Item3  |                                |
 *   c  | - Item4  |                                |
 *   k  |          |                                |
 *   U  |          |                                |
 *   p  |          |                                |
 *   |  |          |                                |
 *   v  +----------+--------------------------------+
 *  ```
 *  This could be expressed via composition in such a way:
 *  ```
 * lineUp {
 *     items {
 *         // Stack *two* items horizontally:
 *         // Menu on the left side
 *         stackUp {
 *             items {
 *                 // Heading and menu items vertical stacked
 *                 h1 {+"Menu:"}
 *                 ul {
 *                     li { +"Item1" }
 *                     li { +"Item2" }
 *                     li { +"Item3" }
 *                     li { +"Item4" }
 *                 }
 *             }
 *         }
 *         // Content on the right side
 *         box {
 *             h1 { +"Item 2" }
 *             p { +"This is the content of Item 2" }
 *         }
 *     }
 * }
 *  ```
 */
abstract class StackComponent {
    companion object {
        val staticCss = staticStyle(
            "stack",
            "align-items: center;"
        )
    }

    var reverse: Boolean = false

    fun reverse(value: () -> Boolean) {
        reverse = value()
    }

    var spacing: ScaledValueProperty = { normal }

    fun spacing(value: ScaledValueProperty) {
        spacing = value
    }

    var items: (Div.() -> Unit)? = null

    fun items(value: Div.() -> Unit) {
        items = value
    }

    abstract val stackStyles: Style<FlexParams>
}


/**
 * This component class just defines the core styling in order to render child items within a flexBox layout
 * vertically.
 *
 * @see StackComponent
 */
class StackUpComponent : StackComponent() {
    override val stackStyles: Style<FlexParams> = {
        if (this@StackUpComponent.reverse) {
            direction { columnReverse }
            children(" > :not(:first-child)") {
                margins { bottom(this@StackUpComponent.spacing) }
            }
        } else {
            direction { column }
            children(" > :not(:first-child)") {
                margins { top(this@StackUpComponent.spacing) }
            }
        }
    }
}


/**
 * This _layout_ component enables the *vertical* stacking of child components.
 *
 * The component itself does not do anything special besides offering a context for child components, that will be
 * rendered vertically from top to bottom. You can configure the _spacing_ between the items and invert the order the
 * child items are rendered.
 *
 * ```
 * stackUp {
 *      spacing { large } // define a margin between two items
 *      items { // open a sub context for adding arbitrary HTML elements or other components!
 *          p { +"Some text paragraph" }
 *          p { +"Another text section }
 *          // go on for arbitrary other HTML elements!
 *      }
 * }
 * ```
 *
 * Pay *attention* tp *always* set the child items within the ``items`` expression and not directly within the
 * StackComponent context. You won't get an error, but the child items are rendered falsy if applied wrong!
 *
 * @see StackComponent
 * @see StackUpComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [StackComponent]
 * @return a [Div] element in order to use this component as top level element of an UI part. This way it can be
 *         directly integrated into one of the _render_ functions!
 */
fun RenderContext.stackUp(
    styling: FlexParams.() -> Unit = {},
    baseClass: StyleClass? = null,
    id: String? = null,
    prefix: String = "stack-up",
    build: StackUpComponent.() -> Unit = {}
): Div {
    val component = StackUpComponent().apply(build)

    return flexBox({
        component.stackStyles()
        styling()
    }, baseClass = baseClass + StackComponent.staticCss, prefix = prefix, id = id) {
        component.items?.let { it() }
    }
}

/**
 * This component class just defines the core styling in order to render child items within a flexBox layout
 * horizontally.
 *
 * @see StackComponent
 */
class LineUpComponent : StackComponent() {
    override val stackStyles: Style<FlexParams> = {
        if (this@LineUpComponent.reverse) {
            direction { rowReverse }
            children(" > :not(:first-child)") {
                margins { right(this@LineUpComponent.spacing) }
            }
        } else {
            direction { row }
            children(" > :not(:first-child)") {
                margins { left(this@LineUpComponent.spacing) }
            }
        }
    }
}


/**
 * This _layout_ component enables the *horizontal* stacking of child components.
 *
 * The component itself does not do anything special besides offering a context for child components, that will be
 * rendered horizontally from left to right. You can configure the _spacing_ between the items and invert the order the
 * child items are rendered.
 *
 * ```
 * lineUp {
 *      spacing { small } // define a margin between two items
 *      items { // open a sub context for adding arbitrary HTML elements or other components!
 *          clickButton { text("Add") } handledBy creationHandler
 *          clickButton { text("Edit") } handledBy creationHandler
 *          clickButton { text("Delete") } handledBy deleteHandler
 *          // go on for arbitrary other HTML elements!
 *      }
 * }
 * ```
 *
 * Pay *attention* tp *always* set the child items within the ``items`` expression and not directly within the
 * StackComponent context. You won't get an error, but the child items are rendered falsy if applied wrong!
 *
 * @see StackComponent
 * @see LineUpComponent
 *
 * @param styling a lambda expression for declaring the styling as fritz2's styling DSL
 * @param baseClass optional CSS class that should be applied to the element
 * @param id the ID of the element
 * @param prefix the prefix for the generated CSS class resulting in the form ``$prefix-$hash``
 * @param build a lambda expression for setting up the component itself. Details in [StackComponent]
 * @return a [Div] element in order to use this component as top level element of an UI part. This way it can be
 *         directly integrated into one of the _render_ functions!
 */
fun RenderContext.lineUp(
    styling: FlexParams.() -> Unit = {},
    baseClass: StyleClass? = null,
    id: String? = null,
    prefix: String = "line-up",
    build: LineUpComponent.() -> Unit = {}
): Div {
    val component = LineUpComponent().apply(build)

    return flexBox({
        component.stackStyles()
        styling()
    }, baseClass = baseClass + StackComponent.staticCss, prefix = prefix, id = id) {
        component.items?.let { it() }
    }
}
