package com.fibu.logic.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

private const val NAVIGATION_STACK_LIMIT = 6

class NavController(
  initialScreen: ScreenInfo
) {

  private val navigationStack: SnapshotStateList<ScreenInfo> = mutableStateListOf(initialScreen)

  val screenTitle: String get() = navigationStack.last().title
  val currentScreen: ScreenInfo get() = navigationStack.last()

  @Composable
  fun CurrentScreen(navController: NavController) {
    navigationStack.last().Content(navController = navController)
  }

  fun canGoBack() : Boolean {
    return (navigationStack.size > 1)
  }

  fun back() {
    if(canGoBack()){
      currentScreen.screenLogic.eventExit(
        exitAction = {
          navigationStack.removeAt(navigationStack.size - 1)
          currentScreen.initFun()
        }
      )
    }
  }
  fun navigate(
    targetScreen: ScreenInfo
  ) {

    if(targetScreen::class == currentScreen::class){
      return
    }

    currentScreen.screenLogic.eventExit(
      exitAction = {
        navigationStack.add(targetScreen)
        currentScreen.initFun()
        if(navigationStack.size > NAVIGATION_STACK_LIMIT){
          navigationStack.removeAt(1)
        }
      }
    )
  }

  init{
    currentScreen.initFun()
  }

}