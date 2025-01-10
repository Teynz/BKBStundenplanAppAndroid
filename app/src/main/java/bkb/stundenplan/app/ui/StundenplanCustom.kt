package bkb.stundenplan.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bkb.stundenplan.app.ViewModelStundenplanData
import bkb.stundenplan.app.getWeek

class StundenplanCustom {

    @Composable
    fun StundenplanCompose(modifier: Modifier, viewModel: ViewModelStundenplanData)
    {
        var week = viewModel.scraping.stundenplanSite?.getWeek()




    }

}