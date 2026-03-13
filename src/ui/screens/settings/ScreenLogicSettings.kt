package com.fibu.ui.screens.settings

import androidx.lifecycle.viewModelScope
import com.fibu.logic.ScreenLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScreenLogicSettings: ScreenLogic() {

  private var p_action: suspend () -> Unit = {}

  private val p_actionAlert: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val actionAlert: StateFlow<Boolean> = p_actionAlert

  fun eventActionAlertOn(
    action: suspend () -> Unit
  ) {
    p_action = action
    p_actionAlert.value = true
  }

  fun eventActionAlertOff(){
    p_action = {}
    p_actionAlert.value = false
  }

  fun eventConfirmAction(){
    p_action.let { action ->
      viewModelScope.launch {
        action()
      }
    }
    p_action = {}
    p_actionAlert.value = false
  }
}