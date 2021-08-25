package dev.fritz2.components.slider


import dev.fritz2.binding.RootStore
import dev.fritz2.binding.Store
import dev.fritz2.binding.watch
import dev.fritz2.components.*
import dev.fritz2.components.foundations.*
import dev.fritz2.dom.EventContext
import dev.fritz2.dom.Window
import dev.fritz2.dom.html.Div
import dev.fritz2.dom.html.Key
import dev.fritz2.dom.html.Keys
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.div
import dev.fritz2.styling.name
import dev.fritz2.styling.params.BasicParams
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.params.Style
import dev.fritz2.styling.theme.*
import kotlinx.coroutines.flow.*
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent

/**
 * Foundation class of the slider data handling that bundles the value and its percentage.
 * @param percent the calculated percent of the value; primarily used for CSS positioning
 * @param value the actual value of the slider
 */
data class Progress(val percent: Int = 0, val value: Int = 0)

/**
 * Definition of the possible directions of a slider.
 * Note that this expresses the direction regarding the [Progress.value] of a slider and not the display direction
 * (as those also supports horizontal layout)
 */
internal enum class Direction {
    UP, DOWN
}

/**
 * This class acts as central UI model for the [StateStore] and [SliderComponent].
 * @param progress backups the pure values of a slider managed by [Progress]
 * @param movementTracking this flag signals the current state of movement, that is whether some sliding action takes
 *                         place currently. This is important for providing a dedicated event that also offers
 *                         value changes during the sliding actions and not only for the _final_ reached value.
 * @param interactive shows whether the slider is _enabled_ or in _disabled_ / _readonly_ mode. If ``false`` no
 *                    user action (mouse clicking, dragging or keys) will modify the value.
 *
 * @see StateStore
 * @see Progress
 */
data class State(
    val progress: Progress = Progress(),
    val movementTracking: Boolean = false,
    val interactive: Boolean = true
)

/**
 * This data class models the range of the value a slider can handle.
 *
 * @param lower the lower bound of the value; can be negative.
 * @param upper the upper bound of the value; can be negative. But must be greater than [lower].
 * @param step defines an optional step size. Must be positive and lower than the difference of [upper] and [lower]
 */
data class Range(val lower: Int = 0, val upper: Int = 100, val step: Int = 1) {
    init {
        require(upper > lower) { "upper bound must greater than lower bound!" }
        require(step > 0) { "step must be positive!" }
    }

    val distance = upper - lower
    fun absolute(value: Int) = (0 - lower) + value
    fun ranged(value: Int) = lower + value
}

/**
 * This store keeps track of the current state ([State]) of the slider.
 *
 * If offers special ``Flow<Int>`` for accessing the values of the slider directly without noise within the UI code.
 *
 * It also offers all needed handlers to manage the state due to user actions.
 *
 * @see State
 * @see Orientation
 * @see Range
 */
internal class StateStore(
    private val range: Range,
    private val orientation: Orientation,
    initialData: State
) :
    RootStore<State>(initialData) {

    lateinit var sliderElement: Div

    /**
     * Extracted value of the state as ``Flow<Int>`` that forwards values only after final position is reached,
     * to be exposed by events or synchronized with external store of Slider
     */
    val value = data.filter { !it.movementTracking }.map { it.progress.value }

    /**
     * Extracted value of the state as ``Flow<Int>`` to be exposed by events; changes also during movement actions!
     */
    val currentValue = data.map { it.progress.value }

    val updateInteractive = handle<Boolean> { state, interactive -> state.copy(interactive = interactive) }

    val updateMovementTracking = handle<Boolean> { value, tracking -> value.copy(movementTracking = tracking) }

    val updateByValue = handle<Int> { state, initialValue ->
        val newValue = range.absolute(initialValue).let { it - (it % range.step) }
        updateSliderByValue(state, newValue)
    }

    val updateByKeystroke = handle<Direction> { state, direction ->
        if (state.interactive) {
            val newValue = range.absolute(state.progress.value) + when (direction) {
                Direction.UP -> range.step
                Direction.DOWN -> -range.step
            }
            updateSliderByValue(state, newValue)
        } else state
    }

    private fun updateSliderByValue(state: State, newValue: Int): State {
        val rangedNewValue = range.ranged(newValue)
        return if (rangedNewValue >= range.lower && rangedNewValue <= range.upper) {
            val percent = (newValue * 100) / range.distance
            state.copy(progress = Progress(percent, rangedNewValue))
        } else state
    }

    val updateByClick = handle<MouseEvent> { value, event ->
        updateSliderByUi(value, event)
    }

    val updateByMovement = handle<MouseEvent> { value, event ->
        if (value.movementTracking) updateSliderByUi(value, event) else value
    }

    private fun updateSliderByUi(state: State, event: MouseEvent): State {
        return if (state.interactive) {
            val containerRect = sliderElement.domNode.getBoundingClientRect()
            val percentage = percentageFromRect(containerRect, event)
            if (percentage == state.progress.percent) {
                state
            } else {
                state.copy(
                    progress = nextProgress(
                        percentage,
                        if (percentage > state.progress.percent) Direction.UP else Direction.DOWN
                    )
                )
            }
        } else state
    }

    private fun percentageFromRect(containerRect: DOMRect, event: MouseEvent): Int = when (orientation) {
        Orientation.HORIZONTAL -> {
            val offset = containerRect.left.toInt()
            val positionAbsolute = maxOf(0, minOf(event.clientX, containerRect.right.toInt()) - offset)
            val percentage = positionAbsolute * 100 / containerRect.width.toInt()
            percentage
        }
        Orientation.VERTICAL -> {
            val offset = containerRect.top.toInt()
            val positionAbsolute = maxOf(0, minOf(event.clientY, containerRect.bottom.toInt()) - offset)
            val percentage = positionAbsolute * 100 / containerRect.height.toInt()
            100 - percentage
        }
    }

    private fun nextProgress(percent: Int, direction: Direction): Progress {
        val scaledValue = (range.distance * percent) / 100
        val remainder = (scaledValue % range.step)
        val steppedValue = when (direction) {
            Direction.UP -> scaledValue - remainder
            Direction.DOWN -> if (remainder > 0) scaledValue - remainder + range.step else scaledValue
        }
        val steppedPercentage = (steppedValue * 100) / range.distance
        return Progress(steppedPercentage, range.ranged(steppedValue))
    }

    init {
        updateByValue(current.progress.value)
    }
}

