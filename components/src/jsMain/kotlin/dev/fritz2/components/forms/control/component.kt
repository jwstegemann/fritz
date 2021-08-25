package dev.fritz2.components.forms.control

import dev.fritz2.binding.Store
import dev.fritz2.components.*
import dev.fritz2.components.checkboxes.CheckboxComponent
import dev.fritz2.components.checkboxes.CheckboxGroupComponent
import dev.fritz2.components.forms.control.FormControlComponent.ControlRegistration
import dev.fritz2.components.forms.formGroupElementContainerMarker
import dev.fritz2.components.foundations.*
import dev.fritz2.components.inputField.InputFieldComponent
import dev.fritz2.components.radios.RadioGroupComponent
import dev.fritz2.components.selectField.SelectFieldComponent
import dev.fritz2.components.slider.SliderComponent
import dev.fritz2.components.switch.SwitchComponent
import dev.fritz2.components.textarea.TextAreaComponent
import dev.fritz2.components.typeAhead.Proposal
import dev.fritz2.components.typeAhead.TypeAheadComponent
import dev.fritz2.components.validation.ComponentValidationMessage
import dev.fritz2.components.validation.Severity
import dev.fritz2.components.validation.validationMessages
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.StyleClass
import dev.fritz2.styling.div
import dev.fritz2.styling.p
import dev.fritz2.styling.params.BasicParams
import dev.fritz2.styling.params.BoxParams
import dev.fritz2.styling.params.Style
import dev.fritz2.styling.theme.FormSizesStyles
import dev.fritz2.styling.theme.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * This interface defines a type for the rendering of one [formControl].
 *
 * As there are different kind of controls, it is necessary to make the rendering a strategy!
 * This offers also an easy way to customize the rendering, if the default implementations do not fit.
 *
 * Remember to apply necessary CSS marker classes to the container elements [formGroupElementContainerMarker] of the
 * rendered structure.
 */
interface ControlRenderer {
    fun render(
        styling: BoxParams.() -> Unit = {},
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = "formControl",
        context: RenderContext,
        control: RenderContext.() -> RenderContext
    )
}

/**
 * This component class manages the configuration of a [formControl] and some render centric functionalities.
 * The former are important for clients of a [formControl], the latter for extending or changing the default behaviors.
 *
 * A [formControl] can be configured in different aspects:
 * - a label for a description of the control as a whole
 * - mark the control as _required_
 * - an optional helper text
 * - provide a validation message as a [Flow<ComponentValidationMessage>]]; the *severity* is reflected by the default
 *   theme!
 * - disable the control
 * - provide a store to the control itself, with optional additional validation ([dev.fritz2.components.validation.WithValidator])
 *   at best, in order to automatically apply model validation and get the results shown
 *
 * Customizing the control:
 *
 * To add a new control, extend this class and add a new control function that wraps the desired control component
 * factory function like [FormControlComponent.inputField], [FormControlComponent.selectField],
 * [FormControlComponent.checkbox], [FormControlComponent.checkboxGroup] and [FormControlComponent.radioGroup] do.
 *
 * In order to simply change the target of some of the default control wrapping function to a different control
 * component, extend this class and override the desired function. Be aware that you cannot provide default arguments
 * for an overridden function, so you must offer a new function with default arguments that just directs to
 * the overridden one.
 *
 * Be aware of the render strategy - pick whether your control should be rendered as a single control or a group.
 * - [SingleControlRenderer] for a control that consists of a single element
 * - [ControlGroupRenderer] for a control that consists of multiple parts (like checkBoxes etc.)
 *
 * If those do not fit, just implement the [ControlRenderer] interface and pair it with the string based key of the
 * related control wrapping function. Have a look at the [initRenderStrategies] and [finalizeRenderStrategies] field and
 * [ControlRegistration.assignee] field to learn how the mapping between control and rendering strategy is done.
 *
 */
