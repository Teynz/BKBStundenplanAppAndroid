package bkb.stundenplan.app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
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
    var urlMaker = URLMaker(this)
    var saveHandler = SaveHandler(context, viewModelScope, this)
    private var scraping = Scraping()
    var portraitMode = mutableStateOf(true)
    var heightTopAppBar = mutableStateOf(80.dp)




    var isPortrait by mutableStateOf(true)


    var TypesMapsObject: MutableState<Scraping.TypeArrays?> = mutableStateOf(null)

    suspend fun updateTypesMapsObject() {
        val URL = URLMaker(this).getBaseUrl() + "frames/navbar.htm"
        TypesMapsObject.value = scraping.extractVariables(scraping.navbarHTML(URL).toString())

    }

    var scrapingSelectBoxes: List<DocElement>? = null
        get() {

            if (field != null) {
                return field
            }
            else {
                runBlocking { field = scraping.getSelectBoxes() }
                return field
            }
        }


    var datesPairMap: Pair<String?, Map<Int, String>?>? = null
        get() {
            if (field != null) {
                return field
            }
            else {
                runBlocking(Dispatchers.IO) {
                    field = scraping.getDatesPairMap(scrapingSelectBoxes)
                }
                return field
            }
        }


    var typesMap: MutableState<Pair<String?, Map<String, String>?>?> = mutableStateOf(null)
        get() {
            if (field.value != null) {
                return field
            }
            else {
                runBlocking(Dispatchers.IO) {
                    field.value = Scraping().getTypesPairMap(scrapingSelectBoxes)
                }
                return field
            }
        }


    var elementMap: Pair<String?, Map<Int, String>?>? = null
        get() {
            if (field != null) {
                return field
            }
            else {
                runBlocking(Dispatchers.IO) {
                    field = scraping.getElementsPairMap(scrapingSelectBoxes)
                }
                return field
            }
        }


    var tablesScraped: MutableState<DocElement?> = mutableStateOf(null)

    @Suppress("MemberVisibilityCanBePrivate")
    var tableJob = Job()
    fun updateTablesScraped() {
        CoroutineScope(Dispatchers.IO).launch {
            tablesScraped.value = scraping.getStundenplanTable(urlMaker.urlStundenplan.value)
            tableJob.complete()
        }
    }


    @Suppress("MemberVisibilityCanBePrivate")

    fun viewModelInit() {
        CoroutineScope(Dispatchers.IO).launch {
            urlMaker.updateURL()
            scrapingSelectBoxes = scraping.getSelectBoxes(
                saveHandler.teacherMode, saveHandler.valueLoginName, saveHandler.valuePassword
            )

            val typesMap = async {
                typesMap.value = Scraping().getTypesPairMap(scrapingSelectBoxes)
            }


            val initElementMap = async {
                if (saveHandler.teacherMode && saveHandler.valueLoginName.trim()
                            .isNotEmpty() && saveHandler.valuePassword.trim().isNotEmpty()
                ) {
                    updateTypesMapsObject()
                    elementMap = Pair(
                        "Art", ParameterWhichMayChangeOverTime.selectType(
                            saveHandler.effectiveValueType, TypesMapsObject.value
                        )
                    )
                    elementMap?.let { if (it.second == null) elementMap = null }

                }

                if (elementMap == null) {
                    elementMap = scraping.getElementsPairMap(scrapingSelectBoxes)

                }
            }


            val initDatesMap =
                async { selectCurrentDate() }//this function also creates the datesMap because of the getter function
            //val typesMapObjectDeffered= async {scraping.extractVariables(scraping.navbarHTML(URLMaker(this).getBaseUrl()).toString())}
            initElementMap.await()
            initDatesMap.await()
            typesMap.await()
            urlMaker.updateURL()
            // updateTypesMapsObject()

        }

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

        datesPairMap?.second?.forEach {
            if (it.value == firstMondayofWeek()) {
                saveHandler.valueDate = it.key
            }
            urlMaker.updateURL()
            if (saveHandler.experimentellerStundenplan) {
                updateTablesScraped()
            }
        }
    }
}