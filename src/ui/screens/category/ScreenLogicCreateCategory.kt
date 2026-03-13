package com.fibu.ui.screens.category

import androidx.lifecycle.viewModelScope
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.DB
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import kotlinx.coroutines.launch

class ScreenLogicCreateCategory() : ScreenLogicCategory() {

  lateinit var p_financeType: FinanceType

  fun init(
    financeType: FinanceType
  ) {
    viewModelScope.launch {
      p_financeType = financeType

      clearCommonUI()

      p_originaluiState = UIState.empty()
      p_uiState.value = p_originaluiState
    }
  }

  override fun eventConfirm(
    action: () -> Unit
  ) {
    if(!confirmError()){

      viewModelScope.launch {
        SingletonProvider.getInstance().getAppData().addCategory(
          Category.withRandomId(
            name = p_uiState.value.name,
            financeType = p_financeType,
            color = p_uiState.value.color,
            icon = p_uiState.value.icon
          )
        )
      }
      p_checkInputChanged = false
      action()

    }
  }
}

