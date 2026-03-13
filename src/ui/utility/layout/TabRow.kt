package com.fibu.ui.utility.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { contentMeasurables, constrains ->
        val contentWidth = constrains.maxWidth / contentMeasurables.size
        val contentPlaceables = contentMeasurables.map {
            it.measure(
                constrains.copy(
                    minWidth = contentWidth,
                    maxWidth = contentWidth
                )
            )
        }
        val layoutHeight = contentPlaceables.first().height
        layout(constrains.maxWidth, layoutHeight){
            var xPosition = 0
            contentPlaceables.forEach {
                it.place(
                    x = xPosition,
                    y = (layoutHeight - it.height) / 2
                )
                xPosition += it.width
            }
        }
    }
}