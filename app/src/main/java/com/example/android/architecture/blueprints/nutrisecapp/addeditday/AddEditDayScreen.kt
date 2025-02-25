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

package com.example.android.architecture.blueprints.nutrisecapp.addeditday

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.util.AddEditDayTopAppBar
import com.example.android.architecture.blueprints.nutrisecapp.util.primaryDarkColor

import com.example.android.architecture.blueprints.nutrisecapp.util.CalculatorContent
import com.example.android.architecture.blueprints.nutrisecapp.util.DayItemProgressBar
import com.example.android.architecture.blueprints.nutrisecapp.util.EditListFoodDay
import com.example.android.architecture.blueprints.nutrisecapp.util.LayoutFoodParameter
import java.util.Calendar

@Composable
fun AddEditDayScreen(
    @StringRes topBarTitle: Int,
    onDayUpdate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditDayViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { AddEditDayTopAppBar(topBarTitle) { onBack() } },
        floatingActionButton = { }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        if(!uiState.isLoading)
            AddEditDayContent(
                day = uiState.day,
                onTitleChanged = viewModel::updateTitle,
                onDescriptionChanged = viewModel::updateDescription,
                onCardioChanged = viewModel::updateCardio,
                onWeightChanged = viewModel::updateWeight,
                onSaveDay = viewModel::saveDay,
                modifier = Modifier.padding(paddingValues)
            )

        // Check if the day is saved and call onDayUpdate event
        LaunchedEffect(uiState.isDaySaved) {
            if (uiState.isDaySaved) {
                onDayUpdate()
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
private fun AddEditDayContent(
    day: Day,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSaveDay: (List<Food>) -> Unit,
    onCardioChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {

        val numberEditableColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryDarkColor,
            unfocusedBorderColor = primaryDarkColor,
            cursorColor = Color.Black
        )
        Column(modifier = modifier.imePadding())
        {
            var editTitle by remember { mutableStateOf(false) }
            DayItemProgressBar(day, emptyList(), onDayClick = { editTitle = !editTitle})
            if(editTitle)
                OutlinedTextField(
                    value = day.title,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    onValueChange = onTitleChanged,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.title_hint),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineSmall
                        .copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
            val foods = remember { mutableStateListOf(*day.foods.toTypedArray()) }
            val layoutFoodParameters = remember { mutableStateListOf(*MutableList(foods.size, init = {_ -> LayoutFoodParameter() }).toTypedArray()) }
            EditListFoodDay(foods,layoutFoodParameters, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), showTime = true)
            OutlinedTextField(
                value = day.calCardio.let { if (it == 0) "" else it.toString() },
                onValueChange = onCardioChanged,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                colors = numberEditableColors,
                maxLines = 1,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Cardio") },
                suffix = { Text("kcal") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text("Total")
                Text(day.foods.sumOf { it.quantity }.toString())
                Text(day.getCalDay.toString())
                Text(day.foods.sumOf { it.protein  }.toString())
                Icon(if (day.isBad) Icons.Filled.Error else Icons.Filled.Done, "Day done", tint = if (day.isBad) Color.Red else primaryDarkColor)
            }
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                var weight by remember { mutableStateOf("Loading") }
                if(weight == "Loading" )
                    weight = day.weight.toString()

                OutlinedTextField(
                    value = weight,
                    onValueChange = {it -> onWeightChanged(it); weight = it},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    colors = numberEditableColors,
                    maxLines = 1,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    label = { Text("Poids (couché)") },
                    suffix = { Text("kg") }
                )

                ElevatedButton(
                    onClick = {onSaveDay(foods.toList())},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 100.dp),
                    colors = ButtonDefaults.buttonColors().copy(containerColor = primaryDarkColor)
                ){
                    Icon(imageVector = Icons.Filled.Done, contentDescription = "Finish",  tint = Color.White)
                }

                CalculatorContent()
            }
        }

}

@Preview
@Composable
private fun AddEditDayScreenPreview() {
    Surface {
        AddEditDayContent(
            day = Day(
                title = "Title",
                description = "Description",
                isCompleted = false,
                calCardio = 10,
                id = 1,
                foods = listOf(Food("Cerise",100, 200, 3), Food("Cerise",100, 200, 3), Food("Cerise",100, 200, 3))
            ),
            onTitleChanged = { },
            onDescriptionChanged = { },
            onCardioChanged = { },
            onWeightChanged = { },
            onSaveDay = {},
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun FullAddEditDayScreenPreview() {
    Surface {
        AddEditDayContent(
            day = Day(
                title = "Title",
                description = "Description",
                isCompleted = false,
                calCardio = 10,
                id = 1,
                foods = listOf(Food("Cerise",100, 200, 3),
                               Food("Cerise",100, 200, 3),
                               Food("Cerise",100, 200, 3),
                               Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3),
                    Food("Cerise",100, 200, 3))
            ),
            onTitleChanged = { },
            onDescriptionChanged = { },
            onCardioChanged = { },
            onWeightChanged = { },
            onSaveDay = {},
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun AllPage(){
    Surface {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {  },
            topBar = { AddEditDayTopAppBar(0, {}) },
            floatingActionButton = { }
        ) { paddingValues ->
            AddEditDayContent(
                day = Day(
                    title = "Title",
                    description = "Description",
                    isCompleted = false,
                    calCardio = 10,
                    id = 1,
                    foods = listOf(Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3),
                        Food("Cerise",100, 200, 3))
                ),
                onTitleChanged = { },
                onDescriptionChanged = { },
                onCardioChanged = { },
                onWeightChanged = { },
                onSaveDay = {},
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
