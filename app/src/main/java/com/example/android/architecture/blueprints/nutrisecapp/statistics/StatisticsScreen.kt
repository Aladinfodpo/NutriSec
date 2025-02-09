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

package com.example.android.architecture.blueprints.nutrisecapp.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android.architecture.blueprints.nutrisecapp.R
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.data.maxCalorie
import com.example.android.architecture.blueprints.nutrisecapp.util.StatisticsTopAppBar
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@Composable
fun StatisticsScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { StatisticsTopAppBar(openDrawer) },
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        StatisticsContent(
            loading = uiState.isLoading,
            empty = uiState.isEmpty,
            activeDaysPercent = uiState.activeDaysPercent,
            completedDaysPercent = uiState.completedDaysPercent,
            daysAll = uiState.days,
            onRefresh = { viewModel.refresh() },
            modifier = modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun StatisticsContent(
    loading: Boolean,
    empty: Boolean,
    activeDaysPercent: Float,
    completedDaysPercent: Float,
    daysAll : List<Day>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val commonModifier = modifier
        .fillMaxSize()

    if (empty || loading )
        Text(
            text = stringResource(id = R.string.statistics_no_days),
            modifier = commonModifier
        )
    else
        Column(
            commonModifier
                .fillMaxSize()
        ) {

                val textMeasurer = rememberTextMeasurer()

                var sliderPosition by remember { mutableFloatStateOf(daysAll.size.toFloat()-1) }
                var interpolationType by remember { mutableStateOf(false) }
                val days = daysAll.slice(max(daysAll.size-1-sliderPosition.toInt(),0)..<(daysAll.size-1))
                val weights = days.map { it.weight }

                val modPer = max(1.0f, (days.size / 7.0F)).toInt()

                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0F)
                    .padding(6.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {interpolationType = !interpolationType})
                    }
                ) {
                    val decalX = 95.0F
                    val widthBox = size.width - decalX
                    val heightBox = size.height *0.85F
                    val minPoids = (weights.minOrNull() ?: 50.0f).run{this - (100-(weights.maxOrNull() ?: 100.0f))}

                    val curve = BezierCubic.drawCurve(
                        days.mapIndexed { index, day -> Point(index.toFloat(), (1.0F - max(min(100.0F, day.weight), minPoids)/(100-minPoids)+minPoids/(100-minPoids))) },
                        offset = Point(decalX, 10.0F),
                        coef = Point(widthBox/days.size, (heightBox-10.0F)),
                        linear = interpolationType
                    )

                    days.forEachIndexed { index, d ->
                        if(index % modPer == 0) {
                            drawLine(color = Color(80,80,200),
                                Offset(x = widthBox / days.size * index + decalX, y = heightBox),
                                Offset(x = widthBox / days.size * index + decalX, y = 10.0F)
                            )

                            withTransform({
                                translate(
                                    left = widthBox / days.size * index + decalX + 25.0F,
                                    top = heightBox+10.0F
                                )
                                rotate(
                                    degrees = 90.0F, pivot = Offset.Zero
                                )
                            }) {
                                drawText(
                                    textMeasurer = textMeasurer, text = days[index].title,
                                    style = TextStyle(
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                    val drawWeight = {weight: Float ->
                        drawLine(color = Color(80,80,200),
                            Offset(x = decalX           , y = (1.0F - max(min(100.0F, weight), minPoids)/(100-minPoids)+minPoids/(100-minPoids)) * (heightBox-10.0F)+10.0F),
                            Offset(x = widthBox + decalX, y = (1.0F - max(min(100.0F, weight), minPoids)/(100-minPoids)+minPoids/(100-minPoids)) * (heightBox-10.0F)+10.0F)
                        )
                        drawText(textMeasurer = textMeasurer, text ="%.1f".format(weight), style = TextStyle(
                            color = Color.Black,
                            fontSize = 14.sp),
                            topLeft = Offset(
                                x = 0.0F,
                                y = (1.0F - max(min(100.0F, weight), minPoids)/(100-minPoids)+minPoids/(100-minPoids)) * (heightBox-10.0F) - 11.0F
                            )
                        )
                    }
                    drawWeight(weights.maxOrNull() ?: 100.0f)
                    drawWeight(weights.minOrNull() ?: minPoids)
                    drawWeight(minPoids)
                    if(weights.isNotEmpty())
                        drawWeight(weights.sum() / weights.size)
                    drawPath(path = curve, color =Color.Red, style = Stroke(6.0f))
                    drawRect(Color.Black, topLeft = Offset( x = decalX, y = 10.0F), size = Size(widthBox, heightBox-10.0F), style = Stroke(4.0f) )

                }
                Slider(
                    modifier = Modifier.padding(20.dp),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    steps = 0,
                    valueRange = 1f..max(1.0F, (daysAll.size-1).toFloat())
                )
                val calFor1Kg = 7.50F
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.Center) {
                    Column {
                        Text("Number of days selected :", modifier = Modifier.padding(horizontal = 6.dp))
                        Text("Calories lost (%.0f kcal/100g):".format(calFor1Kg*100), modifier = Modifier.padding(horizontal = 6.dp))
                        Text("Weight loss estimated :", modifier = Modifier.padding(horizontal = 6.dp))
                        Text("Actual weight loss :", modifier = Modifier.padding(horizontal = 6.dp))
                        Text("Estimated BMR :", modifier = Modifier.padding(horizontal = 6.dp))
                        Text("Cubic interpolation :", modifier = Modifier.padding(horizontal = 6.dp))
                    }
                    Column {
                        Text(days.size.toString(), modifier = Modifier.padding(horizontal = 6.dp))
                        val totalDiffCal = days.sumOf{it.getCalDay - maxCalorie}
                        Text("$totalDiffCal kcal", modifier = Modifier.padding(horizontal = 6.dp))
                        Text("%.0f g".format(totalDiffCal/calFor1Kg), modifier = Modifier.padding(horizontal = 6.dp))
                        val weightLost = (days.last().weight - days[0].weight) * 1000
                        Text("%.0f g".format(weightLost), modifier = Modifier.padding(horizontal = 6.dp))
                        Text("%.0f kcal".format(maxCalorie - weightLost * calFor1Kg / days.size.toFloat()), modifier = Modifier.padding(horizontal = 6.dp))
                        Checkbox(!interpolationType, onCheckedChange = {interpolationType = !interpolationType}, modifier = Modifier.size(18.dp).padding(horizontal = 30.dp) )
                    }
                }
        }

}

@Preview
@Composable
fun StatisticsContentPreview() {
    Surface {
        StatisticsContent(
            loading = false,
            empty = false,
            activeDaysPercent = 80f,
            completedDaysPercent = 20f,
            daysAll = listOf(
                Day(title = "12/12/24", id  = 0, weight = 100.0F, foods = listOf(Food("toto", 100, 38000, 50))),
                Day(title = "13/12/24", id  = 0, weight = 80.0F),
                Day(title = "14/12/24", id  = 0, weight = 80.0F),
                Day(title = "15/12/24", id  = 0, weight = 80.0F),
                Day(title = "16/12/24", id  = 0, weight = 75.0F),
                Day(title = "17/12/24", id  = 0, weight = 80.0F),
                Day(title = "18/12/24", id  = 0, weight = 78.0F),
                Day(title = "19/12/24", id  = 0, weight = 50.0F),
                Day(title = "20/12/24", id  = 0, weight = 60.0F),
                Day(title = "21/12/24", id  = 0, weight = 75.0F),
                Day(title = "22/12/24", id  = 0, weight = 80.0F),
                Day(title = "23/12/24", id  = 0, weight = 50.0F),
                Day(title = "24/12/24", id  = 0, weight = 50.0F),
                Day(title = "25/12/24", id  = 0, weight = 99.95F),
            ),
            onRefresh = { }
        )
    }
}

@Preview
@Composable
fun StatisticsContentLargePreview() {
    Surface {
        StatisticsContent(
            loading = false,
            empty = false,
            activeDaysPercent = 80f,
            completedDaysPercent = 20f,
            daysAll = List<Day>(100, {int -> Day(title ="$int/02/25", id = int.toLong(), weight =  Random.nextInt(80, 100).toFloat() )}),
            onRefresh = { }
        )
    }
}

@Preview
@Composable
fun EmptyStatisticsContentPreview() {
    Surface {
        StatisticsContent(
            loading = false,
            empty = true,
            activeDaysPercent = 80f,
            completedDaysPercent = 20f,
            daysAll = emptyList(),
            onRefresh = { }
        )
    }
}

