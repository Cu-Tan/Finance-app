package com.fibu.logic.navigation.screeninfo

import androidx.compose.runtime.Composable
import com.fibu.logic.navigation.ScreenInfoImpl
import com.fibu.logic.navigation.Screens
import com.fibu.logic.DateRange
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.info.Category
import com.fibu.logic.navigation.NavController
import com.fibu.ui.screens.transaction.ScreenCreateTransaction
import com.fibu.ui.screens.transactions.ScreenTransactions
import com.fibu.ui.screens.transactions.ScreenLogicTransactions
import com.fibu.ui.utility.date.DateSelectorBoxTypes

class ScreenInfoTransactions(
  title: String,
  override val screenLogic: ScreenLogicTransactions,
  override val initFun: () -> Unit
): ScreenInfoImpl<ScreenLogicTransactions>(
  title = title,
  screenLogic = screenLogic,
  initFun = initFun
) {

  @Composable
  override fun Content(navController: NavController) {
    ScreenTransactions(
      navController = navController,
      screenLogic = screenLogic
    )
  }

}

fun Screens.transactions(): ScreenInfoTransactions {

  val screenLogic: ScreenLogicTransactions = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicTransactions::class.java)

  return ScreenInfoTransactions(
    title = "Transactions",
    screenLogic = screenLogic,
    initFun = {
      screenLogic.init()
    }
  )
}

fun Screens.transactions(
  category: Category,
  dateRange: DateRange,
  dateSelectorBoxType: DateSelectorBoxTypes
): ScreenInfoTransactions {

  val screenLogic: ScreenLogicTransactions = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicTransactions::class.java)

  return ScreenInfoTransactions(
    title = "Transactions",
    screenLogic = screenLogic,
    initFun = {
      screenLogic.init(
        category = category,
        dateRange = dateRange,
        dateSelectorBoxType = dateSelectorBoxType
      )
    }
  )
}