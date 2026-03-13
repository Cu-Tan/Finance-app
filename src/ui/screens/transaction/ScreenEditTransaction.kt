package com.fibu.ui.screens.transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.fibu.logic.navigation.NavController
import com.fibu.theme.FiBuTheme
import com.fibu.ui.dialogs.Alert
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.ButtonRowItem
import com.fibu.ui.utility.CancelConfirmButtons

@Composable
fun ScreenEditTransaction(
  navController: NavController,
  screenLogic: ScreenLogicEditTransaction
) {

  val deleteTransactionAlert: Boolean by screenLogic.deleteTransactionAlert.collectAsState()

  BackHandler(
    enabled = navController.canGoBack()
  ) {
    navController.back()
  }

  val focusManager = LocalFocusManager.current
  val openExitDialog: Boolean by screenLogic.openExitDialog.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(FiBuTheme.contentColors.contrast)
      .padding(
        start = 24.dp,
        end = 24.dp,
        bottom = 6.dp
      )
      .pointerInput(Unit) {
        detectTapGestures {
          focusManager.clearFocus()
        }
      }
  ) {
    Column(
      modifier = Modifier.fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {

        TransactionEditBox(screenLogic = screenLogic)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          modifier = Modifier
            .heightIn(min = 25.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(130, 0, 0))
            .padding(6.dp),
          onClick = {
            screenLogic.eventSetDeleteTransactionAlert(true)
          }
        ) {
          Text(
            text = "Delete Transaction",
            color = FiBuTheme.contentColors.primary
          )
        }
      }
      CancelConfirmButtons(
        eventCancel = {
          screenLogic.eventExit(
            exitAction = {
              navController.back()
            }
          )
        },
        eventConfirm = {
          screenLogic.eventConfirm(
            action = {
              navController.back()
            }
          )
        }
      )
    }
  }
  if (deleteTransactionAlert){
    Alert(
      onDismiss = { screenLogic.eventSetDeleteTransactionAlert(false) },
      text = "Are you sure you want to delete this transaction?",
      buttons = listOf(
        ButtonRowItem(
          text = "Cancel",
          onClick = { screenLogic.eventSetDeleteTransactionAlert(false) }
        ),
        ButtonRowItem(
          text = "Delete",
          onClick = {
            screenLogic.deleteTransaction()
            navController.back()
          }
        )
      )
    )
  }
  if(openExitDialog){
    Alert(
      onDismiss = { screenLogic.eventSetOpenExitDialog(false) },
      text = "Input changes detected?\n\nDo you want to exit this screen?",
      buttons = listOf(
        ButtonRowItem(
          text = "No",
          onClick = { screenLogic.eventSetOpenExitDialog(false) }
        ),
        ButtonRowItem(
          text = "Yes",
          onClick = {
            screenLogic.exitAction()
          }
        )
      )
    )
  }
}