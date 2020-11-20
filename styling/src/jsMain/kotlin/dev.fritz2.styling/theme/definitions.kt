package dev.fritz2.styling.theme

import dev.fritz2.styling.params.BasicParams
import dev.fritz2.styling.params.ColorProperty
import dev.fritz2.styling.params.ShadowProperty
import dev.fritz2.styling.params.Style

/**
 * Defines a responsive [Property] that can have different values for different screen sizes.
 * Per default the value for a certain screen size is the same as the value for the next smaller screen size.
 * You can define the concrete screen sizes that apply in the [Theme] you use.
 *
 * @param sm value for small screens like phones (and default for all the others)
 * @param md value for middle screens like tablets  (and default for all the others)
 * @param lg value for large screens (and default for all the others)
 * @param xl value for extra large screens (and default for all the others)
 */
class ResponsiveValue(val sm: Property, val md: Property = sm, val lg: Property = md, val xl: Property = lg)

/**
 * Defines a value that has different expressions for different scales.
 */
open class ScaledValue(
    val
    normal: Property,
    val small: Property = normal,
    val smaller: Property = small,
    val tiny: Property = smaller,
    val large: Property = normal,
    val larger: Property = large,
    val huge: Property = larger,
    val giant: Property = huge,
    open val none: Property = tiny,
    val full: Property = giant
) {
    val initial: Property = "initial"
    val inherit: Property = "inherit"
    val auto: Property = "auto"
}

/**
 * Defines a value that has different expressions for different weights.
 */
class WeightedValue(
    val normal: Property,
    val light: Property = normal,
    val lighter: Property = light,
    val strong: Property = normal,
    val stronger: Property = strong,
    val none: Property = lighter,
    val full: Property = strong
) {
    val initial: Property = "initial"
    val inherit: Property = "inherit"
}

/**
 * Defines a value that has different expressions for different thicknesses.
 */
class Thickness(
    val normal: Property,
    val thin: Property = normal,
    val fat: Property = normal,
    val hair: Property = thin,
) {
    val initial: Property = "initial"
    val inherit: Property = "inherit"
}

/**
 * Defines a value that has different expressions for different sizes.
 */
class Sizes(
    normal: Property,
    small: Property = normal,
    smaller: Property = small,
    tiny: Property = smaller,
    large: Property = normal,
    larger: Property = large,
    huge: Property = larger,
    giant: Property = huge,
    full: Property = giant
) : ScaledValue(normal, small, smaller, tiny, large, larger, huge, giant, full = full) {
    val borderBox: Property = "border-box"
    val contentBox: Property = "content-box"
    val maxContent: Property = "max-content"
    val minContent: Property = "min-content"
    val fitContent: Property = "fit-content"
    val available: Property = "available"
    val unset: Property = "unset"

    fun fitContent(value: Property): Property = "fit-content($value)"
}

/**
 * Defines the scheme for zIndices in fritz2
 *
 * @property baseValue z-index for normal content ("bottom")
 * @property layer start z-index for layers
 * @property layerStep step to add for each new layer
 * @property overlayValue z-index for an overlay
 * @property toast start z-index for toasts
 * @property toastStep step to add for each new toast
 * @property modal start z-index for modals
 * @property modalStep step to add for each new modal
 */
class ZIndices(
    private val baseValue: Int, private val layer: Int, private val layerStep: Int, private val overlayValue: Int,
    private val toast: Int, private val toastStep: Int, private val modal: Int, private val modalStep: Int
) {

    companion object {
        /**
         * key to set z-index-property
         */
        const val key: Property = "z-index: "
    }

    /**
     * [Property] for base z-index
     */
    val base: Property = "$baseValue"

    /**
     * [Property] for overlay z-index
     */
    val overlay: Property = "$overlayValue"

    /**
     * creates [Property] for a specific layer z-index
     *
     * Use self defined constants for the different layers of your UI.
     *
     * @param value number of layer the z-index should be calculated for
     */
    fun layer(value: Int): Property = zIndexFrom(layer, layerStep, value, 0)

    /**
     * creates [Property] for a specific toast z-index
     *
     * @param value number of toast the z-index should be calculated for
     */
    fun toast(value: Int): Property = zIndexFrom(toast, toastStep, value, 0)

    /**
     * creates [Property] for a specific modals z-index
     *
     * @param value number of modal the z-index should be calculated for
     */
    fun modal(value: Int): Property = zIndexFrom(modal, modalStep, value, 0)

    /**
     * creates [Property] for a specific modals z-index shifted by an offset
     *
     * @param value number of modal the z-index should be calculated for
     * @param offset number to add to the final z-index in order to place an element below (negative value!) or on top
     *               (positive value) of a regularly defined modal.
     */
    fun modal(value: Int, offset: Int): Property = zIndexFrom(modal, modalStep, value, offset)

    private fun zIndexFrom(level: Int, step: Int, value: Int, offset: Int) =
        "${level + step * (value - 1) + offset}"
}

