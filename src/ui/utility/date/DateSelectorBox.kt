package com.fibu.ui.utility.date

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fibu.logic.DateRange
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.CancelConfirmButtons
import com.fibu.ui.utility.layout.TabRow

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun DateSelectorBox(
  modifier: Modifier = Modifier,
  selectedDateSelectorBoxType: DateSelectorBoxTypes,
  selectedDateRange: DateRange,
  eventSetDateSelectorBoxType: (DateSelectorBoxTypes) -> Unit,
  onConfirm: (DateRange) -> Unit
){
  var calendarDialog by remember { mutableStateOf(false) }
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(FiBuTheme.contentColors.background)
      .padding(6.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    TabRow (
      modifier = Modifier
        .padding(6.dp)
    ) {
      DateTypeTab(
        title = "Day",
        isSelected = (selectedDateSelectorBoxType == DateSelectorBoxTypes.DAY),
        onClick = {
          eventSetDateSelectorBoxType(DateSelectorBoxTypes.DAY)
        }
      )
      DateTypeTab(
        title = "Month",
        isSelected = (selectedDateSelectorBoxType == DateSelectorBoxTypes.MONTH),
        onClick = {
          eventSetDateSelectorBoxType(DateSelectorBoxTypes.MONTH)
        }
      )
      DateTypeTab(
        title = "Year",
        isSelected = (selectedDateSelectorBoxType == DateSelectorBoxTypes.YEAR),
        onClick = {
          eventSetDateSelectorBoxType(DateSelectorBoxTypes.YEAR)
        }
      )
      DateTypeTab(
        title = "Custom",
        isSelected = (selectedDateSelectorBoxType == DateSelectorBoxTypes.CUSTOM),
        onClick = {
          eventSetDateSelectorBoxType(DateSelectorBoxTypes.CUSTOM)
        }
      )
    }
    Column(
      modifier = Modifier.width(IntrinsicSize.Max)
    ) {
      Text(
        modifier = Modifier
          .clickable(
            interactionSource = MutableInteractionSource(),
            indication = null
          ){
            calendarDialog = true
          },
        text = when(selectedDateSelectorBoxType){
          DateSelectorBoxTypes.DAY -> selectedDateRange.startDate.ymd_text
          DateSelectorBoxTypes.MONTH -> selectedDateRange.startDate.ym_text
          DateSelectorBoxTypes.YEAR -> "${selectedDateRange.startDate.year}"
          DateSelectorBoxTypes.CUSTOM -> selectedDateRange.title
        },
        color = FiBuTheme.contentColors.primary,
        fontSize = 16.sp
      )
      HorizontalDivider(
        thickness = 1.dp,
        color = FiBuTheme.contentColors.primary
      )
    }

  }
  if (calendarDialog){
    when(selectedDateSelectorBoxType){
      DateSelectorBoxTypes.DAY ->
        Dialog(onDismissRequest = {calendarDialog = false}) {
          var newDateRange: DateRange = remember {selectedDateRange}
          Column (
            modifier = Modifier.background(FiBuTheme.contentColors.background)
          ) {
            DatePicker(
              currentDate = selectedDateRange.startDate,
              onSelection = { date ->
                newDateRange = DateRange(startDate = date, endDate = date)
              }
            )
            CancelConfirmButtons(
              eventCancel = {
                calendarDialog = false
              },
              eventConfirm = {
                calendarDialog = false
                onConfirm(newDateRange)
              }
            )
          }
        }
      DateSelectorBoxTypes.MONTH ->
        Dialog(onDismissRequest = {calendarDialog = false}) {
          var newDateRange: DateRange = remember {selectedDateRange}
          Column (
            modifier = Modifier.background(FiBuTheme.contentColors.background)
          ) {
            MonthPicker(
              currentDate = selectedDateRange.startDate,
              onSelection = { dateRange ->
                newDateRange = dateRange
              }
            )
            CancelConfirmButtons(
              eventCancel = {
                calendarDialog = false
              },
              eventConfirm = {
                calendarDialog = false
                onConfirm(newDateRange)
              }
            )
          }
        }
      DateSelectorBoxTypes.YEAR ->
        Dialog(onDismissRequest = { calendarDialog = false }) {
          var newDateRange: DateRange = remember {selectedDateRange}
          Column (
            modifier = Modifier.background(FiBuTheme.contentColors.background)
          ) {
            YearPicker(
              currentDate = selectedDateRange.startDate,
              onSelection = { dateRange ->
                newDateRange = dateRange
              }
            )
            CancelConfirmButtons(
              eventCancel = {
                calendarDialog = false
              },
              eventConfirm = {
                calendarDialog = false
                onConfirm(newDateRange)
              }
            )
          }
        }
      DateSelectorBoxTypes.CUSTOM ->
        Dialog(onDismissRequest = { calendarDialog = false }) {
          var newDateRange: DateRange = remember {selectedDateRange}
          Column (
            modifier = Modifier.background(FiBuTheme.contentColors.background)
          ) {
            DateRangePicker(
              currentDateRange = selectedDateRange,
              onSelection = { dateRange ->
                newDateRange = dateRange
              }
            )
            CancelConfirmButtons(
              eventCancel = {
                calendarDialog = false
              },
              eventConfirm = {
                calendarDialog = false
                onConfirm(newDateRange)
              }
            )
          }
        }
    }
  }
}

@Composable
private fun DateTypeTab(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .padding(start = 4.dp, end = 4.dp)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        onClick()
      },
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      color = if (isSelected) FiBuTheme.contentColors.active else FiBuTheme.contentColors.primary
    )
  }
}

enum class DateSelectorBoxTypes{
  DAY, MONTH, YEAR, CUSTOM
}