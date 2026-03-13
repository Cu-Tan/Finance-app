package com.fibu.ui.utility.date
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fibu.logic.Date
import com.fibu.logic.DateRange
import com.fibu.logic.backMonth
import com.fibu.logic.checkLeapYear
import com.fibu.logic.daysInMonths
import com.fibu.logic.findDayOfTheWeek
import com.fibu.logic.forwardMonth
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.TitleBar

private class DatePickerLogic(
    currentDate: Date
){
    val checkDate: Date
        get() = _checkDate.value
    val selectedDate: Date
        get() = _selectedDate.value
    val dateItems: List<Date>
        get() = _dateItems.value
    fun onBack(){
        _checkDate.value = checkDate.apply { backMonth() }
        _dateItems.value = createCalendarItems(checkDate)
    }
    fun onForward(){
        _checkDate.value = checkDate.apply { forwardMonth() }
        _dateItems.value = createCalendarItems(checkDate)
    }
    fun onDateSelection(
        newDate: Date
    ) {
        _selectedDate.value = newDate
        if (selectedDate.year - checkDate.year < 0 || (selectedDate.year - checkDate.year == 0 && selectedDate.month - checkDate.month < 0)){
            onBack()
        } else if (selectedDate.year - checkDate.year > 0 || (selectedDate.year - checkDate.year == 0 && selectedDate.month - checkDate.month > 0)){
            onForward()
        }
    }

    private val _checkDate: MutableState<Date> = mutableStateOf(Date(
        year = currentDate.year,
        month = currentDate.month,
        day = 1
    ), neverEqualPolicy())
    private val _selectedDate: MutableState<Date> = mutableStateOf(currentDate)
    private val _dateItems: MutableState<List<Date>> = mutableStateOf(createCalendarItems(checkDate))
}
@Composable
fun DatePicker(
    currentDate: Date,
    onSelection: (Date) -> Unit
){
    val datePickerLogic = remember { DatePickerLogic(currentDate) }
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FiBuTheme.contentColors.background)
    ) {
        TitleBar(
            title = datePickerLogic.checkDate.ym_text,
            leftContent = {
                Button(
                    onClick = { datePickerLogic.onBack() }
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
                    onClick = { datePickerLogic.onForward() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        )
        LazyVerticalGrid(
            modifier = Modifier
                .padding(6.dp),
            columns = GridCells.Fixed(7)
        ) {
            // For some reason clicking on the labels chooses the date below it. Have no idea why this is happening
            items(labels){label ->
                Text(
                    text = label,
                    color = FiBuTheme.contentColors.primary,
                    textAlign = TextAlign.Center
                )
            }
            items(datePickerLogic.dateItems){dateItem ->
                DateItem(
                    date = dateItem,
                    isSelected = (datePickerLogic.selectedDate.equals(dateItem)),
                    isGray = (dateItem.year != datePickerLogic.checkDate.year || dateItem.month != datePickerLogic.checkDate.month),
                    onClick = { newDate ->
                        datePickerLogic.onDateSelection(newDate)
                        onSelection(newDate)
                    }
                )
            }
        }
    }
}
private class DateRangePickerLogic(
    currentDateRange: DateRange
) {
    val checkDate: Date
        get() = _checkDate.value
    val selectedDateRange: DateRange
        get() = _selectedDateRange.value
    private val isNewDateRange: Boolean
        get() = _isNewDateRange.value
    val dateItems: List<Date>
        get() = _dateItems.value
    fun onBack(){
        _checkDate.value = checkDate.apply { backMonth() }
        _dateItems.value = createCalendarItems(checkDate)
    }
    fun onForward(){
        _checkDate.value = checkDate.apply { forwardMonth() }
        _dateItems.value = createCalendarItems(checkDate)
    }
    fun insideDateRange(date: Date): Boolean{
        return date.toString() in selectedDateRange.startDate.toString()..selectedDateRange.endDate.toString()
    }
    fun onDateSelection(
        newDate: Date
    ) {
        if (isNewDateRange){
            _selectedDateRange.value = DateRange(newDate, newDate)
        } else {
            _selectedDateRange.value = if (selectedDateRange.startDate.toString() < newDate.toString()){
                DateRange(selectedDateRange.startDate, newDate)
            } else {
                DateRange(newDate, selectedDateRange.startDate)
            }
        }
        _isNewDateRange.value = !isNewDateRange
        if (newDate.year - checkDate.year < 0 || (newDate.year - checkDate.year == 0 && newDate.month - checkDate.month < 0)){
            onBack()
        } else if (newDate.year - checkDate.year > 0 || (newDate.year - checkDate.year == 0 && newDate.month - checkDate.month > 0)){
            onForward()
        }
    }
    private val _checkDate: MutableState<Date> = mutableStateOf(Date(
        year = currentDateRange.startDate.year,
        month = currentDateRange.startDate.month,
        day = 1
    ), neverEqualPolicy())
    private val _selectedDateRange: MutableState<DateRange> = mutableStateOf(currentDateRange)
    private val _isNewDateRange: MutableState<Boolean> = mutableStateOf(true)
    private val _dateItems: MutableState<List<Date>> = mutableStateOf(createCalendarItems(_checkDate.value))
}
@Composable
fun DateRangePicker(
    currentDateRange: DateRange,
    onSelection: (DateRange) -> Unit = {}
){
    val dateRangePickerLogic = remember { DateRangePickerLogic(
        currentDateRange = currentDateRange
    )}
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FiBuTheme.contentColors.background)
    ) {
        TitleBar(
            title = dateRangePickerLogic.checkDate.ym_text,
            leftContent = {
                Button(onClick = { dateRangePickerLogic.onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            rightContent = {
                Button(onClick = { dateRangePickerLogic.onForward() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        )
        LazyVerticalGrid(
            modifier = Modifier
                .padding(6.dp),
            columns = GridCells.Fixed(7)
        ) {
            // For some reason clicking on the labels chooses the date below it. Have no idea why this is happening
            items(labels){label ->
                Text(
                    text = label,
                    color = FiBuTheme.contentColors.primary,
                    textAlign = TextAlign.Center
                )
            }
            items(dateRangePickerLogic.dateItems){dateItem ->
                DateItem(
                    date = dateItem,
                    isSelected = dateRangePickerLogic.insideDateRange(dateItem),
                    isGray = (dateItem.year != dateRangePickerLogic.checkDate.year || dateItem.month != dateRangePickerLogic.checkDate.month),
                    onClick = { newDate ->
                        dateRangePickerLogic.onDateSelection(newDate)
                        onSelection(dateRangePickerLogic.selectedDateRange)
                    }
                )
            }
        }
    }
}
@Composable
private fun DateItem(
    modifier: Modifier = Modifier,
    date: Date,
    isSelected: Boolean,
    isGray: Boolean = true,
    onClick: (Date) -> Unit = {}
){
    Button(
        modifier = modifier,
        onClick = { onClick(date) }
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) FiBuTheme.buttonDefaultColors.secondary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${date.day}",
                color = if (!isGray) FiBuTheme.contentColors.primary else Color.DarkGray
            )
        }
    }
}
private fun createCalendarItems(date: Date): List<Date> {
    val daysInMonth = date.daysInMonth
    val dayOfTheWeek = date.findDayOfTheWeek()
    val items = mutableListOf<Date>()
    date.backMonth()
    var counter = if(date.month == 2 && date.checkLeapYear()) daysInMonths[date.month - 1] - dayOfTheWeek + 2 else daysInMonths[date.month - 1] - dayOfTheWeek + 1
    for (i in 0 until dayOfTheWeek step 1) {
        items.add(
            Date(
                year = date.year,
                month = date.month,
                day = counter
            )
        )
        counter += 1
    }
    date.forwardMonth()
    counter = 1
    for (i in 0 until daysInMonth step 1) {
        items.add(
            Date(
                year = date.year,
                month = date.month,
                day = counter
            )
        )
        counter += 1
    }
    date.forwardMonth()
    counter = 1
    for (i in 0 until (42 - daysInMonth - dayOfTheWeek) step 1) {
        items.add(
            Date(
                year = date.year,
                month = date.month,
                day = counter
            )
        )
        counter += 1
    }
    date.backMonth()
    return items
}