/**
 * Defines the scheme fonts in a theme
 *
 */
interface Fonts {
    val body: Property
    val heading: Property
    val mono: Property
}

/**
 * Defines the scheme colors in a theme
 *
 */
interface Colors {
    val primary: ColorProperty
    val secondary: ColorProperty
    val tertiary: ColorProperty
    val success: ColorProperty
    val danger: ColorProperty
    val warning: ColorProperty
    val info: ColorProperty
    val light: ColorProperty
    val dark: ColorProperty
    val disabled: ColorProperty
}

/**
 * Defines the scheme shadows in a theme
 *
 */
class Shadows(
    val flat: ShadowProperty,
    val raised: ShadowProperty,
    val raisedFurther: ShadowProperty = raised,
    val top: ShadowProperty = raisedFurther,
    val lowered: ShadowProperty,
    val bottom: ShadowProperty = lowered,
    val outline: ShadowProperty,
    val glowing: ShadowProperty = outline,
    val danger: ShadowProperty,
    val none: ShadowProperty = "none"
)

/**
 * Defines a specific icon
 */
class IconDefinition(
    val displayName: String,
    val viewBox: String = "0 0 24 24",
    val svg: String
)

/**
 * Definition of standard-icons
 */
interface Icons {
    val add: IconDefinition
    val all: IconDefinition
    val archive: IconDefinition
    val arrowDown: IconDefinition
    val arrowLeftDown: IconDefinition
    val arrowLeftUp: IconDefinition
    val arrowLeft: IconDefinition
    val arrowRightDown: IconDefinition
    val arrowRightUp: IconDefinition
    val arrowRight: IconDefinition
    val arrowUp: IconDefinition
    val attachment: IconDefinition
    val ban: IconDefinition
    val barChartAlt: IconDefinition
    val barChart: IconDefinition
    val board: IconDefinition
    val book: IconDefinition
    val bookmark: IconDefinition
    val calendar: IconDefinition
    val call: IconDefinition
    val camera: IconDefinition
    val caretDown: IconDefinition
    val caretLeft: IconDefinition
    val caretRight: IconDefinition
    val caretUp: IconDefinition
    val check: IconDefinition
    val chevronDoubleDown: IconDefinition
    val chevronDoubleLeft: IconDefinition
    val chevronDoubleRight: IconDefinition
    val chevronDoubleUp: IconDefinition
    val chevronDown: IconDefinition
    val chevronLeft: IconDefinition
    val chevronRight: IconDefinition
    val chevronUp: IconDefinition
    val circleAdd: IconDefinition
    val circleArrowDown: IconDefinition
    val circleArrowLeft: IconDefinition
    val circleArrowRight: IconDefinition
    val circleArrowUp: IconDefinition
    val circleCheck: IconDefinition
    val circleError: IconDefinition
    val circleHelp: IconDefinition
    val circleInformation: IconDefinition
    val circleRemove: IconDefinition
    val circleWarning: IconDefinition
    val clipboardCheck: IconDefinition
    val clipboardList: IconDefinition
    val clipboard: IconDefinition
    val clock: IconDefinition
    val close: IconDefinition
    val cloudDownload: IconDefinition
    val cloudUpload: IconDefinition
    val cloud: IconDefinition
    val computer: IconDefinition
    val copy: IconDefinition
    val creditCard: IconDefinition
    val delete: IconDefinition
    val documentAdd: IconDefinition
    val documentCheck: IconDefinition
    val documentDownload: IconDefinition
    val documentEmpty: IconDefinition
    val documentRemove: IconDefinition
    val document: IconDefinition
    val download: IconDefinition
    val drag: IconDefinition
    val editAlt: IconDefinition
    val edit: IconDefinition
    val email: IconDefinition
    val expand: IconDefinition
    val export: IconDefinition
    val externalLink: IconDefinition
    val eyeOff: IconDefinition
    val eye: IconDefinition
    val favorite: IconDefinition
    val filterAlt: IconDefinition
    val filter: IconDefinition
    val folderAdd: IconDefinition
    val folderCheck: IconDefinition
    val folderDownload: IconDefinition
    val folderRemove: IconDefinition
    val folder: IconDefinition
    val grid: IconDefinition
    val heart: IconDefinition
    val home: IconDefinition
    val image: IconDefinition
    val inbox: IconDefinition
    val laptop: IconDefinition
    val linkAlt: IconDefinition
    val link: IconDefinition
    val list: IconDefinition
    val location: IconDefinition
    val lock: IconDefinition
    val logOut: IconDefinition
    val map: IconDefinition
    val megaphone: IconDefinition
    val menu: IconDefinition
    val messageAlt: IconDefinition
    val message: IconDefinition
    val mobile: IconDefinition
    val moon: IconDefinition
    val notificationOff: IconDefinition
    val notification: IconDefinition
    val optionsHorizontal: IconDefinition
    val optionsVertical: IconDefinition
    val pause: IconDefinition
    val percentage: IconDefinition
    val pin: IconDefinition
    val play: IconDefinition
    val refresh: IconDefinition
    val remove: IconDefinition
    val search: IconDefinition
    val select: IconDefinition
    val send: IconDefinition
    val settings: IconDefinition
    val share: IconDefinition
    val shoppingCartAdd: IconDefinition
    val shoppingCart: IconDefinition
    val sort: IconDefinition
    val speakers: IconDefinition
    val stop: IconDefinition
    val sun: IconDefinition
    val switch: IconDefinition
    val table: IconDefinition
    val tablet: IconDefinition
    val tag: IconDefinition
    val undo: IconDefinition
    val unlock: IconDefinition
    val userAdd: IconDefinition
    val userCheck: IconDefinition
    val userRemove: IconDefinition
    val user: IconDefinition
    val users: IconDefinition
    val volumeOff: IconDefinition
    val volumeUp: IconDefinition
    val warning: IconDefinition
    val zoomIn: IconDefinition
    val zoomOut: IconDefinition
    val fritz2: IconDefinition
}

