package com.fibu.ui.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.DB
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.theme.FiBuTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun CategorySearchMenu(
  financeType: FinanceType,
  selectedCategory: Category?,
  eventSetCategory: (Category) -> Unit
) {

  val focusRequester = remember { FocusRequester() }

  val p_categories: MutableStateFlow<List<Category>> = remember { MutableStateFlow(emptyList()) }
  val categories: List<Category> by p_categories.collectAsState()

  LaunchedEffect(financeType) {
    p_categories.value = SingletonProvider.getInstance().getAppData().fetchCategories(financeType, true)
  }

  DropdownSearchMenu(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 50.dp)
      .background(FiBuTheme.contentColors.background)
      .focusRequester(focusRequester),
    items = categories,
    itemContent = dropdownSearchMenuItemContent,
    selectedItem = selectedCategory,
    selectedItemContent = dropdownSearchMenuItemContent,
    menuColor = FiBuTheme.contentColors.background,
    searchStringFun = { category ->
      category.name
    },
    onSelection = { category ->
      eventSetCategory(category)
    }
  )
}

private val dropdownSearchMenuItemContent: @Composable (Category) -> Unit = { category ->
  Row(
    modifier = Modifier.padding(6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier
        .background(category.color, RoundedCornerShape(4.dp)),
      painter = painterResource(id = category.icon),
      contentDescription = null,
      tint = Color.White
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = category.name, color = FiBuTheme.contentColors.primary)
  }
}