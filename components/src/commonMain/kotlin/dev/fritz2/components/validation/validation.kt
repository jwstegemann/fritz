package dev.fritz2.components.validation

import dev.fritz2.identification.Inspector
import dev.fritz2.validation.ValidationMessage
import dev.fritz2.validation.Validator

/**
 * Special [ValidationMessage] for fritz2 components.
 *
 * By default, the validation fails if one or more [ComponentValidationMessage]s have
 * a [severity] of [Severity.Error]. You can override the [isError] method to change this
 * behavior.
 *
 * @param path location of the validated field in model
 * @param severity used for rendering the [ValidationMessage]
 * @param message contains the message
 * @param details optional details for extending the message
 */
data class ComponentValidationMessage(
    val path: String,
    val severity: Severity,
    val message: String,
    val details: String? = null,
) : ValidationMessage {
    override fun isError(): Boolean = severity > Severity.Warning
}

/**
 * Enum which specify the [Severity] of [ComponentValidationMessage].
 */
enum class Severity {
    Info, Success, Warning, Error
}

/**
 * Creates [ComponentValidationMessage] with [Severity.Info].
 *
 * @param path location of the validated field in model
 * @param message contains the message
 * @param details optional details for extending the message
 */
fun infoMessage(path: String, message: String, details: String? = null) =
    ComponentValidationMessage(path, Severity.Info, message, details)

/**
 * Creates [ComponentValidationMessage] with [Severity.Info].
 *
 * @param message contains the message
 * @param details optional details for extending the message
 */
fun <T> Inspector<T>.infoMessage(message: String, details: String? = null) =
    ComponentValidationMessage(path, Severity.Info, message, details)

/**
 * Creates [ComponentValidationMessage] with [Severity.Info].
 *
 * @param path location of the validated field in model
 * @param message contains the message
 * @param details optional details for extending the message
 */
fun successMessage(path: String, message: String, details: String? = null) =
    ComponentValidationMessage(path, Severity.Success, message, details)

/**
 * Creates [ComponentValidationMessage] with [Severity.Info].
 *
 * @param message contains the message
 * @param details optional details for extending the message
 */
fun <T> Inspector<T>.successMessage(message: String, details: String? = null) =
    ComponentValidationMessage(path, Severity.Success, message, details)

/**
 * Creates [ComponentValidationMessage] with [Severity.Warning].
 *
 * @param path location of the validated field in model
 * @param message contains the message
 * @param details optional details for extending the message
 */
fun warningMessage(path: String, message: String, details: String? = null) =
    ComponentValidationMessage(path, Severity.Warning, message, details)

/**
 * Creates [ComponentValidationMessage] with [Severity.Warning].
 *
 * @param message contains the message
 * @param details optional details for extending the message
 */
fun <T> Inspector<T>.warningMessage(message: String, details: String? = null) =
    ComponentValidationMessage(path, Severity.Warning, message, details)

/**
 * Creates [ComponentValidationMessage] with [Severity.Error].
 *
 * @param path location of the validated field in model
 * @param message contains the message
 * @param details optional details for extending the message
 */
fun errorMessage(path: String, message: String, details: String? = null) =
    ComponentValidationMessage(path, Severity.Error, message, details)

/**
 * Creates [ComponentValidationMessage] with [Severity.Error].
 *
 * @param message contains the message
 * @param details optional details for extending the message
 */
fun <T> Inspector<T>.errorMessage(message: String, details: String? = null) =
    ComponentValidationMessage(path, Severity.Error, message, details)

/**
 * Special [Validator] for fritz2 components which uses the [ComponentValidationMessage]
 * internally.
 */
abstract class ComponentValidator<D, T> : Validator<D, ComponentValidationMessage, T>()
