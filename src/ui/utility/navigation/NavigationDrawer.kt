package com.fibu.ui.utility.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.zIndex
import com.fibu.theme.FiBuTheme

@Composable
fun NavigationDrawer(
    modifier: Modifier = Modifier,
    state: DrawerState,
    scrimBrush: Brush = SolidColor(FiBuTheme.contentColors.contrast.copy(alpha = 0.7f)),
    onDismiss: () -> Unit,
    drawerContent: @Composable () -> Unit
){
    BackHandler {
        state.close()
    }
    Box(
        modifier = Modifier
            .zIndex(Float.MAX_VALUE)
            .fillMaxSize()
            .background(scrimBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onDismiss()
            }
    )
    Box(
        modifier = modifier
            .zIndex(Float.MAX_VALUE)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {}
    ){
        drawerContent()
    }
}
enum class DrawerStateValue{
    Closed, Open
}
class DrawerState(initialValue: DrawerStateValue = DrawerStateValue.Closed){
    private var currentValue by mutableStateOf(initialValue)
    val isOpen: Boolean
        get() = currentValue == DrawerStateValue.Open
    fun open(){
        currentValue = DrawerStateValue.Open
    }
    fun close(){
        currentValue = DrawerStateValue.Closed
    }
}