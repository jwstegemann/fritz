package dev.fritz2.headless.components


import dev.fritz2.binding.RootStore
import dev.fritz2.binding.storeOf
import dev.fritz2.dom.Tag
import dev.fritz2.dom.html.*
import dev.fritz2.headless.foundation.Aria
import dev.fritz2.headless.foundation.Direction
import dev.fritz2.headless.foundation.Orientation
import dev.fritz2.headless.foundation.TagFactory
import dev.fritz2.headless.foundation.DatabindingHook
import dev.fritz2.headless.foundation.hook
import dev.fritz2.identification.Id
import kotlinx.browser.document
import kotlinx.coroutines.flow.*
import org.w3c.dom.HTMLElement


class HeadlessTabGroup<C : Tag<HTMLElement>>(val renderContext: C, id: String?) :
    RenderContext by renderContext {

    class TabDatabindingHook<C : Tag<HTMLElement>> : DatabindingHook<C, Unit, Int>() {
        override fun C.render(payload: Unit) {
        }
    }

    private class DisabledTabStore(initial: List<Boolean>) : RootStore<List<Boolean>>(initial) {
        val addTab = handle { state -> state + false }

        fun disabledHandler(index: Int) = handle<Boolean> { state, disabled ->
            state.withIndex().map { if (it.index == index) disabled else it.value }
        }
    }

    private val disabledTabs = DisabledTabStore(emptyList())
    val value by lazy { TabDatabindingHook<C>() }

    /**
     * This is a "gate-keeper" of the external data flow for all internal usage!
     *
     * "External" data from `TabDatabindingHook` is *dirty*, which means there is no control, whether the index
     * is valid (enabled and in bounds!) or not. So we have to check and correct the index for internal usage.
     *
     * In order to return the corrected value to the outside, see the `render` method and the handling also by
     * `selectDefaultTab`.
     *
     * Not optimal solution, but without a buffering internal store I cannot imagine a solution with only one central
     * location for correcting the stream.
     */
    val selected by lazy {
        // set a databinding if user has not provided one
        if(!value.isSet) value(storeOf(0))

        value.data.combine(disabledTabs.data) { index, disabledStates ->
            selectDefaultTab(0, index, disabledStates)
        }
    }

    private val state by lazy { selected.combine(disabledTabs.data, ::Pair) }

    private infix fun <T> Flow<T>.handledBy(handler: (Int, T, List<Boolean>) -> Int): Unit? =
        value.handler?.invoke(
            state.flatMapLatest { (currentIndex, disabledTabs) ->
                this.map { nextIndex ->
                    if (disabledTabs.all { it }) -1 else handler(currentIndex, nextIndex, disabledTabs)
                }
            })

    private fun nextByClick(currentIndex: Int, nextIndex: Int, disabledTabs: List<Boolean>) =
        if (disabledTabs[nextIndex]) currentIndex
        else nextIndex

    private fun nextByKeys(currentIndex: Int, direction: Direction, disabledTabs: List<Boolean>) =
        generateSequence {
            disabledTabs.withIndex().let {
                if (direction == Direction.Next) it else it.reversed()
            }
        }.flatten()
            .drop(
                when (direction) {
                    Direction.Next -> currentIndex + 1
                    Direction.Previous -> disabledTabs.size - currentIndex
                }
            ).take(disabledTabs.size + 1)
            .firstOrNull { !it.value }?.index ?: -1

    private fun firstByKey(currentIndex: Int, payload: Unit, disabledTabs: List<Boolean>) =
        disabledTabs.indexOf(false)

    private fun lastByKey(currentIndex: Int, payload: Unit, disabledTabs: List<Boolean>) =
        disabledTabs.lastIndexOf(false)

    private fun selectDefaultTab(currentIndex: Int, desiredIndex: Int, disabledTabs: List<Boolean>) =
        disabledTabs.take(minOf(desiredIndex, disabledTabs.size - 1) + 1).lastIndexOf(false)

    val componentId: String by lazy { id ?: value.id ?: Id.next() }

    private fun tabId(index: Int) = "$componentId-tab-list-tab-$index"
    private fun panelId(index: Int) = "$componentId-tab-panels-panel-$index"

    var orientation = Orientation.Horizontal

    fun C.render() {
        attr("id", componentId)
        hook(value, Unit)
        // We need to emit all internal changes to the outside for realising two-way-data-binding!
        // This includes the automatic correction by `selectDefaultTab` of `selected` setup.
        selected handledBy ::selectDefaultTab
    }

    inner class TabList<CL : Tag<HTMLElement>>(
        val tag: CL
    ) : RenderContext by tag {

        private var nextIndex = 0

        private val backwardsKey by lazy {
            if (orientation == Orientation.Horizontal) Keys.ArrowLeft else Keys.ArrowDown
        }
        private val forwardKey by lazy {
            if (orientation == Orientation.Horizontal) Keys.ArrowRight else Keys.ArrowUp
        }

        fun CL.render() {
            attr("role", Aria.Role.tablist)
            attr(Aria.orientation, orientation.toString().lowercase())

            keydowns.events.mapNotNull { event ->
                when (shortcutOf(event)) {
                    backwardsKey -> Direction.Previous
                    forwardKey -> Direction.Next
                    else -> null
                }.also {
                    if (it != null) {
                        event.stopImmediatePropagation()
                        event.preventDefault()
                    }
                }
            } handledBy ::nextByKeys

            keydowns.events.filter { setOf(Keys.Home, Keys.PageUp).contains(shortcutOf(it)) }.map {
                it.stopImmediatePropagation()
                it.preventDefault()
            } handledBy ::firstByKey

            keydowns.events.filter { setOf(Keys.End, Keys.PageDown).contains(shortcutOf(it)) }.map {
                it.stopImmediatePropagation()
                it.preventDefault()
            } handledBy ::lastByKey
        }

        inner class Tab<CT : Tag<HTMLElement>>(
            val tag: CT,
            val index: Int
        ) : RenderContext by tag {

            val disabled by lazy { disabledTabs.data.map { it[index] } }

            val disable by lazy { disabledTabs.disabledHandler(index) }

            fun CT.render() {
                attr("tabindex", selected.map { if (it == index) "0" else "-1" })
                attr(Aria.selected, selected.map { it == index }.asString())
                attr(Aria.controls, selected.map { if (it == index) panelId(index) else null })
                clicks.map { index } handledBy ::nextByClick
                selected handledBy {
                    if (it == index && domNode != document.activeElement) {
                        domNode.focus()
                    }
                }
            }
        }

        fun <CT : Tag<HTMLElement>> RenderContext.tab(
            classes: String? = null,
            scope: (ScopeContext.() -> Unit) = {},
            tag: TagFactory<CT>,
            initialize: Tab<CT>.() -> Unit
        ) = tag(this, classes, tabId(nextIndex), scope) {
            disabledTabs.addTab()
            Tab(this, nextIndex++).run {
                initialize()
                render()
            }
        }

        fun RenderContext.tab(
            classes: String? = null,
            scope: (ScopeContext.() -> Unit) = {},
            initialize: Tab<Button>.() -> Unit
        ) = tab(classes, scope, RenderContext::button, initialize)
    }

    fun <CL : Tag<HTMLElement>> RenderContext.tabList(
        classes: String? = null,
        scope: (ScopeContext.() -> Unit) = {},
        tag: TagFactory<CL>,
        initialize: TabList<CL>.() -> Unit
    ): CL = tag(this, classes, "$componentId-tab-list", scope) {
        TabList(this).run {
            initialize()
            render()
        }
    }

    fun RenderContext.tabList(
        classes: String? = null,
        scope: (ScopeContext.() -> Unit) = {},
        initialize: TabList<Div>.() -> Unit
    ): Div = tabList(classes, scope, RenderContext::div, initialize)


    inner class TabPanels<CP : Tag<HTMLElement>>(
        val tag: CP
    ) : RenderContext by tag {

        private var panels = mutableListOf<RenderContext.() -> Tag<HTMLElement>>()

        private var nextIndex = 0

        fun CP.render() {
            selected.render { index ->
                if (index != -1) {
                    // the index is always in bounds, so no further check is needed! See `selected` for details.
                    panels[index]()
                }
            }
        }

        fun <CT : Tag<HTMLElement>> RenderContext.panel(
            classes: String? = null,
            scope: (ScopeContext.() -> Unit) = {},
            tag: TagFactory<CT>,
            content: CT.() -> Unit
        ) {
            val currentIndex = nextIndex
            panels.add {
                tag(this, classes, panelId(currentIndex), scope) {
                    content()
                    attr("tabindex", "0")
                    attr("role", Aria.Role.tabpanel)
                    attr(Aria.labelledby, tabId(currentIndex))
                }
            }
            nextIndex += 1
        }

        fun RenderContext.panel(
            classes: String? = null,
            scope: (ScopeContext.() -> Unit) = {},
            content: Div.() -> Unit
        ) = panel(classes, scope, RenderContext::div, content)
    }

    fun <CP : Tag<HTMLElement>> RenderContext.tabPanels(
        classes: String? = null,
        scope: (ScopeContext.() -> Unit) = {},
        tag: TagFactory<CP>,
        initialize: TabPanels<CP>.() -> Unit
    ): CP = tag(this, classes, "$componentId-tab-panels", scope) {
        TabPanels(this).run {
            initialize()
            render()
        }
    }

    fun RenderContext.tabPanels(
        classes: String? = null,
        scope: (ScopeContext.() -> Unit) = {},
        initialize: TabPanels<Div>.() -> Unit
    ): Div = tabPanels(classes, scope, RenderContext::div, initialize)
}

fun <C : Tag<HTMLElement>> RenderContext.headlessTabGroup(
    classes: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    tag: TagFactory<C>,
    initialize: HeadlessTabGroup<C>.() -> Unit
): C = tag(this, classes, id, scope) {
    HeadlessTabGroup(this, id).run {
        initialize()
        render()
    }
}

fun RenderContext.headlessTabGroup(
    classes: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    initialize: HeadlessTabGroup<Div>.() -> Unit
): Div = headlessTabGroup(classes, id, scope, RenderContext::div, initialize)
