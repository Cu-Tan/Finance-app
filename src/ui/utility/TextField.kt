package com.fibu.ui.utility

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fibu.theme.FiBuTheme

@Composable
fun ScrollTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    minLines: Int = 4,
    maxLines: Int = 4
) {
    var focused by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier.padding(6.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            textStyle = TextStyle.Default.copy(
                color = FiBuTheme.contentColors.primary,
                fontSize = 16.sp
            ),
            minLines = minLines,
            maxLines = maxLines,
            decorationBox = { innerTextField ->
                if (value.isEmpty() && !focused) {
                    Text(text = "Notes", color = FiBuTheme.contentColors.primary.copy(alpha = 0.7f))
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    focused = it.isFocused
                }
        )
    }
}