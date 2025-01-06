package bkb.stundenplan.app.ui


import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bkb.stundenplan.app.R


object MenuContent {

    @Composable
    fun LoadMenuContent(
        onStateSettingsChange: () -> Unit,//teil vor -> sind die parameter, der darauffolgende teil sind die Rückgabewerte
        onStateStundenplanChange: () -> Unit, stateSelected: StateSelectedEnum
    ) {
        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.stundenplan)) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.stundenplan),
                    modifier = androidx.compose.ui.Modifier.size(28.dp),
                    contentDescription = "settings icon",
                    tint = Color.Unspecified
                )
            },
            selected = stateSelected == StateSelectedEnum.STUNDENPLAN,
            onClick = { onStateStundenplanChange() })

        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.einstellungen)) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.settings_icon),
                    modifier = androidx.compose.ui.Modifier.size(28.dp),
                    contentDescription = "settings icon",
                    tint = Color.Unspecified
                )
            },
            selected = stateSelected == StateSelectedEnum.SETTINGS,
            onClick = { onStateSettingsChange() })
    }
}


enum class StateSelectedEnum {
    STUNDENPLAN, SETTINGS, UNSELECTED
}
