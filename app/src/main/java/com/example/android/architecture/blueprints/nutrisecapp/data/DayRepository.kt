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

package com.example.android.architecture.blueprints.nutrisecapp.data

import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the data layer.
 */
interface DayRepository {

    fun getDaysStream(): Flow<List<Day>>

    suspend fun getDays(): List<Day>

    fun getDayStream(dayId: Long): Flow<Day?>

    suspend fun getDay(dayId: Long): Day?

    fun getLastDaysStream(n: Int) : Flow<List<Day>>

    suspend fun getLastDays(n: Int) : List<Day>

    suspend fun createDay(day: Day): Long

    suspend fun updateDay(day: Day)

    suspend fun completeDay(dayId: Long)

    suspend fun activateDay(dayId: Long)

    suspend fun clearCompletedDays()

    suspend fun deleteAllDays()

    suspend fun deleteDay(dayId: Long)
}
