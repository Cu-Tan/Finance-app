package com.fibu.logic.navigation.screeninfo

import androidx.compose.runtime.Composable
import com.fibu.logic.navigation.ScreenInfoImpl
import com.fibu.logic.navigation.Screens
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.navigation.NavController
import com.fibu.ui.screens.statistics.ScreenStatistics
import com.fibu.ui.screens.statistics.ScreenLogicStatistics

class ScreenInfoStatistics(
  title: String,
  override val screenLogic: ScreenLogicStatistics,
  override val initFun: () -> Unit
): ScreenInfoImpl<ScreenLogicStatistics>(
  title = title,
  screenLogic = screenLogic,
  initFun = initFun
) {

  @Composable
  override fun Content(navController: NavController) {
    ScreenStatistics(navController, screenLogic)
  }

}

fun Screens.statistics(): ScreenInfoStatistics {

  val screenLogic: ScreenLogicStatistics = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicStatistics::class.java)

  return ScreenInfoStatistics(
    title = "Statistics",
    screenLogic = screenLogic,
    initFun = {
      screenLogic.init()
    }
  )
}