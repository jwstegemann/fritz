package dev.fritz2.styling.theme

import dev.fritz2.styling.params.*
import dev.fritz2.styling.params.BackgroundAttachments.inherit
import dev.fritz2.styling.theme.icons.MonoIcons
import kotlin.math.abs

/**
 * defines the default values and scales for fritz2
 */
open class DefaultTheme : Theme {
    override val name = "default"

    final override val breakPoints = ResponsiveValue("30em", "48em", "62em", "80em")

    override val mediaQueryMd: String = "@media screen and (min-width: ${breakPoints.md})"
    override val mediaQueryLg: String = "@media screen and (min-width: ${breakPoints.lg})"
    override val mediaQueryXl: String = "@media screen and (min-width: ${breakPoints.xl})"

    override val space = ScaledValue(
        none = "0",
        tiny = "0.25rem",
        smaller = "0.5rem",
        small = "0.75rem",
        normal = "1rem",
        large = "1.25rem",
        larger = "1.5rem",
        huge = "2rem",
        giant = "3rem",
        full = "4rem"
    )

    override val position: ScaledValue
        get() = space

    override val gaps: ScaledValue
        get() = space

    // used color scheme https://coolors.co/8ecae6-219ebc-023047-ffb703-fb8500
    override val colors: Colors
        get() = object : Colors {
            override val gray50 = "#F7FAFC"
            override val gray100 = "#EDF2F7"
            override val gray200 = "#E2E8F0"
            override val gray300 = "#CBD5E0"
            override val gray400 = "#A0AEC0"
            override val gray500 = "#718096"
            override val gray600 = "#4A5568"
            override val gray700 = "#2D3748"
            override val gray800 = "#1A202C"
            override val gray900 = "#171923"

            override val neutral =
                ColorScheme(
                    main = "#FFFFFF",
                    mainContrast = gray700,
                    highlight = gray100,
                    highlightContrast = gray700
                )

            override val primary =
                ColorScheme(
                    main = "#0C5173",
                    mainContrast = gray100,
                    highlight = "#CAE4EA",
                    highlightContrast = gray700
                )

            override val secondary =
                ColorScheme(
                    main = "#E6A300",
                    mainContrast = gray100,
                    highlight = "#FFEDCB",
                    highlightContrast = gray700
                )

            override val tertiary =
                ColorScheme(
                    main = gray300,
                    mainContrast = gray700,
                    highlight = gray100,
                    highlightContrast = gray700
                )

            // Signal Colors
            override val info =
                ColorScheme(
                    main = "#219EBC",
                    mainContrast = neutral.main,
                    highlight = "#D2EBF1",
                    highlightContrast = gray700
                )

            override val success =
                ColorScheme(
                    main = "#00A848",
                    mainContrast = neutral.main,
                    highlight = "#CCEDDA",
                    highlightContrast = gray700
                )

            override val warning =
                ColorScheme(
                    main = "#F08B3A",
                    mainContrast = neutral.main,
                    highlight = "#FCE7D7",
                    highlightContrast = gray700
                )

            override val danger =
                ColorScheme(
                    main = "#E14F2A",
                    mainContrast = neutral.main,
                    highlight = "#F9DBD4",
                    highlightContrast = gray700
                )

            //FIXME: move to typography section
            override val font = neutral.mainContrast
            override val background = neutral.main
            override val disabled = gray300
            override val focus = primary.highlight
        }

    override val fontFamilies = object : FontFamilies {
        override val normal =
            "'Segoe UI', Roboto, Helvetica, Calibri, Arial, 'DejaVu Sans', 'Apple Color Emoji', 'Segoe UI Emoji', -apple-system, sans-serif"
        override val mono =
            "Courier, Consolas, 'Liberation Mono', Menlo, 'Courier New', SFMono-Regular, monospace"
    }

    override val fontSizes = ScaledValue(
        smaller = "0.75rem",
        small = "0.875rem",
        normal = "1rem",
        large = "1.25rem",
        larger = "1.5rem",
        huge = "1.875rem",
        giant = "3rem",
        full = "4rem"
    )

    override val lineHeights = ScaledValue(
        normal = "normal",
        tiny = "1.2",
        smaller = "1.25",
        small = "1.3",
        large = "1.5",
        larger = "1.7",
        huge = "2",
        giant = "2.25",
        full = "3"
    )

    override val letterSpacings = ScaledValue(
        smaller = "-0.05em",
        small = "-0.025em",
        normal = "0",
        large = "0.025em",
        larger = "0.05em",
        huge = "0.1em"
    )

    override val sizes = Sizes(
        tiny = "0.25rem",
        smaller = "0.5rem",
        small = "0.75rem",
        normal = "1rem",
        large = "1.25rem",
        larger = "1.5rem",
        huge = "1.75rem",
        giant = "2rem",
        full = "100%",
        wide = ScaledValue(
            tiny = "5rem",
            smaller = "8rem",
            small = "13rem",
            normal = "auto",
            large = "21rem",
            larger = "34rem",
            huge = "55rem",
            giant = "89rem",
        )
    )

    override val borderWidths = Thickness(
        none = "0px",
        normal = "2px",
        thin = "1px",
        fat = "4px",
        hair = "0.1px"
    )

    override val radii = ScaledValue(
        none = "0",
        smaller = "0.125rem",
        small = "0.225rem",
        normal = "0.375rem",
        large = "0.5rem",
        larger = "12px",
        full = "9999px"
    )

    override val shadows: Shadows
        get() = Shadows(
            flat = shadow("0", "1px", "3px", color = rgba(0, 0, 0, 0.12))
                    and shadow("0", "1px", "2px", rgba(0, 0, 0, 0.24)),
            raised = shadow("0", "5px", "10px", rgba(0, 0, 0, 0.25))
                    and shadow(" 0", "5px", "10px", rgba(0, 0, 0, 0.22)),
            raisedFurther = shadow("0", "14px", "28px", rgba(0, 0, 0, 0.25))
                    and shadow("0", "10px", "10px", rgba(0, 0, 0, 0.22)),
            top = shadow("0", "19px", "38px", rgba(0, 0, 0, 0.30))
                    and shadow("0", "15px", "12px", rgba(0, 0, 0, 0.22)),
            lowered = shadow("0", "2px", "4px", color = rgba(0, 0, 0, 0.06), inset = true),
            glowing = shadow("0", "0", "2px", color = rgba(0, 0, 255, 0.5)),
            outline = shadow(
                "0",
                "0",
                "0",
                "2px",
                color = colors.primary.highlight
            ),
            danger = shadow("0", "0", "0", "1px", color = colors.danger.main)
        )

    override val zIndices = ZIndices(
        tableHeader = 10,
        tooltip = 100,
        dropdown = 200,
        popover = 300,
        appFrame = 1000,
        navbar = 1000,
        toast = 2000,
        modal = 3000
    )

    override val opacities = WeightedValue(
        normal = "0.5"
    )

    //from modern-normalize v1.0.0 | MIT License | https://github.com/sindresorhus/modern-normalize
    override val reset: String by lazy {
        """*,::after,::before{box-sizing:border-box}:root{-moz-tab-size:4;tab-size:4}html{line-height:1.15;-webkit-text-size-adjust:100%}body{margin:0}body{font-family:system-ui,-apple-system,'Segoe UI',Roboto,Helvetica,Arial,sans-serif,'Apple Color Emoji','Segoe UI Emoji'}hr{height:0;color:inherit}abbr[title]{text-decoration:underline dotted}b,strong{font-weight:bolder}code,kbd,pre,samp{font-family:ui-monospace,SFMono-Regular,Consolas,'Liberation Mono',Menlo,monospace;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit}button,input,optgroup,select,textarea{font-family:inherit;font-size:100%;line-height:1.15;margin:0}button,select{text-transform:none}[type=button],[type=reset],[type=submit],button{-webkit-appearance:button}::-moz-focus-inner{border-style:none;padding:0}:-moz-focusring{outline:1px dotted ButtonText}:-moz-ui-invalid{box-shadow:none}legend{padding:0}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}"""
    }

