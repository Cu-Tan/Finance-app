package com.fibu.ui.utility.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

/**
 * Scaffold creates a space on the screen that displays topBar, mainContent, bottomBar.
 *
 * topBar and bottomBar takes up the minimum required space while mainContent takes up the remainder.
*/
@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    mainContent: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        contents = listOf(topBar, mainContent, bottomBar)
    ){(topBarMeasurables, mainContentMeasurables, bottomBarMeasurables), constraints ->
        val topBarPlaceable = topBarMeasurables.first().measure(constraints.copy(minHeight = 0))
        val bottomBarPlaceable = bottomBarMeasurables.first().measure(constraints.copy(minHeight = 0))
        val remainingHeight = constraints.maxHeight - (topBarPlaceable.height + bottomBarPlaceable.height)
        val mainContentPlaceable = mainContentMeasurables.first().measure(constraints.copy(
            minHeight = remainingHeight,
            maxHeight = remainingHeight
        ))
        layout(constraints.maxWidth, constraints.maxHeight){
            topBarPlaceable.place(
                x = 0,
                y = 0
            )
            mainContentPlaceable.place(
                x = 0,
                y = topBarPlaceable.height
            )
            bottomBarPlaceable.place(
                x = 0,
                y = constraints.maxHeight - bottomBarPlaceable.height
            )
        }
    }
}
