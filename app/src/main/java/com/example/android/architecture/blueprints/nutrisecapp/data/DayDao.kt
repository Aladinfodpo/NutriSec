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

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the day table.
 */
@Dao
interface DayDao {

    /**
     * Observes list of days.
     *
     * @return all days.
     */
    @Query("SELECT * FROM Days")
    fun observeAll(): Flow<List<Day>>

    /**
     * Observes a single day.
     *
     * @param dayId the day id.
     * @return the day with dayId.
     */
    @Query("SELECT * FROM Days WHERE id = :dayId")
    fun observeById(dayId: String): Flow<Day>

    /**
     * Select all days from the days table.
     *
     * @return all days.
     */
    @Query("SELECT * FROM Days")
    suspend fun getAll(): List<Day>

    /**
     * Select a day by id.
     *
     * @param dayId the day id.
     * @return the day with dayId.
     */
    @Query("SELECT * FROM Days WHERE id = :dayId")
    suspend fun getById(dayId: String): Day?

    /**
     * Insert or update a day in the database. If a day already exists, replace it.
     *
     * @param day the day to be inserted or updated.
     */
    @Upsert
    suspend fun upsert(day: Day)

    /**
     * Insert or update days in the database. If a day already exists, replace it.
     *
     * @param days the days to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAll(days: List<Day>)

    /**
     * Update the complete status of a day
     *
     * @param dayId id of the day
     * @param completed status to be updated
     */
    @Query("UPDATE Days SET isCompleted = :completed WHERE id = :dayId")
    suspend fun updateCompleted(dayId: String, completed: Boolean)

    /**
     * Delete a day by id.
     *
     * @return the number of days deleted. This should always be 1.
     */
    @Query("DELETE FROM Days WHERE id = :dayId")
    suspend fun deleteById(dayId: String): Int

    /**
     * Delete all days.
     */
    @Query("DELETE FROM Days")
    suspend fun deleteAll()

    /**
     * Delete all completed days from the table.
     *
     * @return the number of days deleted.
     */
    @Query("DELETE FROM Days WHERE isCompleted = 1")
    suspend fun deleteCompleted(): Int
}
