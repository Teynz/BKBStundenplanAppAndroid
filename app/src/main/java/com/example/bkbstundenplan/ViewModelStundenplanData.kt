package com.example.bkbstundenplan

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bkbstundenplan.ui.StundenplanPage.DialogStateEnum
import it.skrape.selects.DocElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


//help can be found here: https://developer.android.com/topic/libraries/architecture/viewmodel#kotlin

class ViewModelStundenplanData(val context: Context) : ViewModel() {



    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")




    private fun getDarkModeSave(): Boolean {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            preferences[booleanPreferencesKey("darkmode")] ?: true
        }
    }
    var darkmode: Boolean by mutableStateOf(getDarkModeSave())
    fun updateDarkMode(value: Boolean) {
        darkmode = value
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                settings[booleanPreferencesKey("darkmode")] = value
            }
        }
    }




    private fun getExperimentellerStundenplanSave(): Boolean {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            preferences[booleanPreferencesKey("ExperimentellerStundenplan")] ?: false
        }
    }
    var experimentellerStundenplan by mutableStateOf(getExperimentellerStundenplanSave())
    fun updateExperimentellerStundenplan(value: Boolean) {
        experimentellerStundenplan = value
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                settings[booleanPreferencesKey("ExperimentellerStundenplan")] = value
            }
        }
    }






    var valueDates by mutableIntStateOf(0)
    var valueClasses by mutableIntStateOf(0)




    var urlStundenplan: MutableState<String> = mutableStateOf("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/")
    @SuppressLint("AuthLeak")
    fun updateURLStundenplan(){
        fun classAsString(): String? {
            if (valueClasses < 10)
                return "0${valueClasses}"
            else if (valueClasses > 9)
                return "$valueClasses"
            return null
        }
        if (valueDates != 0 && valueClasses != 0) {



            urlStundenplan.value = "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/${valueDates}/c/c000${classAsString()}.htm"
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
    fun updateTablesScraped() {
        CoroutineScope(Dispatchers.IO).launch { tablesScraped.value = Scraping().getTables(urlStundenplan.value) }
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

    init {


            val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {
            scrapingSelectBoxes = Scraping().getSelectBoxes()
            classMap = Scraping().getClassesMap(scrapingSelectBoxes)
            selectCurrentDate()//this function also creates the datesMap because of the getter function
        }



        /*CoroutineScope(Dispatchers.IO).launch {
            delay(15000)
            job.cancel()
        }*/

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
                                            valueDates = it.key
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
                                            valueClasses = it.key
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
                if (valueDates == 0)
                    valueDates = it.key
            }
        }
    }

}