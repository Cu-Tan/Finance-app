package com.fibu.logic.serialization

import java.io.ByteArrayOutputStream

class SerializedTransaction(
  val id: String,
  val categoryId: String,
  val type: String,
  val value: String,
  val dateTime: String,
  val note: String
): Serializable() {

  override fun getSerialized(): ByteArray {

    if(byteArray == null){
      serialize()
    }

    return byteArray!!

  }

  override fun serialize() {

    val byteStream = ByteArrayOutputStream()
    bufferString(byteStream, id)
    bufferString(byteStream, categoryId)
    bufferString(byteStream, type)
    bufferString(byteStream, value)
    bufferString(byteStream, dateTime)
    bufferString(byteStream, note)
    byteArray = byteStream.toByteArray()

  }

  private var byteArray: ByteArray? = null

}