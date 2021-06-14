package dev.fritz2.components

import dev.fritz2.binding.Store
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
import dev.fritz2.styling.theme.FormSizes
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
 *  - the label(mapping) of a switch (static, dynamic via a [Flow<String>] or customized content of a Div.RenderContext) the the example below
 *  - some predefined styling variants (size)
 *  - the style of the items (checkbox)
 *  - the style checked state
 *  - the style of the label
 *  - the checked icon ( use our icon library of our theme )
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
 * val myStore = storeOf(listOf("B"))
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
 * // use case showing some styling options and a store of List<Pair<Int,String>>
 * val myPairs = listOf((1 to "ffffff"), (2 to "rrrrrr" ), (3 to "iiiiii"), (4 to "tttttt"), ( 5 to "zzzzzz"), (6 to "222222"))
 * val myStore = storeOf(<List<Pair<Int,String>>)
 * checkboxGroup(items = myPairs, values = myStore) {
 *      label { it.second }
 *      size { large }
 *      checkedStyle {
 *           background { color {"green"} }
 *      }
 * }
 * ```
 */
open class CheckboxGroupComponent<T>(
    protected val items: List<T>,
    protected val values: Store<List<T>>?
) : Component<Unit>,
    InputFormProperties by InputFormMixin(),
    SeverityProperties by SeverityMixin(),
    OrientationProperty by OrientationMixin(Orientation.VERTICAL) {

    companion object {
        // TODO: Remove ``direction`` part and therefore ``if`` branch
        fun layoutOf(orientation: Orientation, direction: Direction): Style<BasicParams> = {
            display {
                if (direction == Direction.ROW) {
                    inlineFlex
                } else when (orientation) {
                    Orientation.HORIZONTAL -> inlineFlex
                    Orientation.VERTICAL -> inlineGrid
                }
            }
        }
    }

    val icon = ComponentProperty<Icons.() -> IconDefinition> { Theme().icons.check }
    val label = ComponentProperty<(item: T) -> String> { it.toString() }
    val labelRenderer = ComponentProperty<(RenderContext.(item: T) -> Unit)?>(value = null)
    val size = ComponentProperty<FormSizes.() -> Style<BasicParams>> { Theme().checkbox.sizes.normal }

    enum class Direction {
        COLUMN, ROW
    }

    object DirectionContext {
        @Deprecated("Use orientation { vertical } instead", ReplaceWith("vertical"))
        val column: Direction = Direction.COLUMN

        @Deprecated("Use orientation { horizontal } instead", ReplaceWith("horizontal"))
        val row: Direction = Direction.ROW
    }

    @Deprecated("Use orientation instead", ReplaceWith("orientation"))
    val direction = ComponentProperty<DirectionContext.() -> Direction> { column }

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
    ) {
        val multiSelectionStore: MultiSelectionStore<T> = MultiSelectionStore()
        val grpId = id ?: uniqueId()

        context.apply {
            div({
                CheckboxGroupComponent.layoutOf(
                    this@CheckboxGroupComponent.orientation.value(OrientationContext),
                    this@CheckboxGroupComponent.direction.value(DirectionContext)
                )()
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
                        this@CheckboxGroupComponent.labelRenderer.value
                            ?.let { renderer ->
                                label { renderer(this, item) }
                            } ?: run {
                            label(this@CheckboxGroupComponent.label.value(item))
                        }
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

/**
 * This component generates a *group* of checkboxes.
 *
 * You can set different kind of properties like the label text or different styling aspects like the colors of the
 * background, the label or the checked state. Further more there are configuration functions for accessing the checked
 * state of each box or totally disable it.
 * For a detailed overview about the possible properties of the component object itself, have a look at
 * [CheckboxGroupComponent]
 *
 * Example usage
 * ```
 * // simple use case showing the core functionality
 * val options = listOf("A", "B", "C")
 * val myStore = storeOf(<List<String>>)
 * checkboxGroup(items = options, values = myStore) {
 * }
 *
 * // use case showing some styling options and a store of List<Pair<Int,String>>
 * val myPairs = listOf((1 to "ffffff"), (2 to "rrrrrr" ), (3 to "iiiiii"), (4 to "tttttt"), ( 5 to "zzzzzz"), (6 to "222222"))
 * val myStore = storeOf(<List<Pair<Int,String>>)
 * checkboxGroup(items = myPairs, values = myStore) {
 *      label { it.second }
 *      size { large }
 *      checkedStyle {
 *           background { color { "green" } }
 *      }
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
) {
    CheckboxGroupComponent(items, values).apply(build).render(this, styling, baseClass, id, prefix)
}

