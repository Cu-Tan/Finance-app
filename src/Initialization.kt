package com.fibu

import androidx.activity.ComponentActivity
import com.fibu.logic.data.DB
import com.fibu.logic.ScreenLogicRetriever
import com.fibu.logic.SingletonProvider
import com.fibu.logic.data.AppData
import com.fibu.logic.data.AppDataSubscriber
import com.fibu.ui.screens.categories.ScreenLogicCategories
import com.fibu.ui.screens.root.ScreenLogicRoot
import com.fibu.ui.screens.statistics.ScreenLogicStatistics
import com.fibu.ui.screens.transactions.ScreenLogicTransactions

fun initApp(
  activity: ComponentActivity
) {

  val db: DB = DB(activity.applicationContext)
  ScreenLogicRetriever.createInstance(activity)

  // Temporary owner to initialize SingletonProvider (Might be required in the future to store this owner somewhere)
  val owner: Object = Object()
  SingletonProvider.createInstance(owner)

  val appData: AppData = AppData(db)

  SingletonProvider.getInstance().setAppData(appData)
  SingletonProvider.getInstance().setDB(db)

  appData.addSubscriber(ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicStatistics::class.java))
  appData.addSubscriber(ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicTransactions::class.java))
  appData.addSubscriber(ScreenLogicRetriever.getInstance().getScreenLogic(ScreenLogicCategories::class.java))

}