package bkb.stundenplan.app.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bkb.stundenplan.app.R
import bkb.stundenplan.app.ViewModelStundenplanData


object SettingsPage {


    @Composable
    fun MainPage(
        modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData
    ) {
        val experimentellerStundenplan by viewModel.saveHandler.experimentellerStundenplan.collectAsStateWithLifecycle()

        var appInfoState by rememberSaveable { mutableStateOf(false) }

        if (appInfoState) {
            ImpressumDialog(onStateChange = { appInfoState = it })
        }


        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = (LocalConfiguration.current.screenWidthDp / 20).dp)
                .verticalScroll(rememberScrollState())
        ) {

            SwitchAbfrage(mainText = stringResource(R.string.darkmode),
                subText = null,
                checked = viewModel.saveHandler.darkmode,
                onCheckedChange = { viewModel.saveHandler.saveDarkMode(it) })
            Spacer(modifier = Modifier.padding(10.dp))

            SwitchAbfrage(mainText = stringResource(R.string.adaptive_farben),
                subText = stringResource(R.string.adaptive_farben_description),
                checked = viewModel.saveHandler.adaptiveColor,
                onCheckedChange = { viewModel.saveHandler.saveAdaptiveColor(it) })
            Spacer(modifier = Modifier.padding(10.dp))

            SwitchAbfrage(mainText = stringResource(R.string.experimenteller_stundenplan),
                subText = stringResource(R.string.experimenteller_stundenplan_description),
                checked = experimentellerStundenplan,
                onCheckedChange = {
                    viewModel.saveHandler.saveExperimentellerStundenplan(it)
                })


            Spacer(modifier = Modifier.padding(10.dp))

            if (experimentellerStundenplan) {
                Spacer(modifier = Modifier.padding(5.dp))
                SwitchAbfrage(mainText = stringResource(R.string.stundenplan_zoom),
                    subText = stringResource(R.string.zoomt_den_stundenplan_heran),
                    checked = viewModel.saveHandler.stundenplanZoom.collectAsStateWithLifecycle().value,
                    onCheckedChange = {
                        viewModel.saveHandler.saveStundenplanZoom(it)
                    })

                Spacer(modifier = Modifier.padding(10.dp))

                val fancyStundenplan by viewModel.saveHandler.fancyStundenplan.collectAsStateWithLifecycle()
                SwitchAbfrage(mainText = stringResource(R.string.fancy_stundenplan),
                    subText = stringResource(R.string.verwendet_eine_eigene_ansicht_f_r_die_stundenpl_ne_inspiriert_von_google_calendar),
                    checked = fancyStundenplan,
                    onCheckedChange = {
                        viewModel.saveHandler.saveFancyStundenplan(it)
                    })

                Spacer(modifier = Modifier.padding(15.dp))
                if (fancyStundenplan) {
                    SwitchAbfrage(mainText = stringResource(R.string.verbinde_zellen),
                        subText = stringResource(R.string.verbindet_die_zellen_der_f_cher),
                        checked = viewModel.saveHandler.mergeCells.collectAsStateWithLifecycle().value,
                        onCheckedChange = {
                            viewModel.saveHandler.saveMergeCells(it)
                        })

                    Spacer(modifier = Modifier.padding(15.dp))

                }


            }

            SwitchAbfrage(mainText = stringResource(R.string.alte_stundenpl_ne),
                subText = stringResource(R.string.alte_stundenplaene_description),
                checked = viewModel.saveHandler.alteStundenplaene,
                onCheckedChange = {
                    viewModel.saveHandler.saveAlteStundenplaene(it)
                })


            Spacer(modifier = Modifier.padding(10.dp))
            TeacherSection(viewModel = viewModel)

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
    fun TextFieldAbfrage(

        modifier: Modifier = Modifier,
        mainText: String,
        subText: String? = null,
        textLabel: String,
        value: String,
        onValueChange: ((String) -> Unit)
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

            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(textLabel) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
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
    fun TeacherSection(modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData) {
        val teacherMode by viewModel.saveHandler.teacherMode.collectAsStateWithLifecycle()

        Column(modifier = modifier) {
            SwitchAbfrage(mainText = stringResource(R.string.lehrer_modus),
                subText = stringResource(R.string.schaltet_die_lehrer_stundenpl_ne_frei_anmeldename_und_passwort_m_ssen_daf_r_einmalig_eingetragen_werden),
                checked = teacherMode,
                onCheckedChange = { viewModel.saveHandler.saveTeacherMode(it) })

            if (teacherMode) {
                Spacer(modifier = Modifier.padding(5.dp))
                Login(modifier = Modifier.fillMaxWidth(), viewModel = viewModel)
            }
        }
    }


    @Composable
    fun ImpressumDialog(
        modifier: Modifier = Modifier, onStateChange: (Boolean) -> Unit
    ) {

        Dialog(onDismissRequest = { onStateChange(false) }, content = {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp)
                    .padding(5.dp),
            ) {

                Column {
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
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Text(
                                    modifier = modifier
                                        .align(Alignment.CenterHorizontally)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Left,
                                    text = stringResource(id = R.string.impressum_kontakt)
                                )

                            }

                            item {
                                Text(modifier = modifier
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth(),
                                    textAlign = TextAlign.Left,
                                    text = buildAnnotatedString {
                                        append(
                                            stringResource(
                                                R.string.e_mail
                                            ) + stringResource(
                                                R.string.impressum_Credits
                                            ) + "\n\n" + stringResource(R.string.impressum_zusatz) + "\n\n" + stringResource(
                                                R.string.impressum_urheberrecht
                                            )
                                        )






                                    })
                            }
                            item {
                                BasicText(
                                    buildAnnotatedString {
                                        withLink(
                                            LinkAnnotation.Url(
                                                "https://github.com/Teynz/BKBStundenplanAppAndroid",
                                                TextLinkStyles(style = SpanStyle(color = Color.Blue)),
                                            )
                                        ) {
                                            append("Github Link")
                                        }
                                    },
                                    Modifier.padding(top = 20.dp)

                                )

                            }
                            item {
                                Text(
                                    modifier = modifier
                                        .align(Alignment.CenterHorizontally)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    text = stringResource(R.string.odin_der_feini)
                                )
                            }
                            item {
                                Image(
                                    modifier = modifier
                                        .align(Alignment.CenterHorizontally)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp)),
                                    painter = painterResource(id = R.drawable.odin),
                                    contentDescription = "Odin lololol"
                                )
                            }
                        }
                    }
                }
            }
        })

    }
}

@Composable
fun Login(
    modifier: Modifier = Modifier, viewModel: ViewModelStundenplanData
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        TextField(
            modifier = Modifier
                .weight(1F)
                .padding(end = 5.dp),
            value = viewModel.saveHandler.valueLoginName.collectAsStateWithLifecycle().value,
            onValueChange = { viewModel.saveHandler.saveLoginName(it) },
            label = { Text(stringResource(R.string.anmeldename)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),

            )

        TextField(
            modifier = Modifier
                .weight(1F)
                .padding(start = 5.dp),
            value = viewModel.saveHandler.valuePassword.collectAsStateWithLifecycle().value,
            onValueChange = { viewModel.saveHandler.savePassword(it) },
            label = { Text(stringResource(R.string.passwort)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            )
        )

    }
}

