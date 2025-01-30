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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

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
        //unfocusedContainerColor = Color(red = 200, green = 200, blue = 200, alpha = 50)
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
    }
}


@Preview
@Composable
private fun DayItemProgressBarWithNewFoodPreview(){
    DayItemProgressBar(
        Day("01/02/25", id = 1, foods = listOf(Food("sucre",1500,1000,100))),
        listOf(Food("sucre",100,1200,100)),
        {}
    )
}

@Preview
@Composable
private fun DayItemProgressBarPreview(){
    DayItemProgressBar(
        Day("01/02/25", id = 1, foods = listOf(Food("sucre",1500,1000,100))),
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