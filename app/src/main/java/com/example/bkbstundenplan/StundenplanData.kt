package com.example.bkbstundenplan

import android.os.Parcelable
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
    var valueDates: MutableState<Int> = mutableStateOf(0)

    var valueClasses: MutableState<Int> = mutableStateOf(0)
    var URLStundenplan:MutableState<String> = mutableStateOf("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/")

    var ScrapingSelectBoxes: List<DocElement>? = null
        get() {

            if (field != null) {
                return field
            } else {
                runBlocking { field = Scraping().getSelectBoxes() }


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
        ondialogStateChange: (DialogStateEnum) -> Unit,
        URLStundenplan: String
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
                                        Button(onClick = { valueDates.value = it.key
                                            UpdateURLStundenplan()})
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
                        LazyColumn() {

                            if (classMap != null) {
                                classMap!!.forEach()
                                {
                                    item {
                                        Button(onClick = { valueClasses.value = it.key
                                            UpdateURLStundenplan()})
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

    fun classAsString(): MutableState<String?>? {
        var stringClasses: MutableState<String?>? = null
        if (valueClasses == null)
            {
            return null
            }


        if (valueDates?.value != null && valueClasses?.value != null) {
            if (valueClasses!!.value!!.toInt() < 10)
                stringClasses = mutableStateOf("0${valueClasses.value}")
            else if (valueClasses!!.value!!.toInt() > 9)
                stringClasses = mutableStateOf("${valueClasses.value}")
        }

        return stringClasses ?: null
    }

    fun UpdateURLStundenplan() {

        if (valueDates?.value != null && valueClasses?.value != null) {
            URLStundenplan.value =
                "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/${valueDates.value}/c/c000${classAsString()!!.value}.htm"

        }


    }


}


/*          This is just a plain copy paste from the website js code

*
function n2str(nr)
{
	var str = nr.toString();
	while (str.length < 5) str = "0" + str;
	return(str);
}


function doDisplayTimetable(Form, topDir) {
    if (Form.element.selectedIndex < 0)
        return;
    var week = Form.week[Form.week.selectedIndex].value;
    var type = Form.type[Form.type.selectedIndex].value;
    var FileName = type + n2str(Form.element[Form.element.selectedIndex].value) + ".htm";
    var url;
    if (topDir == "w")
        url =  week + "/" + type + "/" + FileName;
    else
        url =  type + "/" + week + "/" + FileName;
    //parent.main.location = url; In Firefox it is not allowed to just set the location (CORS issue)
    //Instead use postMessage to tell the parent that it should change its location
    parent.postMessage(url, '*');
}
*
* */


