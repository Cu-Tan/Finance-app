package com.fibu.logic.serialization

import java.io.ByteArrayOutputStream

class SerializedCategory(
  val id: String,
  val name: String,
  val financeType: String,
  val color: String,
  val icon: Int
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
    bufferString(byteStream, name)
    bufferString(byteStream, financeType)
    bufferString(byteStream, color)
    bufferInt(byteStream, icon)
    byteArray = byteStream.toByteArray()

  }

  private var byteArray: ByteArray? = null

}