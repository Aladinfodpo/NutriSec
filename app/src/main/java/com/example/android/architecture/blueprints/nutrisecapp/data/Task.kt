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

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar


@Serializable
data class Food(
    var name: String,
    var quantity: Int,
    var calories: Int,
    var protein: Int
)

class FoodConverter {
    @TypeConverter
    fun fromTagsList(tags: List<Food>?): String {
        return Json.encodeToString(tags ?: emptyList())
    }

    @TypeConverter
    fun toTagsList(tagsString: String): List<Food> {
        return Json.decodeFromString(tagsString)
    }
}


/**
 * Immutable model class for a Task.
 *
 * @param title title of the task
 * @param description description of the task
 * @param isCompleted whether or not this task is completed
 * @param id id of the task
 *
 * NutriSec: The constructor of this class should be `internal` but it is used in previews and tests
 *  so that's not possible until those previews/tests are refactored.
 */
@Entity(
    tableName = "task"
)
data class Task(
    val title: String = Calendar.getInstance().let { it.get(Calendar.DAY_OF_MONTH).toString() + "/" + it.get(Calendar.MONTH).toString() +"/"+ it.get(Calendar.YEAR).toString()},
    val description: String = "",
    val isCompleted: Boolean = false,
    val calCardio: Int = 0,
    @PrimaryKey val id: String,
    @TypeConverters(FoodConverter::class) var foods: List<Food> = emptyList()
) {

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()

    val getCalDay: Int
        get() = foods.sumOf { it.calories } - calCardio

    val isBad: Boolean
        get() = getCalDay > 2700
}