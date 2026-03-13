package com.fibu.ui.screens.statistics

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fibu.logic.navigation.NavController
import com.fibu.logic.navigation.Screens
import com.fibu.logic.DateRange
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.logic.navigation.screeninfo.transactions
import com.fibu.theme.FiBuTheme
import com.fibu.ui.dialogs.ExpenseCategoryFilterDialog
import com.fibu.ui.dialogs.IncomeCategoryFilterDialog
import com.fibu.ui.utility.Button
import com.fibu.ui.utility.DonutChart
import com.fibu.ui.utility.DonutChartData
import com.fibu.ui.utility.FinanceTypeSelector
import com.fibu.ui.utility.TitleBar
import com.fibu.ui.utility.date.DateSelectorBox
import com.fibu.ui.utility.date.DateSelectorBoxTypes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun ScreenStatistics(
  navController: NavController,
  screenLogic: ScreenLogicStatistics
) {

  val financeType: FinanceType by screenLogic.getFinanceType().collectAsState()
  val dateSelectorBoxType: DateSelectorBoxTypes by screenLogic.getDateSelectorBoxType().collectAsState()
  val dateRange: DateRange by screenLogic.getDateRange().collectAsState()
  val donutChartData: DonutChartData by screenLogic.getDonutChartData().collectAsState()
  val filterSet: Boolean by screenLogic.getFilterSet().collectAsState()
  val filterDialogActive: Boolean by screenLogic.getFilterDialogActive().collectAsState()

  val statisticsCategories: List<StatisticsCategory> by screenLogic.getStatisticsCategories().collectAsState()
  val filteredCategories: List<Category> by screenLogic.getFilteredCategories().collectAsState()

  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()
  val flashQueue: SnapshotStateMap<Int, Boolean> = remember { mutableStateMapOf() }

  BackHandler(
    enabled = navController.canGoBack()
  ) {
    navController.back()
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(FiBuTheme.contentColors.contrast)
      .padding(start = 24.dp, end = 24.dp, bottom = 6.dp)
  ){
    FinanceTypeSelector(
      selectedFinanceType = financeType,
      onClick = { screenLogic.eventSetFinanceType(it) }
    )
    Column{
      DateSelectorBox(
        selectedDateSelectorBoxType = dateSelectorBoxType,
        selectedDateRange = dateRange,
        eventSetDateSelectorBoxType = { newDateSelectorBoxType ->
          screenLogic.eventSetDateSelectorBoxType(newDateSelectorBoxType)
        },
        onConfirm = { newDateRange ->
          screenLogic.eventSetDateRange(newDateRange)
        }
      )
      DonutChart(
        modifier = Modifier.fillMaxWidth(),
        data = donutChartData,
        chartSize = 250.dp,
        onClick = { itemIndex ->
          coroutineScope.launch {
            listState.animateScrollToItemIfNotVisible(itemIndex)
            flashQueue[itemIndex] = true
            delay(500)
            flashQueue.remove(itemIndex)
          }
        }
      )
      TitleBar(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(FiBuTheme.contentColors.background),
        title = when(financeType) {
          FinanceType.Expense -> "Expenses"
          FinanceType.Income -> "Income"
        },
        leftContent = {
          if(filterSet){
            Button(
              modifier = Modifier
                .border(
                  1.dp,
                  FiBuTheme.buttonDefaultColors.secondary,
                  RoundedCornerShape(4.dp)
                )
                .padding(6.dp),
              onClick = { screenLogic.eventClearFilter() }
            ) {
              Row (verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Filter",
                  color = FiBuTheme.contentColors.primary
                )
                Icon(
                  imageVector = Icons.Filled.Clear,
                  contentDescription = null,
                  tint = Color(130, 0, 0)
                )
              }
            }
          }
        },
        rightContent = {
          Button(
            modifier = Modifier
              .border(
                1.dp,
                FiBuTheme.buttonDefaultColors.secondary,
                RoundedCornerShape(4.dp)
              )
              .padding(6.dp),
            onClick = { screenLogic.eventSetFilterDialog(true) }
          ) {
            Text(
              text = "Set Filter",
              color = FiBuTheme.contentColors.primary
            )
          }
        }
      )
      Spacer(modifier = Modifier.height(4.dp))
      LazyColumn(
        modifier = Modifier.padding(bottom = 6.dp),
        state = listState
      ) {
        items(statisticsCategories){ statisticsCategory ->
          val flash = flashQueue[statisticsCategories.indexOf(statisticsCategory)] ?: false
          val flashColor by animateColorAsState(
            targetValue = if(flash) Color.White.copy(alpha = 0.5f) else FiBuTheme.contentColors.background,
            animationSpec = tween(250),
            label = ""
          )
          StatisticsCategoryBox(
            color = flashColor,
            statisticsCategory = statisticsCategory,
            onClick = {
              navController.navigate(
                targetScreen = Screens.transactions(
                  category = statisticsCategory,
                  dateRange = dateRange,
                  dateSelectorBoxType = dateSelectorBoxType
                )
              )
            }
          )
          Spacer(modifier = Modifier.height(4.dp))
        }
      }
    }
  }
  if(filterDialogActive){
    when(financeType) {
      FinanceType.Expense -> {
        ExpenseCategoryFilterDialog(
          selectedExpenseCategories = filteredCategories,
          searchStringFun = { category ->
            category.name
          },
          onDismiss = {
            screenLogic.eventSetFilterDialog(false)
          },
          onConfirm = { categories ->
            screenLogic.eventSetFilterDialog(false)
            screenLogic.eventSetFilter(categories)
          }
        )
      }
      FinanceType.Income -> {
        IncomeCategoryFilterDialog(
          selectedIncomeCategories = filteredCategories,
          searchStringFun = { category ->
            category.name
          },
          onDismiss = {
            screenLogic.eventSetFilterDialog(false)
          },
          onConfirm = { categories ->
            screenLogic.eventSetFilterDialog(false)
            screenLogic.eventSetFilter(categories)
          }
        )
      }
    }

  }
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun StatisticsCategoryBox(
  color: Color,
  statisticsCategory: StatisticsCategory,
  onClick: () -> Unit
) {
  BoxWithConstraints {
    val leftWidth = maxWidth * .75f
    Row(
      modifier = Modifier
        .clickable { onClick() }
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(color)
        .padding(6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.widthIn(max = leftWidth),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.background(statisticsCategory.color, RoundedCornerShape(4.dp)),
          painter = painterResource(id = statisticsCategory.icon),
          contentDescription = null,
          tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = statisticsCategory.name,
          color = FiBuTheme.contentColors.primary
        )
      }
      Column(
        horizontalAlignment = Alignment.End
      ) {
        Text(
          text = "${String.format("%.2f", statisticsCategory.value)} $",
          color = FiBuTheme.contentColors.primary,
        )
        Text(
          text = "${String.format("%.2f", statisticsCategory.percentage)} %",
          color = Color.Gray,
        )
      }
    }
  }
}
private suspend fun LazyListState.animateScrollToItemIfNotVisible(
  index: Int
) {
  val itemInfo = this.layoutInfo.visibleItemsInfo.find { it.index == index }
  if(itemInfo != null){
    val viewportStartOffset = this.layoutInfo.viewportStartOffset
    val viewportEndOffset = this.layoutInfo.viewportEndOffset
    val itemStartOffset = itemInfo.offset
    val itemEndOffset = itemInfo.offset + itemInfo.size

    val isFullyVisible = itemStartOffset >= viewportStartOffset && itemEndOffset <= viewportEndOffset
    if (!isFullyVisible) {
      this.animateScrollToItem(index)
    }
  } else {
    this.animateScrollToItem(index)
  }
}
class StatisticsCategory(
  category: Category,
  var value: BigDecimal = BigDecimal(0),
  var percentage: BigDecimal = BigDecimal(0)
): Category (
  id = category.id,
  name = category.name,
  financeType = category.financeType,
  color = category.color,
  icon = category.icon
)