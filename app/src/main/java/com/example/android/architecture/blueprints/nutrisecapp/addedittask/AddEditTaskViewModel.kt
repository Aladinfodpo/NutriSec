/*
 * Copyright 2019 The Android Open Source Project
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

package com.example.android.architecture.blueprints.nutrisecapp.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.data.Task
import com.example.android.architecture.blueprints.nutrisecapp.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the Add/Edit screen
 */
data class AddEditTaskUiState(
    val task : Task,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isTaskSaved: Boolean = false
)

/**
 * ViewModel for the Add/Edit screen.
 */
@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: String? = savedStateHandle[NutriSecDestinationsArgs.TASK_ID_ARG]

    // A MutableStateFlow needs to be created in this ViewModel. The source of truth of the current
    // editable Task is the ViewModel, we need to mutate the UI state directly in methods such as
    // `updateTitle` or `updateDescription`
    private val _uiState = MutableStateFlow(AddEditTaskUiState(Task(id = savedStateHandle[NutriSecDestinationsArgs.TASK_ID_ARG]?: "")))
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    // Called when clicking on fab.
    fun saveTask() {
        if (taskId == null) {
            createNewTask()
        } else {
            updateTask()
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(task = it.task.copy(title = newTitle))
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(task = it.task.copy(description = newDescription))
        }
    }

    fun addFood() {
        _uiState.update {

            val newFoods = it.task.foods + Food("", 0, 0 ,0)

            it.copy(task = it.task.copy(foods = newFoods))
        }
    }

    fun removeFoodI(iFood: Int){
        _uiState.update {

            val newFoods = it.task.foods.filterIndexed({i, _ -> i != iFood})

            it.copy(task = it.task.copy(foods = newFoods))
        }
    }

    fun updateFoodIName(iFood: Int, newName: String) {
        _uiState.update {

            val newFoods = it.task.foods.mapIndexed { i, food ->
                if (i == iFood) food.copy(name = newName) else food
            }

            it.copy(task = it.task.copy(foods = newFoods))
        }
    }

    fun updateFoodIQuantity(iFood: Int, newQuantity: String) {
        var value = 0
        newQuantity.toIntOrNull().let {it -> if(it != null && it < 3000) value = it}

        _uiState.update {

            val newFoods = it.task.foods.mapIndexed { i, food ->
                if (i == iFood) food.copy(quantity = value) else food
            }

            it.copy(task = it.task.copy(foods = newFoods))
        }
    }

    fun updateFoodICalories(iFood: Int, newCalories: String) {
        var value = 0
        newCalories.toIntOrNull().let {it -> if(it != null && it < 3000) value = it}
        _uiState.update {

            val newFoods = it.task.foods.mapIndexed { i, food ->
                if (i == iFood) food.copy(calories = value) else food
            }

            it.copy(task = it.task.copy(foods = newFoods))
        }
    }

    fun updateFoodIProtein(iFood: Int, newProtein: String) {
        var value = 0
        newProtein.toIntOrNull().let {it -> if(it != null && it < 100) value = it}

        _uiState.update {
            val newFoods = it.task.foods.mapIndexed { i, food ->
                if (i == iFood) food.copy(protein = value) else food
            }

            it.copy(task = it.task.copy(foods = newFoods))
        }
    }

    fun updateCardio(newCardio: String) {
        var value = 0
        newCardio.toIntOrNull().let { if(it != null && it < 3000) value = it}
        _uiState.update {
            it.copy(task = it.task.copy(calCardio = value))
        }
    }

    private fun createNewTask() = viewModelScope.launch {
        taskRepository.createTask(uiState.value.task.title, uiState.value.task.description)
        _uiState.update {
            it.copy(isTaskSaved = true)
        }
    }

    private fun updateTask() {
        if (taskId == null) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        viewModelScope.launch {
            taskRepository.updateTask(
                taskId = uiState.value.task.id,
                title = uiState.value.task.title,
                description = uiState.value.task.description,
                foods = uiState.value.task.foods,
                cardio = uiState.value.task.calCardio
            )
            _uiState.update {
                it.copy(isTaskSaved = true)
            }
        }
    }

    private fun loadTask(taskId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            taskRepository.getTask(taskId).let { task ->
                if (task != null) {
                    _uiState.update {
                        it.copy(task = task,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }
}