open class FormControlComponent : Component<Unit>, FormProperties by FormMixin() {
    companion object {
        object ControlNames {
            const val inputField = "inputField"
            const val textArea = "textArea"
            const val switch = "switch"
            const val selectField = "selectField"
            const val radioGroup = "radioGroup"
            const val checkbox = "checkbox"
            const val checkboxGroup = "checkboxGroup"
            const val slider = "slider"
            const val typeAhead = "typeAhead"
        }
    }

    data class Control(
        val id: String?,
        val name: String,
        val rendering: RenderContext.() -> RenderContext
    )

    class ControlRegistration {

        private val overflows: MutableList<Control> = mutableListOf()
        var assignee: Control? = null

        fun set(
            controlId: String?,
            controlName: String,
            component: RenderContext.() -> RenderContext,
        ): Boolean {
            val control = Control(controlId, controlName, component)
            if (assignee == null) {
                assignee = control
                return true
            } else {
                overflows.add(control)
            }
            return false
        }

        fun assert() {
            if (overflows.isNotEmpty()) {
                console.error(
                    UnsupportedOperationException(
                        message = "Only one control within a formControl is allowed! Accepted control:"
                                + "${assignee?.name} (${assignee?.id})"
                                + " The following controls are not applied and overflow this form: "
                                + overflows.joinToString(", ") { "${it.name} (${it.id})" }
                                + " Please remove those!"
                    )
                )
            }
        }
    }

    /**
     * Use this method from your own control wrapping methods or if you override an existing one in order to
     * register the control for the form.
     *
     * @param controlName a unique String name / key for the control. Prefer the predefined [ControlNames] if possible
     * @param component pass in some control (could be arbitrary complex!) that should be wrapped by the form.
     * @param onSuccess some optional action that will be executed if the registration was successful (remember that
     *                  only *one* control, so normally the *first*, will be accepted!), for example the temporary
     *                  storage of validation messages from a passed in store.
     */
    protected open fun registerControl(
        controlId: String?,
        controlName: String,
        component: RenderContext.() -> RenderContext,
        onSuccess: FormControlComponent.() -> Unit = {}
    ) {
        if (controlRegistration.set(controlId, controlName, component)) {
            onSuccess(this)
        }
    }

    val controlRegistration = ControlRegistration()

    object FormSizeContext {
        enum class FormSizeSpecifier {
            SMALL, NORMAL, LARGE
        }

        val small = FormSizeSpecifier.SMALL
        val normal = FormSizeSpecifier.NORMAL
        val large = FormSizeSpecifier.LARGE

    }

    val ownSize: Style<BasicParams>
        get() = when (size.value(FormSizeContext)) {
            FormSizeContext.FormSizeSpecifier.SMALL -> Theme().formControl.sizes.small
            FormSizeContext.FormSizeSpecifier.NORMAL -> Theme().formControl.sizes.normal
            FormSizeContext.FormSizeSpecifier.LARGE -> Theme().formControl.sizes.large
        }

    val size = ComponentProperty<FormSizeContext.() -> FormSizeContext.FormSizeSpecifier> { normal }

    protected var sizeBuilder: (FormSizesStyles) -> Style<BasicParams> = { sizes ->
        when (this@FormControlComponent.size.value(FormSizeContext)) {
            FormSizeContext.FormSizeSpecifier.SMALL -> sizes.small
            FormSizeContext.FormSizeSpecifier.NORMAL -> sizes.normal
            FormSizeContext.FormSizeSpecifier.LARGE -> sizes.large
        }
    }

    val label = DynamicComponentProperty<String>(emptyFlow())
    val labelStyle = ComponentProperty(Theme().formControl.label)

    val helperText = ComponentProperty<String?>(null)
    val helperTextStyle = ComponentProperty(Theme().formControl.helperText)

