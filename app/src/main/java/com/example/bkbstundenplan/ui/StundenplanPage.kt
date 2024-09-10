package com.example.bkbstundenplan.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bkbstundenplan.Stundenplan
import com.example.bkbstundenplan.StundenplanData

object StundenplanPage {
    enum class DialogStateEnum {
        NONE, DATE, CLASS;
    }


    @SuppressLint("AuthLeak")
    @Composable
    fun MainPage(
        modifier: Modifier = Modifier,
        login: MutableState<StundenplanData>
    ) {

        val urlStundenplan by rememberSaveable { mutableStateOf("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/") }
        var dialogState by rememberSaveable { mutableStateOf(DialogStateEnum.NONE) }


        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {


            Selection(modifier = modifier,
                login = login,
                onStateSelectedChange = { newState ->
                    dialogState = newState
                })
            Row {
                Text(text = "Datum:${login.value.valueDates.value}  ")
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = "Klasse:${login.value.valueClasses.value}  ")

            }

            Surface(
            ) {


                if (!LocalInspectionMode.current) //returns false if preview
                {
                    if (login.value.valueDates.value != 0 && login.value.valueClasses.value != 0) {
                        StundenplanWebview(
                            login = login,
                            urlStundenplan = urlStundenplan,
                        )
                    }
                } else {
                    Text(
                        modifier = modifier,
                        text = "WebView not available in preview"
                    )
                }
                login.value.SelectionDialog(
                    dialogState = dialogState,
                    ondialogStateChange = { newState ->
                        dialogState = newState
                    },
                    urlStundenplan = urlStundenplan
                )

            }

        }

    }


    @Composable
    fun Selection(
        modifier: Modifier = Modifier,
        login: MutableState<StundenplanData>,
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
        login: MutableState<StundenplanData>,
        urlStundenplan: String,
        tables: Stundenplan? = null
    ) {



        if (tables != null) {

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
                    it.loadUrl(login.value.urlStundenplan.value)
                    it.getSettings().loadWithOverviewMode = true
                    it.getSettings().useWideViewPort = true
                })



        } else {
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
                    it.loadUrl(login.value.urlStundenplan.value)
                    it.getSettings().loadWithOverviewMode = true
                    it.getSettings().useWideViewPort = true
                })
        }


    }

}


@Preview(showBackground = true, apiLevel = 31, device = "id:pixel_8")
@Composable
fun StundenplanAppPreview() {
    val loginState = remember { mutableStateOf(StundenplanData()) }
    StundenplanPage.MainPage(login = loginState)
}