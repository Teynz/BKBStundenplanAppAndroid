package bkb.stundenplan.app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class ViewModelStundenplanData(context: Context) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var urlMaker = URLMaker(this)
    var saveHandler = SaveHandler(context, viewModelScope, this)
    var scraping = ScrapingJSoup()
    var portraitMode = mutableStateOf(true)
    var heightTopAppBar = mutableStateOf(80.dp)
    var isPortrait by mutableStateOf(true)


    @Suppress("MemberVisibilityCanBePrivate")

    fun viewModelInit() {

        urlMaker.updateURL()
        scraping.myInit(
            saveHandler.teacherMode,
            saveHandler.valueLoginName,
            saveHandler.valuePassword,
            urlMaker.urlStundenplan.value
        )

        selectCurrentDate()


    }

    init {
        viewModelInit()

    }


    @SuppressLint("NewApi")
    fun firstMondayofWeek(): String {
        val now = LocalDate.now()
        val fieldISO = WeekFields.of(Locale.GERMANY).dayOfWeek()
        return (now.with(
            fieldISO, 1
        )).format(DateTimeFormatter.ofPattern("d.M.yyyy"))
    }

    private fun selectCurrentDate() {

        scraping.datesPairMap?.second?.forEach {
            if (it.value == firstMondayofWeek()) {
                saveHandler.valueDate = it.key
            }
            urlMaker.updateURL()

        }
    }
}