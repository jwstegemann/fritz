package dev.fritz2.components.foundations

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Generic container for modeling a property for a component class.
 * Use it like this:
 * ```
 * open class SomeComponent {
 *      val myProp = ComponentProperty("some text")
 *      val myBooleanProp = ComponentProperty(false)
 *
 *      // should rather be some implementation in the default theme of course!
 *      class SizesContext {
 *          small: Style<BasicParams> = { fontSize { small } }
 *          normal: Style<BasicParams> = { fontSize { small } }
 *          large: Style<BasicParams> = { fontSize { small } }
 *      }
 *
 *      val sizes = ComponentProperty<SizesContext.() -> Style<BasicParams>> { normal }
 * }
 * // within your UI declaration:
 * someComponent {
 *      myProp("Some specific content") // pass simple parameter
 *      myBooleanProp(true)
 *      sizes { large } // use expression syntax
 * }
 * ```
 */
class ComponentProperty<T>(var value: T) {
    operator fun invoke(newValue: T) {
        value = newValue
    }
}

/**
 * Generic container for modeling a property for a component class that could be either consist on a value or on a
 * [Flow] of a value.
 *
 * Use it like this:
 * ```
 * open class SomeComponent {
 *      val myProp = DynamicComponentProperty("some text")
 *      val myBooleanProp = DynamicComponentProperty(false)
 * }
 * // within your UI declaration and static values:
 * someComponent {
 *      myProp("Some specific content")
 *      myBooleanProp(true)
 * }
 * // within your UI declaration and dynamic values:
 * val contentStore = storeOf("Empty")
 * val toggle = storeOf(false)
 * someComponent {
 *      myProp(contentStore.data)
 *      myBooleanProp(toggle.data)
 * }
 * ```
 */
class DynamicComponentProperty<T>(var values: Flow<T> = emptyFlow()) {
    operator fun invoke(newValue: T) {
        values = flowOf(newValue)
    }

    operator fun invoke(newValues: Flow<T>) {
        values = newValues
    }
}

/**
 * Generic container for modeling a property for a component class that could be either consist on a nullable value
 * or on a [Flow] of a nullable value. This specific implementation could be useful for properties where the distinction
 * between some states and the "not yet set" state is important.
 *
 * Use it like this:
 * ```
 * open class SomeComponent<T> {
 *      val selectedItem = NullableDynamicComponentProperty<T>(emptyFlow())
 * }
 * // within your UI declaration and static values:
 * val selectedStore = storeOf<String>(null)
 * someComponent<String> {
 *      selectedItem(selectedStore.data) // no selection at start up!
 * }
 * ```
 */
class NullableDynamicComponentProperty<T>(var values: Flow<T?>) {
    operator fun invoke(newValue: T?) {
        values = flowOf(newValue)
    }

    operator fun invoke(newValues: Flow<T?>) {
        values = newValues
    }
}