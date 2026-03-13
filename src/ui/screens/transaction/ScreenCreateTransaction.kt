package com.fibu.ui.screens.transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.fibu.logic.navigation.NavController
import com.fibu.theme.FiBuTheme
import com.fibu.ui.dialogs.Alert
import com.fibu.ui.utility.ButtonRowItem
import com.fibu.ui.utility.CancelConfirmButtons

@Composable
fun ScreenCreateTransaction(
  navController: NavController,
  screenLogic: ScreenLogicCreateTransaction
) {

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
      TransactionEditBox(screenLogic = screenLogic)

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