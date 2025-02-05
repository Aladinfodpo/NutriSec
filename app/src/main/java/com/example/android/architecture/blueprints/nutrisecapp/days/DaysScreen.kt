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

package com.example.android.architecture.blueprints.nutrisecapp.days

import android.graphics.Color.RGBToHSV
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.data.maxCalorie
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.ACTIVE_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.ALL_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.COMPLETED_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.util.DayItemProgressBar
import com.example.android.architecture.blueprints.nutrisecapp.util.DaysTopAppBar
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Composable
fun DaysScreen(
    @StringRes userMessage: Int,
    onAddDay: () -> Unit,
    onDayClick: (Day) -> Unit,
    onUserMessageDisplayed: () -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DaysViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DaysTopAppBar(
                openDrawer = openDrawer,
                onFilterAllDays = { viewModel.setFiltering(ALL_DAYS) },
                onFilterActiveDays = { viewModel.setFiltering(ACTIVE_DAYS) },
                onFilterCompletedDays = { viewModel.setFiltering(COMPLETED_DAYS) },
                onClearCompletedDays = { viewModel.clearCompletedDays() },
                onRefresh = { viewModel.refresh() }
            )
        },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = onAddDay) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_day))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        DaysContent(
            loading = uiState.isLoading,
            days = uiState.items,
            currentFilteringLabel = uiState.filteringUiInfo.currentFilteringLabel,
            noDaysLabel = uiState.filteringUiInfo.noDaysLabel,
            noDaysIconRes = uiState.filteringUiInfo.noDayIconRes,
            onRefresh = viewModel::refresh,
            onDayClick = onDayClick,
            onDayCheckedChange = viewModel::completeDay,
            modifier = Modifier.padding(paddingValues)
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(snackbarHostState, viewModel, message, snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }
        }

        // Check if there's a userMessage to show to the user
        val currentOnUserMessageDisplayed by rememberUpdatedState(onUserMessageDisplayed)
        LaunchedEffect(userMessage) {
            if (userMessage != 0) {
                viewModel.showEditResultMessage(userMessage)
                currentOnUserMessageDisplayed()
            }
        }
    }
}

@Composable
private fun DaysContent(
    loading: Boolean,
    days: List<Day>,
    @StringRes currentFilteringLabel: Int,
    @StringRes noDaysLabel: Int,
    @DrawableRes noDaysIconRes: Int,
    onRefresh: () -> Unit,
    onDayClick: (Day) -> Unit,
    onDayCheckedChange: (Day, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if(days.isEmpty() && !loading) {
        DaysEmptyContent(noDaysLabel, noDaysIconRes, modifier)
    }
    else{
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            Text(
                text = stringResource(currentFilteringLabel),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.list_item_padding),
                    vertical = dimensionResource(id = R.dimen.vertical_margin)
                ),
                style = MaterialTheme.typography.headlineSmall
            )
            LazyColumn {
                items(days) { day ->
                    DayItemProgressBar (
                        day = day,
                        foods = emptyList(),
                        onDayClick = onDayClick
                    )
                }
            }
        }
    }
}

@Composable
fun Int.pxToDp() = with(LocalDensity.current) {
    this@pxToDp.toDp()
}

@Composable
private fun DaysEmptyContent(
    @StringRes noDaysLabel: Int,
    @DrawableRes noDaysIconRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = noDaysIconRes),
            contentDescription = stringResource(R.string.no_days_image_content_description),
            modifier = Modifier.size(96.dp)
        )
        Text(stringResource(id = noDaysLabel))
    }
}

@Preview
@Composable
private fun DaysContentPreview() {
    MaterialTheme {
        Surface {
            DaysContent(
                loading = false,
                days = listOf(
                    Day(
                        title = "01/01/20",
                        description = "Description 1",
                        isCompleted = false,
                        foods = listOf(Food("Sucre", 100, 3500,100)),
                        id = -1
                    ),
                    Day(
                        title = "01/01/20",
                        description = "Description 1",
                        isCompleted = false,
                        foods = listOf(Food("Sucre", 100, 3000,100)),
                        id = 1
                    ),
                    Day(
                        title = "02/01/20",
                        description = "Description 2",
                        isCompleted = true,
                        foods = listOf(Food("Sucre", 100, 2700,100)),
                        id = 2
                    ),
                    Day(
                        title = "02.5/01/20",
                        description = "Description 2",
                        isCompleted = true,
                        foods = listOf(Food("Sucre", 100, 2500,100)),
                        id = 2
                    ),
                    Day(
                        title = "03/01/20",
                        description = "Description 3",
                        isCompleted = true,
                        foods = listOf(Food("Sucre", 100, 2200,100)),
                        id = 3
                    ),
                    Day(
                        title = "03.5/01/20",
                        description = "Description 3",
                        isCompleted = true,
                        foods = listOf(Food("Sucre", 100, 2000,100)),
                        id = 3
                    ),
                    Day(
                        title = "04/01/20",
                        description = "Description 4",
                        isCompleted = true,
                        foods = listOf(Food("Sucre", 100, 1500,100)),
                        id = 4
                    ),
                    Day(
                        title = "05/01/20",
                        description = "Description 5",
                        isCompleted = true,
                        foods = listOf(Food("Sucre", 100, 1000,100)),
                        id = 5
                    ),
                    Day(
                        title = "06/01/20",
                        description = "Description 5",
                        isCompleted = true,
                        foods = listOf(Food("Sucre", 100, 500,100)),
                        id = 6
                    ),
                ),
                currentFilteringLabel = R.string.label_all,
                noDaysLabel = R.string.no_days_all,
                noDaysIconRes = R.drawable.logo,
                onRefresh = { },
                onDayClick = { },
                onDayCheckedChange = { _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun DaysContentEmptyPreview() {
    MaterialTheme {
        Surface {
            DaysContent(
                loading = false,
                days = emptyList(),
                currentFilteringLabel = R.string.label_all,
                noDaysLabel = R.string.no_days_all,
                noDaysIconRes = R.drawable.logo,
                onRefresh = { },
                onDayClick = { },
                onDayCheckedChange = { _, _ -> },
            )
        }
    }
}

