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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.android.architecture.blueprints.nutrisecapp.addedittask

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.data.Task
import com.example.android.architecture.blueprints.nutrisecapp.util.AddEditTaskTopAppBar
import com.example.android.architecture.blueprints.nutrisecapp.util.primaryDarkColor

@Composable
fun AddEditTaskScreen(
    @StringRes topBarTitle: Int,
    onTaskUpdate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditTaskViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { AddEditTaskTopAppBar(topBarTitle, {viewModel.saveTask(); onBack()}) },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = viewModel::addFood) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.cd_save_task))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AddEditTaskContent(
            loading = uiState.isLoading,
            task = uiState.task,
            onTitleChanged = viewModel::updateTitle,
            onDescriptionChanged = viewModel::updateDescription,
            onCardioChanged = viewModel::updateCardio,
            onFoodNameChanged = viewModel::updateFoodIName,
            onFoodQuantityChanged = viewModel::updateFoodIQuantity,
            onFoodCalorieChanged = viewModel::updateFoodICalories,
            onFoodProteinChanged = viewModel::updateFoodIProtein,
            modifier = Modifier.padding(paddingValues)
        )

        // Check if the task is saved and call onTaskUpdate event
        LaunchedEffect(uiState.isTaskSaved) {
            if (uiState.isTaskSaved) {
                onTaskUpdate()
            }
        }

        // Check for user messages to display on the screen
        uiState.userMessage?.let { userMessage ->
            val snackbarText = stringResource(userMessage)
            LaunchedEffect(snackbarHostState, viewModel, userMessage, snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }
        }
    }
}

@Composable
private fun AddEditTaskContent(
    loading: Boolean,
    task: Task,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCardioChanged: (String) -> Unit,
    onFoodNameChanged: (Int, String) -> Unit,
    onFoodQuantityChanged: (Int, String) -> Unit,
    onFoodCalorieChanged: (Int, String) -> Unit,
    onFoodProteinChanged: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.Black,
            //unfocusedContainerColor = Color(red = 200, green = 200, blue = 200, alpha = 50)
        )
        val numberEditableColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryDarkColor,
            unfocusedBorderColor = primaryDarkColor,
            cursorColor = Color.Black
        )
        LazyColumn(
            modifier = modifier.fillMaxWidth()
                .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            item {
                OutlinedTextField(
                    value = task.title,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = onTitleChanged,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.title_hint),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineSmall
                        .copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    colors = textFieldColors
                )
            }


            itemsIndexed(task.foods) { i, food ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    OutlinedTextField(
                        value = food.name,
                        onValueChange = { newValue -> onFoodNameChanged(i, newValue) },
                        placeholder = { Text(stringResource(id = R.string.description_hint)) },
                        modifier = Modifier.widthIn(1.dp, 120.dp),
                        colors = textFieldColors,
                        maxLines = 1,
                    )
                    TextField(
                        value = food.quantity.let { if (it == 0) "" else it.toString() },
                        onValueChange = { newValue -> onFoodQuantityChanged(i, newValue) },
                        modifier = Modifier.widthIn(1.dp, 80.dp),
                        colors = textFieldColors,
                        maxLines = 1,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    )
                    TextField(
                        value = food.calories.let { if (it == 0) "" else it.toString() },
                        onValueChange = { newValue -> onFoodCalorieChanged(i, newValue) },
                        modifier = Modifier.widthIn(1.dp, 80.dp),
                        colors = textFieldColors,
                        maxLines = 1,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    )
                    TextField(
                        value = food.protein.let { if (it == 0) "" else it.toString() },
                        onValueChange = { newValue -> onFoodProteinChanged(i, newValue) },
                        modifier = Modifier.widthIn(1.dp, 80.dp),
                        colors = textFieldColors,
                        maxLines = 1,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    )
                }
            }
            item {
                OutlinedTextField(
                    value = task.calCardio.let { if (it == 0) "" else it.toString() },
                    onValueChange = onCardioChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = numberEditableColors,
                    maxLines = 1,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    label = { Text("Cardio") },
                    suffix = { Text("kcal") }
                )
            }

    }

}

@Preview
@Composable
private fun AddEditTaskScreenPreview() {
    Surface {
        AddEditTaskContent(
            loading = false,
            task = Task(
                title = "Title",
                description = "Description",
                isCompleted = false,
                calCardio = 10,
                id = "ID",
                foods = listOf(Food("Cerise",100, 200, 3), Food("Cerise",100, 200, 3), Food("Cerise",100, 200, 3))
            ),
            onTitleChanged = { },
            onDescriptionChanged = { },
            onCardioChanged = { },
            onFoodNameChanged = {_, _ -> Unit },
            onFoodQuantityChanged =  {_, _ -> Unit },
            onFoodCalorieChanged = {_, _ -> Unit },
            onFoodProteinChanged = {_, _ -> Unit },
            modifier = Modifier
        )
    }

}