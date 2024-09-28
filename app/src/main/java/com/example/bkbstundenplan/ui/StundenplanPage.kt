package com.example.bkbstundenplan.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bkbstundenplan.HTMLStrings
import com.example.bkbstundenplan.ViewModelStundenplanData

object StundenplanPage {
    enum class DialogStateEnum {
        NONE, DATE, CLASS;
    }


    @SuppressLint("AuthLeak")
    @Composable
    fun MainPage(
        modifier: Modifier = Modifier,
        viewModel: ViewModelStundenplanData
    ) {
        var dialogState by rememberSaveable { mutableStateOf(DialogStateEnum.NONE) }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {


            Selection(modifier = modifier,
                viewModel = viewModel,
                onStateSelectedChange = { newState ->
                    dialogState = newState
                })
            Row {
                Text(text = "Datum:${viewModel.valueDates}  ")
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = "Klasse:${viewModel.valueClasses}  ")

            }

            Surface(
            ) {
                if (!LocalInspectionMode.current) //returns false if preview
                {

                    if(viewModel.valueDates != 0 && viewModel.valueClasses != 0) {
                        StundenplanWebview(
                            viewModel = viewModel,
                        )
                    }


                } else {
                    Text(
                        modifier = modifier,
                        text = "WebView not available in preview"
                    )
                }
                viewModel.SelectionDialog(
                    dialogState = dialogState,
                    ondialogStateChange = { newState ->
                        dialogState = newState
                    }
                )

            }

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
                Text(text = "Datum auswählen")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = { onStateSelectedChange(DialogStateEnum.CLASS) }) {
                Text(text = "Klasse auswählen")
            }

        }
    }


    @SuppressLint("AuthLeak")
    @Composable
    fun StundenplanWebview(
        modifier: Modifier = Modifier,
        viewModel: ViewModelStundenplanData,
    ) {


        if (viewModel.experimentellerStundenplan == true) {

            viewModel.updateTablesScraped()
            if (viewModel.tablesScraped.value != null) {
                AndroidView(modifier = modifier.fillMaxWidth().padding(horizontal = 2.dp),
                    factory = {
                        WebView(it).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = {

                        it.loadDataWithBaseURL(
                            null, // Base URL (can be null)

                            HTMLStrings.styleExperimentellerStundenplan(viewModel.darkmode) + (viewModel.tablesScraped.value!!.stundenplanTable.toString()),
                            "text/html",
                            "UTF-8",
                            null // History URL (can be null)
                        )

                        it.getSettings().loadWithOverviewMode = true
                        it.getSettings().useWideViewPort = true
                    })


                //other webview stuff


            }
        } else if (viewModel.experimentellerStundenplan == false) {

            AndroidView(modifier = modifier.fillMaxSize(),

                factory = {
                    WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },

                update = {
                    it.loadUrl(viewModel.urlStundenplan.value)
                    it.getSettings().loadWithOverviewMode = true
                    it.getSettings().useWideViewPort = true
                })
        }


    }

}


@Preview(showBackground = true, apiLevel = 31, device = "id:pixel_8")
@Composable
fun StundenplanAppPreview() {
    val appViewModel: ViewModelStundenplanData = viewModel()
    StundenplanPage.MainPage(viewModel = appViewModel)
}