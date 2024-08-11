package com.example.bkbstundenplan.ui


import androidx.compose.material3.Icon

import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.dp
import com.example.bkbstundenplan.R

import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color


object MenuContent
{

    @Composable
    fun LoadMenuContent(
            OnStateSettingsChange: ()-> Unit,//teil vor -> sind die parameter, der darauffolgende teil sind die Rückgabewerte
            OnStateStundenplanChange: ()-> Unit,
            StateSelected: StateSelectedEnum
                       )
    {
        NavigationDrawerItem(label = { Text(text = "Stundenplan") },
                             icon = {
                                 Icon(
                                         painter = painterResource(id = R.drawable.stundenplan),
                                         modifier = androidx.compose.ui.Modifier.size(28.dp),
                                         contentDescription = "settings icon",
                                         tint = Color.Unspecified
                                     )
                             },
                             selected = StateSelected == StateSelectedEnum.STUNDENPLAN,
                             onClick =  {OnStateStundenplanChange()})



        NavigationDrawerItem(label = { Text(text = stringResource(id = R.string.settings)) },
                             icon = {
                                 Icon(
                                         painter = painterResource(id = R.drawable.settings_icon),
                                         modifier = androidx.compose.ui.Modifier.size(28.dp),
                                         contentDescription = "settings icon",
                                         tint = Color.Unspecified
                                     )
                             },
                             selected = StateSelected == StateSelectedEnum.SETTINGS,
                             onClick =  {OnStateSettingsChange()})//() Sehr wichtig, sonst wird die Funktion nicht aufgerufen, sondern nur weitergereicht
    }


}


enum class StateSelectedEnum{
    STUNDENPLAN,SETTINGS, UNSELECTED
}
