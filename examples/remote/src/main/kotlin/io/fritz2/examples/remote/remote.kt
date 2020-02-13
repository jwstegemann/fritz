package io.fritz2.examples.remote

import io.fritz2.binding.RootStore
import io.fritz2.dom.html.html
import io.fritz2.dom.mount
import io.fritz2.remote.body
import io.fritz2.remote.get
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.w3c.dom.url.URL
import org.w3c.fetch.Body
import kotlin.browser.window


data class QueryParams(val q: String)

@ExperimentalCoroutinesApi
@FlowPreview
fun main() {

    val store = object : RootStore<String>("start") {

        val callApi = apply { s : String ->
            URL("http://localhost:9000/get?foo=$s").get().body()
        } andThen update

    }

    val myComponent = html {
        div {
            input {
                value = !"hgfhgfhgf"
                store.callApi <= changes
            }
            div {
                +"value: "
                store.data.bind()
            }
        }
    }

    myComponent.mount("target")
}
