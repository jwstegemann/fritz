package dev.fritz2.components.typeAhead

import dev.fritz2.binding.RootStore
import dev.fritz2.binding.Store
import dev.fritz2.components.*
import dev.fritz2.components.foundations.*
import dev.fritz2.dom.EventContext
import dev.fritz2.dom.Tag
import dev.fritz2.dom.html.Input
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.identification.uniqueId
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.*
import org.w3c.dom.HTMLInputElement


/**
 * This type alias defines the type of the proposal function of a [TypeAheadComponent]
 *
 * The typical pattern of writing such a function is to start with the implicit `it` and use common [Flow] operators
 * on it (pun intended):
 * ```
 * // just map the draft to a list:
 * val proposal: Proposal = {
 *     it.map { draft -> /* ... some API call or alike */ }
 * }
 *
 * // combine the draft with some other Flow (controlled by some other component)
 * val languages = storeOf(listOf("Kotlin", "Scala", "Java", "OCaml", "Haskell"))
 * val proposal: Proposal = {
 *     it.combine(languages.data) { draft, languages -> /* check if draft is contained or alike */ }
 * }
 *
 * ```
 */
typealias Proposal = suspend (Flow<String>) -> Flow<List<String>>

/**
 * This extension function offers a short, convenience method to use a static [List] as [Proposal].
 *
 * As default behaviour it checks, whether the current draft is contained in any item of the list ignoring case
 * sensitivity:
 * ```
 * val languages = listOf("Kotlin", "Scala", "Java", "OCaml", "Haskell")
 * val proposal = languages.asProposal() // called with draft = "ca" -> ["Scala", "OCaml"]
 *                                       //                                ^^       ^^
 * ```
 *
 * A predicate factory for the filter function can be passed as optional parameter:
 * ```
 * // just take only items from the list, that starts with draft:
 * languages.asProposal { draft -> { item -> item.startsWith(draft) } }
 *
 * @param predicate a factory for a predicate function applied to the internal filter; the current draft is the
 *                  outer parameter, the inner one is the current element of the list.
 * ```
 */
fun List<String>.asProposal(
    predicate: (String) -> (String) -> Boolean = { draft ->
        { item ->
            item.contains(
                draft,
                ignoreCase = true
            )
        }
    }
): Proposal = {
    it.map { draft -> this.filter(predicate(draft)) }
}

/**
 * Some internal abbreviation for the transformation of the current draft to an exposed result.
 * This is used for the two options offered by [TypeAheadComponent.strict]:
 * - `true` -> only return a value, if draft matches exactly one item of [TypeAheadComponent.items]
 * - `false` -> just return the draft as it is
 *
 * @see StateStore
 * @see TypeAheadComponent
 */
internal typealias Accepted = (List<String>, String) -> String

/**
 * The fitting state type for the internal state handling of the [TypeAheadComponent] used by [StateStore].
 */
internal data class State(
    val draft: String = "",
    val selected: String = "",
    val proposals: List<String> = emptyList()
)

/**
 * The store for managing the state of [TypeAheadComponent].
 *
 * It offers specific data [Flow]s for the current [draft], the [selected] item and the current valid [proposals]
 * excluding the current draft if it would be part of.
 *
 * It also offers a function to [preselect] some item initially.
 */
internal class StateStore(private val propose: Proposal, accepted: Accepted, limit: Int, draftThreshold: Int) :
    RootStore<State>(State()) {
    val draft = data.map { it.draft }
    val selected = data.map { it.selected }
    val proposals = data.map { it.proposals.filter { proposal -> proposal != it.draft } }.map { it.take(limit) }

    /**
     * This flag is used to enable the preselection possibility just for exactly one time at first rendering of
     * the [TypeAheadComponent]. This is important to break the circuit between external store and current draft!
     * Without this safety belt, the draft would be deleted completely immediately after dropping the last char
     * of the draft for example:
     * ```
     * // during some later point in time:
     * (1) draft: Germany -> selected: Germany -> external Store: Germany
     * (2) user presses `Backspace`
     * (3) draft: German -> selected: <empty> -> external Store: <empty> -{preselect}-> draft: <empty>
     * //         ^^^^^^                                                 ^^^^^^^^^^^^^^
     * //         we want to keep this!                                  this must be prohibited!
     * //
     * // with unsecured preselect <empty> would be passed as draft by the external store to the internal state store!
     * ```
     * Have a look at [TypeAheadComponent.render] to see how [preselect] is getting called!
     */
    private var preselectEnabled = true

    val preselect = handle<String> { state, draft ->
        if (preselectEnabled && draft.length >= draftThreshold) {
            preselectEnabled = false
            val proposals = propose(flowOf(draft)).first()
            State(draft, selected = accepted(proposals, draft), proposals = proposals)
        } else state
    }
}

/**
 * This component class manages the configuration of a [TypeAheadComponent] and does the rendering.
 *
 * The corresponding [typeAhead] function creates such a component. It offers the possibility to input some [String]
 * and get some list of proposals to choose from. Internally this is achieved by adding some datalist to the input field
 * (https://developer.mozilla.org/en-US/docs/Web/HTML/Element/datalist).
 *
 * It is intentional that this component is built upon a built-in mechanism of HTML:
 * Even if there is no way to style the choices list, it will work on any device out of the box, especially on mobiles,
 * where the native selection methods differ extremely from the desktop browsers!
 *
 * The proposals adapt dynamically to the current user input, so it is mandating to provide a function that gets the
 * current string (it is called "draft" throughout this component) and returns a [List] of [String]s as proposals.
 * As this function should be easy to integrate with other fritz2's methodologies, the parameter and the return value
 * are wrapped with a [Flow]. Have a look at [Proposal] for signature details.
 *
 * The typical (and minimal) usage might look like this:
 * ```
 * val proposals = listOf("Kotlin", "Scala", "Java", "OCaml", "Haskell").asProposals()
 * val choice = storeOf("")
 * typeAhead(value = choice, items = proposals) { }
 * ```
 * For production UIs consider a really long static list of possibilities. For the above example it would be much
 * better to use a [selectField] instead! Choose a [typeAhead] if the possibilities are too long for a select
 * component that displays all possibilities at once, or if the possibilities are gathered from an external source,
 * like a remote API.
 *
 * The configuration deals basically with three big groups:
 * - the options for forms basic behaviours ([enabled], [disabled] and [readonly])
 * - the options for visual appearance ([variant], [size] and [placeholder]) that are typical for the internal
 *   used [InputFieldComponent]
 * - the options for manipulate the behaviour of getting or accepting the proposals ([value], [strict], [limit],
 *   [draftThreshold] and [debounce])
 *
 * We focus on the latter ones in the examples:
 * ```
 * // handle the events manually if separation of source and destination flow is needed:
 * val proposals = listOf("Kotlin", "Scala", "Java", "OCaml", "Clojure", "F#").asProposals()
 *
 * val preselection = storeOf("Clojure")
 * val choice = storeOf("")
 * typeAhead(items = proposals) {
 *     value(preselection.data)
 *     events {
 *         value handledBy choice.update
 *     }
 * }
 *
 * // enable to accept also none proposal input values:
 * // this mode resembles a web search: the user gets proposals, but can freely type and commit new Strings if
 * // no suggestion fits:
 * typeAhead(value = choice, items = proposals) {
 *     strict(false) // `true` is default
 * }
 *
 * // limit the proposal list: Show at maximum 5 first entries of the proposals list
 * typeAhead(value = choice, items = proposals) {
 *     limit(5) // `20` is default
 * }
 *
 * // set a threshold of minimum string size of a draft, so below this no proposals are generated
 * // this is especially useful if it is clear, that the smallest proposal is longer in size and there are
 * // so many results, a longer start value before searching improves the selectivity.
 * typeAhead(value = choice, items = proposals) {
 *     draftThreshold(3) // start searching only after input of at least 3 chars; `1` is default
 * }
 *
 * // react to typing speed or "cost" of a remote call: Change the timeout the component will wait with a new
 * // execution of the `items` function to fetch new proposals after last key stroke:
 * typeAhead(value = choice, items = /* some costly remote call */) {
 *     debounce(1000) // wait one second in this case; `250` (ms) is default
 * }
 * ```
 *
 * @see [typeAhead]
 *
 * @param valueStore an instance of a [StateStore] for holding the state of the component
 * @param items a function to create the valid proposals based upon the current [State.draft] value.
 */
open class TypeAheadComponent(protected val valueStore: Store<String>?, protected val items: Proposal) :
    Component<Input>,
    InputFormProperties by InputFormMixin(),
    SeverityProperties by SeverityMixin(),
    InputFieldProperties by InputFieldMixin() {

    val value = DynamicComponentProperty(flowOf(""))
    val strict = ComponentProperty(true)
    val limit = ComponentProperty(20)
    val draftThreshold = ComponentProperty(1)
    val debounce = ComponentProperty(250L)

    class EventsContext<String>(private val element: Tag<HTMLInputElement>, val value: Flow<String>) :
        EventContext<HTMLInputElement> by element

    val events = ComponentProperty<EventsContext<String>.() -> Unit> {}

    private val acceptOnlyProposals: Accepted = { proposals, value ->
        if (proposals.contains(value)) value else ""
    }

    private val acceptDraft: Accepted = { _, value -> value }

    private val draft: MutableStateFlow<String> = MutableStateFlow("")

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ): Input {
        val accepted = if (strict.value) acceptOnlyProposals else acceptDraft
        val internalStore = StateStore(items, accepted, limit.value, draftThreshold.value)
        val proposalsId = "proposals-{${uniqueId()}}"

        return with(context) {
            (this@TypeAheadComponent.valueStore?.data
                ?: this@TypeAheadComponent.value.values) handledBy internalStore.preselect

            val input = inputField(
                {
                    this as BoxParams
                    styling(this)
                },
                baseClass = baseClass, id = id, prefix = prefix
            ) {
                disabled(this@TypeAheadComponent.disabled.values)
                readonly(this@TypeAheadComponent.readonly.values)
                variant { this@TypeAheadComponent.variant.value(Theme().input.variants) }
                size { this@TypeAheadComponent.size.value(Theme().input.sizes) }
                placeholder(this@TypeAheadComponent.placeholder.values)
                severity(this@TypeAheadComponent.severity.values)
                element {
                    attr("list", proposalsId)
                    autocomplete("off") // needed for FF
                }
                value(internalStore.draft)
                events {
                    // short-circuit if draft is already a fetched proposal
                    inputs.events.flatMapLatest { internalStore.proposals }
                        .mapNotNull { it.find { item -> item == domNode.value } }
                        .map { result ->
                            internalStore.current.copy(draft = result, selected = result)
                        } handledBy internalStore.update

                    // delay new proposal lookups
                    // TODO: Would be nice if this won't happen anymore, if above event was been successful!
                    //  Within the above handling some kind of "new" event would have to be triggered, if no
                    //  proposal has been found...
                    inputs.events.debounce(this@TypeAheadComponent.debounce.value)
                        .filter { domNode.value.length >= this@TypeAheadComponent.draftThreshold.value }
                        .onEach { this@TypeAheadComponent.draft.value = domNode.value }
                        .flatMapLatest { this@TypeAheadComponent.items(this@TypeAheadComponent.draft) }
                        .map { proposals ->
                            with(domNode.value) {
                                State(this, accepted(proposals, this), proposals)
                            }
                        } handledBy internalStore.update

                    // drop draft if there is no valid result
                    blurs.events.map {
                        with(internalStore.current) { copy(draft = this.selected.ifBlank { "" }) }
                    } handledBy internalStore.update
                }
            }
            datalist(id = proposalsId) {
                internalStore.proposals.renderEach {
                    option {
                        attr("value", it)
                    }
                }
            }

            EventsContext(input, internalStore.selected).apply {
                this@TypeAheadComponent.events.value(this)
                this@TypeAheadComponent.valueStore?.let { value handledBy it.update }
            }

            input
        }
    }
}
