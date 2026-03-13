package com.fibu.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fibu.logic.SingletonProvider
import com.fibu.logic.navigation.NavController
import com.fibu.logic.data.DB
import com.fibu.logic.data.DataExporter
import com.fibu.logic.data.DataImporter
import com.fibu.logic.holdable
import com.fibu.theme.FiBuTheme
import com.fibu.ui.dialogs.Alert
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.ButtonRowItem

@Composable
fun ScreenSettings(
  navController: NavController,
  screenLogic: ScreenLogicSettings
) {



  val actionAlert: Boolean by screenLogic.actionAlert.collectAsState()

  BackHandler(
    enabled = navController.canGoBack()
  ) {
    navController.back()
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(FiBuTheme.contentColors.contrast)
      .padding(24.dp)
  ) {
    SettingsDB(
      screenLogic = screenLogic
    )
  }

  if(actionAlert){
    Alert(
      onDismiss = {
        screenLogic.eventActionAlertOff()
      },
      text = "Are you sure you want to perform this action?",
      buttons = listOf(
        ButtonRowItem(
          text = "Cancel",
          onClick = {
            screenLogic.eventActionAlertOff()
          }
        ),
        ButtonRowItem(
          text = "Confirm",
          onClick = {
            screenLogic.eventConfirmAction()
          }
        )
      )
    )
  }

}

@Composable
private fun SettingsDB(
  screenLogic: ScreenLogicSettings
){

  val writeFile = rememberWriteFileLauncher()
  val readFile = rememberReadFileLauncher()

  Column {

    Title(text = "Database settings")
    
    Button(
      modifier = Modifier.background(FiBuTheme.buttonDefaultColors.secondary),
      onClick = {
        screenLogic.eventActionAlertOn(
          action = {
            writeFile.launch("FiBu_DB.sql")
          }
        )
      }
    ) {
      Row (
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Filled.Edit,
          contentDescription = null,
          tint = FiBuTheme.contentColors.primary
        )
        Text(
          text = "Export data",
          color = FiBuTheme.contentColors.primary
        )
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Button(
      modifier = Modifier.background(FiBuTheme.buttonDefaultColors.secondary),
      onClick = {
        screenLogic.eventActionAlertOn(
          action = {
            readFile.launch(arrayOf("application/sql"))
          }
        )
      }
    ) {
      Row (
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Filled.Edit,
          contentDescription = null,
          tint = FiBuTheme.contentColors.primary
        )
        Text(
          text = "Import data",
          color = FiBuTheme.contentColors.primary
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Title(text = "Scary zone")

    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .background(Color(130, 0, 0))
          .holdable(
            duration = 3000L,
            onHold = {
              screenLogic.eventActionAlertOn(
                action = {
                  SingletonProvider.getInstance().getAppData().clearData()
                }
              )
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Row (
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = null,
            tint = FiBuTheme.contentColors.primary
          )
          Text(
            text = "Delete all data",
            color = FiBuTheme.contentColors.primary
          )
        }
      }
      Spacer(modifier = Modifier.width(8.dp))
      HorizontalDivider(
        modifier = Modifier
          .width(16.dp),
        color = FiBuTheme.contentColors.background,
        thickness = 2.dp
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Hold for 3 seconds",
        color = FiBuTheme.contentColors.primary,
        fontSize = 12.sp
      )
    }
  }
}

@Composable
private fun Title(
  text: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ){
    HorizontalDivider(
      modifier = Modifier.weight(1f),
      thickness = 2.dp,
      color = FiBuTheme.contentColors.background
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      color = FiBuTheme.contentColors.primary
    )
    Spacer(modifier = Modifier.width(6.dp))
    HorizontalDivider(
      modifier = Modifier.weight(1f),
      thickness = 2.dp,
      color = FiBuTheme.contentColors.background
    )
  }
}

@Composable
fun rememberWriteFileLauncher(): ManagedActivityResultLauncher<String, Uri?> {
  val context = LocalContext.current
  return rememberLauncherForActivityResult(
    ActivityResultContracts.CreateDocument("application/sql")
  ) { uri ->
    uri?.let {

      val dataExporter = DataExporter()

      dataExporter.export(
        context,
        it
      )

    }
  }
}

@Composable
fun rememberReadFileLauncher(): ManagedActivityResultLauncher<Array<String>, Uri?> {
  val context = LocalContext.current
  return rememberLauncherForActivityResult(
    ActivityResultContracts.OpenDocument()
  ) { uri ->
    uri?.let {

      val dataImporter = DataImporter()

      dataImporter.import(
        context,
        it
      )

    }
  }
}