    // extra css for general layout by using theme props
    override val global: String by lazy {
        """
        html {
            line-height: ${lineHeights.large};
            -webkit-text-size-adjust: 100%;
            -webkit-font-smoothing: antialiased;
            text-rendering: optimizelegibility;
        }
            
        body {
            color: ${colors.font};
            font-family: ${fontFamilies.normal};
            background-color: ${colors.background};
            font-feature-settings: "kern";
        }
        
        code,
        kbd,
        samp,
        pre {
            font-family: ${fontFamilies.mono};
            font-size: ${fontSizes.normal};
        }
        
        button,
        input,
        optgroup,
        select,
        textarea {
            line-height: ${lineHeights.large};
        }
        
        b,
        strong {
            font-weight: ${FontWeights.bold};
        }
            
        blockquote, dd, dl, figure, h1, h2, h3, h4, h5, h6, hr, p, pre {
            margin: 0
        }

        button {
            background-color: transparent;
            background-image: none
        }

        fieldset {
            margin: 0;
            padding: 0
        }

        ol, ul {
            list-style: none;
            margin: 0;
            padding: 0
        }
        
        *, ::after, ::before {
            box-sizing: border-box;
            border-width: 0;
            border-style: solid;
            border-color: #e5e7eb
        }
        
        hr {
            border-top-width: 1px
        }
        
        img {
            border-style: solid
        }

        textarea {
            line-height: ${lineHeights.tiny};
            resize: vertical
        }

        input::placeholder, textarea::placeholder {
            color: ${colors.gray500};
            opacity: 0.8
        }

        [role=button], button {
            cursor: pointer
        }
        
        table {
            border-collapse: collapse
        }

        h1, h2, h3, h4, h5, h6 {
            font-size: inherit;
            font-weight: inherit
        }

        a {
            color: inherit;
            text-decoration: inherit
        }

        button, input, optgroup, select, textarea {
            padding: 0;
            line-height: inherit;
            color: inherit
        }

        audio, canvas, embed, iframe, img, object, svg, video {
            display: block;
            vertical-align: middle
        }

        img, video {
            max-width: 100%;
            height: auto
        }
        
        p {
          font-size: ${fontSizes.normal};
          line-height: ${lineHeights.larger};
        }
        
        h1 {
          line-height: ${lineHeights.tiny};
          font-weight: ${FontWeights.medium};
          font-size: ${fontSizes.huge};
          letter-spacing: ${letterSpacings.small};
          outline: 0;
        }
        
        h2 {
          line-height: ${lineHeights.small};
          font-weight: ${FontWeights.medium};
          font-size: ${fontSizes.larger};
          letter-spacing: ${letterSpacings.small};
        }
        
        h3 {
          line-height: ${lineHeights.small};
          font-weight: ${FontWeights.medium};
          font-size: ${fontSizes.large};
          letter-spacing: ${letterSpacings.small};
        }
        
        h4 {
          font-size: ${fontSizes.normal};
          font-weight: ${FontWeights.medium};
        }
        
        h5 {
          font-size: ${fontSizes.small};
          font-weight: ${FontWeights.medium};
        }
        
        h6 {
          font-size: ${fontSizes.smaller};
          font-weight: ${FontWeights.medium};
        }
        
        li {
            display: list-item;
            text-align: -webkit-match-parent;
        }
        
        ul {
            display: block;
            list-style-type: disc;
            margin-block-start: 1em;
            margin-block-end: 1em;
            margin-inline-start: 0px;
            margin-inline-end: 0px;
            padding-inline-start: 1.5em;
        }
        :-moz-focusring {
            outline: none
        }
        """.trimIndent()
    }

    override val icons: Icons = MonoIcons()

    override val input = object : InputFieldStyles {
        override val sizes = object : FormSizesStyles {
            private val basic: Style<BasicParams> = {
                minWidth { "2.5rem" }
                lineHeight { normal }
            }

            override val small: Style<BasicParams> = {
                basic()
                height { "2rem" }
                fontSize { small }
                paddings {
                    horizontal { small }
                }
            }

            override val normal: Style<BasicParams> = {
                basic()
                height { "2.5rem" }
                fontSize { normal }
                paddings {
                    horizontal { small }
                }
            }

            override val large: Style<BasicParams> = {
                basic()
                height { "3rem" }
                fontSize { large }
                paddings {
                    horizontal { small }
                }
            }
        }

        override val variants = object : InputFieldVariants {
            private val basic: Style<BasicParams> = {
                radius { normal }
                fontWeight { normal }
                color { font }
                background { color { background } }

                border {
                    width { thin }
                    style { solid }
                    color { gray300 }
                }

                hover {
                    border {
                        color { gray700 }
                    }
                }

                readOnly {
                    background {
                        color { gray200 }
                    }
                }

                disabled {
                    background {
                        color { neutral.main }
                    }
                    color { disabled }
                    hover {
                        border {
                            color { gray300 }
                        }
                    }
                }

                focus {
                    border {
                        color { primary.main }
                    }
                    boxShadow { outline }
                }
            }
            override val outline: Style<BasicParams> = {
                basic()
            }

            override val filled: Style<BasicParams> = {
                basic()
                background {
                    color { primary.highlight }
                }
                color { neutral.main }

                hover {
                    background { color { gray200 } }
                    color { gray700 }
                }

                focus {
                    background {
                        color { "transparent" }
                    }
                    color { gray700 }
                }
            }
        }

        override val severity = object : SeverityStyles {

            private fun basic(color: ColorProperty, shadow: ShadowProperty): Style<BasicParams> = {
                boxShadow { shadow }
                border {
                    width { thin }
                    style { solid }
                    color { color }
                }

                hover {
                    border {
                        color { color }
                    }
                }

                focus {
                    boxShadow { shadow }
                }
            }

            override val info: Style<BasicParams> = {}
            override val success: Style<BasicParams> = {}
            override val warning: Style<BasicParams> = {}
            override val error: Style<BasicParams>
                get() = basic(colors.danger.main, shadows.danger)
        }
    }

