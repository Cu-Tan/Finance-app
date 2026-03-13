package com.fibu.ui.utility.color

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.fibu.logic.ColorController

private const val thumbRadius = 20f
private val bgColor = Color.Black
@Composable
fun ColorSlideBar(
    modifier: Modifier = Modifier,
    controller: ColorController
) {
    var progress by remember{
        mutableFloatStateOf(controller.brightness)
    }
    var slideBarSize by remember{
        mutableStateOf(IntSize.Zero)
    }
    LaunchedEffect(key1 = progress) {
        controller.setColorBrightness(progress)
        controller.setBrightness(progress)
    }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .onSizeChanged {
                slideBarSize = it
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    progress = (it.x / slideBarSize.width).coerceIn(0f, 1f)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    progress = (change.position.x / slideBarSize.width).coerceIn(0f, 1f)
                }
            }
            .clip(RoundedCornerShape(4.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
    ) {
        drawRect(
            Brush.horizontalGradient(
            colors = listOf(bgColor, controller.wheelColor),
        ))
        drawCircle(
            color = Color.White,
            radius = thumbRadius,
            center = Offset(
                (thumbRadius / 2) + (slideBarSize.width - 2 * thumbRadius) * progress,
                size.height / 2
            )
        )
        drawCircle(
            color = Color.Black,
            radius = thumbRadius,
            center = Offset(
                (thumbRadius / 2) + (slideBarSize.width - 2 * thumbRadius) * progress,
                size.height / 2
            ),
            style = Stroke(4f)
        )
    }
}