/**
 * This component creates a slider.
 *
 * A slider should be used for getting an [Int] based result, where the exact value is not important as it's tedious
 * to provide a specific value. On the other hand the slider gives a goods visual feedback to the user.
 *
 * It handles:
 * - the rendering of the slider (see ``render`` method)
 * - the configuration (with sub-classes) and its DSL including special ``currentValue`` event within ``events``
 *   context.
 * - the state management via [State]
 * - the styling via theme and ad hoc definitions offered within the slider DSL
 *
 * Example usages:
 * ```
 * // pass in a store for backup the value including predefined one
 * val valueStore = storeOf(42) // omitted in further snippets!
 * slider(value = valueStore) { } // no range -> lower=0, upper=100, step=1
 *
 * // defining the range
 * slider(value = valueStore) {
 *     range {
 *         lower(-20) // could be negative
 *         upper(40) // could also be negative, but must be greater than lower!
 *         step(2) // must be positive!
 *     }
 * }
 *
 * // observe sliding movement explicitly:
 * val currentValueStore = storeOf(0) // store the current value
 * slider(value = valueStore) {
 *     events {
 *         // handle changing values also during sliding movement
 *         currentValue handledBy currentValueStore.update
 *     }
 * }
 *
 * // render a vertical slider:
 * slider(value = valueStore) {
 *      orientation { vertical } // ``horizontal`` is default
 * }
 *
 * // change size
 * slider(value = valueStore) {
 *      size { small } // ``normal`` (default) and ``large``
 * }
 *
 * // style slider ad hoc:
 * slider(value = valueStore) {
 *     track {
 *     }
 *     trackFilled { percent ->
 *         // use percent value to change style according to the progress
 *     }
 *     thumb({ percent ->
 *         // use percent value to change style according to the progress
 *     }) { state ->
 *         // use the whole state for creating custom content
 *         // imagine a sticky info box with the current value appearing only
 *         // if ``movementTracking`` is ``true``
 *     }
 * }
 *
 * // disable slider:
 * slider(value = valueStore) {
 *      disabled(true) // could also be a ``Flow<Boolean>`` to make this dynamic.
 *                     // Be aware that the value is still changeable via the store!
 * }
 * ```
 *
 * @see StateStore
 * @see State
 *
 * @param store an optional store for setting and accessing the value of the slider
 */