    override val checkbox = object : CheckboxStyles {
        override val sizes = object : FormSizesStyles {
            override val small: Style<BasicParams> = {
                css("--cb-size: .75rem")
                css("--cb-svg-size: .50rem")
                css("--cb-radius:  ${radii.smaller}")
                fontSize { small }
                lineHeight { small }
                margins { right { tiny } }
            }
            override val normal: Style<BasicParams> = {
                css("--cb-size: 1.0rem")
                css("--cb-svg-size: .75rem")
                css("--cb-radius:  ${radii.small}")
                fontSize { normal }
                lineHeight { normal }
                margins { right { smaller } }
            }
            override val large: Style<BasicParams> = {
                css("--cb-size: 1.5rem")
                css("--cb-svg-size: 1.25rem")
                css("--cb-radius:  ${radii.normal}")
                fontSize { larger }
                lineHeight { larger }
                margins { right { small } }
            }
        }
        override val input: Style<BasicParams> = {
            children("&:focus + div") {
                border {
                    color { gray300 }
                }
                boxShadow { outline }
            }
            children("&[disabled] + div") {
                background {
                    color { disabled }
                }
            }
            children("&[disabled] ~ div") {
                opacity { ".5" }
            }
            children("&:not([checked]) + div > *") {
                css("visibility:hidden;")
            }
        }
        override val icon: Style<BasicParams> = {
            width { "var(--cb-svg-size)" }
            height { "var(--cb-svg-size)" }
            lineHeight { "var(--cb-svg-size)" }
            margins {
                top { ".0625rem" }
                left { ".0625rem" }
            }
        }
        override val label: Style<BasicParams> = {
            margins { left { tiny } }
            display { block }
        }
        override val default: Style<BasicParams> = {
            display { inlineFlex }
            flex {
                shrink { "0" }
            }
            width { "var(--cb-size)" }
            height { "var(--cb-size)" }
            background { color { neutral.main } }
            border {
                width { "1px" }
                style { solid }
                color { gray300 }
            }
            radius { "var(--cb-radius)" }
        }
        override val checked: Style<BasicParams> = {
            background { color { primary.main } }
            border { color { primary.highlight } }
            color { primary.mainContrast }
        }

        override val severity = object : SeverityStyles {
            private fun apply(background: ColorProperty, shadowColor: ColorProperty): Style<BasicParams> = {
                children("&[checked] + div") {
                    background { color { background.important } }
                    focus {
                        boxShadow {
                            shadow("0", "0", "0", "2px", color = shadowColor)
                        }
                    }
                }
                children("+ div") {
                    background { color { background } }
                    focus {
                        boxShadow {
                            shadow("0", "0", "0", "2px", color = shadowColor)
                        }
                    }
                }
            }

            override val info: Style<BasicParams> = {}
            override val success: Style<BasicParams> = {}
            override val warning: Style<BasicParams> = {}
            override val error: Style<BasicParams>
                get() = apply(colors.danger.main, colors.danger.main)
        }
    }

    override val radio = object : RadioStyles {
        override val sizes = object : FormSizesStyles {
            override val small: Style<BasicParams> = {
                css("--rb-size: .75rem")
                fontSize { small }
                lineHeight { small }
                margins { right { tiny } }
            }
            override val normal: Style<BasicParams> = {
                css("--rb-size: 1.0rem")
                fontSize { normal }
                lineHeight { normal }
                margins { right { smaller } }
            }
            override val large: Style<BasicParams> = {
                css("--rb-size: 1.5rem")
                fontSize { larger }
                lineHeight { larger }
                margins { right { small } }
            }
        }
        override val input: Style<BasicParams> = {
            children("&:focus + div") {
                border {
                    color { primary.main }
                }
                boxShadow { outline }
            }
            children("&[disabled] + div") {
                background {
                    color { disabled }
                }
            }
            children("&[disabled] ~ div") {
                opacity { ".5" }
            }
        }
        override val label: Style<BasicParams> = {
            margins { left { tiny } }
            display { block }
        }
        override val default: Style<BasicParams> = {
            display { inlineFlex }
            flex {
                shrink { "0" }
            }
            css("align-items: center;")
            css("justify-content: center;")
            width { "var(--rb-size)" }
            height { "var(--rb-size)" }
            background { color { neutral.main } }
            border {
                width { "2px" }
                style { solid }
                color { gray300 }
            }
            radius { "9999px" }
        }
        override val selected: Style<BasicParams> = {
            background { color { primary.main } }
            border { color { primary.main } }
            color { primary.mainContrast }
            before {
                css("content:\"\";")
                display {
                    inlineBlock
                }
                position {
                    relative { }
                }
                width { "50%" }
                height { "50%" }
                radius { "50%" }
                background {
                    color { neutral.main }
                }
            }
        }

        override val severity: SeverityStyles
            get() = checkbox.severity
    }

    override val switch = object : SwitchStyles {
        override val sizes = object : FormSizesStyles {
            override val small: Style<BasicParams> = {
                css("--sw-width: 1.35rem")
                css("--sw-height: .75rem")
                fontSize { small }
                lineHeight { small }
                margins { right { tiny } }
            }
            override val normal: Style<BasicParams> = {
                css("--sw-width: 1.85rem")
                css("--sw-height: 1rem")
                fontSize { normal }
                lineHeight { normal }
                margins { right { smaller } }
            }
            override val large: Style<BasicParams> = {
                css("--sw-width: 2.875rem")
                css("--sw-height: 1.5rem")
                fontSize { larger }
                lineHeight { larger }
                margins { right { small } }
            }
        }
        override val input: Style<BasicParams> = {
            children("&:focus + div") {
                border {
                    color { gray300 }
                }
                boxShadow { outline }
            }

            children("&[checked] + div div") {
                css("transform:translateX(calc(var(--sw-width) - var(--sw-height)));")

            }

            children("&[disabled] + div") {
                background {
                    color { disabled }
                }
            }
            children("&[disabled] ~ div") {
                opacity { ".5" }
            }
        }
        override val dot: Style<BasicParams> = {
            width { "var(--sw-height)" }
            height { "var(--sw-height)" }
            radius { "9999px" }
            background {
                color { neutral.main }
            }
            css("transition: transform 250ms ease 0s;")


        }
        override val label: Style<BasicParams> = {
            margins { left { tiny } }
            display { block }
        }
        override val default: Style<BasicParams> = {
            display { inlineFlex }
            flex {
                shrink { "0" }
            }
            padding { "2px" }
            width { "var(--sw-width)" }
            height { "var(--sw-height)" }
            background { color { gray300 } }
            radius { "9999px" }
            css("justify-content: flex-start;")
            css("box-sizing: content-box;")
            css("align-items: center;")
            css("transition: all 120ms ease 0s;")
        }
        override val checked: Style<BasicParams> = {
            background { color { primary.main } }
        }

        override val severity: SeverityStyles
            get() = checkbox.severity
    }

    override val button = object : PushButtonStyles {
        override val types: PushButtonTypes = object : PushButtonTypes {
            override val primary
                get() = colors.primary
            override val secondary
                get() = colors.secondary
            override val info
                get() = colors.info
            override val success
                get() = colors.success
            override val warning
                get() = colors.warning
            override val danger
                get() = colors.danger
        }

        override val variants = object : PushButtonVariants {
            private val basic: Style<BasicParams> = {
                lineHeight { smaller }
                radius { normal }
                fontWeight { semiBold }
                focus {
                    boxShadow { outline }
                }
            }

            override val solid: BasicParams.(ColorScheme) -> Unit = { colorScheme ->
                basic()
                background { color { colorScheme.main } }
                color { colorScheme.mainContrast }
                hover {
                    background {
                        color { colorScheme.highlight }
                    }
                    color { colorScheme.highlightContrast }
                }
                active {
                    background {
                        color { colorScheme.highlight }
                    }
                    color { colorScheme.highlightContrast }
                }
            }

            override val outline: BasicParams.(ColorScheme) -> Unit = { colorScheme ->
                basic()
                color { colorScheme.main }
                border {
                    width { thin }
                    style { solid }
                    color { colorScheme.main }
                }
                hover {
                    background {
                        color { colorScheme.main }
                    }
                    color { colorScheme.mainContrast }
                }
            }

            override val ghost: BasicParams.(ColorScheme) -> Unit = { colorScheme ->
                basic()
                color { colorScheme.main }
            }

            override val link: BasicParams.(ColorScheme) -> Unit = { colorScheme ->
                basic()
                paddings { all { none } }
                height { auto }
                lineHeight { normal }
                color { colorScheme.main }
                hover {
                    textDecoration { underline }
                }
                active {
                    color { colorScheme.highlightContrast }
                }
            }
        }

        override val sizes = object : FormSizesStyles {
            override val normal: Style<BasicParams> = {
                height { "2.5rem" }
                minWidth { "2.5rem" }
                fontSize { normal }
                paddings {
                    horizontal { normal }
                }
            }

            override val small: Style<BasicParams> = {
                height { "2rem" }
                minWidth { "2rem" }
                fontSize { small }
                paddings {
                    horizontal { smaller }
                }
            }

            override val large: Style<BasicParams> = {
                height { "3rem" }
                minWidth { "3rem" }
                fontSize { large }
                paddings {
                    horizontal { larger }
                }
            }
        }
    }

