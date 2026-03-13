package com.fibu.ui.screens.transaction

import androidx.lifecycle.viewModelScope
import com.fibu.logic.data.DB
import com.fibu.logic.DateTime
import com.fibu.logic.SingletonProvider
import com.fibu.logic.info.FinanceType
import com.fibu.logic.info.Transaction
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ScreenLogicCreateTransaction() : ScreenLogicTransaction() {

  override fun init(){

    clearCommonUI()

    p_originalUIState = UIState(
      financeType = FinanceType.Expense,
      valueText = "",
      category = null,
      dateTime = DateTime.now(),
      noteText = ""
    )
    p_uiState.value = p_originalUIState

  }

  override fun eventConfirm(
    action: () -> Unit
  ) {

    if(confirmError()){ return }

    viewModelScope.launch {
      val newTransaction: Transaction = Transaction.withRandomID(
        categoryId = p_uiState.value.category!!.id,
        type = p_uiState.value.financeType,
        value = BigDecimal(p_uiState.value.valueText.replace(",", ".")),
        dateTime = p_uiState.value.dateTime,
        note = p_uiState.value.noteText
      )
      SingletonProvider.getInstance().getAppData().addTransaction(newTransaction)
    }
    p_checkInputChanged = false
    action()
  }

}