package com.example.bkbstundenplan.ui

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bkbstundenplan.R
import com.example.bkbstundenplan.StundenplanData

object StundenplanPage
{
    @Composable
    fun MainPage(
            modifier: Modifier = Modifier,
            login: MutableState<StundenplanData>
                )
    {


        Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
            Text(
                    text = "Stundenplan",
                    style = TextStyle(fontSize = 30.sp),
                )
            HorizontalDivider(Modifier.fillMaxWidth())




            Surface(modifier = modifier.fillMaxSize().padding(12.dp)) {

                StundenplanWebview(
                        modifier = modifier.padding(12.dp),
                        login = login
                                  )
            }


            /*
                                    var text:String = stringResource(id = R.string.HTMLStringResFull)
                                    AndroidView(factory = { context ->
                                        TextView(context).apply {
                                            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
                                        }
                                    })*/
        }

    }


    @Composable
    fun Selection(
            modifier: Modifier = Modifier,
            login: MutableState<StundenplanData>
                 )
    {


    }

    @Composable
    fun ClassSelector(
            modifier: Modifier = Modifier,
            login: MutableState<StundenplanData>
                     )
    {


    }

    @Composable
    fun DateSelector(
            modifier: Modifier = Modifier,
            login: MutableState<StundenplanData>
                    )
    {
    }

    @Composable
    fun StundenplanWebview(
            modifier: Modifier = Modifier,
            login: MutableState<StundenplanData>
                          )
    {
        var html: String = stringResource(id = R.string.HTMLStringResMOD)
        var pathUrl: String =
            "https://www.berufskolleg-bottrop.de/index.php"



        //https://medium.com/@kevinnzou/using-webview-in-jetpack-compose-bbf5991cfd14
        AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = {
                    WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                                                             )
                    }
                },
                update = {

                    //it.loadUrl("file:///android_asset/HTMLStundenplanExample.html")
                    it.loadUrl("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/") //authorization missing





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