    override val modal = object : ModalStyles {

        override val base: Style<BasicParams> = {
            background {
                color { neutral.main }
            }
            padding { normal }
            radius { tiny }
            boxShadow { raisedFurther }
            width { "100%" }
        }

        override val overlay: Style<BasicParams> = {
            position {
                fixed {
                    vertical { none }
                    horizontal { none }
                }
            }
            background {
                color { rgba(0, 0, 0, 0.4) }
            }
        }

        override val width: BasicParams.(String, Property) -> Unit = { key, value ->
            maxWidth { value }
            if (key.lowercase() != "full") {
                margins {
                    top { "var(--modal-level)" }
                    left { "var(--modal-level)" }
                    right { "1rem" }
                    bottom { "1rem" }
                }
            }
        }

        override val widths = object : ModalWidths {
            override val full = "100vw"
            override val small = "calc(50vw - var(--modal-level))"
            override val normal = "calc(70vw - var(--modal-level))"
            override val large = "100vw"
        }

        override val sizes = object : ModalSizes {

            private val withMargin: Style<BasicParams> = {
                margins {
                    top { "var(--modal-level)" }
                    left { "var(--modal-level)" }
                    right { "1rem" }
                    bottom { "1rem" }
                }
            }

            override val full: Style<BasicParams> = {
                maxWidth { "100vw" }
            }

            override val large: Style<BasicParams> = {
                maxWidth { "100vw" }
                withMargin()
            }

            override val normal: Style<BasicParams> = {
                maxWidth { "calc(70vw - var(--modal-level))" }
                withMargin()
            }

            override val small: Style<BasicParams> = {
                maxWidth { "calc(50vw - var(--modal-level))" }
                withMargin()
            }
        }

        override val variants = object : ModalVariants {
            override val auto: Style<BasicParams> = {
            }

            override val verticalFilled: Style<BasicParams> = {
            }

            override val centered: Style<BasicParams> = {
            }
        }

        override val internalScrolling: Style<BasicParams> = {
            overflow { auto }
            maxHeight { "calc(100vh - var(--modal-level) - 1rem)" }
        }
    }

    override val paper: PaperStyles = object : PaperStyles {
        override val sizes = object : PaperSizes {
            override val small: Style<BoxParams> = {
                padding { smaller }
                css("--paper-radius: ${Theme().radii.normal}")
                css("--paper-line-width: ${Theme().borderWidths.hair}")
            }

            override val normal: Style<BoxParams> = {
                padding { small }
                css("--paper-radius: ${Theme().radii.large}")
                css("--paper-line-width: ${Theme().borderWidths.thin}")
            }

            override val large: Style<BoxParams> = {
                padding { normal }
                css("--paper-radius: ${Theme().radii.huge}")
                css("--paper-line-width: ${Theme().borderWidths.normal}")
            }
        }

        override val types = object : PaperTypes {
            override val normal: Style<BoxParams> = {
                boxShadow { raised }
            }

            override val outline: Style<BoxParams> = {
                border {
                    color { neutral.highlight }
                    style { solid }
                    width { "var(--paper-line-width)" }
                }
            }

            override val ghost: Style<BoxParams> = {
                // No specific style
            }
        }

        override val background: Style<BoxParams> = {
            background {
                color { neutral.main }
            }
            radius { "var(--paper-radius)" }
        }
    }

    override val card: CardStyles = object : CardStyles {
        override val sizes: CardSizes = object : CardSizes {
            override val small: Style<BoxParams> = {
                fontSize { small }
                // TODO: Use size smaller than 'tiny' once available:
                //css("--card-spacing-header-footer: ${Theme().sizes.tinier}")
                css("--card-spacing-header-footer: 0.12rem")
                css("--card-spacing-content: ${Theme().sizes.smaller}")
                css("--card-separator-width: ${Theme().borderWidths.hair}")
            }

            override val normal: Style<BoxParams> = {
                fontSize { normal }
                css("--card-spacing-header-footer: ${Theme().sizes.tiny}")
                css("--card-spacing-content: ${Theme().sizes.small}")
                css("--card-separator-width: ${Theme().borderWidths.thin}")
            }

            override val large: Style<BoxParams> = {
                fontSize { large }
                css("--card-spacing-header-footer: ${Theme().sizes.smaller}")
                css("--card-spacing-content: ${Theme().sizes.normal}")
                css("--card-separator-width: ${Theme().borderWidths.normal}")
            }
        }

        override val header: Style<BoxParams> = {
            paddings {
                bottom { "var(--card-spacing-header-footer)" }
            }
            margins {
                bottom { "var(--card-spacing-content)" }
            }
            borders {
                bottom {
                    color { neutral.highlight }
                    style { solid }
                    width { "var(--card-separator-width)" }
                }
            }
            fontWeight { bold }
        }

        override val footer: Style<BoxParams> = {
            paddings {
                top { "var(--card-spacing-header-footer)" }
            }
            margins {
                top { "var(--card-spacing-content)" }
            }
            borders {
                top {
                    color { neutral.highlight }
                    style { solid }
                    width { "var(--card-separator-width)" }
                }
            }
        }

        override val content: Style<BoxParams> = {
            // No particular style in this theme
        }
    }

