package com.fibu.logic.navigation.screeninfo

import androidx.compose.runtime.Composable
import com.fibu.logic.navigation.ScreenInfoImpl
import com.fibu.logic.navigation.Screens
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.navigation.NavController
import com.fibu.ui.screens.settings.ScreenSettings
import com.fibu.ui.screens.settings.ScreenLogicSettings
import com.fibu.ui.screens.transaction.ScreenCreateTransaction

class ScreenInfoSettings(
  title: String,
  override val screenLogic: ScreenLogicSettings,
  override val initFun: () -> Unit,
): ScreenInfoImpl<ScreenLogicSettings>(
  title = title,
  screenLogic = screenLogic,
  initFun = initFun
) {

  @Composable
  override fun Content(navController: NavController) {
    ScreenSettings(
      navController = navController,
      screenLogic = screenLogic
    )
  }

}

fun Screens.settings(): ScreenInfoSettings {

  val screenLogic: ScreenLogicSettings = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicSettings::class.java)

  return ScreenInfoSettings(
    title = "Categories",
    screenLogic = screenLogic,
    initFun = {}
  )
}