package com.fibu.ui.screens.categories


import androidx.lifecycle.viewModelScope
import com.fibu.logic.data.DB
import com.fibu.logic.ScreenLogic
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.AppData
import com.fibu.logic.data.AppDataSubscriber
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScreenLogicCategories: ScreenLogic(), AppDataSubscriber {

  override fun onNotify(action: AppData.Action) {

    if(action is AppData.Action.CATEGORY || action is AppData.Action.FULL){
      categoryDataDirty = true
      if(isActive){ init() }
    }

  }

  private val p_financeType: MutableStateFlow<FinanceType> = MutableStateFlow(FinanceType.Expense)
  val financeType: StateFlow<FinanceType> = p_financeType

  private val p_categories: MutableStateFlow<List<Category>> = MutableStateFlow(listOf())
  val categories: StateFlow<List<Category>> = combine(
    p_categories, p_financeType
  ) { _categories, _financeType ->
    _categories.filter { it.financeType == _financeType}
  }
  .stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = emptyList()
  )

  private val p_alertDeleteCategory: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val alertDeleteCategory: StateFlow<Boolean> = p_alertDeleteCategory

  private var p_categoryToDelete: Category? = null

  override fun init(){

    super.init()

    if(categoryDataDirty) {
      categoryDataDirty = false
      CoroutineScope(Dispatchers.Main).launch {
        p_categories.value = SingletonProvider.getInstance().getAppData().fetchCategories()
      }
    }

  }

  fun eventSetFinanceType(
    newFinanceType: FinanceType
  ) {
    p_financeType.value = newFinanceType
  }
  fun eventSetCategoryToDelete(
    newCategoryToDelete: Category
  ) {
    p_categoryToDelete = newCategoryToDelete
  }
  fun eventSetAlertDeleteCategory(
    value: Boolean
  ) {
    p_alertDeleteCategory.value = value
  }
  fun eventDeleteCategory() {

    SingletonProvider.getInstance().getAppData().deleteCategory(p_categoryToDelete!!)
    p_categoryToDelete = null

  }

  private var categoryDataDirty: Boolean = true

}