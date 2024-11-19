package bkb.stundenplan.app.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import bkb.stundenplan.app.ViewModelStundenplanData
import bkb.stundenplan.app.ui.StundenplanPage.DialogStateEnum


@Composable
fun SelectionDialog(
    modifier: Modifier = Modifier,
    viewModel: ViewModelStundenplanData = viewModel(),
    dialogState: DialogStateEnum,
    ondialogStateChange: (DialogStateEnum) -> Unit
) {
    val searchFilter = rememberSaveable { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val orientationVertical by remember {
        mutableStateOf(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
    }

    val filteredElementMap: Map<Int, String>? = if (searchFilter.value.trim().isNotEmpty()) {
        viewModel.elementMap?.second?.filter { entry ->
            entry.value.replace(" ", "")
                .contains(searchFilter.value.replace(" ", ""), ignoreCase = true)
        }
    } else viewModel.elementMap?.second


    if (dialogState == DialogStateEnum.DATE || dialogState == DialogStateEnum.ELEMENT || dialogState == DialogStateEnum.TYPE) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { ondialogStateChange(DialogStateEnum.NONE) }
        ) {
            Column(
                Modifier
                    .padding(horizontal = if (orientationVertical) 10.dp else 80.dp)
                    .fillMaxSize()
                    .background(Color.White),
            ) {
                // This column will take all available space except for the bottom row
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = androidx.compose.ui.Alignment.Bottom
                    ) {
                        val columnMultiplier = if (orientationVertical) 1 else 3

                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight((1 / columnMultiplier.toFloat()))
                        ) {
                            SectionSelectionDialog(
                                modifier = Modifier,
                                map = viewModel.datesPairMap?.second,
                                rowsOfSections = 1,
                                onButtonClick = { viewModel.saveHandler.saveValueDate(it) }
                            )
                        }
                        Spacer(modifier = Modifier.padding(end = 10.dp))
                        if (viewModel.saveHandler.teacherMode && viewModel.saveHandler.valueLoginName.isNotEmpty() && viewModel.saveHandler.valuePassword.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight((1 / columnMultiplier.toFloat()))
                            ) {
                                SectionSelectionDialog(
                                    modifier = Modifier,
                                    map = viewModel.typesMap?.second,
                                    rowsOfSections = 1,
                                    onButtonClick = { viewModel.saveHandler.saveValueType(it) }
                                )
                            }
                            Spacer(modifier = Modifier.padding(end = 10.dp))
                        }
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .weight(1F)
                                .background(Color.Blue)
                        ) {
                            SectionSelectionDialog(
                                modifier = Modifier,
                                map = filteredElementMap,
                                rowsOfSections = columnMultiplier,
                                onButtonClick = { viewModel.saveHandler.saveValueElement(it) }
                            )
                        }
                    }
                }

                // Bottom row with TextField and Button
                if (orientationVertical) {
                    Row(modifier = Modifier.padding(top = 5.dp)) {
                        FindAndSave(
                            searchFilter, ondialogStateChange,
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

@Composable
private fun FindAndSave(
    searchFilter: MutableState<String>,
    ondialogStateChange: (DialogStateEnum) -> Unit,
    @SuppressLint("ModifierParameter") modifierSearchTextField: Modifier,
    modifierSaveButton: Modifier
) {
    TextField(
        modifier = modifierSearchTextField,
        value = searchFilter.value,
        onValueChange = { searchFilter.value = it },
        label = { Text("Element Suchen") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
        ),
        shape = RoundedCornerShape(80.dp)
    )

    Button(
        modifier = modifierSaveButton,
        onClick = { ondialogStateChange(DialogStateEnum.NONE) },
        contentPadding = PaddingValues(8.dp),
    ) {
        Text(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(80.dp)
                )
                .clip(RoundedCornerShape(80.dp)),
            textAlign = TextAlign.Center,
            text = "Speichern",
            color = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primary)
        )
    }
}


@Composable
inline fun <reified T : Any> SectionSelectionDialog(
    modifier: Modifier,
    map: Map<T, String>?,
    secondMap: Map<T, String>? = null,
    rowsOfSections: Int,
    crossinline onButtonClick: (T) -> Unit,
    fontSize: TextUnit = 16.sp
) {

    val newMap = secondMap?.let { secondMapIt ->
        map?.let { map ->
            secondMapIt + map
        }
    } ?: map


    newMap?.let { mapIt ->
        LazyVerticalGrid(
            verticalArrangement = Arrangement.Bottom,
            columns = GridCells.Fixed(rowsOfSections),
            modifier = modifier
        ) {
            items(mapIt.size) { mapCount ->
                Button(
                    onClick = { onButtonClick(mapIt.keys.elementAt(mapCount)) },
                    modifier = Modifier.padding(4.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = if ((secondMap?.containsValue(mapIt.values.elementAt(mapCount))) == true
                    ) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary) else ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.primary
                    )

                ) {
                    Text(
                        text = mapIt.values.elementAt(mapCount),
                        fontSize = fontSize
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
