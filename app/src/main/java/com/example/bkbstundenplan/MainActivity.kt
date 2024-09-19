package com.example.bkbstundenplan

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bkbstundenplan.ui.MenuContent
import com.example.bkbstundenplan.ui.SettingsPage
import com.example.bkbstundenplan.ui.StateSelectedEnum
import com.example.bkbstundenplan.ui.StundenplanPage
import com.example.bkbstundenplan.ui.theme.BKBStundenplanTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity()
{
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appViewModel: ViewModelStundenplanData = viewModel()

            BKBStundenplanTheme(darkTheme = appViewModel.darkmode) {




                AppContent(modifier = Modifier.fillMaxSize(), appViewModel)
            }



        }
    }
}
@Composable
fun AppContent(modifier: Modifier = Modifier, appViewModel: ViewModelStundenplanData = viewModel())
{
    LeftSideBar(modifier,appViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeftSideBar(modifier: Modifier = Modifier, appViewModel: ViewModelStundenplanData = viewModel())
{
    val Title = rememberSaveable{ mutableStateOf("BKBStundenplan")}

    val login = rememberSaveable { mutableStateOf(StundenplanData()) }

    var stateSelected by rememberSaveable { mutableStateOf(StateSelectedEnum.STUNDENPLAN) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()



    ModalNavigationDrawer(drawerState = drawerState,
                          modifier = modifier,
                          drawerContent = {

                              ModalDrawerSheet(modifier = Modifier.width(240.dp)) {

                                  Row(
                                          horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                                          verticalAlignment = Alignment.CenterVertically,
                                     ) {
                                      Text(
                                              text = "Menu",
                                              modifier = Modifier.padding(16.dp)
                                          )
                                      IconButton(onClick = {
                                          scope.launch {
                                              drawerState.apply {
                                                  if (isClosed) open() else close()
                                              }
                                          }
                                      },
                                                 content = {
                                                     Icon(
                                                             painter = painterResource(id = R.drawable.back),
                                                             contentDescription = "Zurück Pfeil"
                                                         )
                                                 })

                                    
                                      
                                      
                                      
                                  }
                                  HorizontalDivider()

                                  MenuContent.LoadMenuContent(
                                          onStateSettingsChange = {
                                              stateSelected =
                                                  if(stateSelected == StateSelectedEnum.SETTINGS) StateSelectedEnum.UNSELECTED
                                                  else StateSelectedEnum.SETTINGS

                                          },
                                          onStateStundenplanChange={
                                              stateSelected =
                                                  if(stateSelected == StateSelectedEnum.STUNDENPLAN) StateSelectedEnum.UNSELECTED
                                                  else StateSelectedEnum.STUNDENPLAN
                                          },
                                          stateSelected = stateSelected
                                                             )
                                  Text(text = "Beta 2.5 \n Entwickelt von Paul Brandt",
                                      modifier = Modifier.weight(1f))
                                  
                              }
                          }) {
        Scaffold(topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(id = R.string.app_name)) },
                                   colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Gray),
                                   navigationIcon = {
                                       IconButton(onClick = {
                                           scope.launch {
                                               drawerState.apply {
                                                   if (isClosed) open() else close()
                                               }
                                           }
                                       }) {
                                           Icon(
                                                   painter = painterResource(id = R.drawable.bkb_logo),
                                                   contentDescription = "Menu",
                                                   tint = Color.Unspecified
                                               )
                                       }

                                   })
        }


                ) { contentPadding ->

            // Screen content

            if (stateSelected == StateSelectedEnum.SETTINGS)
            {

                SettingsPage.MainPage(
                    Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                        viewModel = appViewModel
                                     )
            }
            else if (stateSelected == StateSelectedEnum.STUNDENPLAN)
            {
                StundenplanPage.MainPage(
                    Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                        viewModel = appViewModel
                                        )
            }




        }
    }

}



@Preview(showBackground = true)
@Composable
fun AppPreview()
{
    AppContent()
}