    class ValidationResult(val messages: Flow<List<ComponentValidationMessage>>?) {
        companion object {
            /**
             * Simple factory method to encapsulate the logic that defining some ``validationMessage(s)`` property
             * beats the validation from the store!
             * Also defer the *execution* after the component object is fully initialized, so that the
             * *declaration order* of the control and a possible manually declared validation message property does
             * not matter!
             */
            fun <T> builderOf(formControl: FormControlComponent, store: Store<T>? = null): () -> ValidationResult = {
                ValidationResult(
                    (formControl.validationMessage.value()?.map { if (it == null) emptyList() else listOf(it) }
                        ?: formControl.validationMessages.value()) ?: store?.validationMessages())
            }
        }

        val hasSeverity: Flow<Severity?>
            get() = messages?.map { messages -> messages.map { it.severity }.maxOrNull() } ?: flowOf(null)
    }

    protected var validationMessagesBuilder: (() -> ValidationResult)? = null

    val validationMessage = ComponentProperty<() -> Flow<ComponentValidationMessage?>?> { null }
    val validationMessages = ComponentProperty<() -> Flow<List<ComponentValidationMessage>>?> { null }

    val validationMessageRendering =
        ComponentProperty<RenderContext.(ComponentValidationMessage) -> Unit> { message ->
            message.asAlert(this) {
                size(this@FormControlComponent.sizeBuilder)
                stacking { compact }
                variant { ghost }
            }
        }

    private fun initRenderStrategies(): Map<String, ControlRenderer> = buildMap {
        val singleRenderer = SingleControlRenderer(this@FormControlComponent)
        val groupRenderer = ControlGroupRenderer(this@FormControlComponent)
        sequenceOf(
            ControlNames.inputField,
            ControlNames.switch,
            ControlNames.textArea,
            ControlNames.selectField,
            ControlNames.checkbox,
            ControlNames.slider,
            ControlNames.typeAhead
        ).forEach { put(it, singleRenderer) }
        put(ControlNames.checkboxGroup, groupRenderer)
        put(ControlNames.radioGroup, groupRenderer)
        // call hook for custom setup
        finalizeRenderStrategies(this, singleRenderer, groupRenderer)
    }

    /**
     * Use this hook method to register your custom renderer for existing controls or to register a built-in renderer
     * for a custom control.
     *
     * In order to reuse the built-in renderers, they are passed as parameters. So there is no need to create a
     * new instance of them.
     *
     * Remember to prefer to use the predefined [ControlNames] as key for the first case.
     *
     * Example usage:
     * ```
     * class MyFormControlComponent : FormControlComponent {
     *
     *     // some new factory: `creditCardInput` with key of same name; used with `SingleControlRenderer`
     *
     *     // another new factory: `colorInput` with key of same name and new renderer
     *
     *     override fun finalizeRenderStrategies(
     *         strategies: MutableMap<String, ControlRenderer>,
     *         single: ControlRenderer,
     *         group: ControlRenderer
     *      ) {
     *          // override setup for a built-in factory:
     *          strategies.put(ControlNames.textArea, MySpecialRendererForTextAreas(this))
     *
     *          // register new factory
     *          strategies.put("creditCardInput", single)
     *
     *          // register new factory with new renderer
     *          strategies.put("colorInput", ColorInputRenderer(this))
     *      }
     * }
     * ```
     *
     * @param strategies a map filled with the base mapping for all built-in controls.
     * @param single the built-in renderer for single control field based controls
     * @param group the built-in renderer for group based controls like [checkboxGroup] or [radioGroup]
     */
    protected open fun finalizeRenderStrategies(
        strategies: MutableMap<String, ControlRenderer>,
        single: ControlRenderer,
        group: ControlRenderer
    ) {
    }

    override fun render(
        context: RenderContext,
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String
    ) {
        val renderStrategies = initRenderStrategies()
        controlRegistration.assignee?.rendering?.let {
            renderStrategies[controlRegistration.assignee?.name]?.render(
                {
                    styling()
                }, baseClass, id, prefix, context, it
            )
        }
        controlRegistration.assert()
    }

    open fun renderHelperText(renderContext: RenderContext) {
        renderContext.apply {
            this@FormControlComponent.helperText.value?.let {
                p({
                    this@FormControlComponent.helperTextStyle.value()
                }) { +it }
            }
        }
    }

