package com.example.bkbstundenplan

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Dialog

import com.example.bkbstundenplan.ui.StundenplanPage.DialogStateEnum
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import kotlinx.coroutines.Delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.lang.Thread.sleep
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale


@Parcelize
class StundenplanData(
    var loginName: @RawValue MutableState<String> = mutableStateOf("schueler"),
    var passwort: @RawValue MutableState<String> = mutableStateOf("stundenplan")
) : Parcelable {

    var ScrapingSelectBoxes: List<DocElement>? = null
        get() {

            if (field != null) {
                return field
            } else {
                 val job = runBlocking {  field = Scraping().getSelectBoxes() }


            return field
            }
        }
    var datesList: List<String>? = null
        get() {

            if (field != null) {
                return field
            } else {
                val job = runBlocking {  field = Scraping().getDates(ScrapingSelectBoxes) }


                return field
            }
        }
    var classList: List<String>? = null
        get() {

            if (field != null) {
                return field
            } else {
                val job = runBlocking {  field = Scraping().getClasses(ScrapingSelectBoxes) }


                return field
            }
        }

    init {

        GlobalScope.launch {
            ScrapingSelectBoxes = Scraping().getSelectBoxes()
            datesList = Scraping().getDates(ScrapingSelectBoxes)
            classList = Scraping().getClasses(ScrapingSelectBoxes)
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

                            if (datesList != null) {

                                    items(datesList!!.size) { index ->
                                        Text(text = datesList!![index])
                                    }



                            } else {
                                item {
                                    Text(text = "keine Daten vorhanden")
                                }
                            }
                        }

                    } else if (dialogState == DialogStateEnum.CLASS) {
                        Column {
                            Text(text = "Klasse")
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


