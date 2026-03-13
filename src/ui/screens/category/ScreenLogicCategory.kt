package com.fibu.ui.screens.category

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.fibu.R
import com.fibu.logic.ScreenLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

abstract class ScreenLogicCategory: ScreenLogic() {

  data class UIState(
    val name: String,
    val color: Color,
    val icon: Int,
  ) {
    companion object {
      fun empty(): UIState {
        return UIState (
          name = "",
          color = Color.White,
          icon = R.drawable.unknown_category_icon
        )
      }
    }
  }

  protected var p_checkInputChanged: Boolean = true

  protected var p_originaluiState: UIState = UIState.empty()
  protected val p_uiState: MutableStateFlow<UIState> = MutableStateFlow(p_originaluiState)
  val uiState: StateFlow<UIState> = p_uiState

  protected var p_errorCategory: MutableStateFlow<ErrorCategory> = MutableStateFlow(ErrorCategory.None)
  val errorCategory: StateFlow<Boolean> = p_errorCategory.map { it != ErrorCategory.None }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = false
  )
  val errorCategoryText: StateFlow<String> = p_errorCategory.map { it.text }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Lazily,
    initialValue = ""
  )

  protected var p_openExitDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val openExitDialog: StateFlow<Boolean> = p_openExitDialog

  protected var p_exitAction: () -> Unit = {}
  val exitAction: () -> Unit get() = p_exitAction

  enum class ErrorCategory(
    val text: String
  ) {
    None(
      text = ""
    ),
    Blank(
      text = "Category name can not be blank"
    )
  }

  fun eventSetCategoryName(
    newCategoryName: String
  ) {
    p_uiState.value = p_uiState.value.copy(
      name = newCategoryName
    )
  }
  fun eventSetCategoryColor(
    newCategoryColor: Color
  ) {
    p_uiState.value = p_uiState.value.copy(
      color = newCategoryColor
    )
  }
  fun eventSetCategoryIcon(
    newCategoryIcon: Int
  ) {
    p_uiState.value = p_uiState.value.copy(
      icon = newCategoryIcon
    )
  }
  fun eventSetOpenExitDialog(
    value: Boolean
  ) {
    p_openExitDialog.value = value
  }

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

  abstract fun eventConfirm(
    action: () -> Unit = {}
  )

  protected fun clearCommonUI(){
    p_checkInputChanged = true
    p_errorCategory.value = ErrorCategory.None
    p_openExitDialog.value = false
    p_exitAction = {}
  }

  protected fun confirmError(): Boolean {
    if(p_uiState.value.name.isBlank()){
      p_errorCategory.value = ErrorCategory.Blank
      return true
    }
    return false
  }

  protected fun inputChanged(): Boolean {
    return p_uiState.value.name != p_originaluiState.name
           ||
           p_uiState.value.color != p_originaluiState.color
           ||
           p_uiState.value.icon != p_originaluiState.icon
  }
}