    open fun renderValidationMessages(renderContext: RenderContext) {
        renderContext.apply {
            stackUp({
                width { "100%" }
            }) {
                spacing { none }
                items {
                    this@FormControlComponent.validationMessagesBuilder?.invoke()?.messages?.renderEach { message ->
                        div({
                            width { "100%" }
                        }) {
                            this@FormControlComponent.validationMessageRendering.value.invoke(this, message)
                        }
                    }
                }
            }
        }
    }

    open fun inputField(
        styling: BasicParams.() -> Unit = {},
        value: Store<String>? = null,
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = ControlNames.inputField,
        build: InputFieldComponent.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, value)
        registerControl(
            id,
            ControlNames.inputField,
            {
                inputField(styling, value, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }

    open fun typeAhead(
        styling: BasicParams.() -> Unit = {},
        value: Store<String>? = null,
        items: Proposal,
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = ControlNames.typeAhead,
        build: TypeAheadComponent.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, value)
        registerControl(
            id,
            ControlNames.typeAhead,
            {
                typeAhead(styling, value, items, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }

    open fun switch(
        styling: BasicParams.() -> Unit = {},
        value: Store<Boolean>? = null,
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = ControlNames.switch,
        build: SwitchComponent.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, value)
        registerControl(
            id,
            ControlNames.switch,
            {
                switch(styling, value, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }

    open fun textArea(
        styling: BasicParams.() -> Unit = {},
        value: Store<String>? = null,
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = ControlNames.textArea,
        build: TextAreaComponent.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, value)
        registerControl(
            id,
            ControlNames.textArea,
            {
                textArea(styling, value, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }

    open fun checkbox(
        styling: BasicParams.() -> Unit = {},
        baseClass: StyleClass = StyleClass.None,
        value: Store<Boolean>? = null,
        id: String? = null,
        prefix: String = ControlNames.checkbox,
        build: CheckboxComponent.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, value)
        registerControl(
            id,
            ControlNames.checkbox,
            {
                checkbox({
                    styling()
                }, value, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }

    open fun <T> checkboxGroup(
        styling: BasicParams.() -> Unit = {},
        items: List<T>,
        values: Store<List<T>>? = null,
        baseClass: StyleClass = StyleClass.None,
        id: String? = values?.id,
        prefix: String = ControlNames.checkboxGroup,
        build: CheckboxGroupComponent<T>.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, values)
        registerControl(
            id,
            ControlNames.checkboxGroup,
            {
                checkboxGroup(styling, items, values, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }

    open fun <T> radioGroup(
        styling: BasicParams.() -> Unit = {},
        items: List<T>,
        value: Store<T>? = null,
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = ControlNames.radioGroup,
        build: RadioGroupComponent<T>.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, value)
        registerControl(
            id,
            ControlNames.radioGroup,
            {
                radioGroup(styling, items, value, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }

    open fun <T> selectField(
        styling: BasicParams.() -> Unit = {},
        items: List<T>,
        value: Store<T>? = null,
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = ControlNames.selectField,
        build: SelectFieldComponent<T>.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, value)
        registerControl(
            id,
            ControlNames.selectField,
            {
                selectField(styling, items, value, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }

    open fun slider(
        styling: BasicParams.() -> Unit = {},
        value: Store<Int>? = null,
        baseClass: StyleClass = StyleClass.None,
        id: String? = null,
        prefix: String = ControlNames.slider,
        build: SliderComponent.() -> Unit = {}
    ) {
        val validationMessagesBuilder = ValidationResult.builderOf(this, value)
        registerControl(
            id,
            ControlNames.slider,
            {
                slider(styling, value, baseClass, id, prefix) {
                    size { this@FormControlComponent.sizeBuilder(this) }
                    severity(validationMessagesBuilder().hasSeverity)
                    build()
                }
            },
            { this.validationMessagesBuilder = validationMessagesBuilder }
        )
    }
}