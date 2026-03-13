package com.fibu.logic

import androidx.lifecycle.ViewModel

open class ScreenLogic: ViewModel() {

  protected var isActive = false

  open fun init(){
    isActive = true
  }

  open fun eventExit(
    exitAction: () -> Unit
  ) {
    isActive = false
    exitAction()
  }

}