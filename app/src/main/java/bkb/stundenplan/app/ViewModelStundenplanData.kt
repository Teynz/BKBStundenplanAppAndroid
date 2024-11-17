package bkb.stundenplan.app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.skrape.selects.DocElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class ViewModelStundenplanData(context: Context) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var context = context
    var urlMaker = URLMaker(this)
    var saveHandler = SaveHandler(context, viewModelScope, this)
    var Scraping = Scraping()







    private var scrapingSelectBoxes: List<DocElement>? = null
        get() {

            if (field != null) {
                return field
            } else {
                runBlocking { field = Scraping().getSelectBoxes() }
                return field
            }
        }


    var tablesScraped: MutableState<DocElement?> = mutableStateOf(null)

    @Suppress("MemberVisibilityCanBePrivate")
    var tableJob = Job()
    fun updateTablesScraped() {
        CoroutineScope(Dispatchers.IO).launch {
            tablesScraped.value = Scraping().getStundenplanTable(urlMaker.urlStundenplan.value)
            tableJob.complete()
        }
    }


    var datesMap: Map<Int, String>? = null
        get() {
            if (field != null) {
                return field
            } else {
                runBlocking(Dispatchers.IO) {
                    field = Scraping().getDatesMap(scrapingSelectBoxes)
                }
                return field
            }
        }

    /*
    var typesMap: Map<Int, String>? = null
        get() {
            if (field != null) {
                return field
            } else {
                runBlocking(Dispatchers.IO) {
                    field = Scraping().getTypesMap(scrapingSelectBoxes)
                }
                return field
            }
        }

*/


    var elementMap: Map<Int, String>? = null
        get() {
            if (field != null) {
                return field
            } else {
                runBlocking(Dispatchers.IO) {
                    field = Scraping().getClassesMap(scrapingSelectBoxes)
                }
                return field
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    val viewModelInitJob = Job()

    init {
        CoroutineScope(Dispatchers.IO + viewModelInitJob).launch {
            scrapingSelectBoxes = Scraping().getSelectBoxes()
            val first = async { elementMap = Scraping().getClassesMap(scrapingSelectBoxes) }
            val second =
                async { selectCurrentDate() }//this function also creates the datesMap because of the getter function
            first.await()
            second.await()
            viewModelInitJob.complete()
        }
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
        datesMap!!.forEach() {
            if (it.value == firstMondayofWeek()) {
                if (saveHandler.valueDate == 0) saveHandler.valueDate = it.key
            }
            urlMaker.updateURL()
            if (saveHandler.experimentellerStundenplan) {
                updateTablesScraped()
            }
        }
    }
}