package com.fibu.logic.navigation.screeninfo

import androidx.compose.runtime.Composable
import com.fibu.logic.navigation.ScreenInfoImpl
import com.fibu.logic.navigation.Screens
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.navigation.NavController
import com.fibu.ui.screens.category.ScreenCreateCategory
import com.fibu.ui.screens.transaction.ScreenCreateTransaction
import com.fibu.ui.screens.transaction.ScreenLogicCreateTransaction

class ScreenInfoCreateTransaction(
  title: String,
  override val screenLogic: ScreenLogicCreateTransaction,
  override val initFun: () -> Unit
): ScreenInfoImpl<ScreenLogicCreateTransaction>(
  title = title,
  screenLogic = screenLogic,
  initFun = initFun
) {

  @Composable
  override fun Content(navController: NavController) {
    ScreenCreateTransaction(
      navController = navController,
      screenLogic = screenLogic
    )
  }

}

fun Screens.createTransaction(): ScreenInfoCreateTransaction {

  val screenLogic: ScreenLogicCreateTransaction = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicCreateTransaction::class.java)

  return ScreenInfoCreateTransaction(
    title = "Add transaction",
    screenLogic = screenLogic,
    initFun = {
      screenLogic.init()
    }
  )
}