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

package com.example.android.architecture.blueprints.nutrisecapp.daydetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.util.LoadingContent
import com.example.android.architecture.blueprints.nutrisecapp.util.DayDetailTopAppBar

@Composable
fun DayDetailScreen(
    onEditDay: (String) -> Unit,
    onBack: () -> Unit,
    onDeleteDay: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DayDetailViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { DayDetailTopAppBar(onBack = onBack, onDelete = viewModel::deleteDay) },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = { onEditDay(viewModel.dayId) }) {
                Icon(Icons.Filled.Edit, stringResource(id = R.string.edit_day))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        EditDayContent(
            loading = uiState.isLoading,
            empty = uiState.day == null && !uiState.isLoading,
            day = uiState.day,
            onRefresh = viewModel::refresh,
            onDayCheck = viewModel::setCompleted,
            modifier = Modifier.padding(paddingValues)
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { userMessage ->
            val snackbarText = stringResource(userMessage)
            LaunchedEffect(snackbarHostState, viewModel, userMessage, snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }
        }

        // Check if the day is deleted and call onDeleteDay
        LaunchedEffect(uiState.isDayDeleted) {
            if (uiState.isDayDeleted) {
                onDeleteDay()
            }
        }
    }
}

@Composable
private fun EditDayContent(
    loading: Boolean,
    empty: Boolean,
    day: Day?,
    onDayCheck: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenPadding = Modifier.padding(
        horizontal = dimensionResource(id = R.dimen.horizontal_margin),
        vertical = dimensionResource(id = R.dimen.vertical_margin),
    )
    val commonModifier = modifier
        .fillMaxWidth()
        .then(screenPadding)

    if (day != null) {
            LazyColumn(modifier = commonModifier
                .padding(all = dimensionResource(id = R.dimen.horizontal_margin))) {
                item{
                    Text(text = day.title, style = MaterialTheme.typography.headlineSmall)
                }
                items(day.foods) { food ->
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween){
                        Text(food.name, textAlign = TextAlign.Center)
                        Text(food.quantity.toString() +" g", textAlign = TextAlign.Center)
                        Text(food.calories.toString() + " kcal", textAlign = TextAlign.Center)
                        Text(food.protein.toString()+" g", textAlign = TextAlign.Center)
                    }
                }
                item{
                    Text("Cardio : " +day.calCardio.toString()+" kcal")
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround){
                        Text("Total")
                        Text(day.foods.sumOf { it.quantity }.toString())
                        Text(day.getCalDay.toString())
                        Text(day.foods.sumOf { it.protein  }.toString())
                        Icon(if (day.isBad) Icons.Filled.Error else Icons.Filled.Done, "Day done")
                    }
                }
                item{
                    Spacer(Modifier.height(150.dp))
                    Text("Poids : " +day.weight.toString()+" kg")
                }
            }

    }

}

@Preview
@Composable
private fun EditDayContentPreview() {
    Surface {
        EditDayContent(
            loading = false,
            empty = false,
            Day(
                title = "Title",
                description = "Description",
                isCompleted = false,
                id = "ID",
                foods = mutableListOf(Food("Cerise",100, 200, 3), Food("Tomate",10, 2000, 333), Food("Cerise",100, 200, 3))
            ),
            onDayCheck = { },
            onRefresh = { }
        )
    }

}

@Preview
@Composable
private fun EditDayContentDayCompletedPreview() {
    Surface {
        EditDayContent(
            loading = false,
            empty = false,
            Day(
                title = "Title",
                description = "Description",
                isCompleted = false,
                id = "ID"
            ),
            onDayCheck = { },
            onRefresh = { }
        )
    }
}

@Preview
@Composable
private fun EditDayContentEmptyPreview() {
    Surface {
        EditDayContent(
            loading = false,
            empty = true,
            Day(
                title = "Title",
                description = "Description",
                isCompleted = false,
                id = "ID"
            ),
            onDayCheck = { },
            onRefresh = { }
        )
    }
}
