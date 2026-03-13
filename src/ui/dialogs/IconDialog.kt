package com.fibu.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fibu.R
import com.fibu.logic.info.Icon
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.ButtonRow
import com.fibu.ui.utility.ButtonRowItem
import java.lang.reflect.Field

@Composable
fun IconDialog(
  initIcon: Int,
  selectedColor: Color,
  onDismiss: () -> Unit,
  onConfirm: (Int) -> Unit
) {
  val iconDialogLogic = remember { IconDialogLogic() }
  val keyboardController = LocalSoftwareKeyboardController.current
  LaunchedEffect(initIcon) { iconDialogLogic.initialize(initIcon) }
  Dialog(onDismissRequest = onDismiss) {
    Column(
      modifier = Modifier
        .background(FiBuTheme.contentColors.background)
        .padding(6.dp)
    ) {
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = iconDialogLogic.filterText,
        onValueChange = { newValue ->
          iconDialogLogic.onFilterTextInput(newValue)
        },
        colors = OutlinedTextFieldDefaults.colors().copy(
          unfocusedTextColor = FiBuTheme.contentColors.primary,
          focusedTextColor = FiBuTheme.contentColors.primary
        ),
        leadingIcon = {
          Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null
          )
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
      Spacer(modifier = Modifier.height(4.dp))
      LazyVerticalGrid(columns = GridCells.Fixed(6)) {
        items(iconDialogLogic.filteredIcons){ icon ->
          Button(
            modifier = Modifier
              .aspectRatio(1f)
              .background(
                color = if (iconDialogLogic.selectedIcon == icon) selectedColor else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
              ),
            onClick = {
              keyboardController?.hide()
              iconDialogLogic.onIconSelected(icon)
            }
          ) {
            Icon(
              painter = painterResource(id = icon),
              contentDescription = null,
              tint = Color.White
            )
          }
        }
      }
      ButtonRow(buttons = listOf(
        ButtonRowItem(
          text = "Cancel",
          onClick = onDismiss
        ),
        ButtonRowItem(
          text = "Confirm",
          onClick = { onConfirm(iconDialogLogic.selectedIcon) }
        )
      ))
    }
  }
}
private class IconDialogLogic{
  val icons: List<Icon> = getAllIcons()
  // Private logic states
  private val _filterText: MutableState<String> = mutableStateOf("")
  private val _filteredIcons: MutableState<List<Int>> = mutableStateOf(icons.map { it.iconID })
  private val _selectedIcon: MutableIntState = mutableIntStateOf(icons.firstOrNull()?.iconID ?: R.drawable.unknown_category_icon)
  // UI states
  val filterText: String get() = _filterText.value
  val filteredIcons: List<Int> get() = _filteredIcons.value
  val selectedIcon: Int get() = _selectedIcon.intValue
  fun initialize(
    initIcon: Int
  ) {
    _selectedIcon.intValue = initIcon
  }
  fun onFilterTextInput(
    input: String
  ) {
    _filterText.value = input
    _filteredIcons.value = icons.filter {
      it.name.contains(
        _filterText.value,
        ignoreCase = true
      )
    }.map { it.iconID }
    if(_filteredIcons.value.isEmpty()){
      _filteredIcons.value = icons.map { it.iconID }
    }
  }
  fun onIconSelected(
    icon: Int
  ) {
    _selectedIcon.intValue = icon
  }
  private fun getAllIcons(): List<Icon> {
    val icons = mutableListOf<Icon>()
    try {
      val drawableClass = R.drawable::class.java
      val fields: Array<Field> = drawableClass.declaredFields
      for (field in fields){
        if(field.type == Int::class.javaPrimitiveType && field.name.startsWith("icon_")){
          try {
            icons.add(Icon(
              name = field.name.removePrefix("icon_"),
              iconID = field.getInt(null)
            ))
          } catch (e: IllegalAccessException) {
            e.printStackTrace()
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return icons
  }
}