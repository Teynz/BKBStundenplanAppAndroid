package bkb.stundenplan.app.ui

//import it.skrape.fetcher.request.Json
import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
            ) {
                Selection(modifier = modifier,
                    viewModel = viewModel,
                    onStateSelectedChange = { newState ->
                        dialogState = newState
                    })
            }
            Surface {
                if (!LocalInspectionMode.current) //returns false if preview
                {
                    if (viewModel.saveHandler.valueDate != 0 && viewModel.saveHandler.valueElement != 0) {
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
                                        Button(
                                            colors = if ((dateValue + iter) == viewModel.saveHandler.valueDate) ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                        else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                                            onClick = {
                                                viewModel.saveHandler.valueDate =
                                                    (dateValue + iter)
                                                viewModel.urlMaker.updateURL()
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
                                    Button(
                                        colors = if (it.key == viewModel.saveHandler.valueDate) ButtonDefaults.buttonColors(
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                    else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                        onClick = {
                                            viewModel.saveHandler.valueDate = it.key
                                            viewModel.urlMaker.updateURL()
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
                        if (viewModel.elementMap != null) {
                            viewModel.elementMap!!.forEach {
                                item {
                                    Button(
                                        colors = if (it.key == viewModel.saveHandler.valueElement) ButtonDefaults.buttonColors(
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                    else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                        onClick = {
                                            viewModel.saveHandler.saveValueElement(it.key)
                                            viewModel.urlMaker.updateURL()
                                            viewModel.updateTablesScraped()
                                            ondialogStateChange(DialogStateEnum.NONE)
                                        }) {
                                        Text(text = viewModel.elementMap!!.getValue(it.key))
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
            Button(
                onClick = { onStateSelectedChange(DialogStateEnum.DATE) },
                contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
                modifier = Modifier.height(40.dp)
            )
            {
                Column(modifier = Modifier.padding(0.dp)) {
                    Text(
                        text = stringResource(R.string.datum_auswaehlen),
                        modifier = Modifier.padding(0.dp)
                    )
                    Text(
                        text = stringResource(R.string.datum, viewModel.saveHandler.valueDate),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(0.dp),
                        lineHeight = 8.sp,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }
            Spacer(
                modifier = Modifier
                    .width(10.dp)
                    .border(2.dp, Color.Red)
            )

            Button(
                onClick = { onStateSelectedChange(DialogStateEnum.CLASS) },
                modifier = Modifier.height(40.dp),
                contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
            )
            {
                Column {
                    Text(
                        text = stringResource(R.string.klasse_auswaehlen),
                        modifier = Modifier.padding(0.dp)
                    )
                    Text(
                        text = stringResource(R.string.klasse, viewModel.saveHandler.valueElement),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(0.dp),
                        lineHeight = 8.sp,
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    fun NavigationBarHeight(): PaddingValues {


        val insets = WindowInsets.navigationBars
        val padding = insets.asPaddingValues()
        return padding
    }

    @SuppressLint("AuthLeak")
    @Composable
    fun StundenplanWebview(
        viewModel: ViewModelStundenplanData,
    ) {
        if (viewModel.saveHandler.experimentellerStundenplan) {
            if (viewModel.tablesScraped.value != null) {
                Column {
                    TableWebView(
                        viewModel = viewModel, htmlString = viewModel.tablesScraped.value.toString()
                    )

                }
            }
        } else if (!viewModel.saveHandler.experimentellerStundenplan) {
            AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }, update = {
                it.loadUrl(viewModel.urlMaker.urlStundenplan.value)
                it.getSettings().loadWithOverviewMode = true
                it.getSettings().useWideViewPort = true
                it.getSettings().builtInZoomControls = true
                it.getSettings().displayZoomControls = false
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

    var webViewModifier = modifier

    val webView = remember { mutableStateOf<WebView?>(null) }

    AndroidView(modifier = webViewModifier.fillMaxSize(), factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                loadWithOverviewMode = true
                useWideViewPort = false
                builtInZoomControls = true
                displayZoomControls = false


            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    //place stuff which should be done when WebView ist finished here
                }
            }
            webView.value = this
        }
    }, update = { view ->
        view.setInitialScale(5)
        runBlocking { viewModel.saveHandler.saveHandlerInitJob.join() }
        view.loadDataWithBaseURL(
            null, HTMLStrings.styleExperimentellerStundenplan(
                viewModel.saveHandler.darkmode,
            ) + htmlString, "text/html", "UTF-8", null
        )


    })

}