open class SliderComponent(protected val store: Store<Int>? = null) :
    Component<Div>,
    FormProperties by FormMixin(),
    SeverityProperties by SeverityMixin(),
    OrientationProperty by OrientationMixin(Orientation.HORIZONTAL) {

    companion object {
        const val cssDataFocus = "data-focus"
        const val cssDataDisabled = "data-disabled"
    }

    class RangeContext {
        val lower = ComponentProperty(0)
        val upper = ComponentProperty(100)
        val step = ComponentProperty(1)

        fun toRange() = Range(lower.value, upper.value, step.value)
    }

    val range = ComponentProperty(Range())
    fun range(expr: RangeContext.() -> Unit) {
        range(RangeContext().apply(expr).toRange())
    }

    val value = NullableDynamicComponentProperty<Int>(emptyFlow())

    class EventsContext(
        private val element: RenderContext,
        val value: Flow<Int>,
        val currentValue: Flow<Int>
    ) : EventContext<HTMLElement> by element

    val events = ComponentProperty<EventsContext.() -> Unit> {}

    val icon = ComponentProperty<(Icons.() -> IconDefinition)?>(null)

    fun thumb(styling: BoxParams.(Int) -> Unit = {}, content: Div.(State) -> Unit) {
        thumbStyle(styling)
        thumbContent(content)
    }

    protected val thumbStyle = ComponentProperty<BoxParams.(Int) -> Unit> {}
    protected val thumbContent = ComponentProperty<(Div.(State) -> Unit)> {
        this@SliderComponent.icon.value?.let {
            icon({
                color { primary.main }
                size { auto }
            }) { def(it(Theme().icons)) }
        }
    }

    val track = ComponentProperty<Style<BoxParams>> {}
    val trackFilled = ComponentProperty<BoxParams.(Int) -> Unit> {}
    val size = ComponentProperty<FormSizesStyles.() -> Style<BasicParams>> { Theme().slider.sizes.normal }

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ): Div {
        val internalStore = StateStore(range.value, orientation.value(OrientationContext), State())

        return with(context) {
            (this@SliderComponent.store?.data ?: this@SliderComponent.value.values).map {
                it ?: 0
            } handledBy internalStore.updateByValue

            this@SliderComponent.disabled.values.map { !it } handledBy internalStore.updateInteractive

            div({
                when (this@SliderComponent.orientation.value(OrientationContext)) {
                    Orientation.HORIZONTAL -> width { full }
                    Orientation.VERTICAL -> height { full }
                }
                styling()
            }, baseClass, id, prefix) {
                className(this@SliderComponent.severityClassOf(Theme().slider.severity).name)

                internalStore.sliderElement = flexBox({
                    this@SliderComponent.size.value.invoke(Theme().slider.sizes)()
                    this@SliderComponent.coreStyles().main(this)
                }) {
                    this@SliderComponent.addDataAttributes(this, internalStore)
                    attr("tabindex", internalStore.data.map { if (it.interactive) 0 else -1 })
                    internalStore.data.onEach { state ->
                        if (state.movementTracking) domNode.apply {
                            focus()
                        }
                    }.watch()

                    clicks.events handledBy internalStore.updateByClick
                    Window.mousemoves.events handledBy internalStore.updateByMovement
                    Window.mouseups.events.map { false } handledBy internalStore.updateMovementTracking
                    keydowns.events.mapNotNull {
                        when (Key(it)) {
                            Keys.ArrowDown, Keys.ArrowLeft -> {
                                it.preventDefault()
                                Direction.DOWN
                            }
                            Keys.ArrowUp, Keys.ArrowRight -> {
                                it.preventDefault()
                                Direction.UP
                            }
                            else -> null
                        }
                    } handledBy internalStore.updateByKeystroke

                    internalStore.data.render { state ->
                        div({
                            this@SliderComponent.coreStyles().track(this)
                            this@SliderComponent.track.value(this)
                        }, prefix = "track") {
                            div({
                                this@SliderComponent.coreStyles().trackFilled(this, state.progress.percent)
                                this@SliderComponent.trackFilled.value(this, state.progress.percent)
                            }, prefix = "track-filled") {
                                this@SliderComponent.addDataAttributes(this, internalStore)
                                div({
                                    this@SliderComponent.coreStyles().thumb(this, state.progress.percent)
                                    this@SliderComponent.thumbStyle.value(this, state.progress.percent)
                                }, prefix = "thumb") {
                                    this@SliderComponent.addDataAttributes(this, internalStore)
                                    this@SliderComponent.thumbContent.value(this, state)
                                    mousedowns.events.combine(internalStore.data) { a, b -> a to b }
                                        .filter { it.second.interactive }
                                        .map { true } handledBy internalStore.updateMovementTracking
                                }
                            }
                        }
                    }
                }

                EventsContext(this, internalStore.value, internalStore.currentValue).apply {
                    this@SliderComponent.events.value(this)
                    this@SliderComponent.store?.let { value handledBy it.update }
                }

            }

            internalStore.sliderElement
        }
    }

    private fun coreStyles() = Theme().slider.core(orientation.value(OrientationContext).name)

    private fun addDataAttributes(node: Div, store: StateStore) {
        node.apply {
            attr(cssDataFocus, store.data.map { it.movementTracking })
            attr(cssDataDisabled, store.data.map { !it.interactive })
        }
    }
}
