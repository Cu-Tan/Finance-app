package com.fibu.logic

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

fun Modifier.holdable(
  duration: Long,
  onHold: () -> Unit
): Modifier = composed {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  LaunchedEffect(isPressed) {
    if (isPressed) {
      delay(duration)
      if (isPressed) onHold()
    }
  }

  this.then(
    Modifier.clickable(
      interactionSource = interactionSource,
      indication = LocalIndication.current
    ) { /* no-op on click */ }
  )
}