package bkb.stundenplan.app.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime
import bkb.stundenplan.app.ViewModelStundenplanData
import bkb.stundenplan.app.ui.StundenplanPage.DialogStateEnum
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun SelectionDialog(
    modifier: Modifier = Modifier,
    viewModel: ViewModelStundenplanData = viewModel(),
    dialogState: DialogStateEnum,
    ondialogStateChange: (DialogStateEnum) -> Unit
) {
    val searchFilter = rememberSaveable { mutableStateOf("") }


    val horizontalPaddingRowsSpacer = if (viewModel.isPortrait) 10.dp else 3.dp

    var currentElementMap = ParameterWhichMayChangeOverTime.selectType(viewModel.saveHandler.valueType, viewModel.TypesMapsObject)?: viewModel.elementMap?.second

    val filteredElementMap: Map<Int, String>? = if (searchFilter.value.trim().isNotEmpty()) {
        currentElementMap?.filter { entry ->
            entry.value.replace(" ", "")
                .contains(searchFilter.value.replace(" ", ""), ignoreCase = true)
        }
    } else currentElementMap


    if (dialogState == DialogStateEnum.DATE || dialogState == DialogStateEnum.ELEMENT || dialogState == DialogStateEnum.TYPE) {
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { ondialogStateChange(DialogStateEnum.NONE) }) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .padding(horizontal = if (viewModel.isPortrait) 30.dp else 80.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxSize(),
            ) {
                // This column will take all available space except for the bottom row
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = androidx.compose.ui.Alignment.Bottom
                    ) {
                        val columnMultiplier = if (viewModel.isPortrait) 1 else 3

                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight((1 / columnMultiplier.toFloat()))
                        ) {
                            val weeksBackMap =
                                if (viewModel.saveHandler.alteStundenplaene) weeksAgo(viewModel.datesPairMap?.second) else null
                            SectionSelectionDialog(
                                modifier = Modifier,
                                map = viewModel.datesPairMap?.second,
                                secondMap = weeksBackMap,
                                rowsOfSections = 1,
                                onButtonClick = {
                                    viewModel.saveHandler.saveValueDate(it)
                                    viewModel.urlMaker.updateURL()
                                    viewModel.updateTablesScraped()
                                },
                                swapOrderBeforeDisplay = true,
                                currentValue = viewModel.saveHandler.valueDate
                            )
                        }
                        Spacer(modifier = Modifier.padding(end = horizontalPaddingRowsSpacer))
                        if (viewModel.saveHandler.teacherMode && viewModel.saveHandler.valueLoginName.isNotEmpty() && viewModel.saveHandler.valuePassword.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight((1 / columnMultiplier.toFloat()))
                            ) {
                                SectionSelectionDialog(
                                    modifier = Modifier,
                                    map = viewModel.typesMap?.second,
                                    rowsOfSections = 1,
                                    onButtonClick = {
                                        viewModel.saveHandler.saveValueType(it)
                                        viewModel.urlMaker.updateURL()
                                        viewModel.updateTablesScraped()
                                        runBlocking {viewModel.updateTypesMapsObject()}

                                    },
                                    currentValue = viewModel.saveHandler.valueType
                                )
                            }
                            Spacer(modifier = Modifier.padding(end = horizontalPaddingRowsSpacer))
                        }
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .weight(1F)
                        ) {




                            SectionSelectionDialog(modifier = Modifier.weight(1F),
                                map = filteredElementMap,
                                rowsOfSections = columnMultiplier,
                                onButtonClick = {
                                    viewModel.saveHandler.saveValueElement(it)
                                    viewModel.urlMaker.updateURL()
                                    viewModel.updateTablesScraped()
                                },
                                currentValue = viewModel.saveHandler.valueElement
                            )

                            if (!viewModel.isPortrait) {
                                Row(horizontalArrangement = Arrangement.Center) {
                                    FindAndSave(
                                        searchFilter,
                                        ondialogStateChange,
                                        modifierSearchTextField = Modifier
                                            .height(60.dp)
                                            .weight(0.65F),
                                        modifierSaveButton = Modifier
                                            .height(60.dp)
                                            .weight(0.35F)

                                    )
                                }
                            }
                        }


                    }
                }


                // Bottom row with TextField and Button
                if (viewModel.isPortrait) {
                    Row(modifier = Modifier.padding(top = 5.dp)) {
                        FindAndSave(
                            searchFilter,
                            ondialogStateChange,
                            modifierSearchTextField = Modifier
                                .height(60.dp)
                                .weight(0.65F),
                            modifierSaveButton = Modifier
                                .height(60.dp)
                                .weight(0.35F)
                        )
                    }
                }
            }
        }
    }
}


