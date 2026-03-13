package com.fibu.ui.utility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fibu.theme.FiBuTheme

@Composable
fun <T> DropdownMenu(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    selectedItem: T? = null,
    selectedItemContent: @Composable ((T) -> Unit)? = null,
    menuColor: Color = Color.Transparent,
    onSelection: (T) -> Unit
) {
    val density = LocalDensity.current
    val dropdownMenuLogic: DropdownMenuLogic<T> = remember { DropdownMenuLogic() }
    // Check this LaunchedEffect later
    LaunchedEffect(items) {
        dropdownMenuLogic.initialize(items)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier.onSizeChanged {
            dropdownMenuLogic.setDropdownMenuWidth(
                width = it.width,
                density = density
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            modifier = modifier,
            contentAlignment = Alignment.TopStart,
            onClick = {
                keyboardController?.show()
                dropdownMenuLogic.changeExpanded()
            }
        ) {
            if (selectedItem == null) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Click Here",
                        color = FiBuTheme.contentColors.primary
                    )
                }
            } else {
                selectedItemContent?.let {
                    it(selectedItem)
                } ?: itemContent(selectedItem)
            }
        }
        DropdownMenu(
            modifier = Modifier
                .background(menuColor)
                .heightIn(max = 240.dp)
                .width(dropdownMenuLogic.dropdownMenuWidth),
            expanded = dropdownMenuLogic.expanded,
            onDismissRequest = {
                dropdownMenuLogic.changeExpanded(false)
            }
        ) {
            dropdownMenuLogic.items.forEach { item ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                    onClick = {
                        keyboardController?.hide()
                        onSelection(item)
                        dropdownMenuLogic.onSelection()
                    }
                ) {
                    itemContent(item)
                }
            }
        }
    }
}
private class DropdownMenuLogic<T> {
    val dropdownMenuWidth: Dp
        get() = _dropdownMenuWidth.value
    val items: List<T>
        get() = _items.value
    val expanded: Boolean
        get() = _expanded.value
    fun initialize(
        initItems: List<T>
    ) {
        _items.value = initItems
    }
    fun onSelection(){
        changeExpanded(false)
    }
    fun changeExpanded(
        value: Boolean? = null
    ) {
        _expanded.value = value ?: !expanded
    }
    fun setDropdownMenuWidth(
        width: Int,
        density: Density
    ) {
        with(density){ _dropdownMenuWidth.value = width.toDp() }
    }
    private val _dropdownMenuWidth: MutableState<Dp> = mutableStateOf(0.dp)
    private val _items: MutableState<List<T>> = mutableStateOf(listOf())
    private val _expanded: MutableState<Boolean> = mutableStateOf(false)
}