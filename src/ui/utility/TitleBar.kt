package com.fibu.ui.utility

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fibu.theme.FiBuTheme
import kotlin.math.max

@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    title: String = "TITLE BAR",
    fontSize: TextUnit = 16.sp,
    titleColor: Color = FiBuTheme.contentColors.primary,
    leftContent: @Composable () -> Unit = {},
    rightContent: @Composable () -> Unit = {},
    mainContent: @Composable () -> Unit = {
        Box(contentAlignment = Alignment.Center){
            Text(
                modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                text = title,
                fontSize = fontSize,
                color = titleColor,
            )
        }
    }
) {
    Layout(
        modifier = modifier
            .padding(6.dp),
        contents = listOf(leftContent, mainContent, rightContent)
    ) { (leftContentMeasurables, mainContentMeasurables, rightContentMeasurables), constraints ->
        val lrConstraints = constraints.copy(
            minWidth = 0,
            maxWidth = constraints.maxWidth / 3,
            minHeight = 0,
        )
        val leftContentPlaceable = leftContentMeasurables.map { it.measure(lrConstraints) }
        val lWidth = leftContentPlaceable.firstOrNull()?.width ?: 0
        val rightContentPlaceable = rightContentMeasurables.map { it.measure(lrConstraints) }
        val rWidth = leftContentPlaceable.firstOrNull()?.width ?: 0
        val mainContentPlaceable = mainContentMeasurables.map { it.measure(constraints.copy(
            maxWidth = constraints.maxWidth - (lWidth + rWidth),
            minHeight = 0
        )) }
        val titleBarHeight =
            max(if (mainContentPlaceable.isEmpty()) 0 else mainContentPlaceable.first().height,
                max(if (leftContentPlaceable.isEmpty()) 0 else leftContentPlaceable.first().height,
                    if (rightContentPlaceable.isEmpty()) 0 else rightContentPlaceable.first().height))
        layout(width = constraints.maxWidth, height = titleBarHeight){
            leftContentPlaceable.forEach {
                val lcyPosition = (titleBarHeight - it.height) / 2
                it.place(
                    x = 0,
                    y = lcyPosition
                )
            }
            mainContentPlaceable.forEach {
                val mcxPosition = (constraints.maxWidth - it.width) / 2
                val mcyPosition = (titleBarHeight - it.height) / 2
                it.place(
                    x = mcxPosition,
                    y = mcyPosition
                )
            }
            rightContentPlaceable.forEach {
                val rcxPosition = constraints.maxWidth - it.width
                val rcyPosition = (titleBarHeight - it.height) / 2
                it.place(
                    x = rcxPosition,
                    y = rcyPosition
                )
            }
        }
    }
}