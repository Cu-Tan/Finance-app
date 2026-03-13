package com.fibu.ui.utility.navigation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.fibu.theme.FiBuTheme

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    actionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Surface{
        Layout(
            modifier = modifier,
            contents = listOf(content, actionButton)
        ) { (tabMeasurables, actionButtonMeasurable), constraints ->
            val tabWidth = constraints.maxWidth / (tabMeasurables.size + actionButtonMeasurable.size)
            val actionButtonPlaceable = actionButtonMeasurable.first().measure(
                constraints.copy(
                    minWidth = tabWidth,
                    maxWidth = tabWidth

                )
            )
            val tabPlaceables = tabMeasurables.map { measurable ->
                measurable.measure(
                    constraints.copy(
                        minWidth = tabWidth,
                        maxWidth = tabWidth
                    )
                )
            }.toMutableList()
            val layoutHeight = tabPlaceables.maxOf { it.height }
            // Hardcoded action button placement BAD
            tabPlaceables.add(2, actionButtonPlaceable)
            layout(constraints.maxWidth, layoutHeight) {
                var xPosition = 0
                tabPlaceables.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = 0)
                    xPosition += placeable.width
                }
            }
        }
    }
 }

@Composable
fun NavTab(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .background(FiBuTheme.contentColors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = (if (isSelected) FiBuTheme.contentColors.active else FiBuTheme.contentColors.primary)
        )
        Text(
            text = title,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = (if (isSelected) FiBuTheme.contentColors.active else FiBuTheme.contentColors.primary)
        )
    }
}