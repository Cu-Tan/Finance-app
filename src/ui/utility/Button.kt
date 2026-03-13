package com.fibu.ui.utility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Button(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
){
    Box(
        modifier = modifier
            .clickable(onClick = onClick),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}