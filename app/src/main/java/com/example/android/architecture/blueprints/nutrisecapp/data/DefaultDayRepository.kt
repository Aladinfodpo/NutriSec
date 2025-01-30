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
import com.example.android.architecture.blueprints.nutrisecapp.di.ApplicationScope
import com.example.android.architecture.blueprints.nutrisecapp.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [DayRepository]. Single entry point for managing days' data.
 *
 * @param localDataSource - The local data source
 * @param dispatcher - The dispatcher to be used for long running or complex operations, such as ID
 * generation or mapping many models.
 * @param scope - The coroutine scope used for deferred jobs where the result isn't important, such
 * as sending data to the network.
 */
@Singleton
class DefaultDayRepository @Inject constructor(
    private val localDataSource: DayDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : DayRepository {

    override suspend fun createDay(day: Day): Long {
        val dayId = Calendar.getInstance().timeInMillis
        localDataSource.upsert(day.copy(id = dayId))
        return dayId
    }

    override suspend fun updateDay(day: Day) {
        getDay(dayId = day.id) ?: throw Exception("Day (id $day.id) not found")
        localDataSource.upsert(day)
    }

    override suspend fun getDays(): List<Day> {
        return localDataSource.getAll()
    }

    override suspend fun getLastDays(n: Int) : List<Day>{
        return localDataSource.getLastDays(n)
    }

    override fun getLastDaysStream(n: Int) : Flow<List<Day>>{
        return localDataSource.observeLastDays(n)
    }

    override fun getDaysStream(): Flow<List<Day>> {
        return localDataSource.observeAll().map { days -> days }
    }

    override fun getDayStream(dayId: Long): Flow<Day?> {
        return localDataSource.observeById(dayId).map { it }
    }

    /**
     * Get a Day with the given ID. Will return null if the day cannot be found.
     *
     * @param dayId - The ID of the day
     */
    override suspend fun getDay(dayId: Long): Day? {
        return localDataSource.getById(dayId)
    }

    override suspend fun completeDay(dayId: Long) {
        localDataSource.updateCompleted(dayId = dayId, completed = true)
    }

    override suspend fun activateDay(dayId: Long) {
        localDataSource.updateCompleted(dayId = dayId, completed = false)
    }

    override suspend fun clearCompletedDays() {
        localDataSource.deleteCompleted()
    }

    override suspend fun deleteAllDays() {
        localDataSource.deleteAll()
    }

    override suspend fun deleteDay(dayId: Long) {
        localDataSource.deleteById(dayId)
    }
}
