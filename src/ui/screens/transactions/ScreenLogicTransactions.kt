package com.fibu.ui.screens.transactions

import androidx.lifecycle.viewModelScope
import com.fibu.logic.Date
import com.fibu.logic.DateRange
import com.fibu.logic.ScreenLogic
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.AppData
import com.fibu.logic.data.AppDataSubscriber
import com.fibu.logic.info.SortType
import com.fibu.logic.daysInMonths
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.logic.info.Transaction
import com.fibu.ui.utility.date.DateSelectorBoxTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ScreenLogicTransactions: ScreenLogic(), AppDataSubscriber {

  // region Variables

  private val searchText: MutableStateFlow<String> = MutableStateFlow("")
  private val sortType: MutableStateFlow<SortType> = MutableStateFlow(SortType.Date)

  private val unfilteredTransactions: MutableStateFlow<List<DisplayTransaction>> = MutableStateFlow(emptyList())
  private val transactions: MutableStateFlow<List<DisplayTransaction>> = MutableStateFlow(emptyList())

  private val dateSelectorBoxType: MutableStateFlow<DateSelectorBoxTypes> = MutableStateFlow(DateSelectorBoxTypes.DAY)
  private val dateRange: MutableStateFlow<DateRange> = MutableStateFlow(DateRange.now())

  private val filterDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

  private val filterSet: MutableStateFlow<Boolean> = MutableStateFlow(false)

  private val selectedExpenseCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
  private val selectedIncomeCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())

  private var categoriesDirty: Boolean = true
  private var transactionsDirty: Boolean = true

  // endregion

  // region Functions

  override fun onNotify(action: AppData.Action) {

    when (action) {
      is AppData.Action.CATEGORY -> {
        categoriesDirty = true
        if(isActive){ init() }
      }
      is AppData.Action.TRANSACTION -> {
        transactionsDirty = true
        if(isActive){ init() }
      }
      is AppData.Action.FULL -> {
        categoriesDirty = true
        transactionsDirty = true
        if(isActive){ init() }
      }
    }

  }

  fun getSearchText(): StateFlow<String> {
    return searchText
  }
  fun getSortType(): MutableStateFlow<SortType> {
    return sortType
  }
  fun getTransactions(): StateFlow<List<DisplayTransaction>> {
    return transactions
  }
  fun getDateSelectorBoxType(): MutableStateFlow<DateSelectorBoxTypes> {
    return dateSelectorBoxType
  }
  fun getDateRange(): StateFlow<DateRange> {
    return dateRange
  }
  fun getFilterDialogActive(): StateFlow<Boolean> {
    return filterDialogActive
  }
  fun getFilterSet(): StateFlow<Boolean> {
    return filterSet
  }
  fun getSelectedExpenseCategories(): StateFlow<List<Category>> {
    return selectedExpenseCategories
  }
  fun getSelectedIncomeCategories(): StateFlow<List<Category>> {
    return selectedIncomeCategories
  }

  override fun init() {

    super.init()

    if(categoriesDirty){
      categoriesDirty = false
      transactionsDirty = false
      viewModelScope.launch {
        updateCategories()
        updateTransactions()
      }
    }
    if(transactionsDirty && !categoriesDirty){
      transactionsDirty = false
      viewModelScope.launch {
        updateTransactions()
      }
    }
  }

  fun init(
    category: Category,
    dateRange: DateRange,
    dateSelectorBoxType: DateSelectorBoxTypes
  ) {

    viewModelScope.launch {
      when(category.financeType){
        FinanceType.Expense -> {
          selectedExpenseCategories.value = listOf(category)
          selectedIncomeCategories.value = emptyList()
        }
        FinanceType.Income -> {
          selectedExpenseCategories.value = emptyList()
          selectedIncomeCategories.value = listOf(category)
        }
      }

      this@ScreenLogicTransactions.dateSelectorBoxType.value = dateSelectorBoxType
      this@ScreenLogicTransactions.dateRange.value = dateRange

      filterSet.value = true
      updateTransactions()
    }
  }

  fun onSearchTextInput(
      text: String
  ){
    searchText.value = text
    filterTransactions()
  }
  fun eventSetDateSelectorBoxType(
      newDateSelectorBoxType: DateSelectorBoxTypes
  ) {
    dateSelectorBoxType.value = newDateSelectorBoxType
    when(dateSelectorBoxType.value){
      DateSelectorBoxTypes.DAY -> {
        val currentDate: Date = Date.now()
        dateRange.value = DateRange(
          startDate = currentDate,
          endDate = currentDate
        )
      }
      DateSelectorBoxTypes.MONTH -> {
        val currentDate = Date.now()
        dateRange.value = DateRange(
          startDate = Date(currentDate.year, currentDate.month, 1),
          endDate = Date(currentDate.year, currentDate.month, currentDate.daysInMonth)
        )
      }
      DateSelectorBoxTypes.YEAR -> {
        val currentDate = Date.now()
        dateRange.value = DateRange(
          startDate = Date(currentDate.year, 1, 1),
          endDate = Date(currentDate.year, 12, daysInMonths[12 - 1])
        )
      }
      DateSelectorBoxTypes.CUSTOM -> {

      }
    }
    viewModelScope.launch {
      updateTransactions()
    }
  }
  fun eventSetDateRange(
      newDateRange: DateRange
  ) {
    dateRange.value = newDateRange
    viewModelScope.launch {
      updateTransactions()
    }
  }
  fun eventSetFilterDialog(
      value: Boolean
  ) {
    filterDialogActive.value = value
  }
  fun eventSetFilteredCategories(
      expenseCategories: List<Category>,
      incomeCategories: List<Category>
  ) {

    // No need to do anything if the filter has not changed
    if(selectedExpenseCategories.value == expenseCategories && selectedIncomeCategories.value == incomeCategories){
      return
    }

    filterSet.value = true
    selectedExpenseCategories.value = expenseCategories
    selectedIncomeCategories.value = incomeCategories
    viewModelScope.launch {
      updateTransactions()
    }
  }
  fun eventSetSortType(
      newSortType: SortType
  ) {
    sortType.value = newSortType
    sortTransactions()
    filterTransactions()
  }
  fun eventClearFilter(){

    val appData: AppData = SingletonProvider.getInstance().getAppData()

    filterSet.value = false
    viewModelScope.launch {
      selectedExpenseCategories.value = appData.fetchCategories(
        financeType = FinanceType.Expense,
        withUnknown = true
      )
      selectedIncomeCategories.value = appData.fetchCategories(
        financeType = FinanceType.Income,
        withUnknown = true
      )
      updateTransactions()
    }
  }

  private fun sortTransactions() {
    when(sortType.value){
      SortType.Date -> unfilteredTransactions.value = unfilteredTransactions.value.sortedByDescending { it.transaction.dateTime.toString() }
      SortType.Value -> unfilteredTransactions.value = unfilteredTransactions.value.sortedByDescending { it.transaction.value }
    }
  }
  private fun filterTransactions() {

    if(searchText.value.isBlank()){
      transactions.value = unfilteredTransactions.value
      return
    }

    transactions.value = unfilteredTransactions.value.filter { transaction ->
      transaction.transaction.note.contains(searchText.value, ignoreCase = true)
    }
  }
  private suspend fun updateCategories() {

    val appData: AppData = SingletonProvider.getInstance().getAppData()

    if(!filterSet.value){
      selectedExpenseCategories.value = appData.fetchCategories(
        financeType = FinanceType.Expense,
        withUnknown = true
      )
      selectedIncomeCategories.value = appData.fetchCategories(
        financeType = FinanceType.Income,
        withUnknown = true
      )
    }
    else {

      val newCategories = appData.fetchCategories()

      val newFilteredExpenseCategories: MutableList<Category> = mutableListOf()
      for(category in selectedExpenseCategories.value){
        if(category.id.isEmpty()){
          newFilteredExpenseCategories.add(category)
        }
        else {

          for(newCategory in newCategories){
            if(category.id == newCategory.id){
              newFilteredExpenseCategories.add(category)
            }
          }

        }

      }
      selectedExpenseCategories.value = newFilteredExpenseCategories

      val newFilteredIncomeCategories: MutableList<Category> = mutableListOf()
      for(category in selectedIncomeCategories.value){
        if(category.id.isEmpty()){
          newFilteredIncomeCategories.add(category)
        }
        else {

          for(newCategory in newCategories){
            if(category.id == newCategory.id){
              newFilteredIncomeCategories.add(category)
            }
          }

        }
      }
      selectedIncomeCategories.value = newFilteredIncomeCategories
    }

  }
  private suspend fun updateTransactions(){

    val appData: AppData = SingletonProvider.getInstance().getAppData()

    val newUnfilteredTransactions: MutableList<DisplayTransaction> = mutableListOf()

    outer@ for(transaction in appData.fetchTransactions(dateRange = dateRange.value)){
      when(transaction.type){
        FinanceType.Expense -> {
          for(category in selectedExpenseCategories.value){
            if(transaction.categoryId == category.id){
              newUnfilteredTransactions.add( DisplayTransaction(
                transaction,
                category
              ))
              continue@outer
            }
          }
        }
        FinanceType.Income -> {
          for(category in selectedIncomeCategories.value){
            if(transaction.categoryId == category.id){
              newUnfilteredTransactions.add( DisplayTransaction(
                transaction,
                category
              ))
              continue@outer
            }
          }
        }
      }
    }

    unfilteredTransactions.value = newUnfilteredTransactions

    sortTransactions()
    filterTransactions()
  }

  // endregion

}