package com.fibu.ui.screens.transaction

import androidx.lifecycle.viewModelScope
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.DB
import com.fibu.logic.info.Category
import com.fibu.logic.info.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ScreenLogicEditTransaction: ScreenLogicTransaction() {

  private lateinit var p_oldTransaction: Transaction

  private val p_deleteTransactionAlert: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val deleteTransactionAlert: StateFlow<Boolean> = p_deleteTransactionAlert

  fun init(
    transaction: Transaction
  ) {

    viewModelScope.launch {

      clearCommonUI()

      p_deleteTransactionAlert.value = false
      p_oldTransaction = transaction

      var category: Category? = null

      try{
        category = SingletonProvider.getInstance().getAppData().findCategory(transaction.categoryId)
      } catch (e: Exception){
        category = Category.unknown(financeType = transaction.type)
      }

      p_originalUIState = UIState(
        financeType = transaction.type,
        valueText = transaction.value.toString(),
        category = category,
        dateTime = transaction.dateTime,
        noteText = transaction.note
      )
      p_uiState.value = p_originalUIState

    }
  }

  override fun eventConfirm(
    action: () -> Unit
  ) {

    if(confirmError()){ return }

      val newTransaction: Transaction = Transaction(
        id = p_oldTransaction.id,
        categoryId = p_uiState.value.category!!.id,
        type = p_uiState.value.financeType,
        value = BigDecimal(p_uiState.value.valueText.replace(",", ".")),
        dateTime = p_uiState.value.dateTime,
        note = p_uiState.value.noteText
      )
      SingletonProvider.getInstance().getAppData().updateTransaction(
        oldTransaction = p_oldTransaction,
        newTransaction = newTransaction
      )

    p_checkInputChanged = false
    action()
  }

  fun eventSetDeleteTransactionAlert(
    value: Boolean
  ) {
    p_deleteTransactionAlert.value = value
  }

  fun deleteTransaction(){
    SingletonProvider.getInstance().getAppData().deleteTransaction(p_oldTransaction)
  }
}