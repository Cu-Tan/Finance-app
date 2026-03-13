package com.fibu.ui.screens.category

import androidx.lifecycle.viewModelScope
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.DB
import com.fibu.logic.info.Category
import kotlinx.coroutines.launch

class ScreenLogicEditCategory: ScreenLogicCategory() {

  private lateinit var p_oldCategory: Category

  fun init(
    category: Category
  ) {
    viewModelScope.launch {
      p_oldCategory = category

      clearCommonUI()

      p_originaluiState = UIState(
        name = category.name,
        color = category.color,
        icon = category.icon
      )
      p_uiState.value = p_originaluiState
    }
  }

  override fun eventConfirm(
    action: () -> Unit
  ) {
    if(!confirmError()){

      SingletonProvider.getInstance().getAppData().updateCategory(
        oldCategory = p_oldCategory,
        newCategory = p_oldCategory.copy(
          name = p_uiState.value.name,
          color = p_uiState.value.color,
          icon = p_uiState.value.icon
        )
      )
      p_checkInputChanged = false
      action()

    }
  }
}