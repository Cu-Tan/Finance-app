package com.fibu.ui.utility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun <T> DropdownSearchMenu(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    selectedItem: T? = null,
    selectedItemContent: @Composable ((T) -> Unit)? = null,
    menuColor: Color = Color.Transparent,
    searchStringFun: (T) -> String,
    onSelection: (T) -> Unit
) {
    val density = LocalDensity.current
    val dropdownSearchMenuLogic: DropdownSearchMenuLogic<T> = remember {
        DropdownSearchMenuLogic(searchStringFun)
    }
    // Check this LaunchedEffect later
    LaunchedEffect(items) {
        dropdownSearchMenuLogic.initialize(items)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column{
        Button(
            modifier = modifier.onSizeChanged {
                dropdownSearchMenuLogic.setDropdownMenuWidth(
                    width = it.width,
                    density = density
                )
            },
            contentAlignment = Alignment.CenterStart,
            onClick = {
                keyboardController?.show()
                dropdownSearchMenuLogic.changeExpanded()
            }
        ) {
            if (selectedItem == null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
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
                .width(dropdownSearchMenuLogic.dropdownMenuWidth),
            expanded = dropdownSearchMenuLogic.expanded,
            onDismissRequest = {
                dropdownSearchMenuLogic.changeExpanded(false)
                dropdownSearchMenuLogic.onClosed()
            }
        ) {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = dropdownSearchMenuLogic.searchText,
                    onValueChange = { newValue ->
                        dropdownSearchMenuLogic.onSearchTextInput(newValue)
                    },
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        unfocusedTextColor = FiBuTheme.contentColors.primary,
                        focusedTextColor = FiBuTheme.contentColors.primary
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                    },
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        keyboardController?.show()
                                    }
                                }
                            }
                        }
                )
                Column(
                    modifier = Modifier
                        .heightIn(max = 160.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    dropdownSearchMenuLogic.filteredItems.forEach { item ->
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                            onClick = {
                                keyboardController?.hide()
                                onSelection(item)
                                dropdownSearchMenuLogic.onSelection()
                            }
                        ) {
                            itemContent(item)
                        }
                    }
                }
            }
        }
    }
}
private class DropdownSearchMenuLogic<T>(
    val searchStringFun: (T) -> String
) {
    val dropdownMenuWidth: Dp
        get() = _dropdownMenuWidth.value
    val searchText: String
        get() = _searchText.value
    val filteredItems: List<T>
        get() = _filteredItems.value
    val expanded: Boolean
        get() = _expanded.value
    fun initialize(
        initItems: List<T>
    ) {
        items.value = initItems
        _filteredItems.value = initItems
    }
    fun onSearchTextInput(
        input: String
    ) {
        _searchText.value = input
        _filteredItems.value = items.value.filter { item ->
            searchStringFun(item).contains(
                searchText,
                ignoreCase = true
            )
        }.toMutableList()
        if(filteredItems.isEmpty()){
            _filteredItems.value = items.value
        }
    }
    fun onSelection(){
        _expanded.value = false
        onClosed()
    }
    fun changeExpanded(
        value: Boolean? = null
    ) {
        _expanded.value = value ?: !expanded
    }
    fun onClosed(){
        _searchText.value = ""
        _filteredItems.value = items.value
    }
    fun setDropdownMenuWidth(
        width: Int,
        density: Density
    ) {
        with(density){ _dropdownMenuWidth.value = width.toDp() }
    }
    private val _dropdownMenuWidth: MutableState<Dp> = mutableStateOf(0.dp)
    private val _searchText: MutableState<String> = mutableStateOf("")
    private val items: MutableState<List<T>> = mutableStateOf(listOf())
    private val _filteredItems: MutableState<List<T>> = mutableStateOf(listOf())
    private val _expanded: MutableState<Boolean> = mutableStateOf(false)
}