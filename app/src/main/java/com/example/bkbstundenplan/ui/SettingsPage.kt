package com.example.bkbstundenplan.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bkbstundenplan.R
import com.example.bkbstundenplan.ViewModelStundenplanData


object SettingsPage {


    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun MainPage(
        modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData
    ) {
        var appInfoState by rememberSaveable { mutableStateOf(false) }

        if (appInfoState) {
            AppInfoDialog(appInfoState = appInfoState, onStateChange = { appInfoState = it })
        }

        AppInfoDialog(appInfoState = appInfoState, onStateChange = { appInfoState = it })
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = (LocalConfiguration.current.screenWidthDp / 20).dp)
        ) {

            SwitchAbfrage(
                mainText = stringResource(R.string.darkmode),
                subText = null,
                checked = viewModel.saveHandler.darkmode,
                onCheckedChange = { viewModel.saveHandler.saveDarkMode(it) })
            Spacer(modifier = Modifier.padding(10.dp))

            SwitchAbfrage(
                mainText = stringResource(R.string.adaptive_farben),
                subText = stringResource(R.string.adaptive_farben_description),
                checked = viewModel.saveHandler.adaptiveColor,
                onCheckedChange = { viewModel.saveHandler.saveAdaptiveColor(it) })
            Spacer(modifier = Modifier.padding(10.dp))

            SwitchAbfrage(
                mainText = stringResource(R.string.experimenteller_stundenplan),
                subText = stringResource(R.string.experimenteller_stundenplan_description),
                checked = viewModel.saveHandler.experimentellerStundenplan,
                onCheckedChange = {
                    viewModel.saveHandler.saveExperimentellerStundenplan(it)
                })

            Spacer(modifier = Modifier.padding(10.dp))

            SwitchAbfrage(
                mainText = stringResource(R.string.alte_stundenpl_ne),
                subText = stringResource(R.string.alte_stundenplaene_description),
                checked = viewModel.saveHandler.alteStundenplaene,
                onCheckedChange = {
                    viewModel.saveHandler.saveAlteStundenplaene(it)
                })

            Spacer(modifier = Modifier.weight(1F))


            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
                    .clickable { appInfoState = true },
                text = stringResource(id = R.string.app_information),
                style = TextStyle(fontSize = 10.sp)
            )
        }

    }

    @Composable
    fun SwitchAbfrage(

        modifier: Modifier = Modifier,
        mainText: String,
        subText: String? = null,
        checked: Boolean,
        onCheckedChange: ((Boolean) -> Unit)?
    ) {
        Row(
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {

                Text(
                    text = mainText, textAlign = TextAlign.Left, style = TextStyle(fontSize = 15.sp)
                )
                if (subText != null) {
                    Text(
                        modifier = Modifier.widthIn(
                            min = 0.dp, max = (LocalConfiguration.current.screenWidthDp * 2 / 3).dp
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
                checked = checked, onCheckedChange = onCheckedChange
            )
        }
    }


    @Composable
    fun AppInfoDialog(
        modifier: Modifier = Modifier, appInfoState: Boolean, onStateChange: (Boolean) -> Unit
    ) {
        if (appInfoState) {
            Dialog(onDismissRequest = { onStateChange(false) }, content = {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(390.dp)
                        .padding(11.dp),


                    ) {

                    Column() {
                        Spacer(modifier = Modifier.padding(6.dp))
                        Text(
                            modifier = modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            text = stringResource(id = R.string.app_information),
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                modifier = modifier
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.app_info_description) + stringResource(
                                    R.string.app_info_long_text
                                )
                            )
                        }
                    }
                }
            })
        }
    }
}

@Composable
fun Login(
    modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = viewModel.loginName,
                onValueChange = { viewModel.loginName = it },
                label = { Text("Benutzername") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )

            TextField(
                value = viewModel.loginPasswort,
                onValueChange = { viewModel.loginPasswort = it },
                label = { Text("passwort") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                )
            )
        }
    }
}

