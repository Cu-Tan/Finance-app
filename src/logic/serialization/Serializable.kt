package com.fibu.logic.serialization

import java.io.ByteArrayOutputStream

abstract class Serializable {

  abstract fun getSerialized(): ByteArray
  protected abstract fun serialize()

  companion object {

    fun bufferInt(
      byteStream: ByteArrayOutputStream,
      value: Int
    ) {

      byteStream.write((value shr 24) and 0xFF)
      byteStream.write((value shr 16) and 0xFF)
      byteStream.write((value shr 8) and 0xFF)
      byteStream.write(value and 0xFF)

    }

    fun bufferString(
      byteStream: ByteArrayOutputStream,
      value: String
    ) {

      val byteArray = value.toByteArray()

      bufferInt(
        byteStream,
        byteArray.size
      )

      byteStream.write(byteArray)

    }

  }



}