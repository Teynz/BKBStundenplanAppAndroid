package com.example.bkbstundenplan

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkbstundenplan.ui.StundenplanPage.DialogStateEnum
import it.skrape.selects.DocElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class ViewModelStundenplanData: ViewModel() {

    var valueDates = MutableLiveData<Int>(0)
    var valueClasses= MutableLiveData<Int>(0)

    var experimentellerStundenplan =  MutableLiveData<Boolean>(false)
    var Darkmode=  MutableLiveData<Boolean>(false)

    @SuppressLint("AuthLeak")
    var urlStundenplan =  MutableLiveData<String>("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/")



    private var scrapingSelectBoxes: List<DocElement>? = null
        get() {

            if (field != null) {
                return field
            } else {
                runBlocking { field = Scraping().getSelectBoxes() }


                return field
            }
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
        CoroutineScope(Dispatchers.IO).launch {
            delay(15000)
            job.cancel()
        }

    }


    @Composable
    fun SelectionDialog(
        dialogState: DialogStateEnum,
        ondialogStateChange: (DialogStateEnum) -> Unit,
        urlStundenplan: String
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
                                            valueDates.value = it.key
                                            newURLStundenplan()
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
                                            valueClasses.value = it.key
                                            newURLStundenplan()
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

    private fun classAsString(): String? {
        if (valueClasses.value!! < 10)
            return "0${valueClasses.value}"
        else if (valueClasses.value!! > 9)
            return "${valueClasses.value}"
        return null
    }

    @SuppressLint("AuthLeak")
    fun newURLStundenplan(): String? {
        if (valueDates.value != 0 && valueClasses.value != 0) {
            urlStundenplan.value =
                "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/${valueDates.value}/c/c000${classAsString()!!}.htm"
            return urlStundenplan.value
        }
        return null
    }


    fun selectCurrentDate() {
        datesMap!!.forEach()
        {
            if (it.value == firstMondayofWeek()) {
                if (valueDates.value == 0)
                    valueDates.value = it.key
            }
        }
    }

}