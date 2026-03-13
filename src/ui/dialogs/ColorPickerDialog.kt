package com.fibu.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fibu.logic.ColorController
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.ButtonRow
import com.fibu.ui.utility.ButtonRowItem
import com.fibu.ui.utility.CancelConfirmButtons
import com.fibu.ui.utility.color.ColorSlideBar
import com.fibu.ui.utility.color.ColorTile
import com.fibu.ui.utility.color.HsvColorPicker

@Composable
fun ColorPickerDialog(
  initColor: Color,
  onDismiss: () -> Unit,
  onConfirm: (Color) -> Unit
) {
  val colorController: ColorController = remember { ColorController() }
  Dialog(onDismissRequest = onDismiss) {
    Column(
      modifier = Modifier
        .width(IntrinsicSize.Max)
        .background(FiBuTheme.contentColors.background),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      HsvColorPicker(
        initColor = initColor,
        controller = colorController
      )
      Spacer(modifier = Modifier.height(10.dp))
      ColorSlideBar(controller = colorController)
      Spacer(modifier = Modifier.height(10.dp))
      ColorTile(controller = colorController)

      CancelConfirmButtons(
        eventCancel = onDismiss,
        eventConfirm = {
          onConfirm(colorController.selectedColor)
        }
      )
    }
  }
}