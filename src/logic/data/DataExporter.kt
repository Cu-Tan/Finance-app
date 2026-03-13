package com.fibu.logic.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.fibu.logic.SingletonProvider
import com.fibu.logic.serialization.Serializable
import com.fibu.logic.serialization.SerializedCategory
import com.fibu.logic.serialization.SerializedTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.OutputStream

/**
 * Exports all data to a selected file
 * @param bufferMaxSize max amount of items that are written in one chunk to a file
 */
class DataExporter(
  val bufferMaxSize: Int = 32
) {

  fun export(
    context: Context,
    uri: Uri
  ) {

    CoroutineScope(Dispatchers.IO).launch {

      context.contentResolver.openOutputStream(uri)?.use { stream ->

        val db: DB = SingletonProvider.getInstance().getDB()

        processTable(
          stream,
          bufferMaxSize,
          db::getCategoryCount,
          db::getSerializedCategories
        )
        processTable(
          stream,
          bufferMaxSize,
          db::getTransactionCount,
          db::getSerializedTransactions
        )

      }

    }

  }

  private suspend fun processTable(
    stream: OutputStream,
    bufferMaxSize: Int,
    getRowsCount: suspend () -> Int,
    getRows: suspend ((Serializable) -> Unit) -> Unit
  ) {

    // Write the amount of table rows to the output stream
    val rowCount = getRowsCount()
    val byteStream: ByteArrayOutputStream = ByteArrayOutputStream()
    Serializable.bufferInt(byteStream, rowCount)
    stream.write(byteStream.toByteArray())

    // Write each row in chunks of bufferMaxSize

    val serializedCategoryBuffer: ArrayList<Serializable> = ArrayList(bufferMaxSize)

    getRows{ serializable ->

      serializedCategoryBuffer.add(serializable)

      if(serializedCategoryBuffer.size >= bufferMaxSize){

        writeSerializedData(
          stream,
          serializedCategoryBuffer
        )

        serializedCategoryBuffer.clear()

      }
    }

    if(serializedCategoryBuffer.isNotEmpty()){
      writeSerializedData(
        stream,
        serializedCategoryBuffer
      )
    }

  }

  private fun writeSerializedData(
    stream: OutputStream,
    serializedData: ArrayList<Serializable>
  ) {

    val byteStream: ByteArrayOutputStream = ByteArrayOutputStream()
    serializedData.forEach { serializable ->
      byteStream.write(serializable.getSerialized())
    }
    stream.write(byteStream.toByteArray())

  }

}