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

package com.example.android.architecture.blueprints.nutrisecapp.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.DayRepository
import com.example.android.architecture.blueprints.nutrisecapp.util.Async
import com.example.android.architecture.blueprints.nutrisecapp.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the statistics screen.
 */
data class StatisticsUiState(
    val isEmpty: Boolean = false,
    val isLoading: Boolean = false,
    val activeDaysPercent: Float = 0f,
    val completedDaysPercent: Float = 0f
)

/**
 * ViewModel for the statistics screen.
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val dayRepository: DayRepository
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> =
        dayRepository.getDaysStream()
            .map { Async.Success(it) }
            .catch<Async<List<Day>>> { emit(Async.Error(R.string.loading_days_error)) }
            .map { dayAsync -> produceStatisticsUiState(dayAsync) }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = StatisticsUiState(isLoading = true)
            )

    fun refresh() {
        viewModelScope.launch {
        }
    }

    private fun produceStatisticsUiState(dayLoad: Async<List<Day>>) =
        when (dayLoad) {
            Async.Loading -> {
                StatisticsUiState(isLoading = true, isEmpty = true)
            }
            is Async.Error -> {
                // NutriSec: Show error message?
                StatisticsUiState(isEmpty = true, isLoading = false)
            }
            is Async.Success -> {
                val stats = getActiveAndCompletedStats(dayLoad.data)
                StatisticsUiState(
                    isEmpty = dayLoad.data.isEmpty(),
                    activeDaysPercent = stats.activeDaysPercent,
                    completedDaysPercent = stats.completedDaysPercent,
                    isLoading = false
                )
            }
        }
}
