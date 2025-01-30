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

package com.example.android.architecture.blueprints.nutrisecapp.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.util.StatisticsTopAppBar

@Composable
fun StatisticsScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { StatisticsTopAppBar(openDrawer) },
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        StatisticsContent(
            loading = uiState.isLoading,
            empty = uiState.isEmpty,
            activeDaysPercent = uiState.activeDaysPercent,
            completedDaysPercent = uiState.completedDaysPercent,
            weights = uiState.weights,
            dates = uiState.dates,
            onRefresh = { viewModel.refresh() },
            modifier = modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun StatisticsContent(
    loading: Boolean,
    empty: Boolean,
    activeDaysPercent: Float,
    completedDaysPercent: Float,
    weights: List<Double>,
    dates: List<String>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val commonModifier = modifier
        .fillMaxSize()
        .padding(all = dimensionResource(id = R.dimen.horizontal_margin))

    if (empty)
        Text(
            text = stringResource(id = R.string.statistics_no_days),
            modifier = commonModifier
        )
    else
        Column(
            commonModifier
                .fillMaxSize()
        ) {

                Text(stringResource(id = R.string.statistics_active_days, activeDaysPercent))
                Text(
                    stringResource(
                        id = R.string.statistics_completed_days,
                        completedDaysPercent
                    )
                )
                val textMeasurer = rememberTextMeasurer()
                Canvas(modifier = Modifier.fillMaxSize()) {
                    weights.forEachIndexed { index, d ->
                        drawText(textMeasurer = textMeasurer, text  = d.toString(), style = TextStyle(
                            color = Color.Black,
                            fontSize = 14.sp
                        ),
                            topLeft = Offset(
                                x = size.width / 10*index,
                                y = size.height / 2
                            )
                        )
                    }
                }
        }

}

@Preview
@Composable
fun StatisticsContentPreview() {
    Surface {
        StatisticsContent(
            loading = false,
            empty = false,
            activeDaysPercent = 80f,
            completedDaysPercent = 20f,
            dates = listOf("12/12/2024", "13/12/2024"),
            weights = listOf(90.0, 80.0),
            onRefresh = { }
        )
    }
}

@Preview
@Composable
fun EmptyStatisticsContentPreview() {
    Surface {
        StatisticsContent(
            loading = false,
            empty = true,
            activeDaysPercent = 80f,
            completedDaysPercent = 20f,
            dates = listOf("12/12/2024", "13/12/2024"),
            weights = listOf(90.0, 80.0),
            onRefresh = { }
        )
    }
}

