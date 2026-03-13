package com.fibu.logic.info

import androidx.compose.ui.graphics.Color
import com.fibu.R
import com.fibu.logic.IdGenerator
import kotlinx.coroutines.runBlocking

open class Category(
  val id: String,
  val name: String,
  val financeType: FinanceType,
  val color: Color,
  val icon: Int
) {

  companion object {
    suspend fun withRandomId(
      name: String,
      financeType: FinanceType,
      color: Color,
      icon: Int
    ): Category{
      return Category(
          id = IdGenerator.getInstance().category(),
          name = name,
          financeType = financeType,
          color = color,
          icon = icon
      )
    }

    fun unknown(
      financeType: FinanceType
    ): Category {
      return Category(
        id = "",
        name = "Unknown",
        financeType = financeType,
        color = Color.Black,
        icon = R.drawable.unknown_category_icon
      )
    }
  }

  fun copy(
    name: String = this.name,
    financeType: FinanceType = this.financeType,
    color: Color = this.color,
    icon: Int = this.icon
  ): Category{
    return Category(
      id = this.id,
      name = name,
      financeType = financeType,
      color = color,
      icon = icon
    )
  }

  override fun equals(other: Any?): Boolean {
    if(this === other) { return true }
    if(other !is Category) { return false }
    return this.id == other.id
           &&
           this.financeType == other.financeType
           &&
           this.name == other.name
           &&
           this.color == other.color
           &&
           this.icon == other.icon
  }
}