    override val popover = object : PopoverStyles {
        override val size: PopoverSizes = object : PopoverSizes {
            private val basic: Style<BasicParams> = {
                background {
                    color { neutral.main }
                }
                paddings {
                    top { tiny }
                    bottom { tiny }
                    left { small }
                    right { small }
                }
                radius { small }
                boxShadow { flat }
                zIndex { popover }

            }
            override val auto: Style<BasicParams> = {
                basic()
            }
            override val normal: Style<BasicParams> = {
                basic()
                width { "250px" }
            }
        }
        override val toggle: Style<BasicParams> = {
            display { inlineBlock }
        }
        override val header: Style<BasicParams> = {
            fontWeight { semiBold }
            borders {
                bottom {
                    width { thin }
                    style { solid }
                    color { inherit }
                }
            }
        }
        override val section: Style<BasicParams> = {
            paddings {
                top { small }
                bottom { small }
            }
        }
        override val footer: Style<BasicParams> = {
            borders {
                top {
                    width { thin }
                    style { solid }
                    color { inherit }
                }
            }
        }
        override val placement = object : PopoverPlacements {
            private val basic: Style<BasicParams> = {
                css("transition: transform .2s;")
                zIndex { popover }
            }
            override val top: Style<BasicParams> = {
                basic()
                position {
                    absolute {
                        left { "50%" }
                        top { "-1rem" }
                    }
                }
                css("transform: translate(-50%, -100%) scale(1);")
            }
            override val right: Style<BasicParams> = {
                margins { top { "1rem" } }
                position {
                    absolute {
                        left { "calc(100% + 1rem)" }
                        top { "50%" }
                    }
                }
                css("transform: translate(0, -50%) scale(1);")
            }
            override val bottom: Style<BasicParams> = {
                position {
                    absolute {
                        left { "50%" }
                        top { "calc(100% + 1rem)" }
                    }
                }
                css("transform: translate(-50%, 0) scale(1);")
            }
            override val left: Style<BasicParams> = {
                margins { top { "1rem" } }
                position {
                    absolute {
                        left { "-1rem" }
                        top { "50%" }
                    }
                }
                css("transform: translate(-100%, -50%) scale(1);")
            }
        }
        override val arrowPlacement = object : PopoverArrowPlacements {
            private val basic: Style<BasicParams> = {
                css("transform: rotate(45deg);")
                width { "1rem" }
                height { "1rem" }
                position {
                    absolute {}
                }
                background {
                    color { inherit }
                }
                before {
                    css("content:\"\";")
                    width { "1rem" }
                    height { "1rem" }
                    position { absolute {} }
                }
            }
            override val top: Style<BasicParams> = {
                basic()
                css("left:calc(50% - 0.5rem);")
                position {
                    absolute { top { "-0.5rem" } }
                }
                before {
                    boxShadow {
                        "rgba(0, 0, 0, 0.1) -1px -1px 1px 0px"
                    }
                }
            }
            override val right: Style<BasicParams> = {
                basic()
                css("top: calc(50% - 1.5rem);")
                css("right: calc(-0.5rem - 0.5px);")
                position {
                    absolute {}
                }
                before {
                    boxShadow {
                        "rgba(0, 0, 0, 0.1) -1px 1px 1px 0px inset"
                    }
                }
            }
            override val bottom: Style<BasicParams> = {
                basic()
                css("left:calc(50% - 0.5rem);")
                position {
                    absolute { bottom { "-0.5rem" } }
                }
                before {
                    boxShadow {
                        "rgba(0, 0, 0, 0.1) -1px -1px 1px 0px inset"
                    }
                }
            }
            override val left: Style<BasicParams> = {
                basic()
                css("top:calc(50% - 1.5rem);")
                position {
                    absolute { left { "-0.5rem" } }
                }
                before {
                    boxShadow {
                        "rgba(0, 0, 0, 0.1) -1px 1px 1px 0px"
                    }
                }
            }
        }
        override val closeButton: Style<BasicParams> = {
            position {
                absolute {
                    right { "0.5rem" }
                    top { "0.5rem" }
                }
            }
            padding { tiny }
            width { "1rem" }
            height { "1rem" }
            minWidth { "1rem" }
            fontWeight { semiBold }
            lineHeight { "1rem" }
        }
    }

    override val tooltip = object : TooltipStyles {

        override fun write(vararg value: String): Style<BasicParams> {
            return write(*value) { top }
        }

        override fun write(
            vararg value: String,
            tooltipPlacement: TooltipPlacements.() -> Style<BasicParams>,
        ): Style<BasicParams> {
            return {
                position {
                    relative {}
                }
                after {
                    css("content:\"${value.asList().joinToString("\\A")}\";")
                    background { color { gray700 } }
                    radius { small }
                    color { gray300 }
                    display { none }
                    overflow { hidden }
                    opacity { "0" }
                    zIndex { tooltip }
                    position {
                        absolute {
                            left { "50%" }
                            bottom { "100%" }
                        }
                    }
                    padding { tiny }
                    fontSize { smaller }
                    lineHeight { smaller }
                    css("text-overflow: ellipsis;")
                    css("transition: opacity .2s, transform .2s;")
                    css("white-space: pre;")
                }
                focus {
                    after {
                        display { block }
                        opacity { "1" }
                    }

                }
                hover {
                    after {
                        display { block }
                        opacity { "1" }
                    }
                }
                tooltipPlacement.invoke(placement)()
            }
        }

        override val placement = object : TooltipPlacements {
            override val top: Style<BasicParams> = {
                after {
                    css("transform: translate(-50%, 0.5rem);")
                }
                focus {
                    after {
                        css("transform: translate(-50%, -.5rem);")
                    }
                }
                hover {
                    after {
                        css("transform: translate(-50%, -.5rem);")
                    }
                }
            }
            override val right: Style<BasicParams> = {
                after {
                    position {
                        absolute {
                            bottom { "50%" }
                            left { "100%" }
                        }
                    }
                    css("transform: translate(-0.5rem, 50%);")
                }
                focus {
                    after {
                        css("transform: translate(0.5rem, 50%);")
                    }

                }
                hover {
                    after {
                        css("transform: translate(0.5rem, 50%);")
                    }
                }
            }
            override val bottom: Style<BasicParams> = {
                after {
                    position {
                        absolute {
                            bottom { auto }
                            top { "100%" }
                        }
                    }
                    css("transform: translate(-50%, -.5rem);")
                }
                focus {
                    after {
                        css("transform: translate(-50%, .5rem);")
                    }
                }
                hover {
                    after {
                        css("transform: translate(-50%, .5rem);")
                    }

                }
            }
            override val left: Style<BasicParams> = {
                after {
                    position {
                        absolute {
                            bottom { "50%" }
                            left { auto }
                            right { "100%" }
                        }
                    }
                    css("transform: translate(0.5rem, 50%);")
                }
                focus {
                    after {
                        css("transform: translate(-0.5rem, 50%);")
                    }

                }
                hover {
                    after {
                        css("transform: translate(-0.5rem, 50%);")
                    }
                }
            }
        }
    }

    override val textArea = object : TextAreaStyles {
        override val resize = object : TextAreaResize {
            override val both: Style<BasicParams> = {
                css("resize:both")
            }

            override val none: Style<BasicParams> = {
                css("resize:none")
            }

            override val vertical: Style<BasicParams> = {
                css("resize:vertical")
            }

            override val horizontal: Style<BasicParams> = {
                css("resize:horizontal")
            }
        }

        override val sizes = object : FormSizesStyles {
            override val small: Style<BasicParams> = {
                lineHeight { normal }
                height { "1rem" }
                width { "25%" }

                paddings {
                    vertical { "4px" }
                    horizontal { "0.5rem" }
                }
            }
            override val normal: Style<BasicParams> = {
                lineHeight { normal }
                height { "2rem" }
                width { "50%" }

                paddings {
                    vertical { "8px" }
                    horizontal { "0.75rem" }
                }
            }
            override val large: Style<BasicParams> = {
                lineHeight { normal }
                height { "2.5rem" }
                width { "100%" }

                paddings {
                    vertical { "8px" }
                    horizontal { "0.75rem" }
                }
            }
        }

        override val variants = object : TextAreaVariants {
            override val basic: Style<BasicParams> = {
                radius { normal }
                fontWeight { normal }

                border {
                    width { thin }
                    style { solid }
                    color { gray300 }

                }

                background { color { background } }
                color { font }

                disabled {
                    background {
                        color { neutral.highlight }
                    }
                    color { disabled }
                }

                focus {
                    border {
                        color { focus }
                    }
                    boxShadow { outline }
                }
            }
        }

        override val severity: SeverityStyles
            get() = input.severity
    }

