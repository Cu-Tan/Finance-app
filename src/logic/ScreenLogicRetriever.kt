package com.fibu.logic

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class ScreenLogicRetriever private constructor() {

  companion object {
    private lateinit var p_owner: ViewModelStoreOwner
    private lateinit var p_instance: ScreenLogicRetriever
    fun createInstance(
      owner: ViewModelStoreOwner
    ) {
      p_owner = owner
      p_instance = ScreenLogicRetriever()
    }
    fun getInstance(): ScreenLogicRetriever {
      if(!::p_instance.isInitialized){
        throw Exception("[ScreenLogicRetriever -> getInstance] - instance is not initalized")
      }
      return p_instance
    }
  }

  fun <T: ScreenLogic> getScreenLogic(
    modelClass: Class<T>,
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory()
  ): T {
    return ViewModelProvider(
      owner = p_owner,
      factory = factory
    )[modelClass]
  }
}

/*

REMINDER ON FACTORY SYNTAX

object : ScreenLogicProvider.Factory {
override fun <T : ScreenLogic> create(modelClass: Class<T>): T {
  return CLASS(ARGS) as T
}

 */