package bkb.stundenplan.app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var loginName by mutableStateOf("Schueler")
    var loginPasswort by mutableStateOf("Schueler")
    var saveHandler = SaveHandler(context, viewModelScope, this)


    @SuppressLint("AuthLeak")
    var urlStundenplan: MutableState<String> =
        mutableStateOf("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/")

    @SuppressLint("AuthLeak")
    fun updateURLStundenplan() {
        fun classAsString(): String? {
            if (saveHandler.valueClasses < 10) return "0${saveHandler.valueClasses}"
            else if (saveHandler.valueClasses > 9) return "${saveHandler.valueClasses}"
            return null
        }
        if (saveHandler.valueDates != 0 && saveHandler.valueClasses != 0) {
            urlStundenplan.value =
                "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/${saveHandler.valueDates}/c/c000${classAsString()}.htm"
        }
    }


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
            tablesScraped.value = Scraping().getStundenplanTable(urlStundenplan.value)
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


    var classMap: Map<Int, String>? = null
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
            val first = async { classMap = Scraping().getClassesMap(scrapingSelectBoxes) }
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
                if (saveHandler.valueDates == 0) saveHandler.valueDates = it.key
            }
            updateURLStundenplan()
            if (saveHandler.experimentellerStundenplan) {
                updateTablesScraped()
            }
        }
    }
}