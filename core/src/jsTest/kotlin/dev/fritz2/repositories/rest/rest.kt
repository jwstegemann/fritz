package dev.fritz2.repositories.rest

import dev.fritz2.binding.RootStore
import dev.fritz2.binding.invoke
import dev.fritz2.dom.html.render
import dev.fritz2.identification.uniqueId
import dev.fritz2.lenses.IdProvider
import dev.fritz2.lenses.buildLens
import dev.fritz2.repositories.ResourceNotFoundException
import dev.fritz2.resource.Resource

import dev.fritz2.test.*
import kotlinx.browser.document
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.*

class RestTests {
    @Serializable
    data class RestPerson(val name: String, val age: Int, val _id: String = "") {
        override fun toString(): String {
            return name
        }
    }

    private val nameLens = buildLens("name", RestPerson::name) { p, v -> p.copy(name = v) }
    private val ageLens = buildLens("age", RestPerson::age) { p, v -> p.copy(age = v) }
    private val idLens = buildLens("_id", RestPerson::_id) { p, v -> p.copy(_id = v) }

    object PersonResource : Resource<RestPerson, String> {
        override val idProvider: IdProvider<RestPerson, String> = RestPerson::_id

        override fun serialize(item: RestPerson): String = Json.encodeToString(RestPerson.serializer(), item)
        override fun deserialize(source: String): RestPerson = Json.decodeFromString(RestPerson.serializer(), source)
        override fun serializeList(items: List<RestPerson>): String =
            Json.encodeToString(ListSerializer(RestPerson.serializer()), items)
        override fun deserializeList(source: String): List<RestPerson> =
            Json.decodeFromString(ListSerializer(RestPerson.serializer()), source)
    }

    @Test
    fun testEntityService() = runTest {
        initDocument()

        val defaultPerson = RestPerson("", 0)
        val startPerson = RestPerson("Heinz", 18)
        val changedAge = 99

        val remote = testHttpServer(rest)

        val entityStore = object : RootStore<RestPerson>(defaultPerson) {
            override fun errorHandler(exception: Throwable, oldValue: RestPerson): RestPerson {
                fail(exception.message)
            }

            val rest = restEntity(PersonResource, remote, "")

            val load = handle { _, id: String -> rest.load(id) }
            val saveOrUpdate = handle { entity -> rest.addOrUpdate(entity) }
            val delete = handle { entity -> rest.delete(entity); defaultPerson }
        }

        assertFailsWith(ResourceNotFoundException::class) {
            entityStore.rest.load("unknown")
        }

        val idId = "id-${uniqueId()}"
        val idSubStore = entityStore.sub(idLens)
        val nameId = "name-${uniqueId()}"
        val nameSubStore = entityStore.sub(nameLens)
        val ageId = "age-${uniqueId()}"
        val ageSubStore = entityStore.sub(ageLens)

        render {
            div {
                div(id = idId) { idSubStore.data.asText() }
                div(id = nameId) { nameSubStore.data.asText() }
                div(id = ageId) { ageSubStore.data.asText() }
            }
        }

        entityStore.update(startPerson)
        delay(100)

        val nameAfterStart = document.getElementById(nameId)?.textContent
        assertEquals(startPerson.name, nameAfterStart, "no name after start")

        entityStore.saveOrUpdate()
        delay(100)

        val idAfterSave = document.getElementById(idId)?.textContent
        assertTrue(idAfterSave?.length ?: 0 > 10, "no id after save")

        ageSubStore.update(data = changedAge)
        entityStore.saveOrUpdate()
        delay(100)

        val ageAfterUpdate = document.getElementById(ageId)?.textContent
        assertEquals(changedAge.toString(), ageAfterUpdate, "wrong age after update")

        ageSubStore.update(data = 0)
        entityStore.load(idAfterSave.orEmpty())
        delay(100)

        val ageAfterLoad = document.getElementById(ageId)?.textContent
        assertEquals(changedAge.toString(), ageAfterLoad, "wrong age after load")

        entityStore.delete()
        delay(100)

        val idAfterDelete = document.getElementById(idId)?.textContent
        assertEquals(startPerson._id, idAfterDelete, "wrong id after delete")
    }


