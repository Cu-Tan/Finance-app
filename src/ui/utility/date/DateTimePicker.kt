package com.fibu.ui.utility.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fibu.logic.DateTime
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.WheelPicker

@Composable
fun DateTimePicker(
    currentDateTime: DateTime,
    onSelection: (DateTime) -> Unit
){
    val newDateTime = remember { mutableStateOf(currentDateTime) }
    Column {
        DatePicker(
            currentDate = currentDateTime.toDate(),
            onSelection = { date ->
                newDateTime.value.year = date.year
                newDateTime.value.month = date.month
                newDateTime.value.day = date.day
                onSelection(newDateTime.value)
            }
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 6.dp),
            thickness = 1.dp,
            color = FiBuTheme.contentColors.primary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hours",
                    color = FiBuTheme.contentColors.primary,
                    fontSize = 16.sp
                )
                WheelPicker(
                    width = 100.dp,
                    itemHeight = 25.dp,
                    items = (0..23).toList(),
                    initialItem = currentDateTime.hour,
                    onItemSelection = {
                        newDateTime.value.hour = it
                        onSelection(newDateTime.value)
                    }
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Minutes",
                    color = FiBuTheme.contentColors.primary,
                    fontSize = 16.sp
                )
                WheelPicker(
                    width = 100.dp,
                    itemHeight = 25.dp,
                    items = (0..59).toList(),
                    initialItem = currentDateTime.minute,
                    onItemSelection = {
                        newDateTime.value.minute = it
                        onSelection(newDateTime.value)
                    }
                )
            }
        }
    }
}