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
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min


@Serializable
data class Food(
    var name: String,
    var quantity: Int,
    var calories: Int,
    var protein: Int,
    var fat: Int = 0,
    var glucide: Int = 0,
    var hour: Int = 0,
    var minute: Int = 0
){
    private val coefE = 6.66F
    private val coefP = 5.0F
    private val coefVmin = 1.5F
    private val coefVat = 40.0F

    val waterPercent: Float
        get() = if(quantity == 0) 10.0F else (max(0, quantity - protein - glucide - fat)*100) / max(0.01F, quantity.toFloat())

    val nutriScore: Float
        get() = max(0.0F, min(10.0F, getEnergetism()*coefE + getProteism()*coefP + getBonusVege()))

    private fun getEnergetism(): Float{
        return 1.075F/ (1.0F+ exp(1.1F*(calories/max(0.01F, quantity.toFloat()) -2.5F)))
    }
    private fun getProteism(): Float{
        return protein/ max(1.0F, quantity*waterPercent/100.0F)
    }

    private fun getBonusVege(): Float{
        val a = (10-coefE-coefVmin)/(100.0F - coefVat)
        return if(waterPercent >= coefVat) a * waterPercent + 10.0F - coefE - 100.0F * a  else 0.0F
    }

    fun isPossible(): Boolean{
        if(quantity == 0)
            return false
        if(quantity*1.05 < protein + glucide + fat)
            return false
        if(calories*1.05 < protein*4 + glucide*4 + fat*9)
            return false
        if(calories*0.90 > protein*4 + glucide*4 + fat*9)
            return false
        return true
    }
}

const val maxCalorie: Int = 2700

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
 * Immutable model class for a Day.
 *
 * @param title title of the day
 * @param description description of the day
 * @param isCompleted whether or not this day is completed
 * @param id id of the day
 *
 * NutriSec: The constructor of this class should be `internal` but it is used in previews and tests
 *  so that's not possible until those previews/tests are refactored.
 */
@Entity(
    tableName = "Days"
)
data class Day(
    val title: String = Calendar.getInstance().let { it.get(Calendar.DAY_OF_MONTH).toString() + "/" + (if(it.get(Calendar.MONTH) + 1 < 10) "0" else "") + (it.get(Calendar.MONTH)+1).toString() +"/"+ (it.get(Calendar.YEAR)-2000).toString()},
    val description: String = "",
    val isCompleted: Boolean = false,
    val calCardio: Int = 0,
    val weight: Double = 0.0,
    @PrimaryKey val id: Long,
    @TypeConverters(FoodConverter::class) var foods: List<Food> = emptyList()
) {

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val getCalDay: Int
        get() = foods.sumOf { it.calories } - calCardio

    val isBad: Boolean
        get() = getCalDay > maxCalorie
}