    @Test
    fun testQueryService() = runTest {
        initDocument()

        val testList = listOf(
            RestPerson("A", 0),
            RestPerson("B", 1),
            RestPerson("C", 0)
        )

        val remote = testHttpServer(rest)

        val queryStore = object : RootStore<List<RestPerson>>(emptyList()) {
            override fun errorHandler(exception: Throwable, oldValue: List<RestPerson>): List<RestPerson> {
                fail(exception.message)
            }

            private val rest = restQuery<RestPerson, String, Unit>(PersonResource, remote, "")

            val addOrUpdate = handle<RestPerson> { entities, person -> rest.addOrUpdate(entities, person) }
            val query = handle<Unit> { _, query -> rest.query(query) }
            val delete = handle<String> { entities, id -> rest.delete(entities, id) }
        }

        val listId = "list-${uniqueId()}"
        val firstPersonId = "first-${uniqueId()}"

        render {
            div {
                ul(id = listId) {
                    queryStore.renderEach(RestPerson::_id) { p ->
                        li { p.data.map { it.name }.asText() }
                    }
                }
                span(id = firstPersonId) {
                    queryStore.data.map {
                        if (it.isEmpty()) ""
                        else it.first()._id
                    }.asText()
                }
            }
        }

        testList.forEach {
            queryStore.addOrUpdate(it)
            delay(1)
        }

        delay(200)

        queryStore.query()
        delay(200)

        val listAfterQuery = document.getElementById(listId)?.textContent
        assertEquals(testList.joinToString("") { it.name }, listAfterQuery, "wrong list after query")

        val firstId = document.getElementById(firstPersonId)?.textContent
        assertTrue(firstId != null && firstId.length > 10)

        queryStore.delete(firstId)
        delay(100)

        val listAfterDelete = document.getElementById(listId)?.textContent
        assertEquals(testList.drop(1).joinToString("") { it.name }, listAfterDelete, "wrong list after delete")

        queryStore.update(emptyList())
        delay(1)
        queryStore.query()
        delay(200)

        val listAfterDeleteAndQuery = document.getElementById(listId)?.textContent
        assertEquals(testList.drop(1).joinToString("") { it.name }, listAfterDeleteAndQuery, "wrong list after query")
    }

    @Test
    fun testQueryServiceUpdate() = runTest {
        initDocument()

        val testList = listOf(
            RestPerson("A", 0),
            RestPerson("B", 1),
            RestPerson("C", 0),
            RestPerson("D", 0)
        )

        val remote = testHttpServer(rest)

        val queryStore = object : RootStore<List<RestPerson>>(emptyList()) {
            override fun errorHandler(exception: Throwable, oldValue: List<RestPerson>): List<RestPerson> {
                fail(exception.message)
            }

            private val rest = restQuery<RestPerson, String, Unit>(PersonResource, remote, "")

            val addOrUpdate = handle<RestPerson> { entities, entity ->
                rest.addOrUpdate(entities, entity)
            }
            val updateMany = handle { entities ->
                rest.updateMany(entities, entities.map { it.copy(name = "${it.name}2") })
            }
            val updateSingle = handle { entities ->
                rest.addOrUpdate(entities, entities[2].copy(name = "C3"))
            }
        }

        val listId = "list-${uniqueId()}"

        render {
            div {
                ul(id = listId) {
                    queryStore.renderEach(RestPerson::_id) { p ->
                        li {
                            p.data.map { it.name }.asText()
                        }
                    }
                }
            }
        }

        testList.forEach {
            queryStore.addOrUpdate(it)
            delay(1)
        }

        delay(300)
        val listAfterAdd = document.getElementById(listId)?.textContent
        assertEquals(testList.joinToString("") { it.name }, listAfterAdd, "wrong list after adding")

//        val updatedTestList = testList.map { it.copy(name = "${it.name}2") }
//        queryStore.updateMany()
//        delay(400)
//
//        val listAfterUpdateMany = document.getElementById(listId)?.textContent
//        assertEquals(updatedTestList.joinToString("") { it.name }, listAfterUpdateMany, "wrong list after update many")
//
//        queryStore.updateSingle()
//        delay(200)
//        val listAfterUpdate = document.getElementById(listId)?.textContent
//        assertEquals(updatedTestList.map { if (it.name == "C2") it.copy(name = "C3") else it }
//            .joinToString("") { it.name }, listAfterUpdate, "wrong list after update")
    }
}