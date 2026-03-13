package com.fibu
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.theme.FiBuTheme
import com.fibu.ui.screens.root.ScreenRoot
import com.fibu.ui.screens.root.ScreenLogicRoot
import kotlin.jvm.java


class MainActivity : ComponentActivity() {

  @SuppressLint("UnrememberedMutableState")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    initApp(this)

    setContent {
      Box(modifier = Modifier.padding(
        top = with(LocalDensity.current){WindowInsets.statusBars.getTop(LocalDensity.current).toDp()},
        bottom = with(LocalDensity.current){WindowInsets.navigationBars.getBottom(LocalDensity.current).toDp()}
      )) {
        FiBuTheme {
          ScreenRoot(
            screenLogic = ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicRoot::class.java)
          )
        }
      }
    }
  }
}