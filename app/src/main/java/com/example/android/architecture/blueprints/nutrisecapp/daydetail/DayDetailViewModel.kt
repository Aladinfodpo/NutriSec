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

package com.example.android.architecture.blueprints.nutrisecapp.daydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.DayRepository
import com.example.android.architecture.blueprints.nutrisecapp.util.Async
import com.example.android.architecture.blueprints.nutrisecapp.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the Details screen.
 */
data class DayDetailUiState(
    val day: Day? = null,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isDayDeleted: Boolean = false
)

/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class DayDetailViewModel @Inject constructor(
    private val dayRepository: DayRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val dayId: Long = savedStateHandle[NutriSecDestinationsArgs.DAY_ID_ARG]!!

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isDayDeleted = MutableStateFlow(false)
    private val _dayAsync = dayRepository.getDayStream(dayId)
        .map { handleDay(it) }
        .catch { emit(Async.Error(R.string.loading_day_error)) }

    val uiState: StateFlow<DayDetailUiState> = combine(
        _userMessage, _isLoading, _isDayDeleted, _dayAsync
    ) { userMessage, isLoading, isDayDeleted, dayAsync ->
        when (dayAsync) {
            Async.Loading -> {
                DayDetailUiState(isLoading = true)
            }
            is Async.Error -> {
                DayDetailUiState(
                    userMessage = dayAsync.errorMessage,
                    isDayDeleted = isDayDeleted
                )
            }
            is Async.Success -> {
                DayDetailUiState(
                    day = dayAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage,
                    isDayDeleted = isDayDeleted
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DayDetailUiState(isLoading = true)
        )

    fun deleteDay() = viewModelScope.launch {
        dayRepository.deleteDay(dayId)
        _isDayDeleted.value = true
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val day = uiState.value.day ?: return@launch
        if (completed) {
            dayRepository.completeDay(day.id)
            showSnackbarMessage(R.string.day_marked_complete)
        } else {
            dayRepository.activateDay(day.id)
            showSnackbarMessage(R.string.day_marked_active)
        }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    private fun handleDay(day: Day?): Async<Day?> {
        if (day == null) {
            return Async.Error(R.string.day_not_found)
        }
        return Async.Success(day)
    }
}
