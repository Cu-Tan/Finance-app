package com.fibu.ui.utility

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.fibu.theme.FiBuTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    width: Dp,
    itemHeight: Dp,
    items: List<T>,
    initialItem: T,
    onItemSelection: (T) -> Unit
) {
    val itemHalfHeight = LocalDensity.current.run { itemHeight.toPx() / 2 }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val lastSelectedIndex = remember { mutableIntStateOf(0) }
    LaunchedEffect(items) {
        var targetIndex = items.indexOf(initialItem) - 1
        // I have no idea why items.size does anything here but without it the picker breaks
        targetIndex += (Int.MAX_VALUE / 2 / items.size) * items.size
        lastSelectedIndex.intValue = targetIndex
        listState.scrollToItem(targetIndex)
    }
    LazyColumn(
        modifier = Modifier
            .width(width)
            .height(itemHeight * 3),
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    ) {
        items(Int.MAX_VALUE){ index ->
            val item = items[index % items.size]
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        val y = coordinates.positionInParent().y - itemHalfHeight
                        val parentHalfHeight = (coordinates.parentCoordinates?.size?.height ?: 0) / 2f
                        val isSelected =
                            (y > parentHalfHeight - itemHalfHeight && y < parentHalfHeight + itemHalfHeight)
                        if (isSelected && lastSelectedIndex.intValue != index) {
                            onItemSelection(item)
                            lastSelectedIndex.intValue = index
                        }
                    }
                    .clickable {
                        scope.launch {
                            listState.animateScrollToItem(index - 1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.toString().padStart(2,'0'),
                    color = if (lastSelectedIndex.intValue == index) {
                        FiBuTheme.contentColors.primary
                    } else {
                        Color.DarkGray
                    },
                    fontSize = 16.sp * if (lastSelectedIndex.intValue == index) {
                        1.5f
                    } else {
                        1f
                    }
                )
            }
        }
    }
}