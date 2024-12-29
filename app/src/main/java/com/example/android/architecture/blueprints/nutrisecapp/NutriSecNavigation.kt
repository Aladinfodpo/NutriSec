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

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs.DAY_ID_ARG
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs.TITLE_ARG
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs.USER_MESSAGE_ARG
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecScreens.ADD_EDIT_DAY_SCREEN
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecScreens.STATISTICS_SCREEN
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecScreens.DAYS_SCREEN
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecScreens.DAY_DETAIL_SCREEN

/**
 * Screens used in [NutriSecDestinations]
 */
private object NutriSecScreens {
    const val DAYS_SCREEN = "days"
    const val STATISTICS_SCREEN = "statistics"
    const val DAY_DETAIL_SCREEN = "day"
    const val ADD_EDIT_DAY_SCREEN = "addEditDay"
}

/**
 * Arguments used in [NutriSecDestinations] routes
 */
object NutriSecDestinationsArgs {
    const val USER_MESSAGE_ARG = "userMessage"
    const val DAY_ID_ARG = "dayId"
    const val TITLE_ARG = "title"
}

/**
 * Destinations used in the [NutriSecActivity]
 */
object NutriSecDestinations {
    const val DAYS_ROUTE = "$DAYS_SCREEN?$USER_MESSAGE_ARG={$USER_MESSAGE_ARG}"
    const val STATISTICS_ROUTE = STATISTICS_SCREEN
    const val DAY_DETAIL_ROUTE = "$DAY_DETAIL_SCREEN/{$DAY_ID_ARG}"
    const val ADD_EDIT_DAY_ROUTE = "$ADD_EDIT_DAY_SCREEN/{$TITLE_ARG}?$DAY_ID_ARG={$DAY_ID_ARG}"
}

/**
 * Models the navigation actions in the app.
 */
class NutriSecNavigationActions(private val navController: NavHostController) {

    fun navigateToDays(userMessage: Int = 0) {
        val navigatesFromDrawer = userMessage == 0
        navController.navigate(
            DAYS_SCREEN.let {
                if (userMessage != 0) "$it?$USER_MESSAGE_ARG=$userMessage" else it
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = !navigatesFromDrawer
                saveState = navigatesFromDrawer
            }
            launchSingleTop = true
            restoreState = navigatesFromDrawer
        }
    }

    fun navigateToStatistics() {
        navController.navigate(NutriSecDestinations.STATISTICS_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    fun navigateToDayDetail(dayId: String) {
        navController.navigate("$DAY_DETAIL_SCREEN/$dayId")
    }

    fun navigateToAddEditDay(title: Int, dayId: String?) {
        navController.navigate(
            "$ADD_EDIT_DAY_SCREEN/$title".let {
                if (dayId != null) "$it?$DAY_ID_ARG=$dayId" else it
            }
        )
    }
}
