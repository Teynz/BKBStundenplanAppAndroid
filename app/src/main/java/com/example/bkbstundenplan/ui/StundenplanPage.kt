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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bkbstundenplan.StundenplanData

object StundenplanPage
{
    enum class DialogStateEnum
    {
        NONE, DATE, CLASS;
    }


    @Composable
    fun MainPage(
            modifier: Modifier = Modifier,
            login: MutableState<StundenplanData>
                )
    {

        var URLStundenplan by rememberSaveable{ mutableStateOf("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/")};
        var dialogState by rememberSaveable { mutableStateOf(DialogStateEnum.NONE) }


        Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
              ) {
            Text(
                    text = "Stundenplan",
                    style = TextStyle(fontSize = 30.sp),
                )
            HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                             )

            Selection(modifier = modifier,
                      login = login,
                      onStateSelectedChange = { newState ->
                          dialogState = newState
                      })

            Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(2.dp)
                   ) {


                login.value.SelectionDialog(
                        dialogState = dialogState,
                        ondialogStateChange = { newState ->
                            dialogState = newState
                        },
                        URLStundenplan = URLStundenplan
                    )



                if (!LocalInspectionMode.current) //returns false if preview
                {
                    StundenplanWebview(
                            modifier = modifier,
                            login = login,
                        URLStundenplan = URLStundenplan,
                                      )
                } else
                {
                    Text(
                            modifier = modifier,
                            text = "WebView not available in preview"
                        )
                }

            }

        }

    }


    @Composable
    fun Selection(
            modifier: Modifier = Modifier,
            login: MutableState<StundenplanData>,
            onStateSelectedChange: (DialogStateEnum) -> Unit
                 )
    {
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
            URLStundenplan:String,
                          )
    {
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
                        it.loadUrl(URLStundenplan)
                        it.getSettings().loadWithOverviewMode = true
                        it.getSettings().useWideViewPort = true
                    })


    }

}


@Preview(showBackground = true)
@Composable
fun StundenplanAppPreview()
{
    val loginState = remember { mutableStateOf(StundenplanData()) }
    StundenplanPage.MainPage(login = loginState)
}