package com.fibu.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.ButtonRow
import com.fibu.ui.utility.ButtonRowItem

@Composable
fun Alert(
    onDismiss: () -> Unit = {},
    text: String,
    buttons: List<ButtonRowItem>,
){
    Dialog(onDismissRequest = onDismiss) {
        Column (
            modifier = Modifier
                .background(FiBuTheme.contentColors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                modifier = Modifier.padding(6.dp),
                text = text,
                textAlign = TextAlign.Center, color = FiBuTheme.contentColors.primary,
                fontSize = 16.sp
            )
            ButtonRow(buttons = buttons)
        }
    }
}