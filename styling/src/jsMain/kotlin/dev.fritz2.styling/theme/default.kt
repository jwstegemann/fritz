package dev.fritz2.styling.theme

import dev.fritz2.styling.params.*
import dev.fritz2.styling.params.BackgroundAttachments.inherit
import dev.fritz2.styling.theme.icons.MonoIcons

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

            override val primary = // blue
                ColorScheme(base = "#0C5173", baseContrast = gray100, highlight = "#CAE4EA", highlightContrast = gray600)

            override val secondary = // yellow
                ColorScheme(base = "#E6A300", baseContrast = gray100, highlight = "#FFEDCB", highlightContrast = gray600)

            override val tertiary = // grey
                ColorScheme(base = gray600, baseContrast = gray100, highlight = gray300, highlightContrast = gray600)

            override val info = "#219EBC"       // blue
            override val success = "#00A848"    // green
            override val warning = "#F08B3A"    // orange
            override val danger = "#E14F2A"     // red
            override val neutral = "#ffffff"    // white

            override val disabled = gray300
            override val focus = primary.highlight
        }

    //FIXME: move to typography section

    override val backgroundColor
        get() = colors.neutral

    override val fontColor
        get() = colors.gray700

    override val fontFamilies = object : FontFamilies {
        override val normal =
            "Inter, 'Segoe UI', Roboto, Helvetica, Calibri, Arial, 'Apple Color Emoji', 'Segoe UI Emoji', -apple-system, sans-serif"
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
            danger = shadow("0", "0", "0", "1px", color = colors.danger)
        )

    override val zIndices = ZIndices(1, 100, 2, 200, 300, 2, 400, 2)

    override val opacities = WeightedValue(
        normal = "0.5"
    )

    //from modern-normalize v1.0.0 | MIT License | https://github.com/sindresorhus/modern-normalize
    override val reset: String by lazy {
        """*,::after,::before{box-sizing:border-box}:root{-moz-tab-size:4;tab-size:4}html{line-height:1.15;-webkit-text-size-adjust:100%}body{margin:0}body{font-family:system-ui,-apple-system,'Segoe UI',Roboto,Helvetica,Arial,sans-serif,'Apple Color Emoji','Segoe UI Emoji'}hr{height:0;color:inherit}abbr[title]{text-decoration:underline dotted}b,strong{font-weight:bolder}code,kbd,pre,samp{font-family:ui-monospace,SFMono-Regular,Consolas,'Liberation Mono',Menlo,monospace;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit}button,input,optgroup,select,textarea{font-family:inherit;font-size:100%;line-height:1.15;margin:0}button,select{text-transform:none}[type=button],[type=reset],[type=submit],button{-webkit-appearance:button}::-moz-focus-inner{border-style:none;padding:0}:-moz-focusring{outline:1px dotted ButtonText}:-moz-ui-invalid{box-shadow:none}legend{padding:0}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}"""
    }

    // extra css for general layout by using theme props
    override val global: String by lazy { """
        html {
            line-height: ${lineHeights.large};
            -webkit-text-size-adjust: 100%;
            -webkit-font-smoothing: antialiased;
            text-rendering: optimizelegibility;
        }
            
        body {
            color: ${fontColor};
            font-family: ${fontFamilies.normal};
            background-color: ${backgroundColor};
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

    override val icons = MonoIcons()

    override val input = object : InputFieldStyles {
        override val sizes = object : FormSizes {
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
                        color { neutral }
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
                        color { primary.base }
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
                color { neutral }

                hover {
                    background { color { gray200 } }
                    color { gray700 }
                }

                focus {
                    zIndex { "1" }
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
                get() = basic(colors.danger, shadows.danger)
        }
    }

    override val checkbox = object : CheckboxStyles {
        override val sizes = object : FormSizes {
            private val basic: Style<BasicParams> = {
                display { inlineFlex }
                css("align-items: center;")
            }
            override val small: Style<BasicParams> = {
                basic()
                css("--cb-size: .75rem")
                css("--cb-svg-size: .50rem")
                css("--cb-radius:  ${radii.smaller}")
                fontSize { small }
                lineHeight { small }
                margins { right { tiny } }
            }
            override val normal: Style<BasicParams> = {
                basic()
                css("--cb-size: 1.0rem")
                css("--cb-svg-size: .75rem")
                css("--cb-radius:  ${radii.small}")
                fontSize { normal }
                lineHeight { normal }
                margins { right { smaller } }
            }
            override val large: Style<BasicParams> = {
                basic()
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
            background { color { neutral } }
            border {
                width { "1px" }
                style { solid }
                color { gray300 }
            }
            radius { "var(--cb-radius)" }
        }
        override val checked: Style<BasicParams> = {
            background { color { primary.base } }
            border { color { primary.base } }
            color { neutral }
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
                get() = apply(colors.danger, colors.warning)
        }
    }

    override val radio = object : RadioStyles {
        override val sizes = object : FormSizes {
            private val basic: Style<BasicParams> = {
                display { inlineFlex }
                css("align-items: center;")
            }
            override val small: Style<BasicParams> = {
                basic()
                css("--rb-size: .75rem")
                fontSize { small }
                lineHeight { small }
                margins { right { tiny } }
            }
            override val normal: Style<BasicParams> = {
                basic()
                css("--rb-size: 1.0rem")
                fontSize { normal }
                lineHeight { normal }
                margins { right { smaller } }
            }
            override val large: Style<BasicParams> = {
                basic()
                css("--rb-size: 1.5rem")
                fontSize { larger }
                lineHeight { larger }
                margins { right { small } }
            }
        }
        override val input: Style<BasicParams> = {
            children("&:focus + div") {
                border {
                    color { primary.base }
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
            css("align-items:center;")
            css("justify-content:center;")
            width { "var(--rb-size)" }
            height { "var(--rb-size)" }
            background { color { neutral } }
            border {
                width { "2px" }
                style { solid }
                color { gray300 }
            }
            radius { "9999px" }
        }
        override val selected: Style<BasicParams> = {
            background { color { primary.base } }
            color { gray300 }
            border {
                color { primary.base }
            }
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
                    color { neutral }
                }
            }
        }

        override val severity: SeverityStyles
            get() = checkbox.severity
    }

    override val switch = object : SwitchStyles {
        override val sizes = object : FormSizes {
            private val basic: Style<BasicParams> = {
                display { inlineFlex }
                css("align-items: center;")
            }
            override val small: Style<BasicParams> = {
                basic()
                css("--sw-width: 1.35rem")
                css("--sw-height: .75rem")
                fontSize { small }
                lineHeight { small }
                margins { right { tiny } }
            }
            override val normal: Style<BasicParams> = {
                basic()
                css("--sw-width: 1.85rem")
                css("--sw-height: 1rem")
                fontSize { normal }
                lineHeight { normal }
                margins { right { smaller } }
            }
            override val large: Style<BasicParams> = {
                basic()
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
                color { neutral }
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
            background { color { primary.base } }
        }

        override val severity: SeverityStyles
            get() = checkbox.severity
    }

    override val button = object : PushButtonStyles {
        override val variants = object : PushButtonVariants {
            private val basic: Style<BasicParams> = {
                lineHeight { smaller }
                radius { normal }
                fontWeight { semiBold }
                focus {
                    boxShadow { outline }
                }
            }

            override val solid: Style<BasicParams> = {
                basic()
                background { color { "var(--main-color)" } }
                color { neutral }
                hover {
                    css("filter: brightness(80%);")
                }
                active {
                    css("filter: brightness(120%);")
                }
            }

            override val outline: Style<BasicParams> = {
                basic()
                color { "var(--main-color)" }
                border {
                    width { thin }
                    style { solid }
                    color { "var(--main-color)" }
                }
                hover {
                    background {
                        color { gray200 }
                    }
                    css("filter: brightness(80%);")
                }
            }

            override val ghost: Style<BasicParams> = {
                basic()
                color { "var(--main-color)" }
            }

            override val link: Style<BasicParams> = {
                basic()
                paddings { all { none } }
                height { auto }
                lineHeight { normal }
                color { "var(--main-color)" }
                hover {
                    textDecoration { underline }
                }
                active {
                    css("filter: brightness(120%);")
                }
            }
        }

        override val sizes = object : FormSizes {
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

        override val sizes = object : ModalSizes {

            private val basic: Style<BasicParams> = {
                background {
                    color { neutral }
                }
                padding { normal }
                radius { tiny }
                boxShadow { raisedFurther }
            }

            override val full: Style<BasicParams> = {
                basic()
                width { "100%" }
                height { "100%" }
                position {
                    fixed {
                        horizontal { "0" }
                        vertical { "0" }
                    }
                }
            }

            override val large: Style<BasicParams> = {
                basic()
                position {
                    fixed {
                        left { "var(--main-level)" }
                        top { "var(--main-level)" }
                        right { normal }
                    }
                }
                minHeight { wide.smaller }
            }

            override val normal: Style<BasicParams> = {
                basic()
                position {
                    fixed {
                        top { "var(--main-level)" }
                        left { "50%" }
                    }
                }
                minHeight { wide.smaller }
                minWidth { "50%" }
                css("transform: translateX(-50%);")
            }

            override val small: Style<BasicParams> = {
                basic()
                position {
                    fixed {
                        top { "var(--main-level)" }
                        left { "65%" }
                        bottom { normal }
                    }
                }
                minHeight { wide.smaller }
                minWidth { "35%" }
                css("transform: translateX(-90%);")
            }
        }

        override val variants = object : ModalVariants {
            override val auto: Style<BasicParams> = {
                position {
                    fixed {
                        bottom { auto }
                    }
                }
            }

            override val verticalFilled: Style<BasicParams> = {
                position {
                    fixed {
                        bottom { normal }
                    }
                }
            }

            override val centered: Style<BasicParams> = {
                position {
                    fixed {
                        top { "50%" }
                    }
                }
                // FIXME: does not work! overrides size-settings!
                css("transform: translateY(-50%);")
            }
        }
    }

    override val popover = object : PopoverStyles {
        override val size: PopoverSizes = object : PopoverSizes {
            private val basic: Style<BasicParams> = {
                background {
                    color { neutral }
                }
                paddings {
                    top { tiny }
                    bottom { tiny }
                    left { small }
                    right { small }
                }
                radius { small }
                boxShadow { flat }
                zIndex { "20" }

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
            display { "inline-block" }
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
                zIndex { "50" }
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
                    zIndex { "-1" }
                    css("content:\"\";")
                    width { "1rem" }
                    height { "1rem" }
                    position {
                        absolute {
                        }
                    }
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
                    zIndex { "20" }
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

        override val sizes = object : FormSizes {
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

        override val severity: SeverityStyles
            get() = input.severity
    }

    override val alert: AlertStyles = object : AlertStyles {
        override val severities: AlertSeverities
            get() = object : AlertSeverities {
                override val info: AlertSeverity = object : AlertSeverity {
                    override val color = colors.info
                    override val icon = icons.circleInformation
                }
                override val success: AlertSeverity = object : AlertSeverity {
                    override val color = colors.success
                    override val icon = icons.circleCheck
                }
                override val warning: AlertSeverity = object : AlertSeverity {
                    override val color = colors.warning
                    override val icon = icons.circleWarning
                }
                override val error: AlertSeverity = object : AlertSeverity {
                    override val color = colors.danger
                    override val icon = icons.circleError
                }
            }

        override val variants: AlertVariants = object : AlertVariants {
            /*
            TODO: Use text colors from theme
             */
            private val textColorDark = rgb(0, 0, 0)
            private val textColorLight = rgb(255, 255, 255)

            override val subtle: AlertVariantStyleFactory = { it ->
                object : AlertVariantStyles {
                    override val background: Style<BasicParams> = {
                        background { color { alterHexColorBrightness(it, 1.5) } }
                    }
                    override val text: Style<BasicParams> = {
                        color { textColorDark }
                    }
                    override val accent: Style<BasicParams> = {
                        color { it }
                    }
                    override val decorationLeft: Style<BasicParams> = {
                        css("visibility: hidden")
                    }
                    override val decorationTop: Style<BasicParams> = {
                        css("visibility: hidden")
                    }
                }
            }
            override val solid: AlertVariantStyleFactory = {
                object : AlertVariantStyles {
                    override val background: Style<BasicParams> = {
                        background { color { it } }
                    }
                    override val text: Style<BasicParams> = {
                        color { textColorLight }
                    }
                    override val accent: Style<BasicParams> = {
                        color { textColorLight }
                    }
                    override val decorationLeft: Style<BasicParams> = {
                        css("visibility: hidden")
                    }
                    override val decorationTop: Style<BasicParams> = {
                        css("visibility: hidden")
                    }
                }
            }
            override val leftAccent: AlertVariantStyleFactory = {
                object : AlertVariantStyles {
                    override val background: Style<BasicParams> = {
                        background { color { alterHexColorBrightness(it, 1.5) } }
                    }
                    override val text: Style<BasicParams> = {
                        color { textColorDark }
                    }
                    override val accent: Style<BasicParams> = {
                        color { it }
                    }
                    override val decorationLeft: Style<BasicParams> = {
                        background { color { it } }
                    }
                    override val decorationTop: Style<BasicParams> = {
                        css("visibility: hidden")
                    }
                }
            }
            override val topAccent: AlertVariantStyleFactory = {
                object : AlertVariantStyles {
                    override val background: Style<BasicParams> = {
                        background { color { alterHexColorBrightness(it, 1.5) } }
                    }
                    override val text: Style<BasicParams> = {
                        color { textColorDark }
                    }
                    override val accent: Style<BasicParams> = {
                        color { it }
                    }
                    override val decorationLeft: Style<BasicParams> = {
                        css("visibility: hidden")
                    }
                    override val decorationTop: Style<BasicParams> = {
                        background { color { it } }
                    }
                }
            }

            override val discreet: AlertVariantStyleFactory = {
                object : AlertVariantStyles {
                    override val background: Style<BasicParams> = {
                        background { inherit }
                    }
                    override val text: Style<BasicParams> = {
                        color { it }
                    }
                    override val accent: Style<BasicParams> = {
                        color { it }
                    }
                    override val decorationLeft: Style<BasicParams> = {
                        css("visibility: hidden")
                    }
                    override val decorationTop: Style<BasicParams> = {
                        css("visibility: hidden")
                    }
                }
            }
        }

        override val sizes = object : FormSizes {
            override val small: Style<BasicParams> = {
                css("--al-icon-margin: 0.25rem")
                css("--al-icon-size: ${Theme().fontSizes.small}")
                fontSize { small }
                lineHeight { small }
            }
            override val normal: Style<BasicParams> = {
                css("--al-icon-margin: 0.5rem")
                css("--al-icon-size: ${Theme().fontSizes.normal}")
                fontSize { normal }
                lineHeight { normal }
            }
            override val large: Style<BasicParams> = {
                css("--al-icon-margin: 1rem")
                css("--al-icon-size: ${Theme().fontSizes.larger}")
                fontSize { larger }
                lineHeight { larger }
            }
        }

        override val stacking = object : AlertStacking {
            override val compact: Style<BasicParams> = {
                margin { "0" }
            }
            override val separated: Style<BasicParams> = {
                margin { normal }
            }
        }
    }

    override val toast = object : ToastStyles {
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
        override val status = object : ToastStatus {
            override val success: Style<BasicParams> = {

                background { color { success } }
            }

            override val error: Style<BasicParams> = {
                background { color { danger } }
            }
            override val warning: Style<BasicParams> = {
                background { color { warning } }
            }
            override val info: Style<BasicParams> = {
                background { color { info } }
            }

        }
        override val closeButton = object : ToastButton {
            override val close: Style<BasicParams> = {
                radius { "0.375rem" }
                width { "24px" }
                height { "1rem" }
                fontSize { "10px" }
                css("outline: 0px;")
                flex { shrink { "0" } }
                display { flex }
                css("align-items: center;")
                css("justify-content: center;")
                css("transition: all 0.2s ease 0s;")
                paddings { left { "1rem" } }
                focus {
                    css("outline: none;")
                    boxShadow { none }
                }
            }
        }
    }

    override val select = object : SelectFieldStyles {

        override val variants = object : SelectFieldVariants {
            override val outline: Style<BasicParams> = {
                border {
                    width { thin }
                    style { solid }
                    color { gray300 }
                }
            }

            override val filled: Style<BasicParams> = {
                background {
                    color { gray300 }
                }

                hover {
                    css("filter: brightness(90%);")
                }

                focus {
                    zIndex { "1" }
                    background {
                        color { "transparent" }
                    }
                }
            }
        }

        override val sizes = object : FormSizes {
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
        override val sizes = object : FormSizes {
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

    override val appFrame: AppFrameStyles = object : AppFrameStyles {
        override val headerHeight: Property = "3.6rem"
        override val footerMinHeight: Property = "2.8rem"
        override val mobileSidebarWidth: Property = "85vw"

        override val brand: Style<FlexParams> = {
            //background { color { "rgb(44, 49, 54)"} }
            background { color { primary.base } }
            paddings {
                all { small }
                left { normal }
            }
            color { gray200 }
            alignItems { center }
            borders {
                bottom {
                    width { "1px " }
                    color { gray400 }
                }
            }
        }

        override val sidebar: Style<BasicParams> = {
//            css(
//                sm = "background: linear-gradient(0deg, ${Theme().colors.dark} 0%, ${Theme().colors.primary.base} 20%);",
//                lg = "background: linear-gradient(0deg, ${Theme().colors.dark} 0%, ${Theme().colors.primary.base} 20%);"
//            )
            background { color { primary.base } }
            color { gray200 }
            minWidth { "22vw" }
        }

        override val nav: Style<BasicParams> = {
            paddings {
                top { tiny }
            }
        }

        override val footer: Style<BasicParams> = {
            minHeight { footerMinHeight }
            padding { small }
            borders {
                top {
                    width { "1px" }
                    color { gray400 }
                }
            }
        }

        override val header: Style<FlexParams> = {
            paddings {
                all { small }
                left { normal }
            }
            alignItems { center }
            justifyContent { spaceBetween }
            color { "rgb(44, 49, 54)" }
            borders {
                bottom {
                    width { "1px " }
                    style { solid }
                    color { gray200 }
                }
            }
        }

        override val main: Style<BasicParams> = {
            padding { normal }
            background { color { gray100 } }
            color { "rgb(44, 49, 54)" }
        }

        override val tabs: Style<FlexParams> = {
            borders {
                top {
                    width { "1px " }
                    style { solid }
                    color { gray200 }
                }
            }
            height { footerMinHeight }
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

        override val navLink: Style<FlexParams> = {
            css("cursor: pointer;")
            paddings {
                vertical { "0.6rem" }
                horizontal { small }
            }
            alignItems { AlignItemsValues.center }
            borders {
                left {
                    width { "0.2rem" }
                    color { "transparent" }
                }
            }
            children(" .icon") {
                size { large }
                margins {
                    left { tiny }
                }
            }
            children(" a") {
                display { block }
                fontWeight { "500" }
                fontSize { ".9rem" }
            }
        }

        override val activeNavLink: Style<FlexParams> = {
            background { color { "rgba(0,0,0,0.2)" } }
            borders {
                left {
                    color { gray300.important }
                }
            }
        }

        override val navSection: Style<BasicParams> = {
            paddings {
                vertical { "0.5rem" }
                horizontal { small }
            }
            margins { top { small } }
            textTransform { uppercase }
            fontWeight { semiBold }
            fontSize { ".8rem" }
            color { gray400 }
        }
    }
}