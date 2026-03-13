package com.fibu.logic.data

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.fibu.logic.Date
import com.fibu.logic.SingletonProvider
import com.fibu.logic.convertStringToDateTime
import com.fibu.logic.hexToColor
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.logic.info.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.math.BigDecimal

class DataImporter(
  val bufferMaxSize: Int = 32
) {

  fun import(
    context: Context,
    uri: Uri
  ) {

    CoroutineScope(Dispatchers.IO).launch {

      val db: DB = SingletonProvider.getInstance().getDB()
      db.clearData()

      context.contentResolver.openInputStream(uri)?.use { stream ->

        processCategories(stream, bufferMaxSize, db)
        processTransactions(stream, bufferMaxSize, db)

      }

      SingletonProvider.getInstance().getAppData().notifySubscribers(AppData.Action.FULL)

    }

  }

  private suspend fun processCategories(
    stream: InputStream,
    bufferMaxSize: Int,
    db: DB
  ) {

    val count: Int = readInt(stream)

    var categoryBuffer: ArrayList<Category> = ArrayList(bufferMaxSize)

    for(i in 0 until count){

      val id = readString(stream)
      val name = readString(stream)
      val financeType = readString(stream)
      val color = readString(stream)
      val icon = readInt(stream)

      categoryBuffer.add( Category(
        id = id,
        name = name,
        financeType = FinanceType.valueOf(financeType),
        color = Color.hexToColor(color),
        icon = icon
      ))

      if(categoryBuffer.size >= bufferMaxSize){
        categoryBuffer.forEach { category -> db.addCategory(category)}
        categoryBuffer = ArrayList(bufferMaxSize)
      }

    }

    if(categoryBuffer.isNotEmpty()){
      categoryBuffer.forEach { category -> db.addCategory(category)}
    }

  }

  private suspend fun processTransactions(
    stream: InputStream,
    bufferMaxSize: Int,
    db: DB
  ) {

    val count: Int = readInt(stream)
    var transactionBuffer: ArrayList<Transaction> = ArrayList(bufferMaxSize)

    for(i in 0 until count){

      val id: String = readString(stream)
      val categoryId: String = readString(stream)
      val financeType: String = readString(stream)
      val value: String = readString(stream)
      val dateTime: String = readString(stream)
      val note: String = readString(stream)

      transactionBuffer.add( Transaction(
        id = id,
        categoryId = categoryId,
        type = FinanceType.valueOf(financeType),
        value = BigDecimal(value),
        dateTime = Date.convertStringToDateTime(dateTime),
        note = note
      ))

      if(transactionBuffer.size >= bufferMaxSize){
        transactionBuffer.forEach { transaction -> db.addTransaction(transaction)}
        transactionBuffer = ArrayList(bufferMaxSize)
      }

    }

    if(transactionBuffer.isNotEmpty()){
      transactionBuffer.forEach { transaction -> db.addTransaction(transaction)}
    }

  }

  private fun readInt(
    stream: InputStream
  ): Int {

    var returnInt = stream.read()
    returnInt = (returnInt shl 8) or stream.read()
    returnInt = (returnInt shl 8) or stream.read()
    returnInt = (returnInt shl 8) or stream.read()

    return returnInt
  }

  private fun readString(
    stream: InputStream
  ): String {

    val stringLength = readInt(stream)

    val stringBuilder: StringBuilder = StringBuilder()
    for(i in 0 until stringLength){
      stringBuilder.append(stream.read().toChar())
    }

    return stringBuilder.toString()
  }

}