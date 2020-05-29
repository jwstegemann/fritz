package io.fritz2.dom

import io.fritz2.binding.const
import io.fritz2.dom.html.render
import io.fritz2.test.initDocument
import io.fritz2.test.randomId
import io.fritz2.test.runTest
import io.fritz2.test.targetId
import kotlinx.coroutines.delay
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals


class TextTests {

    @Test
    fun testTextOnString() = runTest {
        initDocument()

        val testId = randomId()
        val text = "testText"

        render {
            div(id = testId) {
                text(text)
            }
        }.mount(targetId)

        delay(100)

        val element = document.getElementById(testId).unsafeCast<HTMLDivElement>()

        assertEquals(testId, element.id)
        assertEquals(text, element.textContent)
    }

    @Test
    fun testTextOnFlowOfString() = runTest {
        initDocument()

        val testId = randomId()
        val text = "testText"

        render {
            div(id = testId) {
                const(text).bind()
            }
        }.mount(targetId)

        delay(100)

        val element = document.getElementById(testId).unsafeCast<HTMLDivElement>()

        assertEquals(testId, element.id)
        assertEquals(text, element.textContent)
    }

    @Test
    fun testTextBind() = runTest {
        initDocument()

        val testId = randomId()
        val text = "testText"

        render {
            div(id = testId) {
                const(text).bind()
            }
        }.mount(targetId)

        delay(100)

        val element = document.getElementById(testId).unsafeCast<HTMLDivElement>()

        assertEquals(testId, element.id)
        assertEquals(text, element.textContent)
    }

}