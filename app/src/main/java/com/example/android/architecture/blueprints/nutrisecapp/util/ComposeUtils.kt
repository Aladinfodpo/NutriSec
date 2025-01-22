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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlin.math.min

val primaryDarkColor: Color = Color(0xFA264238)

/**
 * Display an initial empty state or swipe to refresh content.
 *
 * @param loading (state) when true, display a loading spinner over [content]
 * @param empty (state) when true, display [emptyContent]
 * @param emptyContent (slot) the content to display for the empty state
 * @param onRefresh (event) event to request refresh
 * @param modifier the modifier to apply to this layout.
 * @param content (slot) the main content to show
 */
@Composable
fun LoadingContent(
    loading: Boolean,
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = onRefresh,
            modifier = modifier,
            content = content,
        )
    }
}

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

@Preview
@Composable
private fun CalculatorPreview() {
    Surface {
        CalculatorContent()
    }
}