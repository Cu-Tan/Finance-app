package com.fibu.logic.navigation.screeninfo

import androidx.compose.runtime.Composable
import com.fibu.logic.navigation.ScreenInfoImpl
import com.fibu.logic.navigation.Screens
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.info.FinanceType
import com.fibu.logic.navigation.NavController
import com.fibu.ui.screens.categories.ScreenCategories
import com.fibu.ui.screens.category.ScreenCreateCategory
import com.fibu.ui.screens.category.ScreenLogicCreateCategory

class ScreenInfoCreateCategory(
  title: String,
  override val screenLogic: ScreenLogicCreateCategory,
  override val initFun: () -> Unit
): ScreenInfoImpl<ScreenLogicCreateCategory>(
  title = title,
  screenLogic = screenLogic,
  initFun = initFun
) {

  @Composable
  override fun Content(navController: NavController) {
    ScreenCreateCategory(
      navController = navController,
      screenLogic = screenLogic
    )
  }

}

fun Screens.createCategory(
  financeType: FinanceType
): ScreenInfoCreateCategory {

  val screenLogic: ScreenLogicCreateCategory = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicCreateCategory::class.java)

  return ScreenInfoCreateCategory(
    title = when(financeType){
      FinanceType.Expense -> {
        "Create expense category"
      }
      FinanceType.Income -> {
        "Create income category"
      }
    },
    screenLogic = screenLogic,
    initFun = {
      screenLogic.init(financeType)
    }
  )
}