    override val alert: AlertStyles = object : AlertStyles {
        override val severities: AlertSeverities
            get() = object : AlertSeverities {
                override val info: AlertSeverity = object : AlertSeverity {
                    override val colorScheme = colors.info
                    override val icon = icons.circleInformation
                }
                override val success: AlertSeverity = object : AlertSeverity {
                    override val colorScheme = colors.success
                    override val icon = icons.circleCheck
                }
                override val warning: AlertSeverity = object : AlertSeverity {
                    override val colorScheme = colors.warning
                    override val icon = icons.circleWarning
                }
                override val error: AlertSeverity = object : AlertSeverity {
                    override val colorScheme = colors.danger
                    override val icon = icons.circleError
                }
            }

        override val variants: AlertVariants = object : AlertVariants {
            override val subtle: BasicParams.(ColorScheme) -> Unit = { colorScheme ->
                background {
                    color { colorScheme.highlight }
                }
                color { colorScheme.highlightContrast }
            }
            override val solid: BasicParams.(ColorScheme) -> Unit = { colorScheme ->
                background {
                    color { colorScheme.main }
                }
                color { colorScheme.mainContrast }
            }

            override val leftAccent: BasicParams.(ColorScheme) -> Unit = { colorScheme ->
                background {
                    color { colorScheme.highlight }
                }
                color { colorScheme.highlightContrast }

                borders {
                    left {
                        width { fat }
                        color { colorScheme.main }
                        style { solid }
                    }
                }
            }
            override val topAccent: BasicParams.(ColorScheme) -> Unit = { colorScheme ->
                background {
                    color { colorScheme.highlight }
                }
                color { colorScheme.highlightContrast }

                borders {
                    top {
                        width { fat }
                        color { colorScheme.main }
                        style { solid }
                    }
                }
            }
            override val ghost: BasicParams.(ColorScheme) -> Unit = { _ ->
                background { color { background } }
                color { font }
            }
        }

        override val sizes = object : FormSizesStyles {
            override val small: Style<BasicParams> = {
                css("--al-icon-margin: 0.25rem")
                css("--al-icon-size: ${Theme().fontSizes.small}")
                css("--al-border-width: ${Theme().borderWidths.normal}")
                fontSize { small }
                lineHeight { small }
            }
            override val normal: Style<BasicParams> = {
                css("--al-icon-margin: 0.5rem")
                css("--al-icon-size: ${Theme().fontSizes.normal}")
                css("--al-border-width: ${Theme().borderWidths.fat}")
                fontSize { normal }
                lineHeight { normal }
            }
            override val large: Style<BasicParams> = {
                css("--al-icon-margin: 1rem")
                css("--al-icon-size: ${Theme().fontSizes.larger}")
                css("--al-border-width: ${Theme().borderWidths.fat}")
                fontSize { larger }
                lineHeight { larger }
            }
        }

        override val stacking = object : AlertStacking {
            override val compact: Style<BasicParams> = {
                margin { none }
                padding { none }
            }
            override val separated: Style<BasicParams> = {
                margin { normal }
                padding { normal }
            }
            override val toast: Style<BasicParams> = {
                margin { none }
                paddings {
                    vertical { normal }
                    left { normal }
                    right { huge } // needed to not be overlapped by the close-button
                }
            }
        }
    }

    override val toast = object : ToastStyles {
        override val body: Style<BoxParams> = {
            position { relative { } }
            display { flex }
            direction { row }
            alignItems { center }

            maxWidth { "560px" }
            minWidth { giant }
            minHeight { giant }
            overflow { hidden }
            margin { smaller }
            radius { normal }
            boxShadow { raised }

            css("pointer-events: auto;")
            css("-webkit-box-align: start;")
        }

        override val list: Style<BoxParams> = {
            display { flex }
            direction { column }
            overflow { visible }

            css("transform-origin: 50% 50% 0px;")
        }

        override val placement = object : ToastPlacement {
            override val top: Style<BasicParams> = {
                css("top:0px")
                css("right:0px")
                css("left:0px")
            }
            override val topLeft: Style<BasicParams> = {
                css("left:0px")
                css("top:0px")
            }
            override val topRight: Style<BasicParams> = {
                css("top:0px")
                css("right:0px")
            }
            override val bottom: Style<BasicParams> = {
                css("bottom:0px")
                css("right:0px")
                css("left:0px")
            }
            override val bottomLeft: Style<BasicParams> = {
                css("bottom:0px")
                css("left:0px")
            }
            override val bottomRight: Style<BasicParams> = {
                css("bottom:0px")
                css("right:0px")
            }
        }

        override val alignment: BoxParams.(String) -> Unit = { horizontal ->
            alignItems {
                when (horizontal) {
                    "left" -> flexStart
                    "right" -> flexEnd
                    else -> center
                }
            }
        }

        override val status = object : ToastStatus {
            override val success: Style<BasicParams> = {
                background { color { success.main } }
                color { success.mainContrast }
            }
            override val error: Style<BasicParams> = {
                background { color { danger.main } }
                color { danger.mainContrast }
            }
            override val warning: Style<BasicParams> = {
                background { color { warning.main } }
                color { warning.mainContrast }
            }
            override val info: Style<BasicParams> = {
                background { color { info.main } }
                color { info.mainContrast }
            }

        }

        override val closeButton = object : ToastButton {
            override val close: Style<BasicParams> = {
                position {
                    absolute {
                        top { smaller }
                        right { smaller }
                    }
                }

                fontSize { "10px" }
                css("outline: 0px;")
                css("transition: all 0.2s ease 0s;")
                color { info.mainContrast }

                focus {
                    css("outline: none;")
                    boxShadow { none }
                }
            }
        }
    }

    override val select = object : SelectFieldStyles {

        override val variants = object : SelectFieldVariants {
            private val basic: Style<BasicParams> = {
                width { full }
                paddings {
                    left { "0.75rem" }
                    right { "1.5rem" }
                    bottom { "1px" }
                }
                lineHeight { normal }
                fontSize { "1rem" }
                height { "2.5rem" }
                radius { normal }
                background { color { background } }
                color { font }
                focus {
                    focus {
                        border {
                            color { focus }
                        }
                        boxShadow { outline }
                    }
                }
                hover {
                    border {
                        color { gray700 }
                    }
                }

                disabled {
                    background {
                        color { neutral.highlight }
                    }
                    color { disabled }
                    hover {
                        border {
                            color { gray300 }
                        }
                    }
                }
                position { relative { } }
                minWidth { "2.5rem" }

                css("outline: 0px;")
                css("appearance: none;")
                css("transition: all 0.2s ease 0s;")
            }
            override val outline: Style<BasicParams> = {
                basic()
                border {
                    width { thin }
                    style { solid }
                    color { gray300 }
                }
            }

            override val filled: Style<BasicParams> = {
                basic()
                background {
                    color { gray300 }
                }

                hover {
                    background {
                        color { primary.highlight }
                    }
                    color { primary.highlightContrast }
                }

                focus {
                    background {
                        color { "transparent" }
                    }
                }
            }
        }

        override val sizes = object : FormSizesStyles {
            override val small: Style<BasicParams> = {
                fontSize { small }
                css("--select-icon-size: .75rem")
                height { "2rem" }
            }

            override val normal: Style<BasicParams> = {
                fontSize { normal }
                css("--select-icon-size: 1.0rem")
                height { "2.5rem" }
            }

            override val large: Style<BasicParams> = {
                fontSize { large }
                css("--select-icon-size: 1.5rem")
                height { "3rem" }
            }
        }

        override val severity: SeverityStyles
            get() = input.severity
    }

    override val formControl = object : FormControlStyles {
        override val sizes = object : FormSizesStyles {
            override val small: Style<BasicParams>
                get() = {
                    css("--formControl-vertical-margin: ${Theme().sizes.tiny}")
                    css("--formControl-label-size: ${Theme().fontSizes.small}")
                    css("--formControl-helperText-size: ${Theme().fontSizes.tiny}")
                }
            override val normal: Style<BasicParams>
                get() = {
                    css("--formControl-vertical-margin: ${Theme().sizes.smaller}")
                    css("--formControl-label-size: ${Theme().fontSizes.normal}")
                    css("--formControl-helperText-size: ${Theme().fontSizes.small}")
                }
            override val large: Style<BasicParams>
                get() = {
                    css("--formControl-vertical-margin: ${Theme().sizes.small}")
                    css("--formControl-label-size: ${Theme().fontSizes.large}")
                    css("--formControl-helperText-size: ${Theme().fontSizes.normal}")
                }
        }

        override val label: Style<BasicParams> = {
            css("margin-bottom: var(--formControl-vertical-margin)")
            css("font-size: var(--formControl-label-size) ")
        }

        override val helperText: Style<BasicParams> = {
            css("margin-top: var(--formControl-vertical-margin)")
            css("margin-bottom: var(--formControl-vertical-margin)")
            color { gray600 }
            css("font-size: var(--formControl-helperText-size) ")
            lineHeight { smaller }
        }
    }