interface RadioStyles {
    val sizes: RadioSizes
}

interface RadioSizes {
    val small: Style<BasicParams>
    val normal: Style<BasicParams>
    val large: Style<BasicParams>
}

interface CheckboxStyles {
    val sizes: CheckboxSizes
}

interface CheckboxSizes {
    val small: Style<BasicParams>
    val normal: Style<BasicParams>
    val large: Style<BasicParams>
}

interface InputFieldStyles {
    val small: Style<BasicParams>
    val normal: Style<BasicParams>
    val large: Style<BasicParams>
    val outline: Style<BasicParams>
    val filled: Style<BasicParams>
}

interface PushButtonStyles {
    val variants: PushButtonVariants
    val sizes: PushButtonSizes
}

interface PushButtonVariants {
    val outline: Style<BasicParams>
    val solid: Style<BasicParams>
    val ghost: Style<BasicParams>
    val link: Style<BasicParams>
}

interface PushButtonSizes {
    val small: Style<BasicParams>
    val normal: Style<BasicParams>
    val large: Style<BasicParams>
}

interface ModalStyles {
    val overlay: Style<BasicParams>
    val sizes: ModalSizes
    val variants: ModalVariants
}

interface ModalVariants {
    val auto: Style<BasicParams>
    val verticalFilled: Style<BasicParams>
    val centered: Style<BasicParams>
}

interface ModalSizes {
    val full: Style<BasicParams>
    val small: Style<BasicParams>
    val normal: Style<BasicParams>
    val large: Style<BasicParams>
}


/**
 * definition of the theme's Popover
 */

interface PopoverStyles {
    val size: PopoverSizes
    val trigger: Style<BasicParams>
    val header: Style<BasicParams>
    val section: Style<BasicParams>
    val footer: Style<BasicParams>
    val placement: PopoverPlacements
    val arrowPlacement: PopoverArrowPlacements
    val closeButton: Style<BasicParams>
}

interface PopoverPlacements {
    val top: Style<BasicParams>
    val right: Style<BasicParams>
    val bottom: Style<BasicParams>
    val left: Style<BasicParams>
}

interface PopoverArrowPlacements {
    val top: Style<BasicParams>
    val right: Style<BasicParams>
    val bottom: Style<BasicParams>
    val left: Style<BasicParams>
}

interface PopoverSizes {
    val auto: Style<BasicParams>
    val normal: Style<BasicParams>
}

/**
 * definition of the theme's Popover
 */

interface Tooltip {
    fun write(vararg value: String): Style<BasicParams>
    fun write(vararg value: String, tooltipPlacement: TooltipPlacements.() -> Style<BasicParams>): Style<BasicParams>
    val placement: TooltipPlacements
}

interface TooltipPlacements {
    val top: Style<BasicParams>
    val right: Style<BasicParams>
    val bottom: Style<BasicParams>
    val left: Style<BasicParams>
}