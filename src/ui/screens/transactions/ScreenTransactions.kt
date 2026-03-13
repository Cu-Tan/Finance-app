package com.fibu.ui.screens.transactions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fibu.logic.navigation.NavController
import com.fibu.logic.navigation.Screens
import com.fibu.logic.Date
import com.fibu.logic.DateRange
import com.fibu.logic.info.Category
import com.fibu.logic.info.SortType
import com.fibu.logic.info.FinanceType
import com.fibu.logic.navigation.screeninfo.editTransaction
import com.fibu.theme.FiBuTheme
import com.fibu.ui.dialogs.ExpenseIncomeCategoryFilterDialog
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.DropdownMenu
import com.fibu.ui.utility.TitleBar
import com.fibu.ui.utility.date.DateSelectorBox
import com.fibu.ui.utility.date.DateSelectorBoxTypes

@Composable
fun ScreenTransactions(
  navController: NavController,
  screenLogic: ScreenLogicTransactions,
) {
  val focusRequester = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current

  val searchText: String by screenLogic.getSearchText().collectAsState()
  
  val dateSelectorBoxType: DateSelectorBoxTypes by screenLogic.getDateSelectorBoxType().collectAsState()
  val dateRange: DateRange by screenLogic.getDateRange().collectAsState()
  val sortType: SortType by screenLogic.getSortType().collectAsState()

  val filterDialog: Boolean by screenLogic.getFilterDialogActive().collectAsState()
  val filterSet: Boolean by screenLogic.getFilterSet().collectAsState()

  val displayTransactions: List<DisplayTransaction> by screenLogic.getTransactions().collectAsState()

  val selectedExpenseCategories: List<Category> by screenLogic.getSelectedExpenseCategories().collectAsState()
  val selectedIncomeCategories: List<Category> by screenLogic.getSelectedIncomeCategories().collectAsState()

  BackHandler(
    enabled = navController.canGoBack()
  ) {
    navController.back()
  }
  Column (
    modifier = Modifier
      .background(FiBuTheme.contentColors.contrast)
      .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 6.dp)
      .pointerInput(Unit) {
        detectTapGestures {
          focusManager.clearFocus()
        }
      }
  ) {
    DateSelectorBox(
      selectedDateSelectorBoxType = dateSelectorBoxType,
      selectedDateRange = dateRange,
      eventSetDateSelectorBoxType = { dateSelectorBoxType ->
        screenLogic.eventSetDateSelectorBoxType(dateSelectorBoxType)
      },
      onConfirm = {dateRange ->
        screenLogic.eventSetDateRange(dateRange)
      }
    )
    Spacer(modifier = Modifier.height(4.dp))
    TitleBar(
      leftContent = {
        DropdownMenu(
          modifier = Modifier
            .border(
              1.dp,
              FiBuTheme.buttonDefaultColors.secondary,
              RoundedCornerShape(4.dp)
            )
            .background(
              FiBuTheme.contentColors.background,
              RoundedCornerShape(4.dp)
            )
            .padding(6.dp),
          items = listOf(
            when(sortType){
              SortType.Date -> SortType.Value
              SortType.Value -> SortType.Date
            }
          ),
          itemContent = { item ->
            Box(
              modifier = Modifier.fillMaxWidth(),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = item.name,
                color = FiBuTheme.contentColors.primary
              )
            }
          },
          selectedItem = sortType,
          selectedItemContent = { selectedItem ->
            Text(
              text = "Sort by: ${selectedItem.name}",
              color = FiBuTheme.contentColors.primary
            )
          },
          menuColor = FiBuTheme.contentColors.background,
          onSelection = { sortType ->
            screenLogic.eventSetSortType(sortType)
          }
        )
      },
      mainContent = {
        if(filterSet){
          Button(
            modifier = Modifier
              .border(
                1.dp,
                FiBuTheme.buttonDefaultColors.secondary,
                RoundedCornerShape(4.dp)
              )
              .padding(6.dp),
            onClick = { screenLogic.eventClearFilter() }
          ) {
            Row (verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Filter",
                color = FiBuTheme.contentColors.primary
              )
              Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = null,
                tint = Color(130, 0, 0)
              )
            }
          }
        }
      },
      rightContent = {
        Button(
          modifier = Modifier
            .border(
              1.dp,
              FiBuTheme.buttonDefaultColors.secondary,
              RoundedCornerShape(4.dp)
            )
            .background(
              FiBuTheme.contentColors.background,
              RoundedCornerShape(4.dp)
            )
            .padding(6.dp),
          onClick = { screenLogic.eventSetFilterDialog(true) }
        ) {
          Text(
            text = "Set Filter",
            color = FiBuTheme.contentColors.primary
          )
        }
      }
    )
    OutlinedTextField(
      modifier = Modifier
        .fillMaxWidth()
        .focusRequester(focusRequester),
      value = searchText,
      onValueChange = { newValue ->
        screenLogic.onSearchTextInput(newValue)
      },
      colors = OutlinedTextFieldDefaults.colors().copy(
        unfocusedTextColor = FiBuTheme.contentColors.primary,
        focusedTextColor = FiBuTheme.contentColors.primary
      ),
      leadingIcon = {
        Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
      },
      singleLine = true
    )
    LazyColumn {
      var previousDate: Date? = null
      items(items = displayTransactions) { displayTransaction ->
        previousDate?.let {
          if (it.toString() != displayTransaction.transaction.dateTime.toDateString()) {
            Text(text = displayTransaction.transaction.dateTime.ymd_text, color = FiBuTheme.contentColors.primary)
            previousDate = displayTransaction.transaction.dateTime.toDate()
          }
        } ?: run {
          Text(text = displayTransaction.transaction.dateTime.ymd_text, color = FiBuTheme.contentColors.primary)
          previousDate = displayTransaction.transaction.dateTime.toDate()
        }

        TransactionBox(
          modifier = Modifier
            .clickable{
              navController.navigate(
                targetScreen = Screens.editTransaction(
                  transaction = displayTransaction.transaction
                )
              )
            },
          displayTransaction = displayTransaction
        )
        Spacer(modifier = Modifier.height(4.dp))
      }
    }
  }
  if(filterDialog){
    ExpenseIncomeCategoryFilterDialog(
      selectedExpenseCategories = selectedExpenseCategories,
      selectedIncomeCategories = selectedIncomeCategories,
      searchStringFun = { category ->
        category.name
      },
      onDismiss = {
        screenLogic.eventSetFilterDialog(false)
      },
      onConfirm = { expenseCategories, incomeCategories ->
        screenLogic.eventSetFilterDialog(false)
        screenLogic.eventSetFilteredCategories(
          expenseCategories = expenseCategories,
          incomeCategories = incomeCategories
        )
      }
    )
  }
}
@Composable
private fun TransactionBox(
  modifier: Modifier = Modifier,
  displayTransaction: DisplayTransaction
) {

  Column(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(FiBuTheme.contentColors.background)
      .padding(6.dp)
  ) {
    TitleBar(
      title = displayTransaction.transaction.dateTime.timeText,
      leftContent = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            modifier = Modifier.background(displayTransaction.category.color, RoundedCornerShape(4.dp)),
            painter = painterResource(id = displayTransaction.category.icon),
            contentDescription = null,
            tint = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = displayTransaction.category.name,
              color = FiBuTheme.contentColors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = displayTransaction.transaction.type.name,
              color = FiBuTheme.contentColors.primary,
              fontSize = 10.sp
            )
          }
        }
      },
      rightContent = {
        Text(
          text = "${
            when (displayTransaction.transaction.type) {
              FinanceType.Expense -> '-'; FinanceType.Income -> '+'
            }
          }${String.format("%.2f",displayTransaction.transaction.value)} $",
          color = when (displayTransaction.transaction.type) {
            FinanceType.Expense -> Color(200, 0, 0)
            FinanceType.Income -> Color(0, 150, 0)
          },
          fontSize = 16.sp,
          textAlign = TextAlign.End
        )
      }
    )
  }
}
