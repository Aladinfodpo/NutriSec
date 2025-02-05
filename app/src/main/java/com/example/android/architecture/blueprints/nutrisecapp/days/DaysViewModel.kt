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

package com.example.android.architecture.blueprints.nutrisecapp.days

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.nutrisecapp.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.nutrisecapp.DELETE_RESULT_OK
import com.example.android.architecture.blueprints.nutrisecapp.EDIT_RESULT_OK
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.DayRepository
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.ACTIVE_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.ALL_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysFilterType.COMPLETED_DAYS
import com.example.android.architecture.blueprints.nutrisecapp.util.Async
import com.example.android.architecture.blueprints.nutrisecapp.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the day list screen.
 */
data class DaysUiState(
    val items: List<Day> = emptyList(),
    val isLoading: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = FilteringUiInfo(),
    val userMessage: Int? = null
)

/**
 * ViewModel for the day list screen.
 */
@HiltViewModel
class DaysViewModel @Inject constructor(
    private val dayRepository: DayRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType =
        savedStateHandle.getStateFlow(DAYS_FILTER_SAVED_STATE_KEY, ALL_DAYS)

    private val _filterUiInfo = _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()
    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _filteredDaysAsync =
        combine(dayRepository.getDaysStream(), _savedFilterType) { days, type ->
            filterDays(days, type)
        }
            .map { Async.Success(it) }
            .catch<Async<List<Day>>> { emit(Async.Error(R.string.loading_days_error)) }

    val uiState: StateFlow<DaysUiState> = combine(
        _filterUiInfo, _isLoading, _userMessage, _filteredDaysAsync
    ) { filterUiInfo, isLoading, userMessage, daysAsync ->
        when (daysAsync) {
            Async.Loading -> {
                DaysUiState(isLoading = true)
            }
            is Async.Error -> {
                DaysUiState(userMessage = daysAsync.errorMessage)
            }
            is Async.Success -> {
                DaysUiState(
                    items = daysAsync.data,
                    filteringUiInfo = filterUiInfo,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DaysUiState(isLoading = true)
        )

    fun setFiltering(requestType: DaysFilterType) {
        savedStateHandle[DAYS_FILTER_SAVED_STATE_KEY] = requestType
    }

    fun clearCompletedDays() {
        viewModelScope.launch {
            dayRepository.clearCompletedDays()
            showSnackbarMessage(R.string.completed_days_cleared)
            refresh()
        }
    }

    fun completeDay(day: Day, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            dayRepository.completeDay(day.id)
            showSnackbarMessage(R.string.day_marked_complete)
        } else {
            dayRepository.activateDay(day.id)
            showSnackbarMessage(R.string.day_marked_active)
        }
    }

    fun showEditResultMessage(result: Int) {
        when (result) {
            EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_saved_day_message)
            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_day_message)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_day_message)
        }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            _isLoading.value = false
        }
    }

    private fun filterDays(days: List<Day>, filteringType: DaysFilterType): List<Day> {
        val daysToShow = ArrayList<Day>()
        // We filter the days based on the requestType
        for (day in days) {
            when (filteringType) {
                ALL_DAYS -> daysToShow.add(day)
                ACTIVE_DAYS -> if (day.isActive) {
                    daysToShow.add(day)
                }
                COMPLETED_DAYS -> if (day.isCompleted) {
                    daysToShow.add(day)
                }
            }
        }
        return daysToShow
    }

    private fun getFilterUiInfo(requestType: DaysFilterType): FilteringUiInfo =
        when (requestType) {
            ALL_DAYS -> {
                FilteringUiInfo(
                    R.string.label_all, R.string.no_days_all,
                    R.drawable.logo
                )
            }
            ACTIVE_DAYS -> {
                FilteringUiInfo(
                    R.string.label_active, R.string.no_days_active,
                    R.drawable.ic_check_circle_96dp
                )
            }
            COMPLETED_DAYS -> {
                FilteringUiInfo(
                    R.string.label_completed, R.string.no_days_completed,
                    R.drawable.ic_verified_user_96dp
                )
            }
        }
}

// Used to save the current filtering in SavedStateHandle.
const val DAYS_FILTER_SAVED_STATE_KEY = "DAYS_FILTER_SAVED_STATE_KEY"

data class FilteringUiInfo(
    val currentFilteringLabel: Int = R.string.label_all,
    val noDaysLabel: Int = R.string.no_days_all,
    val noDayIconRes: Int = R.drawable.logo,
)
