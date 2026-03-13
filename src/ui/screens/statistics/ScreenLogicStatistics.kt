package com.fibu.ui.screens.statistics

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.fibu.logic.data.DB
import com.fibu.logic.Date
import com.fibu.logic.DateRange
import com.fibu.logic.ScreenLogic
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.AppData
import com.fibu.logic.data.AppDataSubscriber
import com.fibu.logic.daysInMonths
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.logic.info.Transaction
import com.fibu.ui.utility.DonutChartData
import com.fibu.ui.utility.DonutChartItem
import com.fibu.ui.utility.date.DateSelectorBoxTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.collections.emptyList
import kotlin.collections.forEach

class ScreenLogicStatistics: ScreenLogic(), AppDataSubscriber {

  private val financeType: MutableStateFlow<FinanceType> = MutableStateFlow(FinanceType.Expense)
  private val dateSelectorBoxType: MutableStateFlow<DateSelectorBoxTypes> = MutableStateFlow(DateSelectorBoxTypes.DAY)
  private val donutChartData: MutableStateFlow<DonutChartData> = MutableStateFlow(DonutChartData())
  private val dateRange: MutableStateFlow<DateRange> = MutableStateFlow(DateRange.now())

  private val filteredExpenseCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
  private val filteredIncomeCategories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())

  private val expenseStatisticsCategories: MutableStateFlow<List<StatisticsCategory>> = MutableStateFlow(emptyList())
  private val incomeStatisticsCategories: MutableStateFlow<List<StatisticsCategory>> = MutableStateFlow(emptyList())

  private val expenseFilterSet: MutableStateFlow<Boolean> = MutableStateFlow(false)
  private val incomeFilterSet: MutableStateFlow<Boolean> = MutableStateFlow(false)

  private val filterDialogActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

  private var categoriesDirty: Boolean = true
  private var transactionsDirty: Boolean = true

  override fun onNotify(action: AppData.Action) {

    when(action){
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

  override fun init(){

    super.init()

    if(categoriesDirty){
      viewModelScope.launch {
        updateCategories()
        updateStatisticCategories()
        updateDonutChartData()
      }
    }
    if(transactionsDirty && !categoriesDirty){
      viewModelScope.launch {
        updateStatisticCategories()
        updateDonutChartData()
      }
    }

  }

  fun getFinanceType(): StateFlow<FinanceType> {
    return financeType
  }
  fun getDateSelectorBoxType(): StateFlow<DateSelectorBoxTypes> {
    return dateSelectorBoxType
  }
  fun getDonutChartData(): StateFlow<DonutChartData> {
    return donutChartData
  }
  fun getDateRange(): StateFlow<DateRange> {
    return dateRange
  }
  fun getFilteredCategories(): StateFlow<List<Category>> {
    return combine(
      financeType, filteredExpenseCategories, filteredIncomeCategories
    ) { financeType, expenseCategories, incomeCategories ->
      when(financeType){
        FinanceType.Expense -> { expenseCategories }
        FinanceType.Income -> { incomeCategories }
      }
    }.stateIn(
      viewModelScope,
      SharingStarted.Lazily,
      emptyList()
    )
  }
  fun getStatisticsCategories(): StateFlow<List<StatisticsCategory>> {
    return when(financeType.value){
      FinanceType.Expense -> { expenseStatisticsCategories }
      FinanceType.Income -> { incomeStatisticsCategories }
    }
  }
  fun getFilterSet(): StateFlow<Boolean> {
    return when(financeType.value){
      FinanceType.Expense -> { expenseFilterSet }
      FinanceType.Income -> { incomeFilterSet }
    }
  }
  fun getFilterDialogActive(): StateFlow<Boolean> {
    return filterDialogActive
  }

  fun eventSetFinanceType(
    newFinanceType: FinanceType
  ) {
    financeType.value = newFinanceType
    updateDonutChartData()
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
      updateStatisticCategories()
      updateDonutChartData()
    }
  }

  fun eventSetDateRange(
    newDateRange: DateRange
  ) {
    dateRange.value = newDateRange
    viewModelScope.launch {
      updateStatisticCategories()
      updateDonutChartData()
    }
  }

  fun eventClearFilter(){

    viewModelScope.launch {

      val appData: AppData = SingletonProvider.getInstance().getAppData()

      val newFilteredCategories: MutableList<Category> = mutableListOf()
      when(financeType.value){

        FinanceType.Expense -> {
          for(category in appData.fetchCategories(FinanceType.Expense, true)){
            newFilteredCategories.add(category)
          }
          filteredExpenseCategories.value = newFilteredCategories
          expenseFilterSet.value = false
        }

        FinanceType.Income -> {
          for(category in appData.fetchCategories(FinanceType.Income, true)){
            newFilteredCategories.add(category)
          }
          filteredIncomeCategories.value = newFilteredCategories
          incomeFilterSet.value = false
        }

      }
      updateStatisticCategories()
      updateDonutChartData()
    }
  }

  fun eventSetFilterDialog(
    value: Boolean
  ) {
    filterDialogActive.value = value
  }

  fun eventSetFilter(
    categories: List<Category>
  ) {
    viewModelScope.launch {

      val appData: AppData = SingletonProvider.getInstance().getAppData()

      val allCategories: List<Category> = appData.fetchCategories(financeType.value, true)
      val newFilteredCategories: MutableList<Category> = mutableListOf()

      when(financeType.value){

        FinanceType.Expense -> {
          for(category in categories){
            newFilteredCategories.add(category)
          }
          filteredExpenseCategories.value = newFilteredCategories
          expenseFilterSet.value = (allCategories != filteredExpenseCategories.value)
        }

        FinanceType.Income -> {
          for(category in categories){
            newFilteredCategories.add(category)
          }
          filteredIncomeCategories.value = newFilteredCategories
          incomeFilterSet.value = (allCategories != filteredIncomeCategories.value)
        }
      }
      updateStatisticCategories()
      updateDonutChartData()
    }
  }

  private suspend fun updateCategories(){

    val appData: AppData = SingletonProvider.getInstance().getAppData()

    if(!expenseFilterSet.value){
      filteredExpenseCategories.value = appData.fetchCategories(
        financeType = FinanceType.Expense,
        withUnknown = true
      )
    }
    else {

      val newExpenseCategories = appData.fetchCategories(
        financeType = FinanceType.Expense,
        withUnknown = true
      )

      val newFilteredExpenseCategories: MutableList<Category> = mutableListOf()
      for(category in filteredExpenseCategories.value) {
        // If id is empty it is an UNKNOWN system category
        if(category.id.isEmpty()){
          newFilteredExpenseCategories.add(category)
        }
        else {
          for(newCategory in newExpenseCategories){
            if(category.id == newCategory.id){
              newFilteredExpenseCategories.add(category)
            }
          }
        }
      }
      filteredExpenseCategories.value = newFilteredExpenseCategories

    }

    if(!incomeFilterSet.value){
      filteredIncomeCategories.value = appData.fetchCategories(
        financeType = FinanceType.Income,
        withUnknown = true
      )
    }
    else {

      val newIncomeCategories = appData.fetchCategories(
        financeType = FinanceType.Income,
        withUnknown = true
      )

      val newFilteredIncomeCategories: MutableList<Category> = mutableListOf()
      for(category in filteredExpenseCategories.value) {
        // If id is empty it is an UNKNOWN system category
        if(category.id.isEmpty()){
          newFilteredIncomeCategories.add(category)
        }
        else {
          for(newCategory in newIncomeCategories){
            if(category.id == newCategory.id){
              newFilteredIncomeCategories.add(category)
            }
          }
        }
      }
      filteredIncomeCategories.value = newFilteredIncomeCategories

    }

  }
  private suspend fun updateStatisticCategories() {

    val newStatisticsCategories: MutableList<StatisticsCategory> = mutableListOf()
    var totalSum: BigDecimal = BigDecimal(0)

    val filteredCategories: List<Category> = when(financeType.value){

      FinanceType.Expense -> {
        filteredExpenseCategories.value
      }

      FinanceType.Income -> {
        filteredIncomeCategories.value
      }
    }

    val appData: AppData = SingletonProvider.getInstance().getAppData()

    filteredCategories.forEach { category ->
      val transactions: List<Transaction> = appData.fetchTransactions(
        dateRange = dateRange.value,
        category = category
      )
      val sum = transactions.sumOf { it.value }
      totalSum += sum
      if(sum != BigDecimal(0)){
        val statisticsCategory: StatisticsCategory = StatisticsCategory(
          category = category,
          value = sum
        )
        newStatisticsCategories.add(statisticsCategory)
      }
    }
    newStatisticsCategories.forEach { statisticsCategory ->
      statisticsCategory.percentage = (statisticsCategory.value / totalSum) * BigDecimal(100)
    }

    newStatisticsCategories.sortByDescending { it.value }

    when(financeType.value){
      FinanceType.Expense -> { expenseStatisticsCategories.value = newStatisticsCategories }
      FinanceType.Income -> { incomeStatisticsCategories.value = newStatisticsCategories }
    }
  }
  private fun updateDonutChartData() {
    val donutChartItems: MutableList<DonutChartItem> = mutableListOf()
    getStatisticsCategories().value.forEach { statisticsCategory ->
      donutChartItems.add(
        DonutChartItem(
          value = statisticsCategory.value,
          color = statisticsCategory.color
        )
      )
    }
    donutChartData.value = DonutChartData(
      items = donutChartItems
    )
  }


}