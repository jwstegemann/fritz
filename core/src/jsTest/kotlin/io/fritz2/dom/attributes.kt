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


class AttributeTests {

    @Test
    fun testAttributes() = runTest {
        initDocument()

        val testRange = (0..4)
        val testId = randomId()

        val (name0, value0) = "test0" to "value0"
        val (name1, value1) = "test1" to "value1"
        val (name2, values2) = "test2" to testRange.map { "value$it" }
        val (name3, values3) = "test3" to testRange.map { "value$it" }

        render {
            div(id = testId) {
                attr(name0, value0)
                const(value1).bindAttr(name1)
                attr("data-$name0", value0)
                const(value1).bindAttr("data-$name1")
                attr(name2, values2)
                const(values3).bindAttr(name3)
            }
        }.mount(targetId)

        delay(200)

        val element = document.getElementById(testId).unsafeCast<HTMLDivElement>()

        assertEquals(testId, element.id)
        assertEquals("div", element.localName)

        assertEquals(value0, element.getAttribute(name0))
        assertEquals(value1, element.getAttribute(name1))

        assertEquals(value0, element.getAttribute("data-$name0"))
        assertEquals(value1, element.getAttribute("data-$name1"))

        assertEquals(values2.joinToString(separator = " "), element.getAttribute(name2))
        assertEquals(values3.joinToString(separator = " "), element.getAttribute(name3))

    }
}