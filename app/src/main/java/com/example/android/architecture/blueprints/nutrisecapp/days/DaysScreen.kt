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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecTheme
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.ACTIVE_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.ALL_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.COMPLETED_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.util.LoadingContent
import com.example.android.architecture.blueprints.nutrisecapp.util.DaysTopAppBar

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
    LoadingContent(
        loading = loading,
        empty = days.isEmpty() && !loading,
        emptyContent = { DaysEmptyContent(noDaysLabel, noDaysIconRes, modifier) },
        onRefresh = onRefresh
    ) {
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
                    DayItem(
                        day = day,
                        onDayClick = onDayClick,
                        onCheckedChange = { }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayItem(
    day: Day,
    onCheckedChange: (Boolean) -> Unit,
    onDayClick: (Day) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
            .clickable { onDayClick(day) }
    ) {
        Checkbox(
            checked = !day.isBad,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = day.titleForList,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.horizontal_margin)
            ),
            textDecoration = null

        )
    }
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
                        title = "Title 1",
                        description = "Description 1",
                        isCompleted = false,
                        id = "ID 1"
                    ),
                    Day(
                        title = "Title 2",
                        description = "Description 2",
                        isCompleted = true,
                        id = "ID 2"
                    ),
                    Day(
                        title = "Title 3",
                        description = "Description 3",
                        isCompleted = true,
                        id = "ID 3"
                    ),
                    Day(
                        title = "Title 4",
                        description = "Description 4",
                        isCompleted = false,
                        id = "ID 4"
                    ),
                    Day(
                        title = "Title 5",
                        description = "Description 5",
                        isCompleted = true,
                        id = "ID 5"
                    ),
                ),
                currentFilteringLabel = R.string.label_all,
                noDaysLabel = R.string.no_days_all,
                noDaysIconRes = R.drawable.logo_no_fill,
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
                noDaysIconRes = R.drawable.logo_no_fill,
                onRefresh = { },
                onDayClick = { },
                onDayCheckedChange = { _, _ -> },
            )
        }
    }
}

@Preview
@Composable
private fun DaysEmptyContentPreview() {
    NutriSecTheme {
        Surface {
            DaysEmptyContent(
                noDaysLabel = R.string.no_days_all,
                noDaysIconRes = R.drawable.logo_no_fill
            )
        }
    }
}

@Preview
@Composable
private fun DayItemPreview() {
    MaterialTheme {
        Surface {
            DayItem(
                day = Day(
                    title = "Title",
                    description = "Description",
                    id = "ID"
                ),
                onDayClick = { },
                onCheckedChange = { }
            )
        }
    }
}

@Preview
@Composable
private fun DayItemCompletedPreview() {
    MaterialTheme {
        Surface {
            DayItem(
                day = Day(
                    title = "Title",
                    description = "Description",
                    isCompleted = true,
                    id = "ID"
                ),
                onDayClick = { },
                onCheckedChange = { }
            )
        }
    }
}
