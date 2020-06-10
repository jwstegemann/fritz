package dev.fritz2.dom.html

import dev.fritz2.dom.mount
import dev.fritz2.test.initDocument
import dev.fritz2.test.runTest
import dev.fritz2.test.targetId
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertFailsWith


class HtmlTests {

    @Test
    fun testMultipleRootElementsException() = runTest {
        initDocument()

        assertFailsWith(MultipleRootElementsException::class) {
            render {
                div {
                    text("div1")
                }
                div {
                    text("div2")
                }
            }.mount(targetId)
            delay(250)
        }
    }
}