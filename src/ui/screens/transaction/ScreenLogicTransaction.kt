package com.fibu.ui.screens.transaction

import com.fibu.logic.DateTime
import com.fibu.logic.ScreenLogic
import com.fibu.logic.info.Category
import com.fibu.logic.info.FinanceType
import com.fibu.ui.utility.ValueErrorType
import com.fibu.ui.utility.valueFieldHasError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class ScreenLogicTransaction: ScreenLogic() {

  class UIState(
    val financeType: FinanceType,
    val valueText: String,
    val category: Category?,
    val dateTime: DateTime,
    val noteText: String
  ) {
    companion object{
      fun empty(): UIState {
        return UIState (
          financeType = FinanceType.Expense,
          valueText = "",
          category = null,
          dateTime = DateTime.zero(),
          noteText = ""
        )
      }
    }
    fun copy(
      financeType: FinanceType = this.financeType,
      valueText: String = this.valueText,
      category: Category? = this.category,
      dateTime: DateTime = this.dateTime,
      noteText: String = this.noteText
    ): UIState {
      return UIState(
        financeType = financeType,
        valueText = valueText,
        category = category,
        dateTime = dateTime,
        noteText = noteText
      )
    }
  }

  protected var p_checkInputChanged: Boolean = true

  protected val p_valueErrorType: MutableStateFlow<ValueErrorType> = MutableStateFlow(ValueErrorType.None)
  val valueErrorType: StateFlow<ValueErrorType> get() = p_valueErrorType

  protected val p_categoryError: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val categoryError: StateFlow<Boolean> get() = p_categoryError

  protected var p_originalUIState: UIState = UIState.empty()

  protected val p_uiState: MutableStateFlow<UIState> = MutableStateFlow(p_originalUIState)
  val uiState: StateFlow<UIState> = p_uiState

  protected val p_dateTimeDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val dateTimeDialog: StateFlow<Boolean> = p_dateTimeDialog

  protected val p_openExitDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val openExitDialog: StateFlow<Boolean> get() = p_openExitDialog

  protected var p_exitAction: () -> Unit = {}
  val exitAction: () -> Unit get() = p_exitAction

  fun eventSetFinanceType(
    newFinanceType: FinanceType
  ) {
    p_uiState.value = p_uiState.value.copy(
      financeType = newFinanceType,
      category = null
    )
  }
  fun eventSetValue(
    valueText: String
  ) {
    // Reset value error when changing it
    p_valueErrorType.value = ValueErrorType.None
    p_uiState.value = p_uiState.value.copy(
      valueText = valueText
    )
  }
  fun eventSetCategory(
    category: Category
  ) {
    p_uiState.value = p_uiState.value.copy(
      category = category
    )
    p_categoryError.value = false
  }
  fun eventSetDateTime(
    newDateTime: DateTime
  ) {
    p_uiState.value = p_uiState.value.copy(
      dateTime = newDateTime
    )
  }
  fun eventSetDateTimeDialog(
    value: Boolean
  ) {
    p_dateTimeDialog.value = value
  }
  fun eventSetNote(
    noteText: String
  ) {
    p_uiState.value = p_uiState.value.copy(
      noteText = noteText
    )
  }
  fun eventSetOpenExitDialog(
    value: Boolean
  ) {
    p_openExitDialog.value = value
  }

  abstract fun eventConfirm(
    action: () -> Unit
  )

  override fun eventExit(
    exitAction: () -> Unit
  ) {
    if(p_checkInputChanged && inputChanged()){
      p_openExitDialog.value = true
      p_exitAction = {
        p_checkInputChanged = false
        exitAction()
      }
    } else {
      exitAction()
    }
  }

  protected fun clearCommonUI(){
    p_checkInputChanged = true
    p_valueErrorType.value = ValueErrorType.None
    p_categoryError.value = false
    p_dateTimeDialog.value = false
    p_openExitDialog.value = false
    p_exitAction = {}
  }

  protected fun confirmError(): Boolean {
    p_valueErrorType.value = valueFieldHasError(p_uiState.value.valueText)
    if(p_uiState.value.category == null) { p_categoryError.value = true }
    if(p_valueErrorType.value != ValueErrorType.None || p_categoryError.value){
      return true
    }
    return false
  }

  protected fun inputChanged(): Boolean {
    return p_uiState.value.financeType != p_originalUIState.financeType
           ||
           p_uiState.value.valueText != p_originalUIState.valueText
           ||
           p_uiState.value.category != p_originalUIState.category
           ||
           p_uiState.value.dateTime != p_originalUIState.dateTime
           ||
           p_uiState.value.noteText != p_originalUIState.noteText
  }

}

