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

package com.example.android.architecture.blueprints.nutrisecapp.addeditday

import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.DayRepository
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
data class AddEditDayUiState(
    val day : Day,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isDaySaved: Boolean = false
)

/**
 * ViewModel for the Add/Edit screen.
 */
@HiltViewModel
class AddEditDayViewModel @Inject constructor(
    private val dayRepository: DayRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dayId: Long? = savedStateHandle.get<Long>(NutriSecDestinationsArgs.DAY_ID_ARG).let{ it: Long? -> if(it == 0L) null else it}

    // A MutableStateFlow needs to be created in this ViewModel. The source of truth of the current
    // editable Day is the ViewModel, we need to mutate the UI state directly in methods such as
    // `updateTitle` or `updateDescription`
    private val _uiState = MutableStateFlow(AddEditDayUiState(Day(id = savedStateHandle[NutriSecDestinationsArgs.DAY_ID_ARG]?: 0)))
    val uiState: StateFlow<AddEditDayUiState> = _uiState.asStateFlow()

    init {
        if (dayId != null) {
            loadDay(dayId)
        }
    }

    // Called when clicking on fab.
    fun saveDay() {
        if (dayId == null) {
            createNewDay()
        } else {
            updateDay()
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(day = it.day.copy(title = newTitle))
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(day = it.day.copy(description = newDescription))
        }
    }

    fun addFood() {
        _uiState.update {

            val newFoods = it.day.foods + Food("", 0, 0 ,0)

            it.copy(day = it.day.copy(foods = newFoods))
        }
    }

    fun removeFoodI(iFood: Int){
        _uiState.update {

            val newFoods = it.day.foods.filterIndexed({i, _ -> i != iFood})

            it.copy(day = it.day.copy(foods = newFoods))
        }
    }

    fun updateFoodIName(iFood: Int, newName: String) {
        _uiState.update {

            val newFoods = it.day.foods.mapIndexed { i, food ->
                if (i == iFood) food.copy(name = newName) else food
            }

            it.copy(day = it.day.copy(foods = newFoods))
        }
    }

    fun updateFoodIQuantity(iFood: Int, newQuantity: String) {
        var value = 0
        newQuantity.toIntOrNull().let {it -> if(it != null && it < 3000) value = it}

        _uiState.update {

            val newFoods = it.day.foods.mapIndexed { i, food ->
                if (i == iFood) food.copy(quantity = value) else food
            }

            it.copy(day = it.day.copy(foods = newFoods))
        }
    }

    fun updateFoodICalories(iFood: Int, newCalories: String) {
        var value = 0
        newCalories.toIntOrNull().let {it -> if(it != null && it < 3000) value = it}
        _uiState.update {

            val newFoods = it.day.foods.mapIndexed { i, food ->
                if (i == iFood) food.copy(calories = value) else food
            }

            it.copy(day = it.day.copy(foods = newFoods))
        }
    }

    fun updateFoodIProtein(iFood: Int, newProtein: String) {
        var value = 0
        newProtein.toIntOrNull().let {it -> if(it != null && it < 100) value = it}

        _uiState.update {
            val newFoods = it.day.foods.mapIndexed { i, food ->
                if (i == iFood) food.copy(protein = value) else food
            }

            it.copy(day = it.day.copy(foods = newFoods))
        }
    }

    fun updateCardio(newCardio: String) {
        var value = 0
        newCardio.toIntOrNull().let { if(it != null && it < 3000) value = it}
        _uiState.update {
            it.copy(day = it.day.copy(calCardio = value))
        }
    }

    fun updateWeight(newWeight: String) {
        var value = 0.0
        newWeight.toDoubleOrNull().let { if(it != null) value = it}
        _uiState.update {
            it.copy(day = it.day.copy(weight = value))
        }
    }

    private fun createNewDay() = viewModelScope.launch {
        dayRepository.createDay( day = uiState.value.day)
        _uiState.update {
            it.copy(isDaySaved = true)
        }
    }

    private fun updateDay() {
        if (dayId == null) {
            throw RuntimeException("updateDay() was called but day is new.")
        }
        viewModelScope.launch {
            dayRepository.updateDay(day = uiState.value.day)
            _uiState.update {
                it.copy(isDaySaved = true)
            }
        }
    }

    private fun loadDay(dayId: Long) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            dayRepository.getDay(dayId).let { day ->
                if (day != null) {
                    _uiState.update {
                        it.copy(day = day,
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
