/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.android.architecture.blueprints.nutrisecapp.util

import android.graphics.Color.RGBToHSV
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.data.maxCalorie
import com.example.android.architecture.blueprints.nutrisecapp.days.pxToDp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

val primaryDarkColor: Color = Color(0xFA264238)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorContent(){
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Gray,
        unfocusedBorderColor = Color.Gray,
        cursorColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedTextColor = Color.White
    )
    var expanded by remember { mutableStateOf(false) }
    val operations = listOf("x", "+", "-", ":")
    var iOperation by remember { mutableIntStateOf(0) }
    var lhs by remember { mutableStateOf("") }
    var rhs by remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxWidth().background(primaryDarkColor), horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = lhs,
            onValueChange = {string: String -> if (string.length < 4) lhs = string },
            modifier = Modifier.weight(1.0F).padding(4.dp),
            textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            maxLines = 1,
        )

        ExposedDropdownMenuBox(
            expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(0.5F).padding(4.dp)
        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    operations[iOperation],
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                operations.forEachIndexed { i, operation ->
                    DropdownMenuItem(
                        text = { Text(operation, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center,) },
                        onClick = {
                            iOperation = i;
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding

                    )
                }
            }
        }
        OutlinedTextField(
            value = rhs,
            onValueChange = {string: String -> if (string.length < 4) rhs = string },
            modifier = Modifier.weight(1.0F).padding(4.dp),
            textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            maxLines = 1,
        )
        Text("="  ,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.weight(0.5F).padding(4.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )
        val l = lhs.toIntOrNull() ?: 0
        val r = rhs.toIntOrNull() ?: 0
        Text(
            when (iOperation) {
                0 -> l * r
                1 -> l + r
                2 -> l - r
                else -> l / (rhs.toIntOrNull() ?: 1).toDouble()
            }.toString().let{it.substring(0,min(6,it.length))},
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.weight(1.0F).padding(4.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@Composable
fun TodayNotStarted(
    modifier: Modifier,
    onCreateToday: () -> Unit
) {
    Column(
        modifier = modifier.imePadding().fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically)
    ) {
        Text(
            "Today has not started.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        ElevatedButton(
            onClick = onCreateToday,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) { Text("Start today's tracking") }
    }
}

@Composable
fun DayItemProgressBar(
    day: Day,
    foods: List<Food>,
    onDayClick: (Day) -> Unit
){
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(
            horizontal = dimensionResource(id = R.dimen.horizontal_margin),
            vertical = dimensionResource(id = R.dimen.list_item_padding),
        )
        .clip(RoundedCornerShape(25.dp))
        .background(Color(230, 230, 250))
        .clickable { onDayClick(day) }
        .border(1.dp, Color.Gray, shape = RoundedCornerShape(25.dp))
        .onGloballyPositioned { size = it.size }
    ) {
        val dayCal = day.getCalDay
        val p = max(min(dayCal/ maxCalorie.toFloat(), 1.3F),0.8F)
        val p1 = ((p-0.8F)*2.0F).pow(0.7F)
        val hsvRed = FloatArray(3)
        val hsvGreen = FloatArray(3)
        RGBToHSV(210, 20, 20, hsvRed)
        RGBToHSV(0, 200, 20, hsvGreen)
        val colorInt = Color.hsv(
            (hsvGreen[0] * (1 - p1) + p1 * hsvRed[0]),
            (hsvGreen[1] * (1 - p1) + p1 * hsvRed[1]),
            (hsvGreen[2] * (1 - p1) + p1.pow(0.8F) * hsvRed[2]),
        )
        Row() {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(if(foods.sumOf { it.calories } > 0)  0.dp else 25.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                colorInt.copy(alpha = 1.0F),
                                colorInt.copy(alpha = 0.9F),
                                colorInt.copy(alpha = 0.8F),
                                colorInt.copy(alpha = 0.5F)
                            )
                        )
                    )
                    .width(size.width.pxToDp() * min(1.0F, dayCal / maxCalorie.toFloat()))
                    .height(size.height.pxToDp())
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(0.dp,25.dp,25.dp))
                    .background(Brush.horizontalGradient(
                        listOf(Color(200, 200, 0), Color(200, 200, 0, alpha = 128))
                    ))
                    .padding(start =
                        size.width.pxToDp() * min(
                            1.0F,
                            foods.sumOf { it.calories } / maxCalorie.toFloat()
                        )
                    )
                    .height(size.height.pxToDp())
            )
        }

        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = day.titleForList,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        start = dimensionResource(id = R.dimen.horizontal_margin)
                    ),
                    textDecoration = null

                )
            }
            val addCal = foods.sumOf { it.calories }.let{if(it > 0 ) "+$it" else ""}
            Text("$dayCal$addCal/$maxCalorie kcal", modifier = Modifier.padding(start = 16.dp))
        }
        val oldScore = Food.getMeal(day.foods).getNutriScoreString()
        val newScore = Food.getMeal(day.foods + foods).getNutriScoreString()
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, end = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            Text(oldScore,
                style = if(foods.isEmpty()) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodySmall,
                modifier = if(foods.isEmpty()) Modifier else Modifier.padding(top = 2.dp)
            )
            if(foods.isNotEmpty()) {
                Icon(Icons.AutoMirrored.Filled.ArrowRight,"to")
                Text(newScore, style = MaterialTheme.typography.headlineSmall)
            }
            Text("/10", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

data class LayoutFoodParameter(
    val mode100g: Boolean = true
)

@Composable
private fun TextFieldFor100g(int : Int, setter :(Int) -> Unit, is100g : Boolean, quantity: Int, modifier: Modifier){
    var text by remember(is100g, quantity) { mutableStateOf((if(is100g) (int*100.0F/quantity.toFloat()).toInt() else int).toString()) }
    BasicTextField(
        value = text.run{if(this == "0") "" else this},
        modifier = modifier,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = { string:String -> if(string.length < 4){text = string; setter((text.toIntOrNull() ?: 0).run{if (is100g) (this*quantity/100.0F).toInt() else int})}})
}

@Composable
fun EditListFoodDay(
    foods: MutableList<Food>,
    layoutFoodParameters: MutableList<LayoutFoodParameter>,
    hour: Int = 0,
    minute: Int = 0,
    showTime: Boolean = false,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(6.dp)
        .border(1.dp, primaryDarkColor, shape = RoundedCornerShape(12.dp))) {
        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
            Column(modifier = Modifier.clip(RoundedCornerShape(12.dp, 0.dp,0.dp,12.dp)).background(Color(220,220,220))) {
                Text("name", modifier = Modifier.padding(6.dp))
                Text("mass (g)", modifier = Modifier.padding(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(35.dp).padding(6.dp)){Text("Per 100g ?")}
                Text("cal (kcal)", modifier = Modifier.padding(6.dp))
                Text("prot (g)", modifier = Modifier.padding(6.dp))
                Text("fat (g)", modifier = Modifier.padding(6.dp))
                Text("carb (g)", modifier = Modifier.padding(6.dp))
                Text("water %", modifier = Modifier.padding(6.dp))
                Text("score", modifier = Modifier.padding(6.dp))
                if(showTime)
                    Text("time", modifier = Modifier.padding(6.dp))
                IconButton(
                    onClick = { foods += Food("", 0,0,0, 0,0, hour, minute);  layoutFoodParameters += LayoutFoodParameter()},
                ) {
                    Icon(
                        Icons.Filled.Add,
                        "Delete",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            VerticalDivider(thickness = 2.dp, modifier = Modifier)
        }
        val modifierGrid = Modifier.sizeIn(maxWidth = 110.dp).padding(6.dp)

        LazyRow(modifier = Modifier.weight(1.0F)) {
            itemsIndexed(foods) { i, food ->
                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BasicTextField(value = food.name,                    modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), onValueChange = { string:String -> foods[i] = foods[i].copy(name = string) })
                        BasicTextField(value = food.quantity.run{if(this == 0) "" else this .toString()},     modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            onValueChange = { string:String ->
                                val newQuantity = string.toIntOrNull() ?: 0
                                if(layoutFoodParameters[i].mode100g && foods[i].quantity > 0){
                                    val coef = newQuantity / foods[i].quantity.toFloat()
                                    foods[i] = foods[i].copy(
                                        calories = (foods[i].calories * coef).toInt(),
                                        protein  = (foods[i].protein  * coef).toInt(),
                                        glucide  = (foods[i].glucide  * coef).toInt(),
                                        fat      = (foods[i].fat      * coef).toInt()
                                    )
                                }
                                foods[i] = foods[i].copy(quantity = newQuantity)})
                        Checkbox(layoutFoodParameters[i].mode100g, modifier = Modifier.size(35.dp).padding(6.dp),
                            onCheckedChange = {bool -> layoutFoodParameters[i] = layoutFoodParameters[i].copy(mode100g = bool)})
                        //BasicTextField(value = food.calories.run{if(this == 0) "" else this .toString()},     modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = { string:String -> foods[i] = foods[i].copy(calories = string.toIntOrNull() ?: 0)})
                        TextFieldFor100g(food.calories,{int -> foods[i] = foods[i].copy(calories = int)}, layoutFoodParameters[i].mode100g, food.quantity, modifierGrid )
                        TextFieldFor100g(food.protein ,{int -> foods[i] = foods[i].copy(protein  = int)}, layoutFoodParameters[i].mode100g, food.quantity, modifierGrid )
                        TextFieldFor100g(food.fat     ,{int -> foods[i] = foods[i].copy(fat      = int)}, layoutFoodParameters[i].mode100g, food.quantity, modifierGrid )
                        TextFieldFor100g(food.glucide ,{int -> foods[i] = foods[i].copy(glucide  = int)}, layoutFoodParameters[i].mode100g, food.quantity, modifierGrid )
                        //BasicTextField(value = food.protein.run{if(this == 0) "" else this .toString()},      modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = { string:String -> foods[i] = foods[i].copy(protein  = string.toIntOrNull() ?: 0)})
                        //BasicTextField(value = food.fat.run{if(this == 0) "" else this .toString()},          modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = { string:String -> foods[i] = foods[i].copy(fat      = string.toIntOrNull() ?: 0)})
                        //BasicTextField(value = food.glucide.run{if(this == 0) "" else this .toString()},      modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = { string:String -> foods[i] = foods[i].copy(glucide  = string.toIntOrNull() ?: 0)})
                        Text("%.0f".format(food.waterPercent), modifier = Modifier.padding(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(30.dp).padding(6.dp)){
                            Text(food.getNutriScoreString() + "/10", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                            Icon(if (!food.isPossible()) Icons.Filled.Error else Icons.Filled.Done, "Day done", tint = primaryDarkColor)
                        }
                        if(showTime)
                            TimeEdit(
                                textStyle = MaterialTheme.typography.bodySmall,
                                initMinute = food.minute,
                                initHour = food.hour,
                                setMinute = {int -> foods[i] = foods[i].copy(minute = int)},
                                setHour = {int -> foods[i] = foods[i].copy(hour = int)},
                                modifier = modifier.padding(6.dp)
                            )

                        IconButton(onClick = { foods.removeAt(i); layoutFoodParameters.removeAt(i)}) { Icon(Icons.Filled.Delete, "Delete", modifier = Modifier.size(20.dp)) }
                    }
                    VerticalDivider(thickness = 2.dp, modifier = Modifier)
                }
            }
            item{
                Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.Center) {
                    ElevatedButton(
                        onClick = { foods += Food("", 0,0,0, 0,0, hour, minute) ;  layoutFoodParameters += LayoutFoodParameter()},
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Add food") }
                }
            }
        }
        Row(modifier = Modifier.height(IntrinsicSize.Max).width(80.dp), horizontalArrangement = Arrangement.End) {
            VerticalDivider(thickness = 2.dp, modifier = Modifier)
            val meal = Food.getMeal(foods.toList())
            Column(modifier = Modifier.clip(RoundedCornerShape(0.dp, 12.dp,12.dp)).background(Color(220,220,220)), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", modifier = Modifier.padding(6.dp), color = Color(10,10,90))
                Text(meal.quantity.toString(), modifier = Modifier.padding(6.dp))
                Text("", modifier = Modifier.padding(6.dp))
                Text(meal.calories.toString(), modifier = Modifier.padding(6.dp))
                Text(meal.protein.toString(), modifier = Modifier.padding(6.dp))
                Text(meal.fat.toString(), modifier = Modifier.padding(6.dp))
                Text(meal.glucide.toString(), modifier = Modifier.padding(6.dp))
                Text("%.0f".format(meal.waterPercent), modifier = Modifier.padding(6.dp))
                Text(meal.getNutriScoreString() + "/10", modifier = Modifier.padding(6.dp))
                if(showTime)
                    Text("", modifier = Modifier.padding(6.dp))
                IconButton(
                    onClick = { foods.clear();  layoutFoodParameters.clear() },
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        "Delete",
                        modifier = Modifier.size(20.dp),
                        tint = Color(200, 100, 100)
                    )
                }
            }
        }
    }
}

@Composable
fun TimeEdit(
    initHour: Int,
    initMinute: Int,
    setHour:(Int) -> Unit,
    setMinute:(Int) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier){
    var hour by remember { mutableStateOf(initHour.toString().padStart(2, '0')) }
    var minute by remember { mutableStateOf(initMinute.toString().padStart(2, '0')) }
    Row(modifier) {
        BasicTextField(
            hour,
            { string ->
                if (string.length <= 2) {
                    hour = string; setHour(string.toIntOrNull() ?: 0)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(20.dp),
            textStyle = textStyle.copy(textAlign = TextAlign.Center),
        )
        Text(
            "h",
            style = textStyle.copy(textAlign = TextAlign.Center),
            modifier = Modifier.padding(end = 0.dp)
        )
        BasicTextField(
            minute,
            { string ->
                if (string.length <= 2) {
                    minute = string; setMinute(string.toIntOrNull() ?: 0)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(20.dp),
            textStyle = textStyle.copy(textAlign = TextAlign.Center),
        )
    }
}

@Composable
fun MultipleHeadRadioButton(iSelected: Int, setter: (Int) -> Unit, titles: List<String>, modifier: Modifier = Modifier) {
    val buttonSelected = ButtonDefaults.elevatedButtonColors().copy(containerColor = primaryDarkColor, contentColor = Color.White)
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center){
        titles.forEachIndexed({ i, title ->
            ElevatedButton(
                onClick = {setter(i)},
                modifier = Modifier.padding(horizontal = 10.dp),
                colors = if(iSelected != i) ButtonDefaults.elevatedButtonColors() else buttonSelected
            ) { Text(title) }
        })
    }
}

@Preview
@Composable
private fun MultipleHeadTitle(){
    Surface() {
        MultipleHeadRadioButton(0, {}, listOf("title 1", "title 2"))
    }
}

@Preview
@Composable
private fun EditListFoodPreview(){
    Surface() {
        EditListFoodDay(mutableListOf(Food("sucre", 100, 300, 1), Food("sucre", 100, 100, 1,fat = 99)),
            MutableList(2) { LayoutFoodParameter() })
    }
}

@Preview
@Composable
private fun EditListFoodWithTimePreview(){
    Surface() {
        EditListFoodDay(mutableListOf(Food("sucre", 100, 100, 100, hour = 1, minute = 1)),
            MutableList(2) { LayoutFoodParameter() },
            showTime = true)
    }
}


@Preview
@Composable
private fun DayItemProgressBarWithNewFoodPreview(){
    DayItemProgressBar(
        Day("01/02/25", id = 1, foods = listOf(Food("sucre",100,1000,1))),
        listOf(Food("sucre",100,1000,1)),
        {}
    )
}

@Preview
@Composable
private fun DayItemProgressBarPreview(){
    DayItemProgressBar(
        Day("01/02/25", id = 1, foods = listOf(Food("sucre",100,1000,100))),
        emptyList(),
        {}
    )
}

@Preview
@Composable
private fun CalculatorPreview() {
    Surface {
        CalculatorContent()
    }
}