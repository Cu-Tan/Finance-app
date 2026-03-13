package com.fibu.logic.navigation

import androidx.compose.runtime.Composable

@Composable
fun NavHost(
  navController: NavController,
) {
  navController.CurrentScreen(navController)
}