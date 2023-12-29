package dev.fritz2.headlessdemo.components

import dev.fritz2.core.RenderContext
import dev.fritz2.core.joinClasses
import dev.fritz2.core.placeholder
import dev.fritz2.core.storeOf
import dev.fritz2.headless.components.textArea
import kotlinx.coroutines.flow.map

fun RenderContext.textAreaDemo() {

    val description = storeOf("", id = "textArea")

    div("max-w-sm") {
        textArea {
            value(description)
            textareaLabel(
                """block mb-1.5 ml-1
                | text-sm font-medium text-primary-800""".trimMargin()
            ) {
                +"Describe the framework"
            }
            div("mt-1") {
                textareaTextfield(
                    """w-full py-2.5 px-2.5
                        | bg-white rounded
                        | font-sans text-sm 
                        | disabled:opacity-50""".trimMargin()
                ) {
                    className(value.hasError.map {
                        if (it) joinClasses(
                            """border border-error-600 
                                | text-error-800 placeholder:text-error-400
                                | hover:border-error-800  
                                | focus:outline-none focus:ring-4 focus:ring-error-600 focus:border-error-800""".trimMargin()
                        )
                        else joinClasses(
                            """border border-primary-600 
                                | text-primary-800 placeholder:text-slate-400
                                | hover:border-primary-800  
                                | focus:outline-none focus:ring-4 focus:ring-primary-600 focus:border-primary-800""".trimMargin()
                        )
                    })
                    placeholder("fritz2 is super cool")
                }
            }
            textareaDescription("block ml-1 text-xs text-primary-700") {
                +"Describe the domain, usage and important notes."
            }
        }

        div(
            """mt-6 p-2.5
            | bg-primary-100 rounded shadow-sm
            | ring-2 ring-primary-500 
            | text-sm text-primary-800
            | focus:outline-none focus:ring-4 focus:ring-primary-600 focus:border-primary-800""".trimMargin(),
            id = "result"
        ) {
            attr("tabindex", "0")
            span("font-medium") { +"Description: " }
            span { description.data.renderText() }
        }
    }
}
