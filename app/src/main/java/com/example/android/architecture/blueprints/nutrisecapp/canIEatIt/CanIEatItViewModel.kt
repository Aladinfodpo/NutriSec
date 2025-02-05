package com.example.android.architecture.blueprints.nutrisecapp.canIEatIt

import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.NutriSecDestinationsArgs
import com.example.android.architecture.blueprints.nutrisecapp.addeditday.AddEditDayUiState
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
import kotlin.math.abs

/**
 * UiState for the Add/Edit screen
 */
data class CanIEatItUiState(
    val dayId : Long?,
    val day : Day?,
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

/**
 * ViewModel for the Add/Edit screen.
 */
@HiltViewModel
class CanIEatItViewModel @Inject constructor(
    private val dayRepository: DayRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val dayId: Long? = savedStateHandle.get<Long>(NutriSecDestinationsArgs.DAY_ID_ARG).let{ it: Long? -> if(it == 0L) null else it}
    private val _uiState = MutableStateFlow(CanIEatItUiState(dayId = dayId, dayId?.let{Day(id = dayId)}))
    val uiState: StateFlow<CanIEatItUiState> = _uiState.asStateFlow()

    init {
        if (dayId != null) {
            loadDay(dayId)
        }
    }

    fun createNewDay() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(dayId = dayRepository.createDay(day = Day(id = 0)))
            }
            loadDay(_uiState.value.dayId!!)
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun eatFood(foods: List<Food>){
        if(uiState.value.dayId == null) {
            throw RuntimeException("updateDay() was called but day is new.")
        }

        _uiState.update {
            it.copy(day = it.day!!.copy(foods = it.day.foods + foods))
        }

        updateDay()
    }

    fun unEatLastMeal(): List<Food>{
        if(uiState.value.dayId == null) {
            throw RuntimeException("updateDay() was called but day is new.")
        }

        if(_uiState.value.day!!.foods.isEmpty())
            return emptyList()

        val lastHour = _uiState.value.day!!.foods.last().hour
        val lastMeal = _uiState.value.day!!.foods.filter{food: Food -> abs(food.hour - lastHour) <= 3  }
        val foods = _uiState.value.day!!.foods.filter{food: Food -> abs(food.hour - lastHour) > 3 }

        _uiState.update {
            it.copy(day = it.day!!.copy(foods = foods ))
        }

        updateDay()

        return lastMeal;
    }

    fun addCardio(cardio: Int){
        if(uiState.value.dayId == null) {
            throw RuntimeException("updateDay() was called but day is new.")
        }

        _uiState.update {
            it.copy(day = it.day!!.copy(calCardio = it.day.calCardio + cardio))
        }

        updateDay()
    }


    private fun updateDay() {
        if (uiState.value.dayId == null) {
            throw RuntimeException("updateDay() was called but day is new.")
        }
        viewModelScope.launch {
            dayRepository.updateDay(day = uiState.value.day!!)
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