package com.fibu.logic.data

import com.fibu.logic.DateRange
import com.fibu.logic.IdType
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.logic.info.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AppData(
  val db: DB
) {

  sealed interface Action {
    object CATEGORY : Action
    object TRANSACTION : Action
    object FULL :  Action
  }

  fun addSubscriber(
    subscriber: AppDataSubscriber
  ) {
    subscribers.add(subscriber)
  }

  fun notifySubscribers(
    action: Action
  ) {
    subscribers.forEach { it.onNotify(action) }
  }

  // region DB functions

  fun clearData(){
    dbCoroutineScope.launch {
      db.clearData()
      notifySubscribers(Action.FULL)
    }
  }

  // endregion

  // region Category functions
  fun addCategory(
    category: Category
  ) {
    dbCoroutineScope.launch {
      val result = db.addCategory(category)
      notifySubscribers(Action.CATEGORY)
    }
  }
  fun updateCategory(
    oldCategory: Category,
    newCategory: Category
  ) {
    dbCoroutineScope.launch {
      val result = db.updateCategory(oldCategory, newCategory)
      notifySubscribers(Action.CATEGORY)
    }
  }
  fun deleteCategory(
    category: Category
  ) {
    dbCoroutineScope.launch {
      val result = db.deleteCategory(category)
      notifySubscribers(Action.CATEGORY)
    }
  }
  suspend fun fetchCategories(
    financeType: FinanceType? = null,
    withUnknown: Boolean = false,
  ): List<Category> {
    return db.fetchCategories(financeType, withUnknown)
  }
  suspend fun findCategory(
    id: String
  ): Category {
    return db.findCategory(id)
  }
  // endregion

  // region Transaction functions
  fun addTransaction(
    transaction: Transaction
  ) {
    dbCoroutineScope.launch {
      val result: Boolean = db.addTransaction(transaction)
      notifySubscribers(Action.TRANSACTION)
    }
  }
  fun updateTransaction(
    oldTransaction: Transaction,
    newTransaction: Transaction
  ) {
    dbCoroutineScope.launch {
      val result: Boolean = db.updateTransaction(oldTransaction, newTransaction)
      notifySubscribers(Action.TRANSACTION)
    }
  }
  fun deleteTransaction(
    transaction: Transaction
  ) {
    dbCoroutineScope.launch {
      val result: Boolean = db.deleteTransaction(transaction)
      notifySubscribers(Action.TRANSACTION)
    }
  }
  suspend fun fetchTransactions(
    dateRange: DateRange,
    category: Category? = null,
    financeType: FinanceType? = null,
  ): List<Transaction> {
    return db.fetchTransactions(dateRange, category, financeType)
  }
  // endregion

  // region ID functions

  suspend fun fetchIDS(
    idType: IdType,
  ): List<String> {
    return db.fetchIDS(idType)
  }

  // endregion

  private val subscribers: MutableList<AppDataSubscriber> = mutableListOf()
  private val dbCoroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
  private val categories: MutableList<Category> = mutableListOf()

  init {
    categories.add(Category.unknown(FinanceType.Income))
  }


  // Public vars
  // Public func

  // Private vars
  // Private func

}