package com.example.bkbstundenplan

import android.os.Parcelable
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Dialog
import com.example.bkbstundenplan.ui.StundenplanPage.DialogStateEnum
import it.skrape.selects.DocElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
class StundenplanData(
    var loginName: @RawValue MutableState<String> = mutableStateOf("schueler"),
    var passwort: @RawValue MutableState<String> = mutableStateOf("stundenplan")
) : Parcelable {
    var valueDates: MutableState<Int>? = null
    var valueClasses: MutableState<Int>? = null

    var ScrapingSelectBoxes: List<DocElement>? = null
        get() {

            if (field != null) {
                return field
            } else {
                val job = runBlocking { field = Scraping().getSelectBoxes() }


                return field
            }
        }
    var datesMap: Map<Int, String>? = null
        get() {

            if (field != null) {
                return field
            } else {
                val job = runBlocking(Dispatchers.IO) {
                    field = Scraping().getDatesMap(ScrapingSelectBoxes)
                }


                return field
            }
        }
    var classMap: Map<Int, String>? = null
        get() {

            if (field != null) {
                return field
            } else {
                val job = runBlocking(Dispatchers.IO) {
                    field = Scraping().getClassesMap(ScrapingSelectBoxes)
                }


                return field
            }
        }

    init {

        runBlocking(Dispatchers.IO) {
            ScrapingSelectBoxes = Scraping().getSelectBoxes()
            datesMap = Scraping().getDatesMap(ScrapingSelectBoxes)
            classMap = Scraping().getClassesMap(ScrapingSelectBoxes)
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
                    if (dialogState == DialogStateEnum.DATE) {
                        LazyColumn() {
                            if (datesMap != null) {
                                datesMap!!.forEach()
                                {
                                    item {
                                        Button(onClick = { valueDates = mutableStateOf(it.key) })
                                        {
                                            Text(text = datesMap!!.getValue(it.key))
                                        }
                                    }
                                }
                            } else {item {Text(text = "keine Daten vorhanden")}}
                        }
                    } else if (dialogState == DialogStateEnum.CLASS) {
                        LazyColumn() {

                            if (classMap != null) {
                                classMap!!.forEach()
                                {
                                    item {
                                        Button(onClick = { valueClasses = mutableStateOf(it.key) })
                                        {
                                            Text(text = classMap!!.getValue(it.key))
                                        }
                                    }
                                }
                            } else {item {Text(text = "keine Daten vorhanden")}}
                        }
                    }


                }

            )
        }

    }


    /*@RequiresApi(Build.VERSION_CODES.O)
      fun firstMondayofWeek(): LocalDate
      {
          val now = LocalDate.now()
          val fieldISO = WeekFields.of(Locale.GERMANY).dayOfWeek()
          return (now.with(
                  fieldISO,
                  1
                          ))


      }*/
}