    override val formGroup = object : FormGroupStyles {
        override val base: Style<GridParams> = {
            rowGap { normal }
        }

        override val label = object : FormGroupLabelStyles {
            override val placement = "top"

            override val alignmentLabel: BasicParams.(Map<String, String>) -> Unit = { breakpoints ->
                margins(
                    sm = { applyMarginOnPlacementLeftForLabel(breakpoints["sm"], this) },
                    md = { applyMarginOnPlacementLeftForLabel(breakpoints["md"], this) },
                    lg = { applyMarginOnPlacementLeftForLabel(breakpoints["lg"], this) },
                    xl = { applyMarginOnPlacementLeftForLabel(breakpoints["xl"], this) },
                )
            }

            private fun applyMarginOnPlacementLeftForLabel(breakpoint: String?, context: SpacesContext) {
                if (breakpoint != null && breakpoint == "left") {
                    context.apply {
                        top { small }
                        left { small }
                        right { small }
                    }
                }
            }

            override val alignmentLegend: BasicParams.(Map<String, String>) -> Unit = { breakpoints ->
                margins(
                    sm = { applyMarginOnPlacementLeftForLegend(breakpoints["sm"], this) },
                    md = { applyMarginOnPlacementLeftForLegend(breakpoints["md"], this) },
                    lg = { applyMarginOnPlacementLeftForLegend(breakpoints["lg"], this) },
                    xl = { applyMarginOnPlacementLeftForLegend(breakpoints["xl"], this) },
                )
            }

            private fun applyMarginOnPlacementLeftForLegend(breakpoint: String?, context: SpacesContext) {
                if (breakpoint != null && breakpoint == "left") {
                    context.apply {
                        left { small }
                        right { small }
                    }
                }
            }
        }
    }


    override val dropdown: DropdownStyles = object : DropdownStyles {
        override val container: Style<BasicParams> = {
            position(
                sm = { static },
                md = { relative { } }
            )
            display { inlineFlex }
            width { minContent }
        }

        override val dropdown: Style<BasicParams> = {
            width(
                sm = { "100%" },
                md = { maxContent }
            )
            overflow(
                sm = { hidden },
                md = { visible }
            )
            radius { "6px" }

            zIndex { dropdown }
            boxShadow { raised }
            background { color { background } }

            focus {
                css("outline:none")
            }

            // FIXME: Animation not working
            //opacity { "1" }
            //css("transition: opacity 1s ease-in-out;")
        }

        override val placements = object : DropdownPlacements {
            override val left: Style<BasicParams> = {
                position(
                    sm = { absolute { left { "0px" } } },
                    md = {
                        absolute {
                            left { auto }
                            right { "100%" }
                        }
                    }
                )
            }
            override val right: Style<BasicParams> = {
                position(
                    sm = { absolute { left { "0px" } } },
                    md = {
                        absolute {
                            left { "100%" }
                        }
                    }
                )
            }
            override val top: Style<BasicParams> = {
                position(
                    sm = { absolute { left { "0px" } } }
                )
                position(
                    md = {
                        absolute {
                            bottom { "100%" }
                        }
                    }
                )
            }
            override val bottom: Style<BasicParams> = {
                position(
                    sm = { absolute { left { "0px" } } },
                    md = {
                        absolute {
                            top { "100%" }
                        }
                    }
                )
            }
        }
        override val alignments: DropdownAlignments = object : DropdownAlignments {
            override val horizontalStart: Style<BasicParams> = {
                position(
                    md = {
                        absolute {
                            left { "0px" }
                            right { auto }
                        }
                    }
                )
            }
            override val verticalStart: Style<BasicParams> = {
                position(
                    md = {
                        absolute {
                            top { "0px" }
                            bottom { auto }
                        }
                    }
                )
            }
            override val horizontalEnd: Style<BasicParams> = {
                position(
                    md = {
                        absolute {
                            left { auto }
                            right { "0px" }
                        }
                    }
                )
            }
            override val verticalEnd: Style<BasicParams> = {
                position(
                    md = {
                        absolute {
                            top { auto }
                            bottom { "0px" }
                        }
                    }
                )
            }
        }
    }


    override val menu: MenuStyles = object : MenuStyles {

        // base css for all menu children ('entry' uses special styling though)
        private val base: Style<BasicParams> = {
            width { "calc(100% - ${sizes.normal} * 2)" }
            margins {
                horizontal { normal }
                vertical { smaller }
            }
        }

        override val container: Style<BasicParams> = {
            minWidth { "50px" }
            maxWidth { maxContent }
            paddings {
                vertical { smaller }
            }
        }

        override val sub: Style<BoxParams> = {
            paddings {
                left { normal }
            }
        }

        override val entry: Style<BoxParams> = {
            width { full }
            display { flex }
            justifyContent { start }
            margin { auto }
            paddings {
                horizontal { normal }
                vertical { smaller }
            }
            radius { "6px" }
            css("transition: 0.4s")
            css("user-select: none")

            hover {
                background { color { primary.highlight } }
            }

            disabled {
                opacity { "0.4" }
                css("cursor: not-allowed")
            }
        }

        override val header: Style<BasicParams> = {
            base()
            color { secondary.main }
            fontWeight { bold }
            css("white-space: nowrap")
        }

        override val divider: Style<BasicParams> = {
            base()
            height { "1px" }
            background { color { gray300 } }
        }

        override val custom: Style<BasicParams> = {
            base()
        }

        override val icon: Style<BasicParams> = {
            margins { right { smaller } }
        }
    }

    override val navBar: NavBarStyles = object : NavBarStyles {
        override val header: Style<BasicParams> = {
            zIndex { navbar }
            background { color { neutral.main } }
        }
        override val content: Style<BasicParams> = {
            height { "4.5rem" }
            paddings {
                left { larger }
                right { larger }
            }
            borders {
                top {
                    width { "6px" }
                    style { solid }
                    color { primary.main }
                }

                bottom {
                    width { "2px" }
                    style { solid }
                    color { gray300 }
                }
            }
        }
    }

    override val appFrame: AppFrameStyles = object : AppFrameStyles {
        val brandColor: ColorScheme
            get() = colors.primary

        val headerColor: ColorScheme
            get() = colors.primary

        val sidebarColor: ColorScheme
            get() = colors.tertiary

        val mainColor: ColorScheme
            get() = colors.neutral

        val tabsColor: ColorScheme
            get() = colors.neutral

        override val headerHeight: Property = "3.6rem"
        override val complementaryMinHeight: Property = "2.8rem"
        override val mobileSidebarWidth: Property = "85vw"

        override val brand: Style<FlexParams> = {
            background { color { brandColor.main } }
            paddings {
                all { small }
                left { normal }
            }
            color { brandColor.mainContrast }
            alignItems { center }
        }

        override val sidebar: Style<BasicParams> = {
            background { color { sidebarColor.main } }
            color { sidebarColor.mainContrast }
            minWidth { "22vw" }
        }

        override val navigation: Style<BasicParams> = {
            width { full }
            paddings {
                horizontal { smaller }
                vertical { tiny }
            }
        }

        override val complementary: Style<BasicParams> = {
            minHeight { complementaryMinHeight }
            padding { small }
        }

        override val header: Style<FlexParams> = {
            paddings {
                all { small }
                left { normal }
            }
            alignItems { center }
            justifyContent { spaceBetween }
            color { headerColor.mainContrast }
            background { color { headerColor.main } }
        }

        override val main: Style<BasicParams> = {
            padding { normal }
            background { color { mainColor.main } }
            color { mainColor.mainContrast }
        }

        override val tablist: Style<FlexParams> = {
            color { tabsColor.mainContrast }
            background { color { tabsColor.main } }
            borders {
                top {
                    width { "1px " }
                    style { solid }
                    color { gray200 }
                }
            }
            height { complementaryMinHeight }
            padding { tiny }
            children(" > button") {
                flex {
                    grow { "1" }
                    shrink { "1" }
                    basis { auto }
                }
                radius { none }
                height { full }
                padding { none }
            }
            children(" > button:not(:first-child)") {
                borders {
                    left {
                        width { "1px" }
                        color { gray200 }
                    }
                }
            }
        }

        override val backdrop: Style<BasicParams> = {
            opacity { "0" }
            background { color { "rgba(0,0,0,0.8)" } }
            css("transition: opacity .3s ease-in;")
        }
    }


