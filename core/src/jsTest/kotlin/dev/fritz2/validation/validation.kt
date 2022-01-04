package dev.fritz2.validation

import dev.fritz2.binding.RootStore
import dev.fritz2.binding.SimpleHandler
import dev.fritz2.binding.storeOf
import dev.fritz2.dom.html.render
import dev.fritz2.identification.Id
import dev.fritz2.test.initDocument
import dev.fritz2.test.runTest
import kotlinx.browser.document
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.w3c.dom.HTMLDivElement
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationJSTests {

    @Test
    fun testValidation() = runTest {
        initDocument()

        val carName = "ok"
        val c1 = Car(" ", Color(120, 120, 120))
        val c2 = Car("car1", Color(-1, -1, -1))
        val c3 = Car("car2", Color(256, 256, 256))
        val c4 = Car(" ", Color(256, -1, 120))

        val store = object : RootStore<Car>(Car(carName, Color(120, 120, 120))), ValidatingStore<Car, Message, Unit> {
            override val validator: Validator<Car, Message, Unit> = carValidator

            override val update: SimpleHandler<Car> = handle { old, new ->
                if(validator.isValid(new, Unit)) new else old
            }
        }

        val idData = "data-${Id.next()}"
        val idMessages = "messages-${Id.next()}"
        val idFind = "find-${Id.next()}"
        val idFilter = "filter-${Id.next()}"

        render {
            div {
                div(id = idData) {
                    store.data.map { it.name }.renderText()
                }
                div(id = idMessages) {
                    store.validator.data.renderEach(Message::text, into = this) {
                        p {
                            +it.text
                        }
                    }
                }
                div(id = idFind) {
                    store.validator.find { it == colorValuesAreToLow }.mapNotNull { it }.render {
                        p {
                            +it.text
                        }
                    }
                }
                div(id = idFilter) {
                    store.validator.filter { it == colorValuesAreToLow }.render {
                        p {
                            +(it.firstOrNull()?.text ?: "")
                        }
                    }
                }
            }
        }

        delay(100)
        val divData = document.getElementById(idData) as HTMLDivElement
        val divMessages = document.getElementById(idMessages) as HTMLDivElement
        val findMessages = document.getElementById(idFind)?.firstElementChild as HTMLDivElement
        val filterMessages = document.getElementById(idFilter)?.firstElementChild as HTMLDivElement

        assertEquals(carName, divData.textContent, "initial car name is wrong")
        assertEquals(0, divMessages.childElementCount, "there are messages")

        store.update(c1)
        delay(100)

        assertEquals(carName, divData.innerText, "c1: car name has changed")
        assertEquals(1, divMessages.childElementCount, "c1: there is not 1 message")
        assertEquals(
            carNameIsBlank.text,
            divMessages.firstElementChild?.textContent,
            "c1: there is no expected message"
        )
        assertEquals(0, findMessages.childElementCount, "c1 find: there is not 0 message")

        store.update(c2)
        delay(100)

        assertEquals(carName, divData.innerText, "c2: car name has changed")
        assertEquals(1, divMessages.childElementCount, "c2: there is not 1 message")
        assertEquals(
            colorValuesAreToLow.text,
            divMessages.firstElementChild?.textContent,
            "c2: there is not expected message"
        )
        assertEquals(1, findMessages.childElementCount, "c2 find: there is not 1 message")
        assertEquals(
            colorValuesAreToLow.text,
            findMessages.firstElementChild?.textContent,
            "c2 find: there is not expected message"
        )
        assertEquals(1, filterMessages.childElementCount, "c2 filter: there is not 1 message")
        assertEquals(
            colorValuesAreToLow.text,
            filterMessages.firstElementChild?.textContent,
            "c2 filter: there is no expected message"
        )

        store.update(c3)
        delay(100)

        assertEquals(carName, divData.innerText, "c3: car name has changed")
        assertEquals(1, divMessages.childElementCount, "c3: there is not 1 message")
        assertEquals(
            colorValuesAreToHigh.text,
            divMessages.firstElementChild?.textContent,
            "c3: there is no expected message"
        )

        store.update(c4)
        delay(100)

        assertEquals(carName, divData.innerText, "c4: car name has changed")
        assertEquals(3, divMessages.childElementCount, "c4: there is not 3 message")
    }

    @Test
    fun testSimpleValidation() = runTest {
        initDocument()

val store = storeOf<String, Message, Unit>("foo") { inspector, _ ->
    if(inspector.data.isBlank()) add(Message("input is blank"))
}.apply { enableContinuousValidation(Unit) }

        val idMessages = "div-${Id.next()}"

        render {
            div(id = idMessages) {
                store.validator.data.renderEach(into = this) {
                    span { +it.text }
                }
            }
        }

        delay(200)
        val messageElem = document.getElementById(idMessages) as HTMLDivElement
        assertEquals(0, messageElem.childElementCount)

        store.update("123")
        delay(200)
        assertEquals(0, messageElem.childElementCount, "wrong number of messages after update")

        store.update(" ")
        delay(200)
        assertEquals(1, messageElem.childElementCount, "expected validation message")
    }

}