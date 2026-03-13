package com.fibu.logic.navigation.screeninfo

import androidx.compose.runtime.Composable
import com.fibu.logic.navigation.ScreenInfoImpl
import com.fibu.logic.navigation.Screens
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.info.Category
import com.fibu.logic.navigation.NavController
import com.fibu.ui.screens.category.ScreenEditCategory
import com.fibu.ui.screens.category.ScreenLogicEditCategory
import com.fibu.ui.screens.transaction.ScreenCreateTransaction

class ScreenInfoEditCategory(
  title: String,
  override val screenLogic: ScreenLogicEditCategory,
  override val initFun: () -> Unit
): ScreenInfoImpl<ScreenLogicEditCategory>(
  title = title,
  screenLogic = screenLogic,
  initFun = initFun
) {

  @Composable
  override fun Content(navController: NavController) {
    ScreenEditCategory(
      navController = navController,
      screenLogic = screenLogic
    )
  }

}

fun Screens.editCategory(
  category: Category
): ScreenInfoEditCategory {

  val screenLogic: ScreenLogicEditCategory = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicEditCategory::class.java)

  return ScreenInfoEditCategory(
    title = "Edit category",
    screenLogic = screenLogic,
    initFun = {
      screenLogic.init(category)
    }
  )
}