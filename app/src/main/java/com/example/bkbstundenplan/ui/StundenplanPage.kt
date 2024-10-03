package com.example.bkbstundenplan.ui

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bkbstundenplan.HTMLStrings
import com.example.bkbstundenplan.R
import com.example.bkbstundenplan.ViewModelStundenplanData
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object StundenplanPage {
    enum class DialogStateEnum {
        NONE, DATE, CLASS;
    }


    @SuppressLint("AuthLeak")
    @Composable
    fun MainPage(
        modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData
    ) {
        var dialogState by rememberSaveable { mutableStateOf(DialogStateEnum.NONE) }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Selection(
                modifier = modifier,
                viewModel = viewModel,
                onStateSelectedChange = { newState ->
                    dialogState = newState
                })
            Row {
                Text(text = stringResource(R.string.datum, viewModel.saveHandler.valueDates))
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = stringResource(R.string.klasse, viewModel.saveHandler.valueClasses))

            }
            Surface {
                if (!LocalInspectionMode.current) //returns false if preview
                {
                    if (viewModel.saveHandler.valueDates != 0 && viewModel.saveHandler.valueClasses != 0) {
                        StundenplanWebview(
                            viewModel = viewModel,
                        )
                    }
                } else {
                    Text(
                        modifier = modifier, text = "WebView not available in preview"
                    )
                }
                SelectionDialog(dialogState = dialogState, ondialogStateChange = { newState ->
                    dialogState = newState
                })
            }
        }
    }


    @Composable
    fun SelectionDialog(
        viewModel: ViewModelStundenplanData = viewModel(),
        dialogState: DialogStateEnum,
        ondialogStateChange: (DialogStateEnum) -> Unit
    ) {
        if (dialogState == DialogStateEnum.DATE || dialogState == DialogStateEnum.CLASS) {
            Dialog(onDismissRequest = { ondialogStateChange(DialogStateEnum.NONE) }, content = {
                @Suppress("KotlinConstantConditions") if (dialogState == DialogStateEnum.DATE) {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (viewModel.datesMap != null) {
                            if (viewModel.saveHandler.alteStundenplaene) {

                                fun weeksAgo(weeks: Int): String {
                                    var date: String? = null
                                    try {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            val formatter = DateTimeFormatter.ofPattern("d.M.yyyy")
                                            date = LocalDate.parse(
                                                viewModel.datesMap!!.values.first(), formatter
                                            ).minusWeeks(weeks.toLong()).format(formatter)
                                        }
                                    } catch (_: Exception) {
                                    }

                                    return date ?: "Vor $weeks ${
                                        when (weeks) {
                                            1 -> "Woche"
                                            else -> "Wochen"
                                        }
                                    }"
                                }

                                val weeksBack = 8

                                val dateValue = (viewModel.datesMap!!.keys.first() - weeksBack)
                                @Suppress("ReplaceRangeToWithRangeUntil") for (iter in 0..(weeksBack - 1)) {
                                    item {
                                        Button(colors = if ((dateValue + iter) == viewModel.saveHandler.valueDates) ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                        else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                                            onClick = {
                                                viewModel.saveHandler.valueDates =
                                                    (dateValue + iter)
                                                viewModel.updateURLStundenplan()
                                                viewModel.updateTablesScraped()
                                                ondialogStateChange(DialogStateEnum.NONE)
                                            }) {

                                            Text(
                                                text = weeksAgo((weeksBack - iter))
                                            )
                                        }
                                    }
                                }
                            }

                            viewModel.datesMap!!.forEach() {
                                item {
                                    Button(colors = if (it.key == viewModel.saveHandler.valueDates) ButtonDefaults.buttonColors(
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                    else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                        onClick = {
                                            viewModel.saveHandler.valueDates = it.key
                                            viewModel.updateURLStundenplan()
                                            viewModel.updateTablesScraped()
                                            ondialogStateChange(DialogStateEnum.NONE)
                                        }) {
                                        Text(text = viewModel.datesMap!!.getValue(it.key))
                                    }
                                }
                            }
                        } else {
                            item { Text(text = "keine Daten vorhanden") }
                        }
                    }
                } else if (dialogState == DialogStateEnum.CLASS) {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (viewModel.classMap != null) {
                            viewModel.classMap!!.forEach() {
                                item {
                                    Button(colors = if (it.key == viewModel.saveHandler.valueClasses) ButtonDefaults.buttonColors(
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                    else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                        onClick = {
                                            viewModel.saveHandler.saveValueClasses(it.key)
                                            viewModel.updateURLStundenplan()
                                            viewModel.updateTablesScraped()
                                            ondialogStateChange(DialogStateEnum.NONE)
                                        }) {
                                        Text(text = viewModel.classMap!!.getValue(it.key))
                                    }
                                }
                            }
                        } else {
                            item { Text(text = "keine Daten vorhanden") }
                        }
                    }
                }
            })
        }
    }


    @Composable
    fun Selection(
        modifier: Modifier = Modifier,
        viewModel: ViewModelStundenplanData,
        onStateSelectedChange: (DialogStateEnum) -> Unit
    ) {
        Row {
            Button(onClick = { onStateSelectedChange(DialogStateEnum.DATE) }) {
                Text(text = stringResource(R.string.datum_auswaehlen))
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = { onStateSelectedChange(DialogStateEnum.CLASS) }) {
                Text(text = stringResource(R.string.klasse_auswaehlen))
            }
        }
    }


    @SuppressLint("AuthLeak")
    @Composable
    fun StundenplanWebview(
        modifier: Modifier = Modifier,
        viewModel: ViewModelStundenplanData,
    ) {
        if (viewModel.saveHandler.experimentellerStundenplan) {
            if (viewModel.tablesScraped.value != null) {
                Column {
                    TableWebView(
                        viewModel = viewModel,
                        htmlString = HTMLStrings.styleExperimentellerStundenplan(viewModel.saveHandler.darkmode) + (viewModel.tablesScraped.value.toString())
                    )
                }
            }
        } else if (!viewModel.saveHandler.experimentellerStundenplan) {
            AndroidView(modifier = modifier.fillMaxSize(), factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }, update = {
                it.loadUrl(viewModel.urlStundenplan.value)
                it.getSettings().loadWithOverviewMode = true
                it.getSettings().useWideViewPort = true
            })
        }
    }
}

@Composable
fun TableWebView(
    modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData, htmlString: String
) {
    AndroidView(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 2.dp), factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }, update = {
        runBlocking { viewModel.saveHandler.saveHandlerInitJob.join() }
        it.loadDataWithBaseURL(
            null, // Base URL (can be null)
            htmlString, "text/html", "UTF-8", null // History URL (can be null)
        )

        it.getSettings().loadWithOverviewMode = true
        it.getSettings().useWideViewPort = true
    })

}


@Preview(showBackground = true, apiLevel = 31, device = "id:pixel_8")
@Composable
fun StundenplanAppPreview() {
    val appViewModel: ViewModelStundenplanData = viewModel()
    StundenplanPage.MainPage(viewModel = appViewModel)
}