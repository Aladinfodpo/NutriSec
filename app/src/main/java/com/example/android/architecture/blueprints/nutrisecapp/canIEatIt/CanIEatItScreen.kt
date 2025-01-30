package com.example.android.architecture.blueprints.nutrisecapp.canIEatIt

import java.util.Calendar
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android.architecture.blueprints.nutrisecapp.addeditday.AddEditDayViewModel
import com.example.android.architecture.blueprints.nutrisecapp.data.Day
import com.example.android.architecture.blueprints.nutrisecapp.data.Food
import com.example.android.architecture.blueprints.nutrisecapp.util.AddEditDayTopAppBar
import com.example.android.architecture.blueprints.nutrisecapp.util.CanIEatItTopAppBar
import com.example.android.architecture.blueprints.nutrisecapp.util.DayItemProgressBar
import com.example.android.architecture.blueprints.nutrisecapp.util.TodayNotStarted
import com.example.android.architecture.blueprints.nutrisecapp.util.primaryDarkColor
import kotlin.math.abs
import kotlin.math.round

@Composable
fun CanIEatItScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CanIEatItViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { CanIEatItTopAppBar { openDrawer() } },
        floatingActionButton = { }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        CanIEatItContent(
            loading = uiState.isLoading,
            day = uiState.day,
            onCreateToday = viewModel::createNewDay,
            onAddFoodToday = viewModel::eatFood,
            modifier = Modifier.padding(paddingValues)
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { userMessage ->
            val snackbarText = stringResource(userMessage)
            LaunchedEffect(snackbarHostState, viewModel, userMessage, snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }
        }
    }
}

@Composable
private fun CanIEatItContent(
    loading: Boolean,
    day: Day?,
    onCreateToday: () -> Unit,
    onAddFoodToday: (List<Food>) -> Unit,
    modifier: Modifier = Modifier
) {
    if(loading)
        return

    if(day == null) {
        TodayNotStarted(modifier, onCreateToday)
    }else {
        var hour  by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
        var minute  by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
        val foods = remember { mutableStateListOf(Food("", 100,0,0, 0,0, hour, minute)) }
        Column(modifier = modifier
            .imePadding()
            .fillMaxSize(), verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)) {
            DayItemProgressBar(day, foods, {})
            Row(modifier = Modifier
                .padding(6.dp)
                .border(1.dp, primaryDarkColor, shape = RoundedCornerShape(12.dp))) {
                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    Column(modifier = Modifier) {
                        Text("name", modifier = Modifier.padding(6.dp))
                        Text("mass (g)", modifier = Modifier.padding(6.dp))
                        Text("cal (kcal)", modifier = Modifier.padding(6.dp))
                        Text("prot (g)", modifier = Modifier.padding(6.dp))
                        Text("fat (g)", modifier = Modifier.padding(6.dp))
                        Text("carb (g)", modifier = Modifier.padding(6.dp))
                        Text("water %", modifier = Modifier.padding(6.dp))
                        Text("score", modifier = Modifier.padding(6.dp))
                        IconButton(
                            onClick = { foods.clear() },
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                "Delete",
                                modifier = Modifier.size(20.dp),
                                tint = Color(200, 100, 100)
                            )
                        }
                    }
                    VerticalDivider(thickness = 2.dp, modifier = Modifier)
                }
                val modifierGrid = Modifier.sizeIn(maxWidth = 110.dp).padding(6.dp)

                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(foods) { i, food ->
                        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                BasicTextField(value = food.name,                    modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), onValueChange = {string:String -> foods[i] = foods[i].copy(name = string) })
                                BasicTextField(value = food.quantity.toString(),     modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = {string:String -> foods[i] = foods[i].copy(quantity = string.toIntOrNull() ?: 0)})
                                BasicTextField(value = food.calories.toString(),     modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = {string:String -> foods[i] = foods[i].copy(calories = string.toIntOrNull() ?: 0)})
                                BasicTextField(value = food.protein.toString(),      modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = {string:String -> foods[i] = foods[i].copy(protein  = string.toIntOrNull() ?: 0)})
                                BasicTextField(value = food.fat.toString(),          modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = {string:String -> foods[i] = foods[i].copy(fat      = string.toIntOrNull() ?: 0)})
                                BasicTextField(value = food.glucide.toString(),      modifier = modifierGrid, singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), onValueChange = {string:String -> foods[i] = foods[i].copy(glucide  = string.toIntOrNull() ?: 0)})
                                Text(food.waterPercent.toString(), modifier = Modifier.padding(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically){
                                    Text((if(abs(round(food.nutriScore)-food.nutriScore) < 0.1F) food.nutriScore.toInt().toString() else "%.1f".format(food.nutriScore)) + "/10",     modifier = modifierGrid, style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                                    Icon(if (!food.isPossible()) Icons.Filled.Error else Icons.Filled.Done, "Day done", tint = if (day.isBad) Color.Red else primaryDarkColor)
                                }

                                IconButton(onClick = { foods.removeAt(i)}) { Icon(Icons.Filled.Delete, "Delete", modifier = Modifier.size(20.dp)) }
                            }
                            VerticalDivider(thickness = 2.dp, modifier = Modifier)
                        }
                    }
                    item{
                        Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.Center) {
                            ElevatedButton(
                                onClick = { foods += Food("", 0,0,0, 0,0, hour, minute) },
                                modifier = modifier.fillMaxWidth()
                            ) { Text("Add food") }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Can I eat this meal at",style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center), modifier = Modifier.padding(6.dp))
                BasicTextField(
                    hour.toString().padStart(2, '0'),
                    {string -> hour = string.toIntOrNull() ?: 0},
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(20.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    )
                Text("h",style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center), modifier = Modifier.padding(end = 2.dp))
                BasicTextField(
                    minute.toString().padStart(2, '0'),
                    {string -> minute = string.toIntOrNull() ?: 0},
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(20.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    )
                Text("?",style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                val meal = Food("meal", foods.sumOf { it.quantity }, foods.sumOf { it.calories }, foods.sumOf { it.protein }, foods.sumOf { it.fat }, foods.sumOf { it.glucide }, hour, minute)
                ElevatedButton(
                    onClick = {onAddFoodToday(foods.toList()); foods.clear()},
                    modifier = modifier
                ) { Text(if(meal.nutriScore >= 6.0F) "Let's eat !" else "Hmmm, maybe not ?") }
            }
        }
    }
}



@Preview
@Composable
private fun CanIEatItScreenPreview() {
    Surface {
        CanIEatItContent(
            loading = false,
            day = Day(id = 1),
            onCreateToday = {},
            onAddFoodToday = {},
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun CanIEatItNoTodayScreenPreview() {
    Surface {
        CanIEatItContent(
            loading = false,
            day = null,
            onCreateToday = {},
            onAddFoodToday = {},
            modifier = Modifier
        )
    }
}

