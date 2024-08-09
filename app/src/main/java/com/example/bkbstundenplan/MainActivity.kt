package com.example.bkbstundenplan

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
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.launch
import com.example.bkbstundenplan.ui.MenuContent
import com.example.bkbstundenplan.ui.SettingsPage

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent(modifier = Modifier.fillMaxSize())


        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeftSideBar(modifier: Modifier = Modifier)
{

    var login by remember { mutableStateOf(Stundenplan()) }

    var stateSettings by rememberSaveable { mutableStateOf(true) }
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
                                          OnStateSettingsChange = {
                                              stateSettings = !stateSettings
                                          },
                                          StateSettings = stateSettings
                                                             )
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

            if (stateSettings)
            {
                SettingsPage.MainPage(
                        Modifier
                            .padding(contentPadding)
                            .fillMaxSize(),
                        login = login
                                     )
            }

        }
    }

}

@Composable
fun AppContent(modifier: Modifier = Modifier)
{
    LeftSideBar(modifier)
}

@Preview(showBackground = true)
@Composable
fun AppPreview()
{
    AppContent()
}
