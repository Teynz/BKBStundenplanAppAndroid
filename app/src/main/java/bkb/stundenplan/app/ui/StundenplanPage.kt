package bkb.stundenplan.app.ui

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import bkb.stundenplan.app.HTMLStrings
import bkb.stundenplan.app.R
import bkb.stundenplan.app.ViewModelStundenplanData
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.datum, viewModel.saveHandler.valueDates),
                    modifier = Modifier.padding(3.dp)
                )
                Selection(modifier = modifier,
                    viewModel = viewModel,
                    onStateSelectedChange = { newState ->
                        dialogState = newState
                    })
                Text(text = stringResource(R.string.klasse, viewModel.saveHandler.valueClasses))
            }
            Surface {
                if (!LocalInspectionMode.current) //returns false if preview
                {
                    if (viewModel.saveHandler.valueDates != 0 && viewModel.saveHandler.valueClasses != 0) {
                        StundenplanWebview(
                            modifier = modifier,
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

                            viewModel.datesMap!!.forEach {
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
                            viewModel.classMap!!.forEach {
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
        modifier: Modifier,
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
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }


@Composable
fun TableWebView(
    modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData, htmlString: String
) {

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        val heightCompose = maxHeight
        val widthCompose = maxWidth


        var modifier = modifier
        val zoomed: MutableState<Boolean> = remember { mutableStateOf(false) }
        val webViewHeight: MutableState<Int?> = remember { mutableStateOf(null) }
        val webViewWidth: MutableState<Int?> = remember { mutableStateOf(null) }



        if (webViewWidth.value != null && webViewHeight.value != null) {


            val webViewWidthDp = webViewWidth.value!!.pxToDp()
            val webViewHeightDp = webViewHeight.value!!.pxToDp()

            val ratio = (heightCompose / webViewHeightDp)
            try {


                viewModel.hPadding = ((widthCompose - (widthCompose * ratio)))
                // modifier = modifier.padding(horizontal = hPadding)

            } catch (_: Exception) {
            }
        }




        AndroidView(modifier = modifier, factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT

                )


            }
        }, update = {


            it.settings.apply {
                loadWithOverviewMode = true
                useWideViewPort = true
            }
            it.java
            it.evaluateJavascript(
                """
    document.body.style.paddingLeft = '300px';
    document.body.style.paddingRight = '300px';
    document.body.style.boxSizing = 'border-box';
    document.body.style.width = '100%';
""".trimIndent(), null
            )
            runBlocking { viewModel.saveHandler.saveHandlerInitJob.join() }
            val paddedHtmlString =
                "<div style='padding: 0 300px; box-sizing: border-box;'>$htmlString</div>"

            it.loadDataWithBaseURL(
                null, // Base URL (can be null)
                paddedHtmlString, "text/html", "UTF-8", null // History URL (can be null)
            )


            //it.getSettings().useWideViewPort = false
            //it.setInitialScale(90)


            /*
            if(!zoomed.value) {
                it.setInitialScale(1)
                it.getSettings().useWideViewPort = true
            } else{it.setInitialScale(50)}*/

            it.getSettings().builtInZoomControls = true
            it.getSettings().displayZoomControls = false


            if (!zoomed.value) {
                it.viewTreeObserver.addOnGlobalLayoutListener {

                    if (it.contentHeight > 0) {
                        webViewWidth.value = it.width
                        webViewHeight.value = it.contentHeight
                        zoomed.value = true
                    }
                }
            }


        })

    }

}

