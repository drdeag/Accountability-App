
package com.dev.accountability

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class MealStatus { UPCOMING, DONE, MISSED, SKIPPED }

data class MealItem(val label: String, val status: MealStatus)

data class UiState(
    val streak: Int = 0,
    val nextMealLabel: String = "Lunch",
    val nextMealCountdown: String = "1h 12m",
    val paused: Boolean = false,
    val meals: List<MealItem> = listOf(
        MealItem("Breakfast", MealStatus.MISSED),
        MealItem("Lunch", MealStatus.UPCOMING),
        MealItem("Dinner", MealStatus.UPCOMING),
        MealItem("Snack/Supplement", MealStatus.UPCOMING)
    )
)

class MainViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun togglePause() {
        _uiState.value = _uiState.value.copy(paused = !_uiState.value.paused)
    }

    fun openSettings() { /* TODO: open settings screen in Phase 1.1 */ }

    fun openCameraOrLog() { /* TODO: integrate camera intent in Phase 1.1 */ }

    fun logMeal(label: String, withPhoto: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.meals.map {
                if (it.label == label) it.copy(status = MealStatus.DONE) else it
            }
            _uiState.value = _uiState.value.copy(meals = updated)
        }
    }

    fun skipMeal(label: String) {
        viewModelScope.launch {
            val updated = _uiState.value.meals.map {
                if (it.label == label) it.copy(status = MealStatus.SKIPPED) else it
            }
            _uiState.value = _uiState.value.copy(meals = updated)
        }
    }

    fun openHistory() { /* TODO: history screen in Phase 1.1 */ }
}
