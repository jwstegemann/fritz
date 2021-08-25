package dev.fritz2.components.radios

import dev.fritz2.binding.Store
import dev.fritz2.components.foundations.*
import dev.fritz2.components.radio
import dev.fritz2.dom.EventContext
import dev.fritz2.dom.html.Div
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.dom.states
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.div
import dev.fritz2.styling.params.BasicParams
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.params.Style
import dev.fritz2.styling.theme.FormSizesStyles
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLElement

/**
 * This class combines the _configuration_ and the core styling of a radio group.
 * The rendering itself is also done within the companion object.
 *
 * In order to render a checkbox group use the [radioGroup] factory function!
 *
 * This class offers the following _configuration_ features:
 *  - the items as a [List<T>]
 *  - optionally set a predefined item; if nothing is set or "null", nothing gets selected at first
 *  - the label(mapping) static or dynamic via a [Flow<String>] or customized content see the examples below
 *  - some predefined styling variants (size)
 *  - the style of the items (radio)
 *  - the style selected state
 *  - the style of the label
 *  - choose the orientation of checkbox elements (vertical or horizontal)
 *
 *  This can be done within a functional expression that is the last parameter of the factory function, called
 *  `build`. It offers an initialized instance of this [RadioGroupComponent] class as receiver, so every mutating
 *  method can be called for configuring the desired state for rendering the checkbox.
 *
 * Example usage
 * ```
 * // simple use case showing the core functionality
 * val options = listOf("A", "B", "C")
 * val myStore = storeOf("B") // or ``null`` to select nothing
 * radioGroup(items = options, value = myStore) {
 * }
 *
 * // one can handle the events and preselected item also manually if needed:
 * val options = listOf("A", "B", "C")
 * radioGroup(items = options) {
 *      selectedItem("A") // or "null" (default) if nothing should be selected at all
 *      events {
 *          selected handledBy someStoreOfString
 *      }
 * }
 *
 * // use case showing some styling options and a store of List<Pair<Int,String>>
 * val myPairs = listOf((1 to "ffffff"), (2 to "rrrrrr" ), (3 to "iiiiii"), (4 to "tttttt"), ( 5 to "zzzzzz"), (6 to "222222"))
 * val myStore = storeOf(<List<Pair<Int,String>>)
 * radioGroup(items = myPairs, value = myStore) {
 *     label { it.second }
 *     size { large }
 *     selectedStyle {
 *          background { color {"green"} }
 *     }
 * }
 *
 * // use custom layouts for the checkbox labels by specifying a label-renderer:
 * val options = listOf("A", "B", "C")
 * radioGroup(items = options) {
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
open class RadioGroupComponent<T>(protected val items: List<T>, protected val value: Store<T>? = null) :
    Component<Div>,
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

    val label = ComponentProperty<(item: T) -> String> { it.toString() }
    val labelRendering = ComponentProperty<Div.(item: T) -> Unit> { +this@RadioGroupComponent.label.value(it) }
    val size = ComponentProperty<FormSizesStyles.() -> Style<BasicParams>> { Theme().radio.sizes.normal }

    val itemStyle = ComponentProperty(Theme().radio.default)
    val labelStyle = ComponentProperty(Theme().radio.label)
    val selectedStyle = ComponentProperty(Theme().radio.selected)

    val selectedItem = NullableDynamicComponentProperty<T>(emptyFlow())

    class EventsContext<T>(private val element: RenderContext, val selected: Flow<T>) :
        EventContext<HTMLElement> by element

    val events = ComponentProperty<EventsContext<T>.() -> Unit> {}

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ): Div {
        val internalStore = SingleSelectionStore()
        val groupName = "radioGroup-${randomId()}"

        return with(context) {
            div({
                verticalAlign { top }
                css("-webkit-box-align: center;")
                alignItems { center }
                layoutOf(this@RadioGroupComponent.orientation.value(OrientationContext))()
            }, styling, baseClass, id, prefix) {
                (this@RadioGroupComponent.value?.data ?: this@RadioGroupComponent.selectedItem.values)
                    .map { selectedItem ->
                        this@RadioGroupComponent.items.indexOf(selectedItem).let { if (it == -1) null else it }
                    } handledBy internalStore.update

                this@RadioGroupComponent.items.withIndex().forEach { (index, item) ->
                    val checkedFlow = internalStore.data.map { it == index }.distinctUntilChanged()
                    radio(styling = this@RadioGroupComponent.itemStyle.value) {
                        this.size { this@RadioGroupComponent.size.value.invoke(Theme().radio.sizes) }
                        groupName(groupName)
                        labelStyle(this@RadioGroupComponent.labelStyle.value)
                        selectedStyle(this@RadioGroupComponent.selectedStyle.value)
                        label { this@RadioGroupComponent.labelRendering.value(this, item) }
                        selected(checkedFlow)
                        disabled(this@RadioGroupComponent.disabled.values)
                        severity(this@RadioGroupComponent.severity.values)
                        events {
                            changes.states().map { index } handledBy internalStore.toggle
                        }
                    }
                }
                EventsContext(this, internalStore.toggle.map { this@RadioGroupComponent.items[it] }).apply {
                    this@RadioGroupComponent.events.value(this)
                    this@RadioGroupComponent.value?.let { selected handledBy it.update }
                }
            }
        }
    }
}