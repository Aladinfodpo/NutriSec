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

import kotlinx.coroutines.flow.Flow

/**
 * Interface to the data layer.
 */
interface DayRepository {

    fun getDaysStream(): Flow<List<Day>>

    suspend fun getDays(): List<Day>

    fun getDayStream(dayId: String): Flow<Day?>

    suspend fun getDay(dayId: String): Day?

    suspend fun refreshDay(dayId: String)

    suspend fun createDay(title: String, description: String): String

    suspend fun updateDay(dayId: String, title: String, description: String, foods: List<Food>, cardio: Int)

    suspend fun completeDay(dayId: String)

    suspend fun activateDay(dayId: String)

    suspend fun clearCompletedDays()

    suspend fun deleteAllDays()

    suspend fun deleteDay(dayId: String)
}