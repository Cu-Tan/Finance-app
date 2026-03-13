package com.fibu.ui.screens.root

import com.fibu.logic.navigation.NavController
import com.fibu.logic.navigation.Screens
import com.fibu.logic.ScreenLogic
import com.fibu.logic.data.AppData
import com.fibu.logic.data.AppDataSubscriber
import com.fibu.logic.navigation.screeninfo.statistics
import com.fibu.ui.utility.navigation.DrawerState
import com.fibu.ui.utility.navigation.DrawerStateValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScreenLogicRoot: ScreenLogic() {

  val navController: NavController = NavController(initialScreen = Screens.statistics())

  private val p_sideNavState: MutableStateFlow<DrawerState> = MutableStateFlow(DrawerState(initialValue = DrawerStateValue.Closed))
  val sideNavState: StateFlow<DrawerState> get() = p_sideNavState

  private val p_exitAppDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val exitAppDialog: StateFlow<Boolean> get() = p_exitAppDialog

  fun openSideNav(){
    p_sideNavState.value.open()
  }
  fun closeSideNav(){
    p_sideNavState.value.close()
  }
  fun setExitAppDialog(
    value: Boolean
  ) {
    p_exitAppDialog.value = value
  }

}