package com.example.bkbstundenplan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bkbstundenplan.StundenplanData

object SettingsPage {


    @Composable
    fun MainPage(
        modifier: Modifier = Modifier,
        login: MutableState<StundenplanData>
    ) {


        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = (LocalConfiguration.current.screenWidthDp / 20).dp)
        ) {

            SwitchAbfrage(login = login, mainText = "DarkMode", subText = null, checked = login.value.Darkmode.value,
                onCheckedChange = { login.value.Darkmode.value = it})
            Spacer(modifier = Modifier.padding(10.dp))
            SwitchAbfrage(
                login = login,
                mainText = "Experimentelle Stundenpläne",
                subText = "Aktiviert die Auswahl von Stundenplänen der letzten Wochen, kann zu fehlern führen",
                checked = login.value.experimentellerStundenplan.value,
                onCheckedChange = { login.value.experimentellerStundenplan.value = it }
            )


        }

    }

    @Composable
    fun SwitchAbfrage(

        modifier: Modifier = Modifier,
        login: MutableState<StundenplanData>,
        mainText: String,
        subText: String? = null,
        checked: Boolean,
        onCheckedChange: ((Boolean) -> Unit)?
    ) {
        Row(horizontalArrangement = Arrangement.Absolute.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                Text(
                    text = mainText,
                    textAlign = TextAlign.Left,
                    style = TextStyle(fontSize = 15.sp)
                )
                if (subText != null) {
                    Text(
                        modifier = Modifier.widthIn(
                            min = 0.dp,
                            max = (LocalConfiguration.current.screenWidthDp * 2 / 3).dp
                        ),
                        text = subText,
                        textAlign = TextAlign.Left,
                        style = TextStyle(fontSize = 10.sp),
                        minLines = 2,
                        softWrap = true
                    )
                }
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange)
        }
    }


    @Composable
    fun Login(
        modifier: Modifier = Modifier,
        login: MutableState<StundenplanData>
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    value = login.value.loginName.value,
                    onValueChange = { login.value.loginName.value = it },
                    label = { Text("Benutzername") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                TextField(
                    value = login.value.passwort.value,
                    onValueChange = { login.value.passwort.value = it },
                    label = { Text("passwort") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )


            }


        }
    }


}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {


        val loginState = remember { mutableStateOf(StundenplanData()) }
        SettingsPage.MainPage(login = loginState)
    }

}


/*todo
*  Experimenteller Stundenplan Schalter
*  Dark Theme
*  Custom Timetable
* 
*  MorgenAlarm
* */
