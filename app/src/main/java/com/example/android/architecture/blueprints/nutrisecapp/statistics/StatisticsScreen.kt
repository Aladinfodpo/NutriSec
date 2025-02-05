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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
            weightsAll = uiState.weights,
            datesAll = uiState.dates,
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
    weightsAll: List<Float>,
    datesAll: List<String>,
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
                val courbe = Path()


                var sliderPosition by remember { mutableFloatStateOf(weightsAll.size.toFloat()) }
                val weights = weightsAll.slice(max(weightsAll.size-1-sliderPosition.toInt(),0)..<weightsAll.size)
                val dates   =   datesAll.slice(max(weightsAll.size-1-sliderPosition.toInt(),0)..<weightsAll.size)
                val modPer = max(1.0f, (dates.size / 7.0F)).toInt()

                Canvas(modifier = Modifier.fillMaxWidth().weight(1.0F).padding(6.dp)) {
                    val decalX = 95.0F
                    val widthBox = size.width - decalX
                    val heightBox = size.height *0.85F
                    val minPoids = (weights.minOrNull() ?: 50.0f).run{this - (100-(weights.maxOrNull() ?: 100.0f))}

                    weights.forEachIndexed { index, w ->
                        if(index != 0)
                            courbe.lineTo(x = widthBox / dates.size * index + decalX, y = (1.0F - max(min(100.0F, w), minPoids)/(100-minPoids)+minPoids/(100-minPoids)) * (heightBox-10.0F)+10.0F)
                        courbe.moveTo(    x = widthBox / dates.size * index + decalX, y = (1.0F - max(min(100.0F, w), minPoids)/(100-minPoids)+minPoids/(100-minPoids)) * (heightBox-10.0F)+10.0F)

                        if(index % modPer == dates.size % modPer) {
                            drawLine(color = Color(80,80,200),
                                Offset(x = widthBox / dates.size * index + decalX, y = heightBox),
                                Offset(x = widthBox / dates.size * index + decalX, y = 10.0F)
                            )

                            withTransform({
                                translate(
                                    left = widthBox / dates.size * index + decalX + 25.0F,
                                    top = heightBox+10.0F
                                )
                                rotate(
                                    degrees = 90.0F, pivot = Offset.Zero
                                )
                            }) {
                                drawText(
                                    textMeasurer = textMeasurer, text = dates[index],
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
                    drawPath(path = courbe,color =Color.Red, style = Stroke(6.0f))
                    drawRect(Color.Black, topLeft = Offset( x = decalX, y = 10.0F), size = Size(widthBox, heightBox-10.0F), style = Stroke(4.0f) )

                }
                Slider(
                    modifier = Modifier.padding(20.dp),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    steps = 0,
                    valueRange = 1f..max(1.0F, weightsAll.size.toFloat())
                )

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
            datesAll = listOf("12/12/24", "13/12/24", "14/12/24", "15/12/24", "16/12/24", "17/12/24", "18/12/24", "19/12/24", "20/12/24", "21/12/24", "22/12/24", "23/12/24", "24/12/24", "25/12/24", "26/12/24"),
            weightsAll = listOf(100.0F, 80.0F,80.0F,80.0F,75.0F,80.0F,75.0F,75.0F,75.0F,75.0F,75.0F,52.0F,51.0F,55.0F),
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
            datesAll = List<String>(100, {int -> "$int/02/25"}),
            weightsAll = List<Float>(100,  {Random.nextInt(80, 100).toFloat()}),
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
            datesAll = listOf("12/12/2024", "13/12/2024"),
            weightsAll = listOf(90.0F, 80.0F),
            onRefresh = { }
        )
    }
}

