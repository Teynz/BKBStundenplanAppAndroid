package bkb.stundenplan.app.ui

//import it.skrape.fetcher.request.Json

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bkb.stundenplan.app.HTMLStrings
import bkb.stundenplan.app.ViewModelStundenplanData
import bkb.stundenplan.app.ui.StundenplanCustom.StundenplanCompose
import kotlinx.coroutines.runBlocking


object StundenplanPage {


    @SuppressLint("AuthLeak")
    @Composable
    fun MainPage(
        modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData,  dialogState: Boolean, onDialogStateChange: (Boolean) -> Unit
    ) {

        val configuration = LocalConfiguration.current
        val orientationVertical = configuration.orientation == Configuration.ORIENTATION_PORTRAIT




        if (orientationVertical) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    Stundenplan(
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


                Box(
                    modifier = Modifier.weight(1f)
                ) {


                    Stundenplan(
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
                onDialogStateChange(newState)
            })

    }


    @Composable
    fun Stundenplan(
        modifier: Modifier,
        viewModel: ViewModelStundenplanData,
    ) {
        if (viewModel.saveHandler.effectiveFancyStundenplan.collectAsStateWithLifecycle().value) {
            val configuration = LocalConfiguration.current

            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp

            StundenplanCompose(
                modifier = modifier, viewModel = viewModel, cellWidth = (screenWidth.value/7).dp, cellHeight = (screenHeight.value/12.2).dp
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
                it.settings.loadWithOverviewMode = true
                it.settings.useWideViewPort = true
                it.settings.builtInZoomControls = true
                it.settings.displayZoomControls = false
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
                it.settings.loadWithOverviewMode = true
                it.settings.useWideViewPort = true
                it.settings.builtInZoomControls = true
                it.settings.displayZoomControls = false
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

        view.settings.loadWithOverviewMode = true
        view.settings.useWideViewPort = true
        view.settings.builtInZoomControls = true
        view.settings.displayZoomControls = false


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

