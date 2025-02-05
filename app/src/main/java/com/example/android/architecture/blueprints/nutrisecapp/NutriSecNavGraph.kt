/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.nutrisecapp

import android.app.Activity
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs.DAY_ID_ARG
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs.TITLE_ARG
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs.USER_MESSAGE_ARG
import com.example.android.architecture.blueprints.nutrisecapp.addeditday.AddEditDayScreen
import com.example.android.architecture.blueprints.nutrisecapp.canIEatIt.CanIEatItScreen
import com.example.android.architecture.blueprints.nutrisecapp.statistics.StatisticsScreen
import com.example.android.architecture.blueprints.nutrisecapp.daydetail.DayDetailScreen
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysScreen
import com.example.android.architecture.blueprints.nutrisecapp.drawer.AppModalDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NutriSecNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = NutriSecDestinations.DAYS_ROUTE,
    navActions: NutriSecNavigationActions = remember(navController) {
        NutriSecNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            NutriSecDestinations.DAYS_ROUTE,
            arguments = listOf(
                navArgument(USER_MESSAGE_ARG) { type = NavType.IntType; defaultValue = 0 }
            )
        ) { entry ->
            AppModalDrawer(drawerState, currentRoute, navActions) {
                DaysScreen(
                    userMessage = entry.arguments?.getInt(USER_MESSAGE_ARG)!!,
                    onUserMessageDisplayed = { entry.arguments?.putInt(USER_MESSAGE_ARG, 0) },
                    onAddDay = { navActions.navigateToAddEditDay(R.string.add_day, null) },
                    onDayClick = { day -> navActions.navigateToDayDetail(day.id) },
                    openDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(NutriSecDestinations.STATISTICS_ROUTE) {
            AppModalDrawer(drawerState, currentRoute, navActions) {
                StatisticsScreen(openDrawer = { coroutineScope.launch { drawerState.open() } })
            }
        }
        composable(
            NutriSecDestinations.ADD_EDIT_DAY_ROUTE,
            arguments = listOf(
                navArgument(TITLE_ARG) { type = NavType.IntType },
                navArgument(DAY_ID_ARG) { type = NavType.LongType},
            )
        ) { entry ->
            val taskId = entry.arguments?.getLong(DAY_ID_ARG)
            AddEditDayScreen(
                topBarTitle = entry.arguments?.getInt(TITLE_ARG)!!,
                onDayUpdate = {
                    navActions.navigateToDays(
                        if (taskId == 0L) ADD_EDIT_RESULT_OK else EDIT_RESULT_OK
                    )
                },
                onBack = { navActions.navigateToDays(
                    if (taskId == 0L) ADD_EDIT_RESULT_OK else EDIT_RESULT_OK
                ) }
            )
        }
        composable(NutriSecDestinations.DAY_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(DAY_ID_ARG) { type = NavType.LongType}
            )) {
            DayDetailScreen(
                onEditDay = { taskId ->
                    navActions.navigateToAddEditDay(R.string.edit_day, taskId)
                },
                onBack = { if(navController.previousBackStackEntry != null) navController.popBackStack() },
                onDeleteDay = { navActions.navigateToDays(DELETE_RESULT_OK) }
            )
        }
        composable(NutriSecDestinations.CAN_I_EAT_IT_ROUTE,
            arguments = listOf(
                navArgument(DAY_ID_ARG) { type = NavType.LongType}
            )) {
            AppModalDrawer(drawerState, currentRoute, navActions) {
                CanIEatItScreen(
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                    onDayProgressBarClick = {day -> navActions.navigateToDayDetail(day.id)}
                )
            }
        }
    }
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
