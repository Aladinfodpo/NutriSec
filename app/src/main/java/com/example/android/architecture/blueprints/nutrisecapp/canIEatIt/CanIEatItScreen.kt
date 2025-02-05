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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
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
import com.example.android.architecture.blueprints.nutrisecapp.util.CalculatorContent
import com.example.android.architecture.blueprints.nutrisecapp.util.CanIEatItTopAppBar
import com.example.android.architecture.blueprints.nutrisecapp.util.DayItemProgressBar
import com.example.android.architecture.blueprints.nutrisecapp.util.EditListFoodDay
import com.example.android.architecture.blueprints.nutrisecapp.util.LayoutFoodParameter
import com.example.android.architecture.blueprints.nutrisecapp.util.MultipleHeadRadioButton
import com.example.android.architecture.blueprints.nutrisecapp.util.TimeEdit
import com.example.android.architecture.blueprints.nutrisecapp.util.TodayNotStarted
import com.example.android.architecture.blueprints.nutrisecapp.util.primaryDarkColor
import kotlin.math.abs
import kotlin.math.round

@Composable
fun CanIEatItScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    onDayProgressBarClick: (Day) -> Unit,
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
            onUnEatMealToday = viewModel::unEatLastMeal,
            onAddCardioToday = viewModel::addCardio,
            onDayProgressBarClick = onDayProgressBarClick,
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
    onUnEatMealToday: () -> List<Food>,
    onAddCardioToday: (Int) -> Unit,
    onDayProgressBarClick : (Day) -> Unit,
    foodsInit: List<Food> = emptyList(),
    modifier: Modifier = Modifier
) {
    if(loading)
        return

    if(day == null) {
        TodayNotStarted(modifier, onCreateToday)
    }else {
        var hour  by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
        var minute  by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
        val foods = remember { mutableStateListOf(*foodsInit.toTypedArray()) }
        val layoutFoodParameters = remember { mutableStateListOf(*MutableList(foods.size, init = {_ -> LayoutFoodParameter() }).toTypedArray()) }
        Column (modifier = modifier.fillMaxSize(),){
            var indexMenu by remember { mutableIntStateOf(0) }
            MultipleHeadRadioButton(
                indexMenu,
                { int -> indexMenu = int },
                listOf("Food", "Cardio"),
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .imePadding()
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (indexMenu == 0) {
                    DayItemProgressBar(day, foods, onDayProgressBarClick)


                    EditListFoodDay(foods, layoutFoodParameters, hour, minute)
                    IconButton(
                        onClick = {
                            val newFoods = onUnEatMealToday()
                            layoutFoodParameters += List(newFoods.size, {LayoutFoodParameter()})
                            foods += newFoods
                        },
                        modifier = Modifier.width(180.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Reload last meal", modifier = Modifier.padding(6.dp))
                            Icon(
                                Icons.Filled.Refresh,
                                "Delete",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Can I eat this meal at",
                            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                            modifier = Modifier.padding(6.dp)
                        )
                        TimeEdit(
                            textStyle = MaterialTheme.typography.bodyLarge,
                            initMinute = minute,
                            initHour = hour,
                            setMinute = { int -> minute = int; foods.forEach { it.minute = int } },
                            setHour = { int -> hour = int; foods.forEach { it.hour = int } },
                            modifier = Modifier
                        )
                        Text(
                            "?",
                            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val isMealBad = Food.getMeal(foods).nutriScore < 6.0F
                        var isAccepted by remember { mutableStateOf(false) }
                        if (isAccepted && !isMealBad)
                            isAccepted = false

                        if (isMealBad && !isAccepted)
                            ElevatedButton(
                                onClick = { isAccepted = true },
                                modifier = Modifier
                            ) { Text("I'm sure") }
                        ElevatedButton(
                            enabled = !isMealBad || isAccepted,
                            onClick = {
                                onAddFoodToday(foods.toList()); foods.clear(); isAccepted = false
                            },
                            modifier = Modifier
                        ) { Text(if (!isMealBad || isAccepted) "Let's eat !" else "Hmmm, maybe not ?") }

                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CalculatorContent()
                    }
                }
                if(indexMenu == 1){
                    AddCardio(onAddCardioToday, day.calCardio)
                }
            }
        }
    }
}

@Composable
private fun AddCardio( setter: (Int) -> Unit, cardioToday: Int = 0){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var cardio by remember { mutableIntStateOf(0) }
        val numberEditableColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryDarkColor,
            unfocusedBorderColor = primaryDarkColor,
            cursorColor = Color.Black
        )
        Text("Already $cardioToday kcal burned today, let's add :")
        OutlinedTextField(
            value = cardio.let { if (it == 0) "" else it.toString() },
            onValueChange = {string:String -> cardio = string.toInt()},
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
            colors = numberEditableColors,
            maxLines = 1,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            suffix = {Text("kcal")}
        )
        ElevatedButton(
            onClick = {setter(cardio)},
            modifier = Modifier
        ) { Text("Good job !") }
    }
}

@Preview
@Composable
private fun AddCardioPreview(){
    Surface {
        AddCardio({}, 100)
    }
}

@Preview
@Composable
private fun CanIEatItScreenEmptyPreview() {
    Surface {
        CanIEatItContent(
            loading = false,
            day = Day(id = 1),
            onCreateToday = {},
            onAddFoodToday = {},
            onAddCardioToday = {},
            onDayProgressBarClick = {},
            onUnEatMealToday = { -> emptyList()},
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun CanIEatItScreenPreview() {
    Surface {
        CanIEatItContent(
            loading = false,
            day = Day(id = 1, foods = listOf(Food("sucre", 10,1200,1))),
            onCreateToday = {},
            onAddFoodToday = {},
            onAddCardioToday = {},
            onDayProgressBarClick = {},
            foodsInit = listOf(Food("sucre",100,200,20,4,20)),
            onUnEatMealToday = { -> emptyList()},
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun CanIEatItScreenTooMuchPreview() {
    Surface {
        CanIEatItContent(
            loading = false,
            day = Day(id = 1),
            onCreateToday = {},
            onAddFoodToday = {},
            onAddCardioToday = {},
            onDayProgressBarClick = {},
            foodsInit = listOf(Food("sucre",100,2000,20,4,20)),
            onUnEatMealToday = { -> emptyList()},
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
            onAddCardioToday = {},
            onDayProgressBarClick = {},
            onUnEatMealToday = { -> emptyList()},
            modifier = Modifier
        )
    }
}

