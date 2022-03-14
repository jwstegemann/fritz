package dev.fritz2.headlessdemo


import dev.fritz2.core.RenderContext
import dev.fritz2.core.placeholder
import tooltip


fun RenderContext.tooltipButton() {
    button(
        """text-white group bg-blue-700 px-3 py-2 rounded-md inline-flex items-center text-base 
                | font-medium hover:text-opacity-100 focus:outline-none focus-visible:ring-2 hover:bg-blue-800 
                | focus-visible:ring-blue-600 focus-visible:ring-opacity-75""".trimMargin()
    ) {
        +"Some Button"
    }.tooltip("text-sm text-gray-50 bg-gray-800 px-2 py-1 rounded") {
        arrow()
        +"Some more Information"
    }
}

fun RenderContext.tooltipInput() {
    input(
        """shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-24 h-10 sm:text-sm 
            | border-gray-300 rounded-md""".trimMargin()
    ) {
        placeholder("some input")
    }.tooltip("text-sm text-gray-50 bg-gray-800 px-2 py-1 rounded") {
        arrow()
        +"Some more Information"
    }
}

fun RenderContext.tooltipDemo() {
div("-m-4 w-screen h-[4000px] flex flex-col items-stretch") {
    div("p-4 flex justify-center") { tooltipButton() }
    div("flex flex-row flex-1 items-center") {
        div("p-4") { tooltipInput() }
        div("border border-gray-800 flex-1 p-4 flex justify-center") {
            div("h-48 w-full overflow-scroll") {
                div("h-screen w-full flex flex-col items-center justify-between") {
                    repeat(5) {
                        tooltipButton()
                    }
                }
            }
        }
        div("p-4") { tooltipButton() }
    }
    div("p-4 flex justify-center") { tooltipInput() }
}

}
