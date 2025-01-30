package com.example.android.architecture.blueprints.nutrisecapp.drawer

import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.DayRepository
import com.example.android.architecture.blueprints.nutrisecapp.days.DaysUiState
import com.example.android.architecture.blueprints.nutrisecapp.statistics.StatisticsUiState
import com.example.android.architecture.blueprints.nutrisecapp.statistics.getActiveAndCompletedStats
import com.example.android.architecture.blueprints.nutrisecapp.util.Async
import com.example.android.architecture.blueprints.nutrisecapp.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * UiState for the Add/Edit screen
 */
data class DrawerUiState(
    val todayId : Long ?= null,
    val isLoading : Boolean = true
)

/**
 * ViewModel for the Add/Edit screen.
 */
@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val dayRepository: DayRepository
) : ViewModel() {
    val uiState: StateFlow<DrawerUiState> = dayRepository.getLastDaysStream(1)
        .map { Async.Success(it) }
        .catch<Async<List<Day>>> { emit(Async.Error(R.string.loading_days_error)) }
        .map { dayAsync -> deduceLastDay(dayAsync) }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DrawerUiState(isLoading = true)
        )

    private fun deduceLastDay(dayLoad: Async<List<Day>>) =
        when (dayLoad) {
        Async.Loading -> {
            DrawerUiState()
        }
        is Async.Error -> {
            DrawerUiState(isLoading = false)
        }
        is Async.Success -> {
            val milliToday = Calendar.getInstance()
            milliToday.set(Calendar.HOUR_OF_DAY, 0)
            milliToday.set(Calendar.MINUTE, 0)
            milliToday.set(Calendar.SECOND, 0)
            milliToday.set(Calendar.MILLISECOND, 0)


            if(dayLoad.data.isEmpty())
                DrawerUiState(isLoading = false)
            else {
                var milliLoad = Calendar.getInstance()
                milliLoad.timeInMillis = dayLoad.data[0].id
                milliLoad.set(Calendar.HOUR_OF_DAY, 0)
                milliLoad.set(Calendar.MINUTE, 0)
                milliLoad.set(Calendar.SECOND, 0)
                milliLoad.set(Calendar.MILLISECOND, 0)
                if(milliLoad == milliToday)
                    DrawerUiState(todayId = dayLoad.data[0].id)
                else
                    DrawerUiState(isLoading = false)
            }
        }
    }


}