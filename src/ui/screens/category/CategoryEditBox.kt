package com.fibu.ui.screens.category

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fibu.theme.FiBuTheme
import com.fibu.ui.dialogs.ColorPickerDialog
import com.fibu.ui.dialogs.IconDialog
import com.fibu.ui.utility.Button

@Composable
fun CategoryEditBox(
  screenLogic: ScreenLogicCategory
) {

  val focusRequester = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current

  var openColorDialog: Boolean by remember { mutableStateOf(false) }
  var openIconDialog: Boolean by remember { mutableStateOf(false) }

  val uiState: ScreenLogicCategory.UIState by screenLogic.uiState.collectAsState()

  val errorCategory: Boolean by screenLogic.errorCategory.collectAsState()
  val errorCategoryText: String by screenLogic.errorCategoryText.collectAsState()

  Column{
    Text(
      text = "Category name",
      color = FiBuTheme.contentColors.active
    )
    TextField(
      modifier = Modifier
        .fillMaxWidth()
        .focusRequester(focusRequester),
      value = uiState.name,
      onValueChange = { screenLogic.eventSetCategoryName(it) },
      textStyle = TextStyle(
        color = FiBuTheme.contentColors.primary,
        fontSize = 18.sp
      ),
      colors = TextFieldDefaults.colors(
        focusedContainerColor = FiBuTheme.contentColors.background,
        unfocusedContainerColor = FiBuTheme.contentColors.background,
        errorContainerColor = FiBuTheme.contentColors.background
      ),
      singleLine = true,
      isError = errorCategory
    )
    if (errorCategory) {
      Text(
        text = errorCategoryText,
        color = Color.Red
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "Color",
      color = FiBuTheme.contentColors.active
    )
    Canvas(modifier = Modifier
      .size(50.dp)
      .clickable {
        focusManager.clearFocus()
        openColorDialog = true
      }
    ) {
      drawCircle(
        color = uiState.color,
        radius = 50f
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "Icon",
      color = FiBuTheme.contentColors.active
    )
    Button(
      modifier = Modifier
        .width(50.dp)
        .height(50.dp)
        .aspectRatio(1f)
        .background(
          color = uiState.color,
          shape = RoundedCornerShape(4.dp)
        ),
      onClick = {
        focusManager.clearFocus()
        openIconDialog = true
      }
    ) {
      Icon(
        painter = painterResource(id = uiState.icon),
        contentDescription = null,
        tint = Color.White
      )
    }
  }
  if (openColorDialog){
    ColorPickerDialog(
      initColor = uiState.color,
      onDismiss = { openColorDialog = false },
      onConfirm = { newCategoryColor ->
        screenLogic.eventSetCategoryColor(newCategoryColor)
        openColorDialog = false
      }
    )
  }
  if (openIconDialog){
    IconDialog(
      initIcon = uiState.icon,
      selectedColor = uiState.color,
      onDismiss = { openIconDialog = false },
      onConfirm = {
        newCategoryIcon -> screenLogic.eventSetCategoryIcon(newCategoryIcon)
        openIconDialog = false
      }
    )
  }
}