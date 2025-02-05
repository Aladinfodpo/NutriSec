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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt


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
    val coefE = 6.66F
    val coefP = 3.0F
    val coefVmin = 1.5F
    val coefVat = 40.0F

    val waterPercent: Float
        get() = if(quantity == 0) 10.0F else (max(0, quantity - protein - glucide - fat)*100) / max(0.01F, quantity.toFloat())

    val nutriScore: Float
        get() = max(0.0F, min(10.0F, getEnergetism()*coefE + getProteism()*coefP + getBonusVege()))

    fun getNutriScoreString(): String = nutriScore.run{if(abs(round(this) - this) < 0.1F) this.roundToInt().toString() else "%.1f".format(this)}

    fun getEnergetism(): Float{
        return 1.075F/ (1.0F+ exp(1.1F*(calories/max(0.01F, quantity.toFloat()) -2.5F)))
    }
    fun getProteism(): Float{
        return protein/ max(1.0F, quantity*(100-waterPercent)/100.0F)
    }

    fun getBonusVege(): Float{
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
        if(calories*0.85 > protein*4 + glucide*4 + fat*9)
            return false
        return true
    }

    companion object {
        fun getMeal(foods: List<Food>) : Food {
            return if(foods.isEmpty()) Food("Total", 0,0,0) else Food("Total", quantity = foods.sumOf { it.quantity }, calories = foods.sumOf { it.calories }, protein = foods.sumOf { it.protein }, fat = foods.sumOf { it.fat }, glucide = foods.sumOf { it.glucide }, hour = foods[0].hour, minute = foods[0].minute )
        }
    }
}

@Preview
@Composable
fun FoodStatPreview(){
    Surface(){
        val foods = listOf(
            Food("sucre"      , 100,400, 0,  0,100),
            Food("eau"        , 100,  0, 0,  0,  0),
            Food("pates seche", 100,360,13,  2, 70),
            Food("pates humid", 200,360,13,  2, 70),
            Food("whey       ", 100,380,70,  2, 20),
            Food("whey  humi5", 500,380,70,  2, 20),
            Food("whey  humi2", 200,380,70,  2, 20),
            Food("barre nu   ", 100,360,25, 16, 29),
            Food("jambon   "  , 100,145,21,  6,  1),
            Food("champignon" , 100, 18, 2,  0,  1),
            Food("pois chiche", 100,125, 7,  3, 16),
            Food("chocolatine", 100,423, 6,  24,43),
            )
        LazyColumn (modifier = Modifier.fillMaxWidth()){
            items(foods){food ->
                Row(modifier = Modifier.fillMaxWidth()){
                    Text(modifier = Modifier.width(80.dp), text =food.name)
                    Text( " E = %.1f".format(food.getEnergetism()*food.coefE) + "   P = "+"%.1f".format(food.getProteism()*food.coefP).padStart(4, '0') + "   V("+"%.0f)".format(food.waterPercent).padStart(4, '0') +" = %.1f ".format(food.getBonusVege()) + "   " + "%.1f/10".format(food.nutriScore).padStart(7, '0'))

                }
            }
        }
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
    val title: String = Calendar.getInstance().let { it.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0') + "/" + (it.get(Calendar.MONTH)+1).toString().padStart(2, '0') + "/"+ (it.get(Calendar.YEAR)-2000).toString()},
    val description: String = "",
    val isCompleted: Boolean = false,
    val calCardio: Int = 0,
    val weight: Float = 0.0F,
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