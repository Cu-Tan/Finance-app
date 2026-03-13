package com.fibu.logic.navigation.screeninfo

import androidx.compose.runtime.Composable
import com.fibu.logic.navigation.ScreenInfoImpl
import com.fibu.logic.navigation.Screens
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.navigation.NavController
import com.fibu.ui.screens.categories.ScreenCategories
import com.fibu.ui.screens.categories.ScreenLogicCategories
import com.fibu.ui.screens.statistics.ScreenStatistics

class ScreenInfoCategories(
  title: String,
  override val screenLogic: ScreenLogicCategories,
  override val initFun: () -> Unit,
): ScreenInfoImpl<ScreenLogicCategories>(
  title = title,
  screenLogic = screenLogic,
  initFun = initFun
) {

  @Composable
  override fun Content(navController: NavController) {
    ScreenCategories(
      navController = navController,
      screenLogic = screenLogic
    )
  }

}

fun Screens.categories(): ScreenInfoCategories {

  val screenLogic: ScreenLogicCategories = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicCategories::class.java)

  return ScreenInfoCategories(
    title = "Categories",
    screenLogic = screenLogic,
    initFun = {
      screenLogic.init()
    }
  )
}