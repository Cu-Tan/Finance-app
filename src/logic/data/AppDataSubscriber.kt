package com.fibu.logic.data

interface AppDataSubscriber {

  fun onNotify(action: AppData.Action)

}