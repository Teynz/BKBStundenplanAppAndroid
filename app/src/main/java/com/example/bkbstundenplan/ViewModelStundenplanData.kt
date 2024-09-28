package com.example.bkbstundenplan

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bkbstundenplan.ui.StundenplanPage.DialogStateEnum
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


//help can be found here: https://developer.android.com/topic/libraries/architecture/viewmodel#kotlin

class ViewModelStundenplanData(val context: Context) : ViewModel() {

    var loginName by mutableStateOf("Schueler")
    var loginPasswort by mutableStateOf("Schueler")
    var saveHandler = SaveHandler(context, viewModelScope,this)







    @SuppressLint("AuthLeak")
    var urlStundenplan: MutableState<String> = mutableStateOf("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/")
    @SuppressLint("AuthLeak")
    fun updateURLStundenplan(){
        fun classAsString(): String? {
            if (saveHandler.valueClasses < 10)
                return "0${saveHandler.valueClasses}"
            else if (saveHandler.valueClasses > 9)
                return "${saveHandler.valueClasses}"
            return null
        }
        if (saveHandler.valueDates != 0 && saveHandler.valueClasses != 0) {



            urlStundenplan.value = "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/${saveHandler.valueDates}/c/c000${classAsString()}.htm"
            //"https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/${valueDates}/c/c000${classAsString()}.htm"


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

    var tablesScraped: MutableState<Scraping.Stundenplan?> = mutableStateOf(null)
    var tableJob = Job()
    fun updateTablesScraped() {
        CoroutineScope(Dispatchers.IO ).launch { tablesScraped.value = Scraping().getTables(urlStundenplan.value)
        tableJob.complete()}
    }



    private var datesMap: Map<Int, String>? = null
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


    private var classMap: Map<Int, String>? = null
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

    val viewModelInitJob = Job()
    init {
        CoroutineScope(Dispatchers.IO + viewModelInitJob).launch {
            scrapingSelectBoxes = Scraping().getSelectBoxes()
            val first =async {classMap = Scraping().getClassesMap(scrapingSelectBoxes)}
            val second =async {selectCurrentDate()}//this function also creates the datesMap because of the getter function
            first.await()
            second.await()
            viewModelInitJob.complete()
        }
    }

    @Composable
    fun SelectionDialog(
        dialogState: DialogStateEnum,
        ondialogStateChange: (DialogStateEnum) -> Unit
    ) {
        if (dialogState == DialogStateEnum.DATE || dialogState == DialogStateEnum.CLASS) {
            Dialog(onDismissRequest = { ondialogStateChange(DialogStateEnum.NONE) },
                content = {
                    @Suppress("KotlinConstantConditions")
                    if (dialogState == DialogStateEnum.DATE) {
                        LazyColumn {
                            if (datesMap != null) {
                                datesMap!!.forEach()
                                {
                                    item {
                                        Button(onClick = {
                                            saveHandler.valueDates = it.key
                                            updateURLStundenplan()
                                            ondialogStateChange(DialogStateEnum.NONE)
                                        })
                                        {
                                            Text(text = datesMap!!.getValue(it.key))
                                        }
                                    }
                                }
                            } else {
                                item { Text(text = "keine Daten vorhanden") }
                            }
                        }
                    } else if (dialogState == DialogStateEnum.CLASS) {
                        LazyColumn {

                            if (classMap != null) {
                                classMap!!.forEach()
                                {
                                    item {
                                        Button(onClick = {
                                            saveHandler.saveValueClasses(it.key)
                                            updateURLStundenplan()
                                            ondialogStateChange(DialogStateEnum.NONE)
                                        })
                                        {
                                            Text(text = classMap!!.getValue(it.key))
                                        }
                                    }
                                }
                            } else {
                                item { Text(text = "keine Daten vorhanden") }
                            }
                        }
                    }
                }
            )
        }

    }


    @SuppressLint("NewApi")
    fun firstMondayofWeek(): String {
        val now = LocalDate.now()
        val fieldISO = WeekFields.of(Locale.GERMANY).dayOfWeek()
        return (now.with(
            fieldISO,
            1
        )).format(DateTimeFormatter.ofPattern("d.M.yyyy"))


    }






    private fun selectCurrentDate() {
        datesMap!!.forEach()
        {
            if (it.value == firstMondayofWeek()) {
                if (saveHandler.valueDates == 0)
                    saveHandler.valueDates = it.key
            }
            updateURLStundenplan()
            if (saveHandler.experimentellerStundenplan == true){updateTablesScraped()}
        }
    }

}