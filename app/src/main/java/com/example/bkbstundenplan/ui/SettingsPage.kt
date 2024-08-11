package com.example.bkbstundenplan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bkbstundenplan.StundenplanData

object SettingsPage
{



    @Composable
    fun MainPage(modifier: Modifier = Modifier, login: MutableState<StundenplanData>)
    {



        Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
            Text(
                    text = "Einstellungen",
                    style = TextStyle(fontSize = 30.sp),
                )
            HorizontalDivider(Modifier.fillMaxWidth())
            Login(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally),
                    login = login
                 )


        }

    }


    @Composable
    fun Login(modifier: Modifier = Modifier,  login: MutableState<StundenplanData>)
    {
        Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
               ) {


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                        value = login.value.LoginName.value,
                        onValueChange = { login.value.LoginName.value = it },
                        label = { Text("Benutzername") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next)
                                                                                       )

                TextField(
                        value = login.value.Passwort.value,
                        onValueChange = {  login.value.Passwort.value = it  },
                        label = { Text("passwort") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next)
                         )


            }


        }
    }

    @Composable
    fun ClassSelector(modifier: Modifier = Modifier)
    {}

    @Composable
    fun DateSelector(modifier: Modifier = Modifier)
    {}


}

@Preview(showBackground = true)
@Composable
fun AppPreview()
{
    val loginState = remember { mutableStateOf(StundenplanData()) }
    SettingsPage.MainPage(login = loginState)
}

