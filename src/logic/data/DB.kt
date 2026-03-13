package com.fibu.logic.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.fibu.logic.Date
import com.fibu.logic.DateRange
import com.fibu.logic.DateTime
import com.fibu.logic.IdType
import com.fibu.logic.convertStringToDateTime
import com.fibu.logic.hexToColor
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.logic.info.Transaction
import com.fibu.logic.serialization.Serializable
import com.fibu.logic.serialization.SerializedCategory
import com.fibu.logic.serialization.SerializedTransaction
import com.fibu.logic.toHex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.math.BigDecimal

/* TODO: Add clear logs*/

//region SQL_TABLE_CATEGORIES constants
private const val SQL_TABLE_CATEGORIES = "categories"
private const val SQL_TABLE_CATEGORIES_COLUMN_ID = "id"
private const val SQL_TABLE_CATEGORIES_COLUMN_NAME = "name"
private const val SQL_TABLE_CATEGORIES_COLUMN_TYPE = "type"
private const val SQL_TABLE_CATEGORIES_COLUMN_COLOR = "color"
private const val SQL_TABLE_CATEGORIES_COLUMN_ICON = "icon"
private const val SQL_TABLE_CATEGORIES_CREATE =
  "CREATE TABLE $SQL_TABLE_CATEGORIES (" +
      "$SQL_TABLE_CATEGORIES_COLUMN_ID TEXT PRIMARY KEY, " +
      "$SQL_TABLE_CATEGORIES_COLUMN_NAME TEXT, " +
      "$SQL_TABLE_CATEGORIES_COLUMN_TYPE TEXT, " +
      "$SQL_TABLE_CATEGORIES_COLUMN_COLOR TEXT, " +
      "$SQL_TABLE_CATEGORIES_COLUMN_ICON INTEGER)"

//endregion
//region SQL_TABLE_TRANSACTIONS constants
private const val SQL_TABLE_TRANSACTIONS = "transactions"
private const val SQL_TABLE_TRANSACTIONS_COLUMN_ID = "id"
private const val SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID = "category_id"
private const val SQL_TABLE_TRANSACTIONS_COLUMN_TYPE = "type"
private const val SQL_TABLE_TRANSACTIONS_COLUMN_VALUE = "value"
private const val SQL_TABLE_TRANSACTIONS_COLUMN_DATETIME = "date_time"
private const val SQL_TABLE_TRANSACTIONS_COLUMN_NOTE = "note"
private const val SQL_TABLE_TRANSACTIONS_CREATE =
  "CREATE TABLE $SQL_TABLE_TRANSACTIONS (" +
      "$SQL_TABLE_TRANSACTIONS_COLUMN_ID TEXT PRIMARY KEY, " +
      "$SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID TEXT, " +
      "$SQL_TABLE_TRANSACTIONS_COLUMN_TYPE TEXT, " +
      "$SQL_TABLE_TRANSACTIONS_COLUMN_VALUE TEXT, " +
      "$SQL_TABLE_TRANSACTIONS_COLUMN_DATETIME TEXT, " +
      "$SQL_TABLE_TRANSACTIONS_COLUMN_NOTE TEXT)"
//endregion
/**
 *  Singleton class to add, update, delete, fetch categories and transactions
 *  @param applicationContext ties the database to an application
 */
