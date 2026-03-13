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
import com.fibu.logic.daysInMonths
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.TitleBar

const val YEARSINGRID = 12

@Composable
fun YearPicker(
    currentDate: Date,
    onSelection: (DateRange) -> Unit = {}
){
    var yearRange by remember {
        mutableStateOf(initYearRange(currentDate), neverEqualPolicy())
    }
    var yearItems by remember {
        mutableStateOf(initYearItems(yearRange), neverEqualPolicy())
    }
    var selectedYear by remember {
        mutableStateOf(Date(currentDate.year, 1, 1))
    }
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(FiBuTheme.contentColors.background),
        contentAlignment = Alignment.Center
    ) {
        Column {
            TitleBar(
                title = "${yearRange.startYear}-${yearRange.endYear}",
                leftContent = {
                    Button(
                        onClick = {
                            yearRange.goBack()
                            yearRange = yearRange.stateUpdate()
                            yearItems = initYearItems(yearRange)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                rightContent = {
                    Button(
                        onClick = {
                            yearRange.goForward()
                            yearRange = yearRange.stateUpdate()
                            yearItems = initYearItems(yearRange)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
            LazyVerticalGrid(columns = GridCells.Fixed(4)) {
                items(yearItems){yearItem ->
                    YearItem(
                        modifier = Modifier
                            .padding(3.dp),
                        date = yearItem,
                        isSelected = yearItem.equals(selectedYear),
                        onClick = { newDate ->
                            selectedYear = newDate
                            onSelection(DateRange(
                                    Date(selectedYear.year, 1, 1),
                                    Date(selectedYear.year, 12, daysInMonths[12 - 1])
                            ))
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun YearItem(
    modifier: Modifier = Modifier,
    date: Date,
    isSelected: Boolean,
    onClick: (Date) -> Unit = {}
){
    Button(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) FiBuTheme.buttonDefaultColors.secondary else Color.Transparent),
        onClick = {onClick(date)}
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(text = "${date.year}", color = FiBuTheme.contentColors.primary)
        }
    }
}
private fun initYearItems(yearRange: YearRange): List<Date>{
    val yearItems = mutableListOf<Date>()
    for(i in yearRange.startYear..yearRange.endYear){
        yearItems.add(Date(i, 1, 1))
    }
    return yearItems
}
private fun initYearRange(currentDate: Date): YearRange {
    val mod = currentDate.year % YEARSINGRID
    return YearRange(startYear = currentDate.year - mod, endYear = currentDate.year + (YEARSINGRID - 1 - mod))
}
private data class YearRange(
    var startYear: Int,
    var endYear: Int
){
    fun goBack(){
        startYear -= YEARSINGRID
        endYear -= YEARSINGRID
    }
    fun goForward(){
        startYear += YEARSINGRID
        endYear += YEARSINGRID
    }
    fun stateUpdate(): YearRange {
        return this
    }
}