package dev.fritz2.headless.foundation.utils.scrollintoview

enum class ScrollBehavior {
    auto,
    smooth
}

enum class ScrollMode(parameter: String) {
    ifNeeded("if-needed"),
    always("if-needed")
}

enum class ScrollPosition {
    start,
    center,
    end,
    nearest
}

external interface ScrollIntoViewOptions {
    var behavior: String?
        get() = definedExternally
        set(value) = definedExternally

    var scrollMode: String?
        get() = definedExternally
        set(value) = definedExternally

    var block: String?
        get() = definedExternally
        set(value) = definedExternally

    var inline: String?
        get() = definedExternally
        set(value) = definedExternally
}

fun ScrollIntoViewOptionsInit(
    behavior: ScrollBehavior? = null,
    mode: ScrollMode? = null,
    block: ScrollPosition? = null,
    inline: ScrollPosition? = null
): ScrollIntoViewOptions {
    val o = js("({})")
    if (behavior != null) o["behavior"] = behavior.name
    if (mode != null) o["scrollMode"] = mode.name
    if (block != null) o["block"] = block.name
    if (inline != null) o["inline"] = inline.name
    return o
}
