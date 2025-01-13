package bkb.stundenplan.app.ui

//import it.skrape.fetcher.request.Json

import bkb.stundenplan.app.ui.StundenplanCustom.StundenplanCompose
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bkb.stundenplan.app.HTMLStrings
import bkb.stundenplan.app.R
import bkb.stundenplan.app.ViewModelStundenplanData
import kotlinx.coroutines.runBlocking


object StundenplanPage {
    enum class DialogStateEnum {
        NONE, DATE, TYPE, ELEMENT;
    }


    @SuppressLint("AuthLeak")
    @Composable
    fun MainPage(
        modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData
    ) {

        val configuration = LocalConfiguration.current
        val orientationVertical = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        var dialogState by rememberSaveable { mutableStateOf(DialogStateEnum.NONE) }


        if (orientationVertical) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth()
            ) {
                Selection(modifier = Modifier
                    .padding(5.dp)
                    .height(45.dp),

                    valueDate = viewModel.saveHandler.valueDate.collectAsStateWithLifecycle().value,
                    valueType = viewModel.saveHandler.valueType.collectAsStateWithLifecycle().value,
                    valueElement = viewModel.saveHandler.valueElement.collectAsStateWithLifecycle().value,
                    onStateSelectedChange = { enumState ->
                        dialogState = enumState


                    })

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {


                    StundenplanWebview(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp),
                        viewModel = viewModel,
                    )

                }


            }

        } else {
            Row(
                verticalAlignment = Alignment.Top, modifier = modifier.fillMaxWidth()
            ) {

                Selection(
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .padding(5.dp)
                        .height(50.dp),
                    valueDate = viewModel.saveHandler.valueDate.collectAsStateWithLifecycle().value,
                    valueType = viewModel.saveHandler.valueType.collectAsStateWithLifecycle().value,
                    valueElement = viewModel.saveHandler.valueElement.collectAsStateWithLifecycle().value,
                    onStateSelectedChange = { newState ->
                        dialogState = newState
                    })
                Box(
                    modifier = Modifier.weight(1f)
                ) {


                    StundenplanWebview(
                        modifier = Modifier.padding(
                            start = 4.dp,
                            end = 8.dp,
                            top = 2.dp,
                            bottom = 2.dp
                        ),
                        viewModel = viewModel,
                    )

                }

            }
        }



        SelectionDialog(modifier = modifier,
            dialogState = dialogState,
            ondialogStateChange = { newState ->
                dialogState = newState
            })

    }


    @Composable
    fun Selection(
        modifier: Modifier = Modifier,
        valueDate: Int,
        valueType: String,
        valueElement: Int,
        onStateSelectedChange: (DialogStateEnum) -> Unit
    ) {
        Button(
            onClick = { onStateSelectedChange(DialogStateEnum.DATE) },
            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.ausw_hlen), modifier = Modifier.padding(0.dp)
                )
                Text(
                    text = "${stringResource(R.string.datum)}: $valueDate " + "${
                        stringResource(
                            R.string.art
                        )
                    }: $valueType " + "${stringResource(R.string.element)}: $valueElement",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(0.dp),
                    lineHeight = 6.sp,
                    fontSize = 6.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }


    @Composable
    fun Stundenplan(
        modifier: Modifier,
        viewModel: ViewModelStundenplanData,
    ) {
        if (viewModel.saveHandler.effectiveFancyStundenplan.collectAsStateWithLifecycle().value) {
            StundenplanCompose(
                modifier = modifier, viewModel = viewModel
            )

        } else if (viewModel.saveHandler.effectiveStundenplanZoom.collectAsStateWithLifecycle().value) {
            viewModel.scraping.stundenplanSite?.select("table")?.get(0)?.let { valueTablesScraped ->
                TableWebView(
                    viewModel = viewModel,
                    htmlString = valueTablesScraped.toString(),
                    modifier = modifier
                )
            } ?: run {
                Text(
                    modifier = modifier, text = "Fehler: Experimenteller Stundenplan"
                )

            }

        } else {
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


    @SuppressLint("AuthLeak")
    @Composable
    fun StundenplanWebview(
        modifier: Modifier,
        viewModel: ViewModelStundenplanData,
    ) {
        if (viewModel.saveHandler.experimentellerStundenplan.collectAsStateWithLifecycle().value && viewModel.saveHandler.effectiveValueType.collectAsStateWithLifecycle().value == "c") {
            viewModel.scraping.stundenplanSite?.select("table")?.get(0)?.let { valueTablesScraped ->
                TableWebView(
                    viewModel = viewModel,
                    htmlString = valueTablesScraped.toString(),
                    modifier = modifier
                )


            } ?: run {
                Text(
                    modifier = modifier, text = "Fehler: Experimenteller Stundenplan"
                )

            }

        } else {
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
    modifier: Modifier, viewModel: ViewModelStundenplanData, htmlString: String
) {
    val styleHTML: HTMLStrings.Styling =
        HTMLStrings.Styling(
            typeValue = viewModel.saveHandler.effectiveValueType.collectAsStateWithLifecycle().value,
            darkMode = viewModel.saveHandler.darkmode
        )

    val webView = remember { mutableStateOf<WebView?>(null) }

    AndroidView(modifier = modifier, factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    //place stuff which should be done when WebView ist finished here
                }
            }
            webView.value = this
        }
    }, update = { view ->

        view.getSettings().loadWithOverviewMode = true
        view.getSettings().useWideViewPort = true
        view.getSettings().builtInZoomControls = true
        view.getSettings().displayZoomControls = false


        //view.setInitialScale(5)
        runBlocking { viewModel.saveHandler.saveHandlerInitJob.join() }


        view.loadDataWithBaseURL(
            null,
            HTMLStrings.styleExperimentellerStundenplan(styleHTML) + htmlString,
            "text/html",
            "UTF-8",
            null
        )


    })

}