    override val dataTableStyles = object : DataTableStyles {
        val headerColors: ColorScheme
            get() = colors.primary

        /**
         * Semantic: One [ColorScheme] per row:
         *  - base: background of the cell
         *  - baseContrast: text color of the cell
         *  - highlight: background of the row (each cell) that is hovered
         *  - highlightContrast: text color of row (each cell) that is hovered
         *
         *  Use cases:
         *   - alternating (odd - even) rows
         *   - grouping for value based categories (for example different ranges applied for visual analyzing)
         *
         *   Therefore a [List] fits best: Very small overhead, but clear semantics and arbitrary coloring is possible.
         */
        val columnColors: List<ColorScheme>
            get() = listOf(
                colors.tertiary,
                colors.tertiary.inverted()
            )

        val selectionColor: ColorScheme
            get() = colors.secondary

        val hoveringColors: ColorScheme
            get() = colors.tertiary

        private val basic: Style<BasicParams> = {
            paddings {
                vertical { smaller }
                left { smaller }
                right { large }
            }
        }

        override val headerStyle: BasicParams.(sorted: Boolean) -> Unit
            get() = {
                background { color { headerColors.main } }
                color { headerColors.mainContrast }
                verticalAlign { middle }
                fontSize { normal }
                position { relative {} }
                basic()
                borders {
                    right {
                        width { "1px" }
                        style { solid }
                        color { headerColors.mainContrast }
                    }
                }
            }

        override val cellStyle: BasicParams.(
            value: IndexedValue<Any>,
            selected: Boolean,
            sorted: Boolean
        ) -> Unit
            get() = { (index, _), selected, sorted ->
                basic()
                with((index + 1) % 2) {
                    if (selected) {
                        background { color { selectionColor.main } }
                        color { selectionColor.mainContrast }
                    } else {
                        background { color { columnColors[this@with].main } }
                        color { columnColors[this@with].mainContrast }
                    }
                    borders {
                        right {
                            width { "1px" }
                            style { solid }
                            color { columnColors[abs(this@with - 1)].main }
                        }
                    }
                }
                if (sorted) {
                    borders {
                        right {
                            color { headerColors.main }
                            width { normal }
                            style { solid }
                        }
                        left {
                            color { headerColors.main }
                            width { normal }
                            style { solid }
                        }
                    }
                }
            }

        override val hoveringStyle: BasicParams.(
            value: IndexedValue<Any>,
            selected: Boolean,
            sorted: Boolean
        ) -> Unit
            get() = { _, _, _ ->
                color { hoveringColors.mainContrast }
                borders {
                    horizontal {
                        color { hoveringColors.mainContrast }
                        width { thin }
                        style { solid }
                    }
                }
            }

        override val sorterStyle: BasicParams.(sorted: Boolean) -> Unit
            get() = { sorted ->
                display { flex }
                position {
                    absolute {
                        right { "-1.125rem" }
                        top { "calc(50% -15px)" }
                    }
                }
                css("cursor:pointer;")
                if (sorted) {
                    color { headerColors.highlight }
                }
            }
    }

    override val slider = object : SliderStyles {

        val self = this

        override val sizes = object : FormSizesStyles {
            override val large: Style<BasicParams> = {
                css("outline: 0")
                css("--sl-size: 1.5rem")
                css("--sl-track-spacing: 2px")
                fontSize { tiny }
                lineHeight { tiny }
            }
            override val normal: Style<BasicParams> = {
                css("outline: 0")
                css("--sl-size: 1rem")
                css("--sl-track-spacing: 2px")
                fontSize { small }
                lineHeight { small }
            }
            override val small: Style<BasicParams> = {
                css("outline: 0")
                css("--sl-size: 0.75rem")
                css("--sl-track-spacing: 2px")
                fontSize { normal }
                lineHeight { normal }
            }
        }


        override val horizontal = object : SliderCoreStyles {
            override val main: Style<FlexParams> = {
                alignItems { center }
                height { "calc(var(--sl-size) + var(--sl-track-spacing) * 2)" }
                width { full }
                position { relative { } }
                hover {
                    css("cursor: pointer")
                }

                children("&:focus, &[data-focus]") {
                    radius { "9999px" }
                    border {
                        color { gray300 }
                    }
                    boxShadow { outline }
                }
            }
            override val track: Style<BoxParams> = {
                paddings {
                    right { "var(--sl-size)" }
                }
                width { full }
                height { "calc(var(--sl-size) + var(--sl-track-spacing) * 2)" }
                radius { "9999px" }
                background { color { gray300 } }
            }
            override val trackFilled: BoxParams.(Int) -> Unit = { percent ->
                width { "min(calc(${percent}% + var(--sl-size) + var(--sl-track-spacing) * 2), calc(100% + var(--sl-size)))" }
                height { "calc(var(--sl-size) + var(--sl-track-spacing) * 2)" }
                padding { "var(--sl-track-spacing)" }
                radius { "9999px" }
                position { relative { } }
                background { color { primary.main } }
                display { flex }
                justifyContent { flexEnd }
                alignItems { center }
                children("&[data-disabled]") {
                    opacity { ".5" }
                }
            }
            override val thumb: BoxParams.(Int) -> Unit = { _ ->
                display { flex }
                justifyContent { center }
                alignItems { center }
                width { "var(--sl-size)" }
                height { "var(--sl-size)" }
                radius { "9999px" }
                background { color { neutral.main } }
                border { color { gray300 } }
            }
        }

        override val vertical = object : SliderCoreStyles {
            override val main: Style<FlexParams> = {
                self.horizontal.main()
                direction { columnReverse }
                height { full }
                width { "calc(var(--sl-size) + var(--sl-track-spacing) * 2)" }
            }
            override val track: Style<BoxParams> = {
                display { flex }
                alignItems { flexEnd }
                paddings { top { "var(--sl-size)" } }
                height { full }
                width { "calc(var(--sl-size) + var(--sl-track-spacing) * 2)" }
                radius { "9999px" }
                background { color { gray300 } }
            }
            override val trackFilled: BoxParams.(Int) -> Unit = { percent ->
                self.horizontal.trackFilled(this, percent)
                justifyContent { center }
                alignItems { flexStart }
                height { "min(calc(${percent}% + var(--sl-size) + var(--sl-track-spacing) * 2), calc(100% + var(--sl-size)))" }
                width { "calc(var(--sl-size) + var(--sl-track-spacing) * 2)" }
            }
            override val thumb: BoxParams.(Int) -> Unit = { percent ->
                self.horizontal.thumb(this, percent)
            }
        }

        override val severity: SeverityStyles
            get() = input.severity
    }
}