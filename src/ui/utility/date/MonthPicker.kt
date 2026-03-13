package com.fibu.ui.utility.date

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fibu.logic.Date
import com.fibu.logic.DateRange
import com.fibu.logic.monthNames
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.TitleBar

@Composable
fun MonthPicker(
    currentDate: Date,
    onSelection: (DateRange) -> Unit = {}
){
    var selectedDate by remember { mutableStateOf(Date(currentDate.year, currentDate.month, 1), neverEqualPolicy()) }
    var checkDate by remember { mutableStateOf(Date(year = selectedDate.year, month = selectedDate.month, day = 1), neverEqualPolicy()) }
    val gridItems = createGridItems(checkDate)

    // Not sure if box is needed here
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(FiBuTheme.contentColors.background),
        contentAlignment = Alignment.Center
    ) {
        Column {
            TitleBar(
                title = "${checkDate.year}",
                leftContent = {
                    Button(
                        onClick = {
                            checkDate.year -= 1
                            // I absolutely hate states >:(
                            checkDate = checkDate.stateUpdate()
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                rightContent = {
                    Button(
                        onClick = {
                            checkDate.year += 1
                            // I absolutely hate states >:(
                            checkDate = checkDate.stateUpdate()
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(6.dp),
                columns = GridCells.Fixed(4)
            ) {
                items(gridItems){date ->
                    MonthItem(
                        date = date,
                        isSelected = (selectedDate.equals(date)),
                        onClick = { newDate ->
                            selectedDate = newDate
                            onSelection(DateRange(
                                Date(selectedDate.year, selectedDate.month, 1),
                                Date(selectedDate.year, selectedDate.month, selectedDate.daysInMonth)
                            ))
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun MonthItem(
    modifier: Modifier = Modifier,
    date: Date,
    isSelected: Boolean,
    onClick: (Date) -> Unit = {}
){
    Button(
        modifier = modifier,
        onClick = {onClick(date)}
    ) {
        Box(
            modifier = Modifier
                .padding(3.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) FiBuTheme.buttonDefaultColors.secondary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(text = monthNames[date.month - 1], color = FiBuTheme.contentColors.primary)
        }
    }
}
private fun Date.stateUpdate(): Date{
    return this
}
private fun createGridItems(checkDate: Date): List<Date>{
    val calendarItems = mutableListOf<Date>()
    for(i in 1 .. 12){
        calendarItems.add(Date(checkDate.year, i, 1))
    }
    return calendarItems
}