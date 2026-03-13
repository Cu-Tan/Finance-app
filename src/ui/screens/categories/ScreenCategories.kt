package com.fibu.ui.screens.categories


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fibu.logic.navigation.NavController
import com.fibu.logic.navigation.Screens
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.logic.navigation.screeninfo.createCategory
import com.fibu.logic.navigation.screeninfo.editCategory
import com.fibu.theme.FiBuTheme
import com.fibu.ui.dialogs.Alert
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.ButtonRowItem
import com.fibu.ui.utility.FinanceTypeSelector
import com.fibu.ui.utility.TitleBar

@Composable
fun ScreenCategories(
  navController: NavController,
  screenLogic: ScreenLogicCategories
){

  LaunchedEffect(Unit) {
    screenLogic.init()
  }

  BackHandler(
    enabled = navController.canGoBack()
  ) {
    navController.back()
  }

  val categories: List<Category> by screenLogic.categories.collectAsState()
  val financeType: FinanceType by screenLogic.financeType.collectAsState()
  val alertDeleteCategory: Boolean by screenLogic.alertDeleteCategory.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(FiBuTheme.contentColors.contrast)
      .padding(start = 24.dp, end = 24.dp, bottom = 6.dp)
  ) {
    FinanceTypeSelector(
      selectedFinanceType = financeType,
      onClick = { newFinanceType ->
        screenLogic.eventSetFinanceType(newFinanceType)
      }
    )
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(FiBuTheme.contentColors.background)
          .clickable {
            navController.navigate(
              targetScreen = Screens.createCategory(
                financeType = financeType
              )
            )
          },
        contentAlignment = Alignment.Center
      ) {
        Row(
          modifier = Modifier.padding(6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = Color.White
          )
          Text(
            text = when (financeType) {
              FinanceType.Expense -> "Add Expense Category"
              FinanceType.Income -> "Add Income Category"
            },
            color = FiBuTheme.contentColors.primary
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      LazyColumn{
        items(categories) { category ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(FiBuTheme.contentColors.background)
              .clickable {
                navController.navigate(
                  targetScreen = Screens.editCategory(
                    category = category
                  )
                )
              }
          ) {
            TitleBar(
              modifier = Modifier.background(FiBuTheme.contentColors.background),
              title = category.name,
              titleColor = FiBuTheme.contentColors.primary,
              leftContent = {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .background(
                      category.color,
                      RoundedCornerShape(4.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    painter = painterResource(id = category.icon),
                    contentDescription = null,
                    tint = Color.White
                  )
                }
              },
              rightContent = {
                Button(
                  onClick = {
                    screenLogic.eventSetAlertDeleteCategory(true)
                    screenLogic.eventSetCategoryToDelete(category)
                  }
                ) {
                  Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = Color.White
                  )
                }
              }
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
        }
      }
    }
  }
  if (alertDeleteCategory){
    Alert(
      onDismiss = { screenLogic.eventSetAlertDeleteCategory(false) },
      text = "Are you sure you want to delete this category?\n\nAny transactions tied to this category will be set to UNKNOWN category.",
      buttons = listOf(
        ButtonRowItem(
          text = "Cancel",
          onClick = { screenLogic.eventSetAlertDeleteCategory(false) }
        ),
        ButtonRowItem(
          text = "Delete",
          onClick = {
            screenLogic.eventSetAlertDeleteCategory(false)
            screenLogic.eventDeleteCategory()
          }
        )
      )
    )
  }
}