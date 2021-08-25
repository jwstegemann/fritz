package dev.fritz2.components.forms.control

import dev.fritz2.components.*
import dev.fritz2.components.forms.formGroupElementContainerMarker
import dev.fritz2.components.forms.formGroupElementLabelMarker
import dev.fritz2.components.forms.formGroupElementLegendMarker
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.styling.*
import dev.fritz2.styling.params.BoxParams

/**
 * This implementation of a [ControlRenderer] is meant for controls that offer a single control field, like
 * an [inputField] or a [selectField], which have only the one label, that the form control adds.
 */
class SingleControlRenderer(private val component: FormControlComponent) : ControlRenderer {
    override fun render(
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String,
        context: RenderContext,
        control: RenderContext.() -> RenderContext
    ) {
        context.stackUp(
            {
                alignItems { start }
                width { full }
                component.ownSize()
                styling(this as BoxParams)
            },
            baseClass = baseClass,
            id = id,
            prefix = prefix
        ) {
            spacing { tiny }
            items {
                val label = label({
                    component.labelStyle.value()
                }) {
                    className(formGroupElementLabelMarker)
                    component.label.values.asText()
                }.domNode
                stackUp({
                    alignItems { start }
                    width { full }
                }) {
                    spacing { none }
                    items {
                        control(this).apply {
                            label.addEventListener("click", {
                                this.domNode.focus()
                            })
                        }
                        component.renderHelperText(this)
                        component.renderValidationMessages(this)
                    }
                }
            }
        }.apply {
            className(formGroupElementContainerMarker)
        }
    }

}

/**
 * This implementation of a [ControlRenderer] is meant for controls that offer multiple control field, like
 * a [checkboxGroup] or a [radioGroup], which already have labels for each control and rather a legend element that
 * the form control adds.
 */
class ControlGroupRenderer(private val component: FormControlComponent) : ControlRenderer {
    override fun render(
        styling: BoxParams.() -> Unit,
        baseClass: StyleClass,
        id: String?,
        prefix: String,
        context: RenderContext,
        control: RenderContext.() -> RenderContext
    ) {
        context.div({
            width { full }
        }) {
            className(formGroupElementContainerMarker)
            fieldset({
                component.ownSize()
                styling()
            }, baseClass, id, prefix) {
                className(formGroupElementContainerMarker)
                legend({
                    component.labelStyle.value()
                }) {
                    className(formGroupElementLegendMarker)
                    component.label.values.asText()
                }
                stackUp {
                    spacing { none }
                    items {
                        control(this)
                        component.renderHelperText(this)
                        component.renderValidationMessages(this)
                    }
                }
            }
        }
    }
}