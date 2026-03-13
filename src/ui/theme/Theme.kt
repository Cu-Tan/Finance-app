package com.fibu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ColorSet(
    val active: Color,
    val primary: Color,
    val background: Color,
    val contrast: Color
)
data class ButtonDefaultColors(
    val primary: Color,
    val secondary: Color
)
val LocalContentColors = staticCompositionLocalOf {
    ColorSet(
        Color.Unspecified,
        Color.Unspecified,
        Color.Unspecified,
        Color.Unspecified
    )
}
val LocalButtonDefaultColors = staticCompositionLocalOf {
    ButtonDefaultColors(
        Color.Unspecified,
        Color.Unspecified
    )
}

@Composable
fun FiBuTheme(
    content: @Composable () -> Unit
) {
    val contentColors = ColorSet(
        Color(188, 132, 255,255),
        Color(170,170,170,255),
        Color(46, 46, 46,255),
        Color(33, 33, 33, 255)
    )
    val buttonDefaultColors = ButtonDefaultColors(
        Color(188, 132, 255,255),
        Color(60,50,80,255)
    )
    CompositionLocalProvider(
        LocalContentColors provides contentColors,
        LocalButtonDefaultColors provides buttonDefaultColors,
        content = content
    )
}
object FiBuTheme{
    val contentColors: ColorSet
        @Composable
        get() = LocalContentColors.current
    val buttonDefaultColors: ButtonDefaultColors
        @Composable
        get() = LocalButtonDefaultColors.current
}