package com.fibu.ui.screens.root

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fibu.logic.navigation.NavHost
import com.fibu.logic.navigation.ScreenInfo
import com.fibu.logic.navigation.Screens
import com.fibu.logic.navigation.screeninfo.categories
import com.fibu.logic.navigation.screeninfo.createTransaction
import com.fibu.logic.navigation.screeninfo.settings
import com.fibu.logic.navigation.screeninfo.statistics
import com.fibu.logic.navigation.screeninfo.transactions
import com.fibu.theme.FiBuTheme
import com.fibu.ui.dialogs.Alert
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.ButtonRowItem
import com.fibu.ui.utility.TitleBar
import com.fibu.ui.utility.layout.Scaffold
import com.fibu.ui.utility.navigation.DrawerState
import com.fibu.ui.utility.navigation.NavBar
import com.fibu.ui.utility.navigation.NavTab
import com.fibu.ui.utility.navigation.NavigationDrawer

@Composable
fun ScreenRoot(
  screenLogic: ScreenLogicRoot
) {

  val bottomNavBarTabs = listOf(
    NavTabInfo.Statistics,
    NavTabInfo.Transactions,
    NavTabInfo.Categories
  )
  val sideNavTabs = listOf(
    NavTabInfo.Statistics,
    NavTabInfo.Transactions,
    NavTabInfo.Categories,
    NavTabInfo.Settings
  )

  val sideNavState: DrawerState by screenLogic.sideNavState.collectAsState()
  val exitAppDialog: Boolean by screenLogic.exitAppDialog.collectAsState()

  BackHandler(
    enabled = !screenLogic.navController.canGoBack()
  ) {
    screenLogic.setExitAppDialog(true)
  }

  Scaffold(
    topBar = {
      TitleBar(
        modifier = Modifier.background(FiBuTheme.contentColors.background),
        title = screenLogic.navController.screenTitle
      )
    },
    bottomBar = {
      NavBar(
        modifier = Modifier
          .background(FiBuTheme.contentColors.background)
          .height(50.dp),
        actionButton = {
          Surface(
            modifier = Modifier
              .clickable {
                screenLogic.navController.navigate(
                  targetScreen = Screens.createTransaction()
                )
              }
              .padding(6.dp),
            shape = RoundedCornerShape(12.dp),
            color = FiBuTheme.buttonDefaultColors.secondary
          ) {
            Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = null,
              tint = FiBuTheme.buttonDefaultColors.primary
            )
          }
        }
      ) {
        bottomNavBarTabs.forEach{ navTabInfo ->
          NavTab(
            title = navTabInfo.title,
            icon = navTabInfo.icon,
            isSelected = (navTabInfo.screenInfo::class == screenLogic.navController.currentScreen::class),
            onClick = {
              screenLogic.navController.navigate(
                targetScreen = navTabInfo.screenInfo()
              )
            }
          )
        }
        NavTab(
          title = "More",
          icon = Icons.Filled.Menu,
          isSelected = false
        ) {
          screenLogic.openSideNav()
        }
      }
    }
  ) {
    NavHost(navController = screenLogic.navController)
  }
  if(sideNavState.isOpen){
    NavigationDrawer(
      modifier = Modifier
        .offset(x = LocalConfiguration.current.screenWidthDp.dp * 0.3f)
        .fillMaxSize()
        .background(FiBuTheme.contentColors.background),
      state = sideNavState,
      onDismiss = { screenLogic.closeSideNav() }
    ){
      Column{
        sideNavTabs.forEach { navTabInfo ->
          SideNavTab(
            title = navTabInfo.title,
            icon = navTabInfo.icon,
            onClick = {
              screenLogic.closeSideNav()
              screenLogic.navController.navigate(
                targetScreen = navTabInfo.screenInfo()
              )
            }
          )
        }
      }
    }
  }
  if(exitAppDialog){
    val context = LocalContext.current as Activity
    Alert(
      onDismiss = { screenLogic.setExitAppDialog(false) },
      text = "Do you wish to exit the app?",
      buttons = listOf(
        ButtonRowItem(
          text = "No",
          onClick = { screenLogic.setExitAppDialog(false) }
        ),
        ButtonRowItem(
          text = "Yes",
          onClick = { context.finish() }
        )
      )
    )
  }
}

@Composable
private fun SideNavTab(
  title: String,
  icon: ImageVector,
  onClick: () -> Unit
){
  Button(onClick = onClick) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ){
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White
      )
      Text(
        text = title,
        color = FiBuTheme.contentColors.primary
      )
    }
  }
}


private enum class NavTabInfo(
  val title: String,
  val icon: ImageVector,
  val screenInfo: () -> ScreenInfo
) {
  Statistics(
    title = "Statistics",
    icon = Icons.Filled.Home,
    screenInfo = { Screens.statistics() }
  ),
  Transactions(
    title = "Transaction",
    icon = Icons.Filled.Create,
    screenInfo = { Screens.transactions() }
  ),
  Categories(
    title = "Categories",
    icon = Icons.Filled.Create,
    screenInfo = { Screens.categories() }
  ),
  Settings(
    title = "Settings",
    icon = Icons.Filled.Settings,
    screenInfo = { Screens.settings() }
  )
}