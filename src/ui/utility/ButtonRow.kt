package com.fibu.ui.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fibu.theme.FiBuTheme

@Composable
fun ButtonRow(
    buttons: List<ButtonRowItem>
){
    Row (
        modifier = Modifier
            .padding(6.dp)
    ) {
        buttons.forEach { button ->
            Button(
                modifier = Modifier
                    .heightIn(min = 35.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FiBuTheme.buttonDefaultColors.secondary)
                    .weight(1f),
                onClick = button.onClick
            ) {
                Text(button.text, color = FiBuTheme.buttonDefaultColors.primary)
            }
            if(button != buttons.last()){
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
data class ButtonRowItem(
    val text: String,
    val onClick: () -> Unit
)