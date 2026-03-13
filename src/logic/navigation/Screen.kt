package com.fibu.logic.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fibu.logic.ScreenLogic

//region Base classes
interface ScreenInfo{
  var title: String
  val screenLogic: ScreenLogic

  val initFun: () -> Unit

  @Composable
  fun Content(navController: NavController)

}

abstract class ScreenInfoImpl<T : ScreenLogic>(
  override var title: String = "",
  override val initFun: () -> Unit,
  override val screenLogic: T
): ScreenInfo {

  @Composable
  override fun Content(navController: NavController) { Text("NO CONTENT") }
}


object Screens