package dev.fritz2.validation

import dev.fritz2.binding.Store
import dev.fritz2.identification.Inspector
import dev.fritz2.identification.inspectorOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Describes the logic for validating a given data-model.
 * By implementing this you must describe, how a certain data-model should be validated.
 * This is done by returning a [List] of [ValidationMessage]s in the [validate] functions.
 *
 * It is recommended to put the concrete implementation of this [Validator] right next to your data classes
 * in the `commonMain` section of your Kotlin multiplatform project.
 * Then you can write the validation logic once and use them in the JS and JVM world.
 */
actual abstract class Validator<D, M : ValidationMessage, T> {

    private val state = MutableStateFlow<List<M>>(emptyList())

    /**
     * Gives a [Flow] of [ValidationMessage]s which gets updated every time when [isValid] gets called.
     * If no messages result from validation its returns an empty list.
     */
    val data: Flow<List<M>> = state.asStateFlow()

    /**
     * Represents the current [List] of [ValidationMessage]s.
     */
    val current: List<M>
        get() = state.value

    /**
     * Resets the validation result.
     *
     * @param messages list of messages to reset to. Default is an empty list.
     */
    fun reset(messages: List<M> = emptyList()) {
        state.value = messages
    }

    /**
     * Finds the first [ValidationMessage] matching the given [predicate].
     * If no such element was found, nothing gets called afterwards.
     */
    fun find(predicate: (M) -> Boolean): Flow<M?> = data.map { it.find(predicate) }

    /**
     * Returns a [Flow] of list containing only
     * [ValidationMessage]s matching the given [predicate].
     */
    fun filter(predicate: (M) -> Boolean): Flow<List<M>> = data.map { it.filter(predicate) }

    /**
     * validates the given [inspector] and it's containing data
     * by using the given [metadata] and adds validation messages if needed.
     * To add a message use the [MutableList.add] function.
     *
     * @param inspector inspector containing the data to validate
     * @param metadata extra information for the validation process
     */
    actual abstract fun MutableList<M>.validate(inspector: Inspector<D>, metadata: T)

    /**
     * validates the given [data] by using the given [metadata] and returns
     * a [List] of [ValidationMessage]s.
     *
     * @param data data to validate
     * @param metadata extra information for the validation process
     * @return [List] of [ValidationMessage]s
     */
    actual fun getMessages(data: D, metadata: T): List<M> =
        buildList { validate(inspectorOf(data), metadata) }.also { state.value = it }

    /**
     * validates the given model by using the [validate] functions
     * which must be implemented with the validation rules.
     *
     * @param data model to validate
     * @param metadata extra information for the validation process
     * @return a [Boolean] when data is valid for using in if conditions
     */
    fun isValid(data: D, metadata: T): Boolean =
        getMessages(data, metadata).none(ValidationMessage::isError)

    /**
     * A [Flow] representing the current state of the model (valid or not).
     */
    val valid: Flow<Boolean> = data.map { it.none(ValidationMessage::isError) }
}

/**
 * Extending a [Store] with validation possibility.
 * Therefore, you need to specify the [validator] to use.
 */
interface ValidatingStore<D, M : ValidationMessage, T>: Store<D> {
    /**
     * [Validator] to be used.
     */
    val validator: Validator<D, M, T>

    /**
     * Call this function inside the init-block of your [Store] to
     * enable automatic validation of new data.
     */
    fun enableContinuousValidation(metadata: T) {
        syncBy(handle {
            validator.isValid(it, metadata)
            it
        })
    }
}
