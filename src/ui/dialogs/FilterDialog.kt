package com.fibu.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.DB
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.theme.FiBuTheme
import com.fibu.ui.utility.ButtonRow
import com.fibu.ui.utility.ButtonRowItem
import com.fibu.ui.utility.Checklist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// region Expense

@Composable
fun ExpenseCategoryFilterDialog(
  selectedExpenseCategories: List<Category>,
  searchStringFun: (Category) -> String,
  onDismiss: () -> Unit,
  onConfirm: (List<Category>) -> Unit
) {

  val expenseCategoryFilterDialogLogic = remember {
    ExpenseCategoryFilterDialogLogic(
      init_selectedExpenseCategories = selectedExpenseCategories
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Column(
      modifier = Modifier
        .background(FiBuTheme.contentColors.contrast)
        .padding(6.dp)
    ) {

      ExpenseCategoryFilterDialogBox(
        expenseCategoryFilterDialogLogic = expenseCategoryFilterDialogLogic,
        searchStringFun = searchStringFun
      )

      ButtonRow(buttons = listOf(
        ButtonRowItem(
          text = "Cancel",
          onClick = onDismiss
        ),
        ButtonRowItem(
          text = "Confirm",
          onClick = {
            onConfirm(expenseCategoryFilterDialogLogic.selectedExpenseCategories.value)
          }
        )
      ))
    }
  }
}

@Composable
private fun ExpenseCategoryFilterDialogBox(
  expenseCategoryFilterDialogLogic: ExpenseCategoryFilterDialogLogic,
  searchStringFun: (Category) -> String,
) {


  val expenseCategories: List<Category> by expenseCategoryFilterDialogLogic.expenseCategories.collectAsState()
  val selectedExpenseCategories: List<Category> by expenseCategoryFilterDialogLogic.selectedExpenseCategories.collectAsState()

  CategoryCheckList(
    text = "Expense categories",
    items = expenseCategories,
    checkedItems = selectedExpenseCategories,
    searchStringFun = searchStringFun,
    onSelection = { selectedItems ->
      expenseCategoryFilterDialogLogic.eventSelection(
        new_selectedExpenseCategories = selectedItems
      )
    }
  )
}

private class ExpenseCategoryFilterDialogLogic(
  init_selectedExpenseCategories: List<Category>
) {
  private val p_selectedExpenseCategories: MutableStateFlow<List<Category>> = MutableStateFlow(init_selectedExpenseCategories)
  val selectedExpenseCategories: StateFlow<List<Category>> get() = p_selectedExpenseCategories

  private val p_expenseCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
  val expenseCategories: StateFlow<List<Category>> get() = p_expenseCategories

  init {
    CoroutineScope(context = Dispatchers.Default).launch {
      p_expenseCategories.value = SingletonProvider.getInstance().getAppData().fetchCategories(
        financeType = FinanceType.Expense,
        withUnknown = true
      )
    }
  }

  fun eventSelection(
    new_selectedExpenseCategories: List<Category>
  ) {
    p_selectedExpenseCategories.value = new_selectedExpenseCategories
  }
}

// endregion

// region Income

@Composable
fun IncomeCategoryFilterDialog(
  selectedIncomeCategories: List<Category>,
  searchStringFun: (Category) -> String,
  onDismiss: () -> Unit,
  onConfirm: (List<Category>) -> Unit
) {

  val incomeCategoryFilterDialogLogic = remember {
    IncomeCategoryFilterDialogLogic(
      init_selectedIncomeCategories = selectedIncomeCategories
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Column(
      modifier = Modifier
        .background(FiBuTheme.contentColors.contrast)
        .padding(6.dp)
    ) {

      IncomeCategoryFilterDialogBox(
        incomeCategoryFilterDialogLogic = incomeCategoryFilterDialogLogic,
        searchStringFun = searchStringFun
      )

      ButtonRow(buttons = listOf(
        ButtonRowItem(
          text = "Cancel",
          onClick = onDismiss
        ),
        ButtonRowItem(
          text = "Confirm",
          onClick = {
            onConfirm(incomeCategoryFilterDialogLogic.selectedIncomeCategories.value)
          }
        )
      ))
    }
  }
}

@Composable
private fun IncomeCategoryFilterDialogBox(
  incomeCategoryFilterDialogLogic: IncomeCategoryFilterDialogLogic,
  searchStringFun: (Category) -> String,
) {


  val incomeCategories: List<Category> by incomeCategoryFilterDialogLogic.incomeCategories.collectAsState()
  val selectedIncomeCategories: List<Category> by incomeCategoryFilterDialogLogic.selectedIncomeCategories.collectAsState()

  CategoryCheckList(
    text = "Income categories",
    items = incomeCategories,
    checkedItems = selectedIncomeCategories,
    searchStringFun = searchStringFun,
    onSelection = { selectedItems ->
      incomeCategoryFilterDialogLogic.eventSelection(
        new_selectedIncomeCategories = selectedItems
      )
    }
  )
}

private class IncomeCategoryFilterDialogLogic(
  init_selectedIncomeCategories: List<Category>
) {
  private val p_selectedIncomeCategories: MutableStateFlow<List<Category>> = MutableStateFlow(init_selectedIncomeCategories)
  val selectedIncomeCategories: StateFlow<List<Category>> get() = p_selectedIncomeCategories

  private val p_incomeCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
  val incomeCategories: StateFlow<List<Category>> get() = p_incomeCategories

  init {
    CoroutineScope(context = Dispatchers.Default).launch {
      p_incomeCategories.value = SingletonProvider.getInstance().getAppData().fetchCategories(
        financeType = FinanceType.Income,
        withUnknown = true
      )
    }
  }

  fun eventSelection(
    new_selectedIncomeCategories: List<Category>
  ) {
    p_selectedIncomeCategories.value = new_selectedIncomeCategories
  }
}

// endregion

// region Expense and Income

@Composable
fun ExpenseIncomeCategoryFilterDialog(
  selectedExpenseCategories: List<Category>,
  selectedIncomeCategories: List<Category>,
  searchStringFun: (Category) -> String,
  onDismiss: () -> Unit,
  onConfirm: (List<Category>, List<Category>) -> Unit
) {

  val expenseCategoryFilterDialogLogic = remember {
    ExpenseCategoryFilterDialogLogic(
      init_selectedExpenseCategories = selectedExpenseCategories
    )
  }
  val incomeCategoryFilterDialogLogic = remember {
    IncomeCategoryFilterDialogLogic(
      init_selectedIncomeCategories = selectedIncomeCategories
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Column(
      modifier = Modifier
        .background(FiBuTheme.contentColors.contrast)
        .padding(6.dp)
    ) {

      ExpenseCategoryFilterDialogBox(
        expenseCategoryFilterDialogLogic = expenseCategoryFilterDialogLogic,
        searchStringFun = searchStringFun
      )
      IncomeCategoryFilterDialogBox(
        incomeCategoryFilterDialogLogic = incomeCategoryFilterDialogLogic,
        searchStringFun = searchStringFun
      )

      ButtonRow(buttons = listOf(
        ButtonRowItem(
          text = "Cancel",
          onClick = onDismiss
        ),
        ButtonRowItem(
          text = "Confirm",
          onClick = {
            onConfirm(
              expenseCategoryFilterDialogLogic.selectedExpenseCategories.value,
              incomeCategoryFilterDialogLogic.selectedIncomeCategories.value
            )
          }
        )
      ))
    }
  }
}

// endregion

@Composable
private fun CategoryCheckList(
  text: String,
  items: List<Category>,
  checkedItems: List<Category>,
  searchStringFun: (Category) -> String,
  onSelection: (List<Category>) -> Unit
){
  Text(
    text = text,
    color = FiBuTheme.contentColors.primary
  )
  Checklist(
    modifier = Modifier
      .height(50.dp)
      .background(FiBuTheme.contentColors.background),
    items = items,
    checkedItems = checkedItems,
    content = { transactionCategory ->
      Row(
        modifier = Modifier.padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier
            .background(transactionCategory.color, RoundedCornerShape(4.dp)),
          painter = painterResource(id = transactionCategory.icon),
          contentDescription = null,
          tint = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = transactionCategory.name,
          color = FiBuTheme.contentColors.primary
        )
      }
    },
    searchStringFun = searchStringFun,
    onSelection = { selectedItems -> onSelection(selectedItems)}
  )
}