package com.fibu.ui.utility.color

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.fibu.logic.ColorController


@Composable
fun ColorTile(
    modifier: Modifier = Modifier,
    controller: ColorController,
    size: Dp = 50.dp
) {
    var tileSize by remember{
        mutableStateOf(IntSize.Zero)
    }
    Canvas(
        modifier = modifier
            .size(size)
            .border(2.dp, Color.Black)
            .onSizeChanged {
                tileSize = it
            }
    ) {
        drawRect(
            color = controller.selectedColor,
            size = Size(tileSize.height.toFloat(), tileSize.width.toFloat())
        )
    }
}