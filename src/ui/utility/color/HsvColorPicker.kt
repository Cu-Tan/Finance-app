package com.fibu.ui.utility.color

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fibu.logic.ColorController
import kotlin.math.min

@Composable
fun HsvColorPicker(
    modifier: Modifier = Modifier,
    initColor: Color = Color.White,
    controller: ColorController,
) {
    val context = LocalContext.current
    var hsvBitmapDrawable: HsvBitmapDrawable? = null
    Canvas(
        modifier = modifier
            .size(250.dp)
            .onSizeChanged { newSize ->
                val size =
                    newSize.takeIf { it.width != 0 && it.height != 0 } ?: return@onSizeChanged
                controller.canvasSize.value = size.also {
                    controller.wheelRadius.intValue = min(size.width, size.height) / 2
                    val x = (it.width / 2).toFloat()
                    val y = (it.height / 2).toFloat()
                    controller.initialize(initColor)
                }
                hsvBitmapDrawable =
                    HsvBitmapDrawable(context.resources, ImageBitmap(size.width, size.height, ImageBitmapConfig.Argb8888).asAndroidBitmap()).apply {
                        setBounds(
                            0,
                            0,
                            size.width,
                            size.height,
                        )
                    }
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    controller.selectColor(it.x, it.y)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    controller.selectColor(change.position.x, change.position.y)
                }
            }
    ) {
        drawIntoCanvas { canvas ->
            hsvBitmapDrawable?.draw(canvas.nativeCanvas)
            drawCircle(controller.wheelColor, radius = 30f, center = controller.pointerLocation)
            drawCircle(Color.Black, radius = 30f, center = controller.pointerLocation, style = Stroke(5f))
        }
    }
}