class DB (
  applicationContext: Context
): SQLiteOpenHelper (
  applicationContext, "CSFM.db", null, 1
) {

  private val _mutex = Mutex()

  override fun onCreate(db: SQLiteDatabase?) {
    CoroutineScope(Dispatchers.IO).launch {
      _mutex.withLock {
        try {
          Log.d("DBInfo", "onCreate: Start")
          val writabledb = this@DB.writableDatabase
          writabledb.execSQL(SQL_TABLE_CATEGORIES_CREATE)
          writabledb.execSQL(SQL_TABLE_TRANSACTIONS_CREATE)
          writabledb.close()
          Log.d("DBInfo", "onCreate: End")
        } catch (e: Exception) {
          Log.e("DBFail", "onCreate: Failed", e)
        }
      }
    }
  }
  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    /* TODO: IMPLEMENT WHEN NEEDED*/
  }

  /**
   * Clears the data inside all database tables
   */
  suspend fun clearData() {
    _mutex.withLock {
      withContext(Dispatchers.IO) {
        try {
          Log.d("DBInfo", "clearData: Start")
          val writabledb = this@DB.writableDatabase
          writabledb.delete(SQL_TABLE_CATEGORIES, null, null)
          writabledb.delete(SQL_TABLE_TRANSACTIONS, null, null)
          writabledb.close()
          Log.d("DBInfo", "clearData: End")
        } catch (e: Exception) {
          Log.e("DBFail", "clearData failed", e)
        }
      }
    }
  }

  /**
   * Drops and recreates all database tables
   */
  suspend fun nukeDB() {
    _mutex.withLock {
      withContext(Dispatchers.IO) {
        try {
          Log.d("DBInfo", "nukeDB: Start")
          val writabledb = this@DB.writableDatabase
          writabledb.execSQL("DROP TABLE IF EXISTS $SQL_TABLE_CATEGORIES")
          writabledb.execSQL("DROP TABLE IF EXISTS $SQL_TABLE_TRANSACTIONS")
          writabledb.execSQL(SQL_TABLE_CATEGORIES_CREATE)
          writabledb.execSQL(SQL_TABLE_TRANSACTIONS_CREATE)
          writabledb.close()
          Log.d("DBInfo", "nukeDB: End")
        } catch (e: Exception) {
          Log.e("DBFail", "nukeDB: Failed", e)
        }
      }
    }
  }

  //region Category functions
  suspend fun addCategory(
    category: Category
  ) : Boolean {

    var success: Boolean = false

    _mutex.withLock {
      withContext(Dispatchers.IO) {
        try {
          Log.d("DB", "addCategory: Start")
          val writableDB = this@DB.writableDatabase
          val values = ContentValues().apply {
            put(SQL_TABLE_CATEGORIES_COLUMN_ID, category.id)
            put(SQL_TABLE_CATEGORIES_COLUMN_NAME, category.name)
            put(SQL_TABLE_CATEGORIES_COLUMN_TYPE, category.financeType.name)
            put(SQL_TABLE_CATEGORIES_COLUMN_COLOR, category.color.toHex())
            put(SQL_TABLE_CATEGORIES_COLUMN_ICON, category.icon)
          }
          writableDB.insert(SQL_TABLE_CATEGORIES, null, values)
          writableDB.close()
          Log.d("DB", "addCategory: End")
          success = true
        } catch (e: Exception) {
          Log.e("DB", "addCategory failed", e)
        }
      }
    }

    return success
  }
  suspend fun updateCategory(
    oldCategory: Category,
    newCategory: Category
  ) : Boolean {

    var success: Boolean = false

    _mutex.withLock {
      withContext(Dispatchers.IO) {
        try {
          Log.d("DBInfo", "updateCategory: Start")
          val values = ContentValues().apply {
            if (oldCategory.name != newCategory.name)
              put(SQL_TABLE_CATEGORIES_COLUMN_NAME, newCategory.name)
            if (oldCategory.financeType != newCategory.financeType)
              put(SQL_TABLE_CATEGORIES_COLUMN_TYPE, newCategory.financeType.name)
            if (oldCategory.color != newCategory.color)
              put(SQL_TABLE_CATEGORIES_COLUMN_COLOR, newCategory.color.toHex())
            if (oldCategory.icon != newCategory.icon)
              put(SQL_TABLE_CATEGORIES_COLUMN_ICON, newCategory.icon)
          }
          if (values.size() != 0) {
            val writableDB = this@DB.writableDatabase
            val whereClause = "$SQL_TABLE_CATEGORIES_COLUMN_ID = ?"
            val whereArgs = arrayOf(oldCategory.id)
            writableDB.update(SQL_TABLE_CATEGORIES, values, whereClause, whereArgs)
            writableDB.close()
          }
          Log.d("DBInfo", "updateCategory: End")
          success = true
        } catch (e: Exception) {
          Log.e("DBFail", "updateCategory failed", e)
        }
      }
    }

    return success

  }
  suspend fun deleteCategory(
    category: Category
  ) : Boolean {

    var success: Boolean = false

    _mutex.withLock {
      withContext(Dispatchers.IO) {
        try {
          Log.d("DBInfo", "deleteCategory: Start")
          val writabledb = this@DB.writableDatabase
          val whereClause = "$SQL_TABLE_CATEGORIES_COLUMN_ID = ?"
          val whereArgs = arrayOf(category.id.toString())
          writabledb.delete(SQL_TABLE_CATEGORIES, whereClause, whereArgs)
          // Set unknown category for transactions
          val updateQuery =
            "UPDATE $SQL_TABLE_TRANSACTIONS " +
                "SET $SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID = '' " +
                "WHERE $SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID = '${category.id}'"
          writabledb.execSQL(updateQuery)
          writabledb.close()
          Log.d("DBInfo", "deleteCategory: End")
          success = true
        } catch (e: Exception) {
          Log.e("DBFail", "deleteCategory failed", e)
        }
      }
    }

    return success

  }
  suspend fun fetchCategories(
    financeType: FinanceType? = null,
    withUnknown: Boolean = false
  ): List<Category> {
    _mutex.withLock {
      return withContext(Dispatchers.IO) {
        val categories = mutableListOf<Category>()
        try {
          Log.d("DBInfo", "fetchCategories: Start")
          val query = "SELECT * FROM $SQL_TABLE_CATEGORIES " +
              (financeType?.let { "WHERE $SQL_TABLE_CATEGORIES_COLUMN_TYPE = '${financeType.name}'" } ?: "")
          val readabledb = this@DB.readableDatabase
          val cursor = readabledb.rawQuery(query, null)
          while (cursor.moveToNext()) {
            //region id
            val idColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_ID)
            val id: String =
              if (idColumnIndex != -1)
                cursor.getString(idColumnIndex)
              else throw Exception()
            //endregion
            //region name
            val nameColumnIndex = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_NAME)
            val name: String =
              if (nameColumnIndex != -1)
                cursor.getString(nameColumnIndex)
              else throw Exception()
            //endregion
            //region type
            val typeColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_TYPE)
            val type: FinanceType =
              if (typeColumnIndex != -1)
                FinanceType.valueOf(cursor.getString(typeColumnIndex))
              else throw Exception()
            //endregion
            //region color
            val colorColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_COLOR)
            val color: Color =
              if (colorColumnIndex != -1)
                Color.Companion.hexToColor(cursor.getString(colorColumnIndex))
              else throw Exception()
            //endregion
            //region icon
            val iconColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_ICON)
            val icon: Int =
              if (iconColumnIndex != -1)
                cursor.getInt(iconColumnIndex)
              else throw Exception()
            //endregion
            categories.add(
              Category(
                id = id,
                name = name,
                financeType = type,
                color = color,
                icon = icon
              )
            )
          }
          cursor.close()
          readabledb.close()

          if(withUnknown){
            financeType?.let {
              when(financeType){
                FinanceType.Expense -> { categories.add(Category.unknown(FinanceType.Expense)) }
                FinanceType.Income -> { categories.add(Category.unknown(FinanceType.Income)) }
              }
            } ?: run {
              categories.add(Category.unknown(FinanceType.Expense))
              categories.add(Category.unknown(FinanceType.Income))
            }
          }

          Log.d("DBInfo", "fetchCategories: Finish")
        } catch (e: Exception) {
          Log.e("DBFail", "fetchCategories failed", e)
        }
        categories
      }
    }
  }
  /**
   * @throws Exception() if the findCategory fails to retrieve the category
  */
  suspend fun findCategory(
    id: String
  ): Category {
    _mutex.withLock {
      return withContext(Dispatchers.IO) {
        var category: Category? = null
        try {
          Log.d("DB", "findCategory: Start")
          val query = "SELECT * FROM ${SQL_TABLE_CATEGORIES} WHERE ${SQL_TABLE_CATEGORIES_COLUMN_ID} = '$id'"
          val readabledb = this@DB.readableDatabase
          val cursor = readabledb.rawQuery(query, null)
          while (cursor.moveToNext()) {
            //region name
            val nameColumnIndex = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_NAME)
            val name: String =
              if (nameColumnIndex != -1)
                cursor.getString(nameColumnIndex)
              else throw Exception()
            //endregion
            //region type
            val typeColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_TYPE)
            val type: FinanceType =
              if (typeColumnIndex != -1)
                FinanceType.valueOf(cursor.getString(typeColumnIndex))
              else throw Exception()
            //endregion
            //region color
            val colorColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_COLOR)
            val color: Color =
              if (colorColumnIndex != -1)
                Color.Companion.hexToColor(cursor.getString(colorColumnIndex))
              else throw Exception()
            //endregion
            //region icon
            val iconColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_ICON)
            val icon: Int =
              if (iconColumnIndex != -1)
                cursor.getInt(iconColumnIndex)
              else throw Exception()
            //endregion
            category = Category(
              id = id,
              name = name,
              financeType = type,
              color = color,
              icon = icon
            )
          }
          cursor.close()
          Log.d("DB", "findCategory: End")
        } catch (e: Exception) {
          Log.e("DB-ERR", "findCategory failed", e)
        }
        if(category == null){
          throw Exception("[DB - findCategory] - failed to find category")
        }
        category
      }
    }
  }

  //endregion
  //region Transaction functions
  @SuppressLint("DefaultLocale")
  suspend fun addTransaction(
    transaction: Transaction
  ) : Boolean {

    var success: Boolean = false

    _mutex.withLock {
      withContext(Dispatchers.IO) {
        try {
          Log.d("DBInfo", "addTransaction: Start")
          val writabledb = this@DB.writableDatabase
          val values = ContentValues().apply {
            put(SQL_TABLE_TRANSACTIONS_COLUMN_ID, transaction.id)
            put(SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID, transaction.categoryId)
            put(SQL_TABLE_TRANSACTIONS_COLUMN_TYPE, transaction.type.name)
            put(SQL_TABLE_TRANSACTIONS_COLUMN_VALUE, String.format("%.2f", transaction.value))
            put(SQL_TABLE_TRANSACTIONS_COLUMN_DATETIME, transaction.dateTime.toString())
            put(SQL_TABLE_TRANSACTIONS_COLUMN_NOTE, transaction.note)
          }
          writabledb?.insert(SQL_TABLE_TRANSACTIONS, null, values)
          writabledb.close()
          Log.d("DBInfo", "addTransaction: Finish")
          success = true
        } catch (e: Exception) {
          Log.e("DBFail", "addTransaction failed", e)
        }
      }
    }

    return success
  }
  @SuppressLint("DefaultLocale")
  suspend fun updateTransaction(
    oldTransaction: Transaction,
    newTransaction: Transaction
  ) : Boolean {

    var success: Boolean = false

    _mutex.withLock {
      withContext(Dispatchers.IO) {
        try {
          Log.d("DBInfo", "updateTransaction: Start")
          val values = ContentValues().apply {
            // Not sure if checking on app side is faster than db insertion. Might be worse for app performance
            if (oldTransaction.categoryId != newTransaction.categoryId)
              put(SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID, newTransaction.categoryId)
            if (oldTransaction.type != newTransaction.type)
              put(SQL_TABLE_TRANSACTIONS_COLUMN_TYPE, newTransaction.type.name)
            if (oldTransaction.value != newTransaction.value)
              put(SQL_TABLE_TRANSACTIONS_COLUMN_VALUE, String.format("%.2f", newTransaction.value))
            if (oldTransaction.dateTime != newTransaction.dateTime)
              put(SQL_TABLE_TRANSACTIONS_COLUMN_DATETIME, newTransaction.dateTime.toString())
            if (oldTransaction.note != newTransaction.note)
              put(SQL_TABLE_TRANSACTIONS_COLUMN_NOTE, newTransaction.note)
          }
          if (values.size() != 0) {
            val writabledb = this@DB.writableDatabase
            val whereClause = "$SQL_TABLE_TRANSACTIONS_COLUMN_ID = ?"
            val whereArgs = arrayOf(oldTransaction.id)
            writabledb.update(SQL_TABLE_TRANSACTIONS, values, whereClause, whereArgs)
            writabledb.close()
          }
          Log.d("DBInfo", "updateTransaction: End")
          success = true
        } catch (e: Exception) {
          Log.e("DBFail", "updateTransaction failed", e)
        }
      }
    }

    return success

  }
  suspend fun deleteTransaction(
    transaction: Transaction
  ) : Boolean {

    var success: Boolean = false

    _mutex.withLock {
      withContext(Dispatchers.IO) {
        try {
          Log.d("DBInfo", "deleteTransaction: Start")
          val writabledb = this@DB.writableDatabase
          val whereClause = "$SQL_TABLE_TRANSACTIONS_COLUMN_ID = ?"
          val whereArgs = arrayOf(transaction.id)
          writabledb.delete(SQL_TABLE_TRANSACTIONS, whereClause, whereArgs)
          writabledb.close()
          Log.d("DBInfo", "deleteTransaction: End")
          success = true
        } catch (e: Exception) {
          Log.e("DBFail", "deleteTransaction failed", e)
        }
      }
    }

    return success

  }
  suspend fun fetchTransactions(
    dateRange: DateRange,
    category: Category? = null,
    financeType: FinanceType? = null
  ): List<Transaction> {
    _mutex.withLock {
      return withContext(Dispatchers.IO) {
        val transactions: MutableList<Transaction> = mutableListOf()
        try {
          Log.d("DBInfo", "fetchTransactions: Start")
          val readabledb = this@DB.readableDatabase
          val query = "SELECT * FROM $SQL_TABLE_TRANSACTIONS WHERE " +
              "$SQL_TABLE_TRANSACTIONS_COLUMN_DATETIME >= '${dateRange.startDate.toString() + " 00:00:00"}' " +
              "AND $SQL_TABLE_TRANSACTIONS_COLUMN_DATETIME <= '${dateRange.endDate.toString() + " 23:59:59"}' " +
              (financeType?.let { "AND $SQL_TABLE_TRANSACTIONS_COLUMN_TYPE = '${it.name}' "} ?: "") +
              (category?.let { "AND $SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID = '${it.id}' "} ?: "")
          val cursor = readabledb.rawQuery(query, null)
          while (cursor.moveToNext()) {
            //region id
            val idColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_TRANSACTIONS_COLUMN_ID)
            val id: String =
              if (idColumnIndex != -1)
                cursor.getString(idColumnIndex)
              else throw Exception()
            //endregion
            //region categoryId
            val categoryIdColumnIndex: Int =
              cursor.getColumnIndex(SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID)
            val categoryId: String =
              if (categoryIdColumnIndex != -1)
                cursor.getString(categoryIdColumnIndex)
              else throw Exception()
            //endregion
            //region type
            val typeColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_TRANSACTIONS_COLUMN_TYPE)
            val type: FinanceType =
              if (typeColumnIndex != -1)
                FinanceType.valueOf(cursor.getString(typeColumnIndex))
              else throw Exception()
            //endregion
            //region value
            val valueColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_TRANSACTIONS_COLUMN_VALUE)
            val value: BigDecimal =
              if (valueColumnIndex != -1)
                BigDecimal(cursor.getString(valueColumnIndex))
              else throw Exception()
            //endregion
            //region dateTime
            val dateTimeColumnIndex: Int =
              cursor.getColumnIndex(SQL_TABLE_TRANSACTIONS_COLUMN_DATETIME)
            val dateTime: DateTime =
              if (dateTimeColumnIndex != -1)
                Date.Companion.convertStringToDateTime(cursor.getString(dateTimeColumnIndex))
              else throw Exception()
            //endregion
            //region note
            val noteColumnIndex = cursor.getColumnIndex(SQL_TABLE_TRANSACTIONS_COLUMN_NOTE)
            val note =
              if (noteColumnIndex != -1)
                cursor.getString(noteColumnIndex)
              else throw Exception()
            //endregion
            transactions.add(
              Transaction(
                id = id,
                categoryId = categoryId,
                value = value,
                dateTime = dateTime,
                type = type,
                note = note
              )
            )
          }
          cursor.close()
          readabledb.close()
          Log.d("DBInfo", "fetchTransactions: Finish")
        } catch (e: Exception) {
          Log.e("DBFail", "fetchTransactions failed", e)
        }
        transactions
      }
    }
  }
  //endregion

  // region ID functions

  suspend fun fetchIDS(
    idType: IdType
  ): List<String> {
    _mutex.withLock {

      val ids: MutableList<String> = mutableListOf()

      return withContext(Dispatchers.IO) {
        try {
          val readabledb = this@DB.readableDatabase
          val query: String = when(idType){
            IdType.CATEGORY -> {
              "SELECT $SQL_TABLE_CATEGORIES_COLUMN_ID FROM $SQL_TABLE_CATEGORIES"
            }

            IdType.INCOME -> {
              "SELECT $SQL_TABLE_TRANSACTIONS_COLUMN_ID FROM $SQL_TABLE_TRANSACTIONS"
            }
          }
          val cursor: Cursor = readabledb.rawQuery(query, null)
          while (cursor.moveToNext()) {
            val idColumnIndex: Int = cursor.getColumnIndex(SQL_TABLE_CATEGORIES_COLUMN_ID)
            val id: String =
              if (idColumnIndex != -1)
                cursor.getString(idColumnIndex)
              else throw Exception()
            ids.add(id)
          }
          cursor.close()

        } catch (e: Exception) {
          Log.e("DBFail", "fetchIDS failed", e)
        }
        ids
      }
    }
  }

  // endregion

  /**
   * Retrieves the count of categories inside categories table
   * @return the number of categories  or -1 if the operation failed
   */
  suspend fun getCategoryCount(): Int {

    _mutex.withLock {
      return withContext(Dispatchers.IO) {

        var count: Int = 0

        try {

          val readableDB: SQLiteDatabase = readableDatabase
          val cursor = readableDB.rawQuery("SELECT COUNT(*) FROM $SQL_TABLE_CATEGORIES", null)

          if(cursor.moveToFirst()){
            count = cursor.getInt(0)
          }

          cursor.close()

        }
        catch (e: Exception){
          // TODO: Add logging
        }

        count

      }
    }

  }

  /**
   * Retrieves the count of transactions inside categories table
   * @return the number of transactions  or -1 if the operation failed
   */
  suspend fun getTransactionCount(): Int {

    _mutex.withLock {
      return withContext(Dispatchers.IO) {

        var count: Int = 0

        try {

          val readableDB: SQLiteDatabase = readableDatabase
          val cursor = readableDB.rawQuery("SELECT COUNT(*) FROM $SQL_TABLE_TRANSACTIONS", null)

          if(cursor.moveToFirst()){
            count = cursor.getInt(0)
          }

          cursor.close()

        }
        catch (e: Exception){
          // TODO: Add logging
        }

        count

      }
    }

  }

  /* TODO: possible optimization required
      note that provideSerializedCategory is running inside IO Thread withContext(Dispatchers.IO) not exactly sure what implications this has
  */

  suspend fun getSerializedCategories(
    provideSerializedCategory: (Serializable) -> Unit,
  ) {

    _mutex.withLock {
      withContext(Dispatchers.IO){

        try{

          val readableDB: SQLiteDatabase = readableDatabase
          val cursor = readableDB.rawQuery("SELECT * FROM $SQL_TABLE_CATEGORIES", null)

          while(cursor.moveToNext()) {

            val id: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_CATEGORIES_COLUMN_ID))
            val financeType: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_CATEGORIES_COLUMN_TYPE))
            val name: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_CATEGORIES_COLUMN_NAME))
            val color: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_CATEGORIES_COLUMN_COLOR))
            val icon: Int = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_TABLE_CATEGORIES_COLUMN_ICON))

            provideSerializedCategory(
              SerializedCategory(
                id,
                name,
                financeType,
                color,
                icon
              )
            )

          }

          cursor.close()
          readableDB.close()

        }
        catch (e: Exception){
          Log.e("DBInfo", "exportCategories: Failed", e)
        }

      }
    }

  }

  suspend fun getSerializedTransactions(
    provideSerializedTransaction: (Serializable) -> Unit,
  ) {

    _mutex.withLock {
      withContext(Dispatchers.IO) {

        try {

          val readableDB = this@DB.readableDatabase
          val cursor = readableDB.rawQuery("SELECT * FROM $SQL_TABLE_TRANSACTIONS", null)

          while(cursor.moveToNext()) {

            val id: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_TRANSACTIONS_COLUMN_ID))
            val categoryId: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_TRANSACTIONS_COLUMN_CATEGORY_ID))
            val financeType: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_TRANSACTIONS_COLUMN_TYPE))
            val value: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_TRANSACTIONS_COLUMN_VALUE))
            val dateTime: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_TRANSACTIONS_COLUMN_DATETIME))
            val note: String = cursor.getString(cursor.getColumnIndexOrThrow(SQL_TABLE_TRANSACTIONS_COLUMN_NOTE))

            provideSerializedTransaction(
              SerializedTransaction(
                id,
                categoryId,
                financeType,
                value,
                dateTime,
                note
              )
            )

          }

          cursor.close()
          readableDB.close()
        }
        catch (e: Exception){
          Log.e("DBInfo", "exportTransactions: Failed", e)
        }

      }
    }

  }

}