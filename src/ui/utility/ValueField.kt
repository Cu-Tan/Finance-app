package com.fibu.ui.utility

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.fibu.theme.FiBuTheme

@Composable
fun ValueField(
    modifier: Modifier = Modifier,
    value: String = "",
    valueError: Boolean = false,
    valueErrorText: String = ValueErrorType.None.text,
    onValueChange: (String) -> Unit
) {
    val valueFieldLogic: ValueFieldLogic = remember { ValueFieldLogic(value) }
    LaunchedEffect (value) {
        valueFieldLogic.onValueInput(value)
    }
    Text(
        text = "Value",
        color = FiBuTheme.contentColors.active
    )
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = valueFieldLogic.valueText,
        onValueChange = {
            valueFieldLogic.onValueInput(it)
            onValueChange(valueFieldLogic.valueText)
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
        textStyle = TextStyle(
            color = FiBuTheme.contentColors.primary,
            fontSize = 16.sp
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FiBuTheme.contentColors.background,
            unfocusedContainerColor = FiBuTheme.contentColors.background,
            errorContainerColor = FiBuTheme.contentColors.background
        ),
        placeholder = { Text(
            text = "0.00",
            color = FiBuTheme.contentColors.primary
        ) },
        isError = valueError
    )
    if (valueError) {
        Text(
            text = valueErrorText,
            color = Color.Red
        )
    }
}
private class ValueFieldLogic(
    value: String
) {
    val valueText: String
        get() = _valueText.value
    fun onValueInput(
        input: String
    ) {
        _valueText.value = formatValueText(input)
    }
    private val _valueText: MutableState<String> = mutableStateOf(value)
    private fun formatValueText(
        newText: String
    ): String {
        var leadingZeroTrack = false
        var leadingZeroCount = 0
        val newTextLength = newText.length

        var dotCount = 0
        var decimalCount = 0
        for(i in 0 until newTextLength){
            if(!newText[i].isDigit() && newText[i] != ',' && newText[i] != '.'){
                return _valueText.value
            }
            if(i == 0 && newText[i] == '0') {
                leadingZeroTrack = true
                continue
            }
            if(newText[i] == ',' || newText[i] == '.'){
                dotCount++
                if (leadingZeroTrack) { leadingZeroTrack = false }
                if (dotCount > 1){ return _valueText.value}
                continue
            }
            if (leadingZeroTrack){
                if (newText[i] == '0') { leadingZeroCount++ }
                else {
                    leadingZeroCount++
                    leadingZeroTrack = false
                }
            }
            if(dotCount == 1){
                decimalCount++
                if(decimalCount > 2) {
                    return newText.take(newText.length - 2) + newText.last()
                }
            }
        }
        var returnText = ""
        for(i in leadingZeroCount until newTextLength){
            returnText = "$returnText${newText[i]}"
        }
        return returnText
    }
}
enum class ValueErrorType(val text: String){
    None(
        text = ""
    ),
    Blank(
        text = "Value can not be blank"
    ),
    Zero(
        text = "Value can not be 0"
    ),
    Invalid(
        text = "Invalid number format"
    )
}
fun valueFieldHasError(
    text: String
): ValueErrorType {
    if(text.isEmpty()){
        return ValueErrorType.Blank
    }
    val regex = Regex("^\\d*[.,]?\\d{0,2}\$")
    val numberRegex = Regex("^\\s*[.,]\\s*$")
    if(!regex.matches(text) || numberRegex.matches(text)){
        return ValueErrorType.Invalid
    }
    if (text.replace(",", ".").toFloat() == 0f) {
        return ValueErrorType.Zero
    }
    return ValueErrorType.None
}