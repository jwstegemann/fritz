package dev.fritz2.components.checkboxes

import dev.fritz2.binding.Store
import dev.fritz2.components.*
import dev.fritz2.components.foundations.*
import dev.fritz2.dom.EventContext
import dev.fritz2.dom.html.Div
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.dom.states
import dev.fritz2.identification.uniqueId
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.div
import dev.fritz2.styling.params.BasicParams
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.params.Style
import dev.fritz2.styling.theme.FormSizesStyles
import dev.fritz2.styling.theme.IconDefinition
import dev.fritz2.styling.theme.Icons
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLElement

/**
 * This class combines the _configuration_ and the core styling of a checkbox group.
 * The rendering itself is also done within the companion object.
 *
 * In order to render a checkbox group use the [checkboxGroup] factory function!
 *
 * This class offers the following _configuration_ features:
 *  - the items as a [List<T>]
 *  - the preselected items via a [Flow<List<T>>]
 *  - the label(mapping) static or dynamic via a [Flow<String>] or customized content see the examples below
 *  - some predefined styling variants (size)
 *  - the style of the items (checkbox)
 *  - the style checked state
 *  - the style of the label
 *  - the checked icon ( use our icon library of our theme)
 *  - choose the orientation of checkbox elements (vertical or horizontal)
 *
 *  This can be done within a functional expression that is the last parameter of the factory function, called
 *  `build`. It offers an initialized instance of this [CheckboxGroupComponent] class as receiver, so every mutating
 *  method can be called for configuring the desired state for rendering the checkbox.
 *
 * Example usage
 * ```
 * // simple use case showing the core functionality
 * val options = listOf("A", "B", "C")
 * val myStore = storeOf<List<String>>(emptyList())
 * checkboxGroup(items = options, values = myStore) {
 * }
 *
 * // one can handle the events and preselected item also manually if needed:
 * val options = listOf("A", "B", "C")
 * checkboxGroup(items = options) {
 *      selectedItems(options.skip(1)) // for selecting "B" and "C" or an empty list (default)
 *                                     // if nothing should be selected at all
 *      events {
 *          selected handledBy someStoreOfString
 *      }
 * }
 *
 * // use case showing some styling options and a complex data type
 * val myPairs = listOf(
 *     (1 to "ffffff"),
 *     (2 to "rrrrrr"),
 *     (3 to "iiiiii"),
 *     (4 to "tttttt"),
 *     (5 to "zzzzzz"),
 *     (6 to "222222")
 * )
 * val myStore = storeOf<List<Pair<Int,String>>(emptyList())
 * checkboxGroup(items = myPairs, values = myStore) {
 *      label { it.second } // adjust label representation to specific model characteristics
 *      size { large }
 *      checkedStyle {
 *           background { color {"green"} }
 *      }
 * }
 *
 * // use custom layouts for the checkbox labels by specifying a label-renderer:
 * val options = listOf("A", "B", "C")
 * checkboxGroup(items = options) {
 *      labelRendering { item ->
 *          span({
 *              fontFamily { mono }
 *              background {
 *                  color { primary.highlight }
 *              }
 *          }) {
 *              +item
 *          }
*      }
 * }
 * ```
 */
open class CheckboxGroupComponent<T>(
    protected val items: List<T>,
    protected val values: Store<List<T>>?
) : Component<Div>,
    InputFormProperties by InputFormMixin(),
    SeverityProperties by SeverityMixin(),
    OrientationProperty by OrientationMixin(Orientation.VERTICAL) {

    companion object {
        fun layoutOf(orientation: Orientation): Style<BasicParams> = {
            display {
                when (orientation) {
                    Orientation.HORIZONTAL -> inlineFlex
                    Orientation.VERTICAL -> inlineGrid
                }
            }
        }
    }

    val icon = ComponentProperty<Icons.() -> IconDefinition> { Theme().icons.check }
    val label = ComponentProperty<(item: T) -> String> { it.toString() }
    val labelRendering = ComponentProperty<RenderContext.(item: T) -> Unit> {
        span {
            +this@CheckboxGroupComponent.label.value(it)
        }
    }
    val size = ComponentProperty<FormSizesStyles.() -> Style<BasicParams>> { Theme().checkbox.sizes.normal }

    val itemStyle = ComponentProperty(Theme().checkbox.default)
    var labelStyle = ComponentProperty(Theme().checkbox.label)
    val checkedStyle = ComponentProperty(Theme().checkbox.checked)

    val selectedItems = DynamicComponentProperty<List<T>>(flowOf(emptyList()))

    class EventsContext<T>(private val element: RenderContext, val selected: Flow<List<T>>) :
        EventContext<HTMLElement> by element

    val events = ComponentProperty<EventsContext<T>.() -> Unit> {}

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ): Div {
        val multiSelectionStore: MultiSelectionStore<T> = MultiSelectionStore()
        val grpId = id ?: uniqueId()

        return with(context) {
            div({
                layoutOf(this@CheckboxGroupComponent.orientation.value(OrientationContext))()
            }, styling, baseClass, id, prefix) {
                (this@CheckboxGroupComponent.values?.data
                    ?: this@CheckboxGroupComponent.selectedItems.values) handledBy multiSelectionStore.update

                this@CheckboxGroupComponent.items.forEach { item ->
                    val checkedFlow = multiSelectionStore.data.map { it.contains(item) }.distinctUntilChanged()
                    checkbox(
                        styling = this@CheckboxGroupComponent.itemStyle.value,
                        id = grpId + "-grp-item-" + uniqueId()
                    ) {
                        size { this@CheckboxGroupComponent.size.value.invoke(Theme().checkbox.sizes) }
                        icon { this@CheckboxGroupComponent.icon.value(Theme().icons) }
                        labelStyle(this@CheckboxGroupComponent.labelStyle.value)
                        checkedStyle(this@CheckboxGroupComponent.checkedStyle.value)
                        label { this@CheckboxGroupComponent.labelRendering.value(this, item) }
                        checked(checkedFlow)
                        disabled(this@CheckboxGroupComponent.disabled.values)
                        severity(this@CheckboxGroupComponent.severity.values)
                        events {
                            changes.states().map { item } handledBy multiSelectionStore.toggle
                        }
                    }
                }
                EventsContext(this, multiSelectionStore.toggle).apply {
                    this@CheckboxGroupComponent.events.value(this)
                    this@CheckboxGroupComponent.values?.let { selected handledBy it.update }
                }
            }
        }
    }
}