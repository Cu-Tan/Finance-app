package com.fibu.logic

import com.fibu.logic.data.DB
import java.util.UUID

class IdGenerator private constructor() {

  companion object {
    private var instance: IdGenerator? = null
    fun getInstance(): IdGenerator {
      return instance ?: IdGenerator().also { instance = it }
    }
  }

  suspend fun category() : String{
    return generate(IdType.CATEGORY)
  }
  suspend fun transaction() : String{
    return generate(IdType.INCOME)
  }

  private suspend fun generate(
    idType: IdType
  ): String{
    lateinit var id: String
    val ids: List<String> = SingletonProvider.getInstance().getAppData().fetchIDS(idType)
    do{
      id = UUID.randomUUID().toString()
    } while(ids.contains(id))
    return id
  }
}

enum class IdType{
  CATEGORY, INCOME
}