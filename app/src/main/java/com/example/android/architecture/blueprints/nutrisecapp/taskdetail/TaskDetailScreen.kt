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

package com.example.android.architecture.blueprints.nutrisecapp.taskdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Edit
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
import com.example.android.architecture.blueprints.nutrisecapp.data.Task
import com.example.android.architecture.blueprints.nutrisecapp.util.LoadingContent
import com.example.android.architecture.blueprints.nutrisecapp.util.TaskDetailTopAppBar

@Composable
fun TaskDetailScreen(
    onEditTask: (String) -> Unit,
    onBack: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TaskDetailTopAppBar(onBack = onBack, onDelete = viewModel::deleteTask) },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = { onEditTask(viewModel.taskId) }) {
                Icon(Icons.Filled.Edit, stringResource(id = R.string.edit_task))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        EditTaskContent(
            loading = uiState.isLoading,
            empty = uiState.task == null && !uiState.isLoading,
            task = uiState.task,
            onRefresh = viewModel::refresh,
            onTaskCheck = viewModel::setCompleted,
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

        // Check if the task is deleted and call onDeleteTask
        LaunchedEffect(uiState.isTaskDeleted) {
            if (uiState.isTaskDeleted) {
                onDeleteTask()
            }
        }
    }
}

@Composable
private fun EditTaskContent(
    loading: Boolean,
    empty: Boolean,
    task: Task?,
    onTaskCheck: (Boolean) -> Unit,
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

    if (task != null) {
            LazyColumn(modifier = commonModifier
                .padding(all = dimensionResource(id = R.dimen.horizontal_margin))) {
                item{
                    Text(text = task.title, style = MaterialTheme.typography.headlineSmall)
                }
                items(task.foods) { food ->
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween){
                        Text(food.name, textAlign = TextAlign.Center)
                        Text(food.quantity.toString() +" g", textAlign = TextAlign.Center)
                        Text(food.calories.toString() + " kcal", textAlign = TextAlign.Center)
                        Text(food.protein.toString()+" g", textAlign = TextAlign.Center)
                    }
                }
                item{
                    Text("Cardio : " +task.calCardio.toString()+" kcal")
                }
            }

    }

}

@Preview
@Composable
private fun EditTaskContentPreview() {
    Surface {
        EditTaskContent(
            loading = false,
            empty = false,
            Task(
                title = "Title",
                description = "Description",
                isCompleted = false,
                id = "ID",
                foods = mutableListOf(Food("Cerise",100, 200, 3), Food("Tomate",10, 2000, 333), Food("Cerise",100, 200, 3))
            ),
            onTaskCheck = { },
            onRefresh = { }
        )
    }

}

@Preview
@Composable
private fun EditTaskContentTaskCompletedPreview() {
    Surface {
        EditTaskContent(
            loading = false,
            empty = false,
            Task(
                title = "Title",
                description = "Description",
                isCompleted = false,
                id = "ID"
            ),
            onTaskCheck = { },
            onRefresh = { }
        )
    }
}

@Preview
@Composable
private fun EditTaskContentEmptyPreview() {
    Surface {
        EditTaskContent(
            loading = false,
            empty = true,
            Task(
                title = "Title",
                description = "Description",
                isCompleted = false,
                id = "ID"
            ),
            onTaskCheck = { },
            onRefresh = { }
        )
    }
}
