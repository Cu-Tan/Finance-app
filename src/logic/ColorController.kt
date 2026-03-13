package com.fibu.logic

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class ColorController{
    val selectedColor: Color
        get() = _selectedColor.value
    val pointerLocation: Offset
        get() = _pointerLocation.value
    val wheelColor: Color
        get() = _wheelColor.value
    val brightness: Float
        get() = _brightness.floatValue
    internal val canvasSize = mutableStateOf(IntSize(0, 0))
    internal val wheelRadius = mutableIntStateOf(0)
    fun setBrightness(
        brightness: Float
    ) { _brightness.floatValue = brightness}
    fun setColorBrightness(
        brightness: Float
    ) {
        val maxValue = maxOf(selectedColor.red, selectedColor.blue, selectedColor.green)

        val scale = brightness / maxValue
        val r = ((selectedColor.red * scale)*255).toInt()
        val g = ((selectedColor.green * scale)*255).toInt()
        val b = ((selectedColor.blue * scale)*255).toInt()
        _selectedColor.value = Color(r, g, b)
    }
    fun selectColor(
        x: Float,
        y: Float
    ) {
        val dx = x - wheelRadius.intValue
        val dy = y - wheelRadius.intValue
        val radius = wheelRadius.intValue
        val checkRadius = sqrt(dx.pow(2) + dy.pow(2))
        val angle = atan2(dy, dx) * (180 / Math.PI).toFloat()
        val hue = if(angle < 0) angle + 360 else angle
        if (checkRadius <= radius){
            _pointerLocation.value = Offset(
                dx + radius,
                dy + radius
            )
            _selectedColor.value = Color.HSVToColor(hue, checkRadius / radius, brightness)
            _wheelColor.value = Color.HSVToColor(hue, checkRadius / radius, 1f)
        } else {
            _pointerLocation.value = Offset(
                dx * (radius / checkRadius) + radius,
                dy * (radius / checkRadius) + radius
            )
            _selectedColor.value = Color.HSVToColor(hue, 1f, brightness)
            _wheelColor.value = Color.HSVToColor(hue, 1f, 1f)
        }
    }
    fun initialize(
        initColor: Color
    ) {
        _selectedColor.value = initColor
        val (h, s, v) = initColor.toHSV()
        _pointerLocation.value = Offset(
            x = (cos(h*(Math.PI / 180f)) * s * wheelRadius.intValue).toFloat() + wheelRadius.intValue,
            y = (sin(h*(Math.PI / 180f)) * s * wheelRadius.intValue).toFloat() + wheelRadius.intValue
        )
        _brightness.floatValue = v
        _wheelColor.value = Color.HSVToColor(h, s, 1f)
    }
    private val _selectedColor: MutableState<Color> = mutableStateOf(Color.Transparent)
    private var _pointerLocation: MutableState<Offset> = mutableStateOf(Offset(0f, 0f), neverEqualPolicy())
    private val _wheelColor: MutableState<Color> = mutableStateOf(Color.Transparent)
    private val _brightness = mutableFloatStateOf(1f)
}