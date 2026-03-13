package com.fibu.ui.utility

import android.R
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fibu.logic.info.Category
import com.fibu.theme.FiBuTheme

/**
 * A composable that allows to select items with a checkbox display
 *
 * @param modifier modifier applied to CheckList
 * @param items A list of items that should be displayed in the list
 * @param checkedItems A list of items that should be checked by default. If null is passed all items are checked by default
 * @param content A composable function defining how items should be displayed (checkbox is always there by default)
 * @param onSelection When a check is made it return a list of all checked items
 */
@Composable
fun <T> Checklist(
  modifier: Modifier = Modifier,
  items: List<T>,
  checkedItems: List<T>? = null,
  content: @Composable (T) -> Unit,
  searchStringFun: (T) -> String,
  onSelection: (List<T>) -> Unit = {}
) {

  val density = LocalDensity.current
  val checklistLogic: ChecklistLogic<T> = remember { ChecklistLogic(
    initItems = items,
    initCheckedItems = checkedItems,
    searchStringFun = searchStringFun
  ) }

  val keyboardController = LocalSoftwareKeyboardController.current
  Column{
    Button(
      modifier = modifier.onSizeChanged {
        checklistLogic.setChecklistWidth(
          width = it.width,
          density = density
        )
      },
      onClick = {
        keyboardController?.show()
        checklistLogic.changeExpanded()
      },
      contentAlignment = Alignment.CenterStart
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Selected items: ${checklistLogic.selectedItems.size}",
          color = FiBuTheme.contentColors.primary
        )
      }
      DropdownMenu(
        modifier = Modifier
          .background(FiBuTheme.contentColors.background)
          .width(checklistLogic.checklistWidth),
        expanded = checklistLogic.expanded,
        onDismissRequest = {
          checklistLogic.changeExpanded(false)
          checklistLogic.onClosed()
        }
      ) {
        Column {
          OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = checklistLogic.searchText,
            onValueChange = { newValue ->
              checklistLogic.onSearchTextInput(newValue)
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
            Button(
              modifier = Modifier.fillMaxWidth(),
              contentAlignment = Alignment.CenterStart,
              onClick = {
                checklistLogic.onAllClick()
                onSelection(checklistLogic.selectedItems)
              }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  checked = checklistLogic.checkAll,
                  onCheckedChange = {
                    checklistLogic.onAllClick()
                    onSelection(checklistLogic.selectedItems)
                  }
                )
                Text(
                  text = "All",
                  color = FiBuTheme.contentColors.primary
                )
              }
            }
            checklistLogic.filteredItems.forEach { checkItem ->
              Button(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
                onClick = {
                  keyboardController?.hide()
                  checklistLogic.onSelection(checkItem)
                  onSelection(checklistLogic.selectedItems)
                }
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Checkbox(
                    checked = checkItem.checked,
                    onCheckedChange = {
                      checklistLogic.onSelection(checkItem)
                      onSelection(checklistLogic.selectedItems)
                    }
                  )
                  content(checkItem.item)
                }
              }
            }
          }
        }
      }
    }
  }
}
private class ChecklistLogic<T>(
  initItems: List<T>,
  initCheckedItems: List<T>?,
  val searchStringFun: (T) -> String
) {
  val checklistWidth: Dp
    get() = _checklistWidth.value
  val selectedItems: List<T>
    get() = mutableListOf<T>().apply{
      _items.value.filter { it.checked }.forEach { checkItem ->
        add(checkItem.item)
      }
    }
  val searchText: String
    get() = _searchText.value
  val filteredItems: List<CheckItem<T>>
    get() = _filteredItems.value
  val expanded: Boolean
    get() = _expanded.value
  val checkAll: Boolean
    get() = _checkAll.value
  fun onSearchTextInput(
    input: String
  ) {
    _searchText.value = input
    _filteredItems.value = _items.value.filter { checkItem ->
      searchStringFun(checkItem.item).contains(
        searchText,
        ignoreCase = true
      )
    }.toMutableList()
    _checkAll.value = allItemsSelected()
  }
  fun onAllClick(){
    _checkAll.value = !checkAll
    _filteredItems.value.forEach { it.checked = checkAll }
    _items.value = _items.value
  }
  fun onSelection(
    checkItem: CheckItem<T>
  ) {
    _items.value.find { it == checkItem }?.apply { checked = !checked }
    _checkAll.value = allItemsSelected()
    _items.value = _items.value
  }
  fun changeExpanded(
    value: Boolean? = null
  ) {
    _expanded.value = value ?: !expanded
  }
  fun onClosed(){
    _searchText.value = ""
    _filteredItems.value = _items.value
  }
  fun setChecklistWidth(
    width: Int,
    density: Density
  ) {
    with(density){ _checklistWidth.value = width.toDp() }
  }
  private val _checklistWidth: MutableState<Dp> = mutableStateOf(0.dp)
  private val _items: MutableState<List<CheckItem<T>>> = mutableStateOf(listOf(), neverEqualPolicy())
  private val _searchText: MutableState<String> = mutableStateOf("")
  private val _filteredItems: MutableState<List<CheckItem<T>>> = mutableStateOf(listOf())
  private val _expanded: MutableState<Boolean> = mutableStateOf(false)
  private val _checkAll: MutableState<Boolean> = mutableStateOf(true)
  init {
    val tempItems: MutableList<CheckItem<T>> = mutableListOf()
    initItems.forEach { item ->
      var checked = true
      initCheckedItems?.let { checkedItems ->
        checked = false
        checkedItems.find { it == item }?.also {
          checked = true
        }
      }
      tempItems.add(CheckItem(
        item = item,
        checked = checked
      ))
    }
    _items.value = tempItems
    _filteredItems.value = tempItems
    _checkAll.value = allItemsSelected()
  }
  private fun allItemsSelected(): Boolean {
    var checkedCount = 0
    _filteredItems.value.forEach {
      if(it.checked) { checkedCount++ }
    }
    if(checkedCount == 0){ return false }
    return (checkedCount == _filteredItems.value.size)
  }
}
private data class CheckItem<T>(
  val item: T,
  var checked: Boolean = true
)