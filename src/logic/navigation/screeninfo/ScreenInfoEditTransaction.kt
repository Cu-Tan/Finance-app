package com.fibu.logic.navigation.screeninfo

import androidx.compose.runtime.Composable
import com.fibu.logic.navigation.ScreenInfoImpl
import com.fibu.logic.navigation.Screens
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.info.Transaction
import com.fibu.logic.navigation.NavController
import com.fibu.ui.screens.transaction.ScreenCreateTransaction
import com.fibu.ui.screens.transaction.ScreenEditTransaction
import com.fibu.ui.screens.transaction.ScreenLogicEditTransaction

class ScreenInfoEditTransaction(
  title: String,
  override val screenLogic: ScreenLogicEditTransaction,
  override val initFun: () -> Unit
): ScreenInfoImpl<ScreenLogicEditTransaction>(
  title = title,
  screenLogic = screenLogic,
  initFun = initFun
) {

  @Composable
  override fun Content(navController: NavController) {
    ScreenEditTransaction(
      navController = navController,
      screenLogic = screenLogic
    )
  }

}

fun Screens.editTransaction(
  transaction: Transaction
): ScreenInfoEditTransaction {

  val screenLogic: ScreenLogicEditTransaction = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicEditTransaction::class.java)

  return ScreenInfoEditTransaction(
    title = "Edit transaction",
    screenLogic = screenLogic,
    initFun = {
      screenLogic.init(
        transaction = transaction
      )
    }
  )
}