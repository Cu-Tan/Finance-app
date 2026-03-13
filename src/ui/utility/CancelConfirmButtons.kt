package com.fibu.ui.utility

import androidx.compose.runtime.Composable

@Composable
fun CancelConfirmButtons(
  eventCancel: () -> Unit,
  eventConfirm: () -> Unit
) {
  ButtonRow(
    buttons = listOf(
      ButtonRowItem(
        text = "Cancel",
        onClick = eventCancel
      ),
      ButtonRowItem(
        text = "Confirm",
        onClick = eventConfirm
      )
    )
  )
}