fun weeksAgo(
    datesMap: Map<Int, String>?,
    weeksAgo: Int = 8
): Map<Int, String> {
    val firstValue = datesMap?.keys?.first()

    val newMap = mutableMapOf<Int, String>()

    for (weekCounter in weeksAgo downTo 1) {
        val entryValue = firstValue?.minus(weekCounter)
        val dateString = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("d.M.yyyy")
                LocalDate.parse(datesMap?.values?.first(), formatter)
                    .minusWeeks(weekCounter.toLong())
                    .format(formatter)
            }
            else {
                null
            }
        }
        catch (_: Exception) {
            null
        }

        val formattedDate = dateString ?: "Vor $weekCounter ${
            when (weekCounter) {
                1 -> "Woche"
                else -> "Wochen"
            }
        }"

        newMap.let { newMapIt ->
            entryValue?.let { entryValueIt ->
                newMapIt.put(entryValueIt, formattedDate)
            }
        }

    }

    return newMap
}


@Composable
private fun FindAndSave(
    searchFilter: MutableState<String>,
    ondialogStateChange: (DialogStateEnum) -> Unit,
    @SuppressLint("ModifierParameter") modifierSearchTextField: Modifier,
    modifierSaveButton: Modifier
) {

    TextField(
        modifier = modifierSearchTextField.border(
            width = 0.3.dp,
            color = Color.Gray,
            shape = RoundedCornerShape(80.dp)
        ),
        value = searchFilter.value,
        onValueChange = { searchFilter.value = it },
        label = { Text("Element Suchen") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ),
        shape = RoundedCornerShape(80.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent, // No underline when unfocused
            focusedIndicatorColor = Color.Transparent    // No underline when focused
        )
    )

    Button(
        modifier = modifierSaveButton,
        onClick = { ondialogStateChange(DialogStateEnum.NONE) },
        contentPadding = PaddingValues(8.dp),
    ) {
        Text(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(80.dp)
                )
                .clip(RoundedCornerShape(80.dp)),
            textAlign = TextAlign.Center,
            text = "Speichern",
            color = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primary)
        )
    }
}

fun <K, V> Map<K, V>.reverse(): Map<K, V> {
    return this.entries.reversed().associate { it.toPair() }
}

@Composable
inline fun <reified T : Any> SectionSelectionDialog(
    modifier: Modifier,
    map: Map<T, String>?,
    secondMap: Map<T, String>? = null,
    rowsOfSections: Int,
    crossinline onButtonClick: (T) -> Unit,
    fontSize: TextUnit = 16.sp,
    reverseLayout: Boolean = true,
    swapOrderBeforeDisplay: Boolean = false,
    currentValue: T? = null
) {

    var newMap = secondMap?.let { secondMapIt ->
        map?.let { map ->
            secondMapIt + map
        }
    } ?: map

    if (swapOrderBeforeDisplay) newMap = newMap?.reverse()

    newMap?.let { mapIt ->
        LazyVerticalGrid(
            verticalArrangement = Arrangement.Bottom,
            columns = GridCells.Fixed(rowsOfSections),
            modifier = modifier,
            reverseLayout = reverseLayout
        ) {
            items(mapIt.size) { mapCount ->
                Button(
                    onClick = { onButtonClick(mapIt.keys.elementAt(mapCount)) },
                    modifier = Modifier.padding(4.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = if (currentValue == mapIt.keys.elementAt(mapCount)) {
                        ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.secondary
                        )
                    }
                    else if ((secondMap?.containsValue(mapIt.values.elementAt(mapCount))) == true) ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.tertiary
                    )
                    else ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.primary
                    )

                ) {
                    Text(
                        text = mapIt.values.elementAt(mapCount), fontSize = fontSize
                    )
                }
            }
        }
    } ?: run { Text(text = "keine Daten vorhanden") }

}


//                                    Button(onClick = {}
//
//                                    ) {
//                                        Text(text = "mapC: $mapCount")
//
//                                    }
