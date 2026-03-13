package com.fibu.ui.screens.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fibu.logic.DateTime
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.CancelConfirmButtons
import com.fibu.ui.utility.CategorySearchMenu
import com.fibu.ui.utility.FinanceTypeSelector
import com.fibu.ui.utility.ScrollTextField
import com.fibu.ui.utility.ValueErrorType
import com.fibu.ui.utility.ValueField
import com.fibu.ui.utility.date.DateTimePicker

@Composable
fun TransactionEditBox(
  screenLogic: ScreenLogicTransaction
) {

  val focusRequester = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current

  val uiState: ScreenLogicTransaction.UIState by screenLogic.uiState.collectAsState()
  val valueErrorType: ValueErrorType by screenLogic.valueErrorType.collectAsState()
  val categoryError: Boolean by screenLogic.categoryError.collectAsState()
  val dateTimeDialog: Boolean by screenLogic.dateTimeDialog.collectAsState()

  Column {
    FinanceTypeSelector(
      selectedFinanceType = uiState.financeType,
      onClick = { newFinanceType ->
        screenLogic.eventSetFinanceType(newFinanceType)
      }
    )

    Spacer(modifier = Modifier.height(4.dp))

    ValueField(
      modifier = Modifier.focusRequester(focusRequester),
      value = uiState.valueText,
      valueError = (valueErrorType != ValueErrorType.None),
      valueErrorText = valueErrorType.text,
      onValueChange = { newValueText ->
        screenLogic.eventSetValue(newValueText)
      }
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "Category",
      color = FiBuTheme.contentColors.active
    )
    CategorySearchMenu(
      financeType = uiState.financeType,
      selectedCategory = uiState.category,
      eventSetCategory = { category ->
        screenLogic.eventSetCategory(category)
      }
    )
    if (categoryError) {
      Text(
        text = "Select category",
        color = Color.Red
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "Notes",
      color = FiBuTheme.contentColors.active
    )
    ScrollTextField(
      modifier = Modifier
        .fillMaxWidth()
        .background(FiBuTheme.contentColors.background)
        .focusRequester(focusRequester),
      value = uiState.noteText,
      onValueChanged = { screenLogic.eventSetNote(it) }
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "Date",
      color = FiBuTheme.contentColors.active
    )
    TextField(
      modifier = Modifier
        .fillMaxWidth()
        .clickable {
          focusManager.clearFocus()
          screenLogic.eventSetDateTimeDialog(true)
        }
        .focusRequester(focusRequester),
      value = uiState.dateTime.ymdhmText,
      onValueChange = {},
      colors = TextFieldDefaults.colors(
        disabledContainerColor = FiBuTheme.contentColors.background,
        disabledTextColor = FiBuTheme.contentColors.primary
      ),
      enabled = false
    )
  }

  if(dateTimeDialog){
    Dialog(
      onDismissRequest = { screenLogic.eventSetDateTimeDialog(false) }
    ) {
      var dateTime: DateTime = uiState.dateTime.copy()
      Column(
        modifier = Modifier.background(FiBuTheme.contentColors.background)
      ) {
        DateTimePicker(
          currentDateTime = dateTime,
          onSelection = { dateTime = it }
        )
        CancelConfirmButtons(
          eventCancel = {
            screenLogic.eventSetDateTimeDialog(false)
          },
          eventConfirm = {
            screenLogic.eventSetDateTime(dateTime)
            screenLogic.eventSetDateTimeDialog(false)
          }
        )
      }

    }
  }

}