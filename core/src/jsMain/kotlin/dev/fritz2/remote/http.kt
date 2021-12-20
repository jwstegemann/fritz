package dev.fritz2.remote

import dev.fritz2.binding.storeOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.khronos.webgl.ArrayBuffer
import org.w3c.fetch.*
import org.w3c.files.Blob
import org.w3c.xhr.FormData
import kotlinx.browser.window as browserWindow


/**
 * [Exception] type for handling http exceptions
 *
 * @property statusCode the http response status code
 * @property body the body of the error-response
 */
class FetchException(val statusCode: Short, val body: String, val response: Response) : Exception(
    "code=$statusCode, url=${response.url}, body=$body"
)

/**
 * creates a new [Request]
 *
 * @param baseUrl the common base of all urls that you want to call using the template
 */
fun http(baseUrl: String = "") = Request(baseUrl = baseUrl)

/**
 * Represents the common fields an attributes of a given set of http requests.
 *
 * Use it to define common headers, error-handling, base url, etc. for a specific API for example.
 * By calling one of the executing methods like [get] or [post] a specific request is built from the template and send to the server.
 *
 * @property baseUrl the common base of all urls that you want to call using this template
 */
open class Request(
    private val baseUrl: String = "",
    private val headers: Map<String, String> = emptyMap(),
    private val body: dynamic = undefined,
    private val referrer: String? = undefined,
    private val referrerPolicy: dynamic = undefined,
    private val mode: RequestMode? = undefined,
    private val credentials: RequestCredentials? = undefined,
    private val cache: RequestCache? = undefined,
    private val redirect: RequestRedirect? = undefined,
    private val integrity: String? = undefined,
    private val keepalive: Boolean? = undefined,
    private val reqWindow: Any? = undefined,
    private val authentication: Authentication<*>? = null
) {

    /**
     * builds a request, sends it to the server, awaits the response (async), creates a flow of it and attaches the defined errorHandler
     *
     * @param subUrl function do derive the url (so you can use baseUrl)
     * @param init an instance of [RequestInit] defining the attributes of the request
     */
    private suspend fun execute(subUrl: String, init: RequestInit): Response {
        val url = buildString {
            append(baseUrl.trimEnd('/'))
            if (subUrl.isNotEmpty()) {
                append("/${subUrl.trimStart('/')}")
            }
        }

        var response = browserWindow.fetch(url, init).await()
        if (authentication != null) {
            if (authentication.errorcodesEnforcingAuthentication.contains(response.status)) {
                //authentication.startAuthentication()
                if (!authentication.isAuthRunning()) {
                    authentication.startAuthentication()
                }
                // Wir warten aufs Login...
                authentication.getPrincipal()
                val redo = authentication.enrichRequest(this).buildInit(init.method!!)
                response = browserWindow.fetch(url, redo).await()
            }
        }
        if (response.ok) return response
        else throw FetchException(response.status, response.getBody(), response)
    }


    /**
     * builds a [RequestInit] with a body from the template using [method]
     *
     * @param method the http method to use (GET, POST, etc.)
     */
    private suspend fun buildInit(method: String): RequestInit {
        // enrich request if authentication is available
        val request = authentication?.enrichRequest(this) ?: this
        // Headers has no methods for reading key-value-pairs
        val reqHeader = Headers()
        for ((k, v) in request.headers) reqHeader.set(k, v)
        return RequestInit(
            method = method,
            body = request.body,
            headers = reqHeader,
            referrer = request.referrer,
            referrerPolicy = request.referrerPolicy,
            mode = request.mode,
            credentials = request.credentials,
            cache = request.cache,
            redirect = request.redirect,
            integrity = request.integrity,
            keepalive = request.keepalive,
            window = request.reqWindow
        )
    }

    // Methods

    /**
     * issues a get request returning a flow of it's response
     *
     * @param subUrl endpoint url which getting appended to the [baseUrl] with `/`
     */
    suspend fun get(subUrl: String = "") = execute(subUrl, buildInit("GET"))

    /**
     * issues a head request returning a flow of it's response
     *
     * @param subUrl endpoint url which getting appended to the [baseUrl] with `/`
     */
    suspend fun head(subUrl: String = "") = execute(subUrl, buildInit("HEAD"))

    /**
     * issues a connect request returning a flow of it's response
     *
     * @param subUrl endpoint url which getting appended to the [baseUrl] with `/`
     */
    suspend fun connect(subUrl: String = "") = execute(subUrl, buildInit("CONNECT"))

    /**
     * issues a options request returning a flow of it's response
     *
     * @param subUrl endpoint url which getting appended to the [baseUrl] with `/`
     */
    suspend fun options(subUrl: String = "") = execute(subUrl, buildInit("OPTIONS"))

    /**
     * issues a delete request returning a flow of it's response
     *
     * @param subUrl endpoint url which getting appended to the [baseUrl] with `/`
     */
    open suspend fun delete(subUrl: String = "") = execute(subUrl, buildInit("DELETE"))

    /**
     * issues a post request returning a flow of it's response
     *
     * @param subUrl endpoint url which getting appended to the [baseUrl] with `/`
     */
    suspend fun post(subUrl: String = "") = execute(subUrl, buildInit("POST"))

    /**
     * issues a put request returning a flow of it's response
     *
     * @param subUrl endpoint url which getting appended to the [baseUrl] with `/`
     */
    suspend fun put(subUrl: String = "") = execute(subUrl, buildInit("PUT"))


    /**
     * issues a patch request returning a flow of it's response
     *
     * @param subUrl endpoint url which getting appended to the [baseUrl] with `/`
     */
    suspend fun patch(subUrl: String = "") = execute(subUrl, buildInit("PATCH"))

    /**
     * appends the given [subUrl] to the [baseUrl]
     *
     * @param subUrl url which getting appended to the [baseUrl] with `/`
     */
    fun append(subUrl: String) = Request(
        "${baseUrl.trimEnd('/')}/${subUrl.trimStart('/')}",
        headers, body, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the body content to the request
     *
     * @param content body as [String]
     */
    fun body(content: String) = Request(
        baseUrl, headers, content, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the [ArrayBuffer] content to the request
     *
     * @param content body as [ArrayBuffer]
     */
    fun arrayBuffer(content: ArrayBuffer) = Request(
        baseUrl, headers, content, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the [FormData] content to the request
     *
     * @param content body as [FormData]
     */
    fun formData(content: FormData) = Request(
        baseUrl, headers, content, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the [Blob] content to the request
     *
     * @param content body as [Blob]
     */
    fun blob(content: Blob) = Request(
        baseUrl, headers, content, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * adds the given http header to the request
     *
     * @param name name of the http header to add
     * @param value value of the header field
     */
    fun header(name: String, value: String) = Request(
        baseUrl, headers.plus(name to value), body, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * adds the given [Content-Type](https://developer.mozilla.org/en/docs/Web/HTTP/Headers/Content-Type)
     * value to the http headers
     *
     * @param value cache-control value
     */
    fun contentType(value: String) = header("Content-Type", value)

    /**
     * adds the basic [Authorization](https://developer.mozilla.org/en/docs/Web/HTTP/Headers/Authorization)
     * header for the given username and password
     *
     * @param username name of the user
     * @param password password of the user
     */
    fun basicAuth(username: String, password: String) =
        header("Authorization", "Basic ${btoa("$username:$password")}")

    /**
     * adds the given [Cache-Control](https://developer.mozilla.org/en/docs/Web/HTTP/Headers/Cache-Control)
     * value to the http headers
     *
     * @param value cache-control value
     */
    fun cacheControl(value: String) = header("Cache-Control", value)

    /**
     * adds the given [Accept](https://developer.mozilla.org/en/docs/Web/HTTP/Headers/Accept)
     * value to the http headers, e.g "application/pdf"
     *
     * @param value media type to accept
     */
    fun accept(value: String) = header("Accept", value)

    /**
     * adds a header to accept JSON as response
     */
    fun acceptJson() = accept("application/json")

    /**
     * sets the referrer property of the [Request]
     *
     * @param value of the property
     */
    fun referrer(value: String) = Request(
        baseUrl, headers, body, value, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the referrerPolicy property of the [Request]
     *
     * @param value of the property
     */
    fun referrerPolicy(value: dynamic) = Request(
        baseUrl, headers, body, referrer, value, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the requestMode property of the [Request]
     *
     * @param value of the property
     */
    fun requestMode(value: RequestMode) = Request(
        baseUrl, headers, body, referrer, referrerPolicy, value,
        credentials, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the credentials property of the [Request]
     *
     * @param value of the property
     */
    fun credentials(value: RequestCredentials) = Request(
        baseUrl, headers, body, referrer, referrerPolicy, mode,
        value, cache, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the cache property of the [Request]
     *
     * @param value of the property
     */
    fun cache(value: RequestCache) = Request(
        baseUrl, headers, body, referrer, referrerPolicy, mode,
        credentials, value, redirect, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the redirect property of the [Request]
     *
     * @param value of the property
     */
    fun redirect(value: RequestRedirect) = Request(
        baseUrl, headers, body, referrer, referrerPolicy, mode,
        credentials, cache, value, integrity, keepalive, reqWindow, authentication
    )

    /**
     * sets the integrity property of the [Request]
     *
     * @param value of the property
     */
    fun integrity(value: String) = Request(
        baseUrl, headers, body, referrer, referrerPolicy, mode,
        credentials, cache, redirect, value, keepalive, reqWindow, authentication
    )

    /**
     * sets the keepalive property of the [Request]
     *
     * @param value of the property
     */
    fun keepalive(value: Boolean) = Request(
        baseUrl, headers, body, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, value, reqWindow, authentication
    )

    /**
     * sets the reqWindow property of the [Request]
     *
     * @param value of the property
     */
    fun reqWindow(value: Any) = Request(
        baseUrl, headers, body, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, value, authentication
    )


    /**
     * sets the general [Authentication] mechanism of the [Request]
     *
     * @param auth [Authentication] mechanism
     */
    fun <T>authentication(auth: Authentication<T>) = Request(
        baseUrl, headers, body, referrer, referrerPolicy, mode,
        credentials, cache, redirect, integrity, keepalive, reqWindow, auth
    )
}

// Response

/**
 * extracts the body as string from the given [Response]
 */
suspend fun Response.getBody() = this.text().await()

/**
 * returns the [Headers] from the given [Response]
 */
fun Response.getHeaders() = this.headers

/**
 * extracts the body as blob from the given [Response]
 */
suspend fun Response.getBlob() = this.blob().await()

/**
 * extracts the body as arrayBuffer from the given [Response]
 */
suspend fun Response.getArrayBuffer() = this.arrayBuffer().await()

/**
 * extracts the body as formData from the given [Response]
 */
suspend fun Response.getFormData() = this.formData().await()

/**
 * extracts the body as json from the given [Response]
 */
suspend fun Response.getJson() = json().await()

external fun btoa(decoded: String): String

/**
 * Represents the functions needed to authenticate a user
 * and in which cases the authentication should be made.
 */
abstract class Authentication<P> {

    private val principalStore = storeOf<P?>(null)

    private var state: CompletableDeferred<P>? = null

    /**
     * List of HTTP-Status-Codes forcing an authentication.
     * Defaults are 401 (unauthorized) and 403 (forbidden)
     */
    open val errorcodesEnforcingAuthentication: List<Short>
        get() = listOf(401, 403)

    /**
     * function enriching the request with authentication information depending on the
     * servers need. For example the server could expect sepcial header-information, that could be
     * set by this function.
     *
     * @param request the request-object that is enriched with the login-information.
     */
    abstract suspend fun enrichRequest(request: Request): Request

    /**
     * function doing the authentication
     */
    abstract fun authenticate()

    /**
     * performing a logout
     */
    fun logout() {
        state = null
        principalStore.update(null)
    }

    internal fun startAuthentication() {
        state = CompletableDeferred()
        authenticate()
    }

    fun isAuthenticated(): Boolean = state != null && !isAuthRunning()

    val authenticated: Flow<Boolean> = principalStore.data.map { it != null }

    val principal = principalStore.data

    internal fun isAuthRunning() = state.let {it?.isActive} ?: false

    suspend fun getPrincipal(): P? = state.let {it?.await()}

    fun login(p: P){

        if(state==null) {
            state = CompletableDeferred()
        }
        state?.complete(p)
        principalStore.update(p)
    }
}