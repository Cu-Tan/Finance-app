package com.fibu.logic

import com.fibu.logic.data.AppData
import com.fibu.logic.data.DB

class SingletonProvider private constructor() {

  companion object {

    // TODO: might want to log instead of returning on failure
    fun deleteInstance(owner: Object) {

      if(this.owner == null){
        return
      }

      if(this.owner != owner){
        return
      }

      instance = null

    }
    fun createInstance(owner: Object) {

      if(instance != null){
        return
      }

      this.owner = owner
      instance = SingletonProvider()

    }
    fun getInstance(): SingletonProvider {

      if(instance == null){
        throw Exception("Instance is null")
      }

      return instance!!

    }

    private var owner: Object? = null
    private var instance: SingletonProvider? = null

  }

  fun setAppData(appData: AppData) {
    this.appData = appData
  }
  fun getAppData(): AppData {

    if(appData == null){
      throw Exception("Appdata null")
    }

    return appData!!
  }

  fun setDB(db: DB) {
    this.db = db
  }
  fun getDB(): DB {

    if(db == null){
      throw Exception("DB null")
    }

    return db!!

  }

  private var appData: AppData? = null
  private var db: DB? = null

}