package com.fibu.logic.info
import com.fibu.logic.DateTime
import com.fibu.logic.IdGenerator
import java.math.BigDecimal

open class Transaction (
  val id: String,
  var categoryId: String,
  var type: FinanceType,
  var value: BigDecimal,
  var dateTime: DateTime,
  var note: String = ""
) {

  companion object{
    suspend fun withRandomID(
      categoryId: String,
      type: FinanceType,
      value: BigDecimal,
      dateTime: DateTime,
      note: String
    ): Transaction {
      return Transaction(
        id = IdGenerator.getInstance().transaction(),
        categoryId = categoryId,
        type = type,
        value = value,
        dateTime = dateTime,
        note = note
      )
    }
  }

  fun copy(
    categoryId: String = this.categoryId,
    type: FinanceType = this.type,
    value: BigDecimal = this.value,
    dateTime: DateTime = this.dateTime,
    note: String = this.note
  ): Transaction {
    return Transaction(
      id = this.id,
      categoryId = categoryId,
      type = type,
      value = value,
      dateTime = dateTime,
      note = note
    )
  }
}