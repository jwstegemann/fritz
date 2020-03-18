package io.fritz2.dom

import io.fritz2.dom.html.html
import io.fritz2.test.initDocument
import io.fritz2.test.randomId
import io.fritz2.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlin.test.Test

@ExperimentalCoroutinesApi
@FlowPreview
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

        html {
            div(testId) {
                attribute(name0, value0)
                attribute(name1, !value1)
                attributeData(name0, value0)
                attributeData(name1, !value1)
                attribute(name2, values2)
                attribute(name3, !values3)
            }
        }.mount("target")

        /*
        delay(500)

        val element = document.getElementById(testId).unsafeCast<HTMLDivElement>()

        assertEquals(testId, element.id)
        assertEquals("div", element.localName)

        assertEquals(value0, element.getAttribute(name0))
        assertEquals(value1, element.getAttribute(name1))

        assertEquals(value0, element.getAttribute("data-$name0"))
        assertEquals(value1, element.getAttribute("data-$name1"))

        assertEquals(values2.joinToString(separator = " "), element.getAttribute(name2))
        assertEquals(values3.joinToString(separator = " "), element.getAttribute(name3))

         */
    }
}