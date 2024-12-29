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

import com.example.android.architecture.blueprints.nutrisecapp.data.Day

/**
 * Function that does some trivial computation. Used to showcase unit tests.
 */
internal fun getActiveAndCompletedStats(days: List<Day>): StatsResult {

    return if (days.isEmpty()) {
        StatsResult(0f, 0f)
    } else {
        val totalDays = days.size
        val numberOfActiveDays = days.count { it.isActive }
        StatsResult(
            activeDaysPercent = 100f * numberOfActiveDays / days.size,
            completedDaysPercent = 100f * (totalDays - numberOfActiveDays) / days.size
        )
    }
}

data class StatsResult(val activeDaysPercent: Float, val completedDaysPercent: Float)
