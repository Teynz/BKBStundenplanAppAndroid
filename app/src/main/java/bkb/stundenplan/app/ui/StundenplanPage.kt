package bkb.stundenplan.app.ui

//import it.skrape.fetcher.request.Json

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
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
        val orientationVertical by remember {
            mutableStateOf(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        }

        var dialogState by rememberSaveable { mutableStateOf(DialogStateEnum.NONE) }
        Box(
            contentAlignment = Alignment.TopStart
        ) {

            if (orientationVertical) Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                ) {
                    Selection(modifier = modifier,
                        viewModel = viewModel,
                        onStateSelectedChange = { newState ->
                            dialogState = newState
                        })
                }
            }
            if (!orientationVertical) Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
            ) {
                Selection(modifier = modifier,
                    viewModel = viewModel,
                    onStateSelectedChange = { newState ->
                        dialogState = newState
                    })
            }


            /*if (!LocalInspectionMode.current) //returns false if preview
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
                }*/



            SelectionDialog(
                modifier = modifier,
                dialogState = dialogState,
                ondialogStateChange = { newState ->
                    dialogState = newState
                })

        }
    }


    @Composable
    fun Selection(
        modifier: Modifier = Modifier,
        viewModel: ViewModelStundenplanData,
        onStateSelectedChange: (DialogStateEnum) -> Unit
    ) {
        Button(
            onClick = { onStateSelectedChange(DialogStateEnum.DATE) },
            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Column(modifier = Modifier.padding(0.dp)) {
                Text(
                    text = stringResource(R.string.ausw_hlen), modifier = Modifier.padding(0.dp)
                )
                Text(
                    text = "${stringResource(R.string.datum)}: ${viewModel.saveHandler.valueDate} " +
                            "${stringResource(R.string.art)}: ${viewModel.saveHandler.valueType} " +
                            "${stringResource(R.string.element)}: ${viewModel.saveHandler.valueElement}",
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

    val webView = remember { mutableStateOf<WebView?>(null) }

    AndroidView(modifier = modifier.fillMaxSize(), factory = { context ->
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

