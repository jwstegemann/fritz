package io.fritz2.binding

import io.fritz2.dom.html.render
import io.fritz2.dom.mount
import io.fritz2.dom.values
import io.fritz2.test.initDocument
import io.fritz2.test.randomId
import io.fritz2.test.runTest
import io.fritz2.test.targetId
import kotlinx.coroutines.delay
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals


class EventTests {

    @Test
    fun eventHandlerDomChange() = runTest {
        initDocument()

        val resultId = randomId("result")
        val buttonId = randomId("button")

        val store = object : RootStore<String>("start") {
            var countHandlerCalls = 0

            val addADot = handle { model ->
                countHandlerCalls++
                "$model."
            }
        }


        render {
            section {
                input {
                    value = store.data
                    changes.values() handledBy store.update
                }
                div(id = resultId) {
                    text("value: ")
                    store.data.bind()
                }
                button(id = buttonId) {
                    text("add one more little dot")
                    clicks handledBy store.addADot
                }
            }
        }.mount(targetId)

        delay(100)

        val result = document.getElementById(resultId).unsafeCast<HTMLDivElement>()
        val button = document.getElementById(buttonId).unsafeCast<HTMLButtonElement>()

        assertEquals(0, store.countHandlerCalls, "wrong number of handler calls")
        assertEquals("value: start", result.textContent, "wrong dom content of result-node")

        button.click()
        delay(100)
        assertEquals(1, store.countHandlerCalls, "wrong number of handler calls")
        assertEquals("value: start.", result.textContent, "wrong dom content of result-node")

        button.click()
        delay(100)
        assertEquals(2, store.countHandlerCalls, "wrong number of handler calls")
        assertEquals("value: start..", result.textContent, "wrong dom content of result-node")
    }

}