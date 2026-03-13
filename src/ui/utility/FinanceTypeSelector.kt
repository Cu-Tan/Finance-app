package com.fibu.ui.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fibu.logic.info.FinanceType
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.layout.TabRow

@Composable
fun FinanceTypeSelector(
    selectedFinanceType: FinanceType,
    onClick: (FinanceType) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(6.dp)
    ) {
        TabRow (
            modifier = Modifier
                .background(FiBuTheme.contentColors.contrast)
                .padding(6.dp)
        ) {
            FinanceTypeSelectorTab(
                title = "Expense",
                isSelected = (selectedFinanceType == FinanceType.Expense),
                onClick = { onClick(FinanceType.Expense) }
            )
            FinanceTypeSelectorTab(
                title = "Income",
                isSelected = (selectedFinanceType == FinanceType.Income),
                onClick = { onClick(FinanceType.Income) }
            )
        }
    }
}
@Composable
private fun FinanceTypeSelectorTab(
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
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = if (isSelected) FiBuTheme.contentColors.active else FiBuTheme.contentColors.primary
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = if (isSelected) FiBuTheme.contentColors.active else FiBuTheme.contentColors.primary
            )
        }
    }
}