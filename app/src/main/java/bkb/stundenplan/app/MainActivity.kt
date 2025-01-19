package bkb.stundenplan.app


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import bkb.stundenplan.app.ui.MenuContent
import bkb.stundenplan.app.ui.SettingsPage
import bkb.stundenplan.app.ui.StateSelectedEnum
import bkb.stundenplan.app.ui.StundenplanPage
import bkb.stundenplan.app.ui.theme.BKBStundenplanTheme
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {


    @SuppressLint("StateFlowValueCalledInComposition")

    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Inhalt der APP
        setContent {
            //Viewmodel, welches die Daten beinhaltet, welche sich während der Nutzung der App ändern
            val appViewModel =
                viewModel<ViewModelStundenplanData>(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST") return ViewModelStundenplanData(context = applicationContext) as T
                    }
                })


            appViewModel.isPortrait =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
            appViewModel.heightTopAppBar.value = if (!appViewModel.isPortrait) 60.dp else 100.dp

            BKBStundenplanTheme(viewModel = appViewModel) {
                AppContent(
                    modifier = Modifier.fillMaxSize(), appViewModel
                )
            }
        }
    }
}


@Composable
fun AppContent(
    modifier: Modifier = Modifier, appViewModel: ViewModelStundenplanData = viewModel()
) {
    LeftSideBar(
        modifier, appViewModel
    )
}



@Composable
fun AppBarAction(viewModel: ViewModelStundenplanData, onCalendarClick: () -> Unit) {


        //backbutton per week
        IconButton( onClick = {
            viewModel.saveHandler.saveValueDate(viewModel.saveHandler.valueDate.value - 1)

        }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                contentDescription = stringResource(R.string.menu),
                tint = Color.Unspecified,
                modifier = Modifier.scale(0.5f)
            )


        }


        //select Button per week
        IconButton(
            onClick = {
                onCalendarClick()

            }) {
            Icon(
                painter = painterResource(id = R.drawable.outline_calendar_month_24),
                contentDescription = stringResource(R.string.menu),
                tint = Color.Unspecified,
                modifier = Modifier.scale(0.5f)
            )


        }


//forwardButton per week
        IconButton(
            onClick = {
                viewModel.saveHandler.saveValueDate(viewModel.saveHandler.valueDate.value - 1)
            }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                contentDescription = stringResource(R.string.menu),
                tint = Color.Unspecified,
                modifier = Modifier.scale(scaleX = -1f, scaleY = 1f).scale(0.5f)
            )


        }






}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeftSideBar(
    modifier: Modifier = Modifier, appViewModel: ViewModelStundenplanData = viewModel()
) {
    var stateSelected by rememberSaveable { mutableStateOf(StateSelectedEnum.STUNDENPLAN) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet(modifier = Modifier.width(160.dp)) {
            Row(
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Menu", modifier = Modifier.padding(16.dp)
                )
                IconButton(onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }, content = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "Zurück Pfeil",
                        modifier = Modifier.scale(0.5f)
                    )
                })
            }

            HorizontalDivider()

            MenuContent.LoadMenuContent(onStateSettingsChange = {
                stateSelected =
                    if (stateSelected == StateSelectedEnum.SETTINGS) StateSelectedEnum.UNSELECTED
                    else StateSelectedEnum.SETTINGS

            }, onStateStundenplanChange = {
                stateSelected =
                    if (stateSelected == StateSelectedEnum.STUNDENPLAN) StateSelectedEnum.UNSELECTED
                    else StateSelectedEnum.STUNDENPLAN
            }, stateSelected = stateSelected
            )
            Spacer(modifier.weight(1f))
            Text(
                textAlign = TextAlign.Right,
                lineHeight = 10.sp,
                fontSize = 10.sp,
                text = stringResource(
                    R.string.version,
                    stringResource(R.string.app_Version),
                ),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(4.dp)
            )
        }
    }) {
        Scaffold(topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(id = R.string.app_name)) },
//MaterialTheme.colorScheme.secondaryContainer
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.height(appViewModel.heightTopAppBar.value),
                navigationIcon = {


                    IconButton(modifier = Modifier, onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }) {
                        Icon(

                            painter = painterResource(id = R.drawable.menu_24px),
                            contentDescription = stringResource(R.string.menu),
                            tint = Color.Black
                        )


                    }


                },
                actions = {
                    AppBarAction(viewModel = appViewModel)


                })
        }


        ) { contentPadding ->
            // Screen content
            if (stateSelected == StateSelectedEnum.SETTINGS) {
                SettingsPage.MainPage(
                    Modifier
                        .padding(contentPadding)
                        .fillMaxSize(), viewModel = appViewModel
                )
            } else if (stateSelected == StateSelectedEnum.STUNDENPLAN) {
                StundenplanPage.MainPage(
                    Modifier
                        .padding(contentPadding)
                        .fillMaxSize(), viewModel = appViewModel
                )
            }
        }



    }


}

/*
* todo
*  replace if (!= null)
* with .let{}
*
* */