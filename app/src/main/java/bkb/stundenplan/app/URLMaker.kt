package bkb.stundenplan.app

import android.annotation.SuppressLint
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.STUNDENPLANLOGIN
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.STUNDENPLANPASSWORT
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.VERZEICHNISSNAMELEHRER
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.VERZEICHNISSNAMESCHUELER
import kotlinx.coroutines.flow.MutableStateFlow

class URLMaker(private var viewModel: ViewModelStundenplanData) {

    @SuppressLint("AuthLeak")

    var urlStundenplan: MutableStateFlow<String> =
        MutableStateFlow("https://$STUNDENPLANLOGIN:$STUNDENPLANPASSWORT@stundenplan.bkb.nrw/$VERZEICHNISSNAMESCHUELER/")

    private fun getBaseUrl(
    ): String {
        return "https://${
            if (viewModel.saveHandler.effectiveTeacherMode.value
            ) viewModel.saveHandler.valueLoginName.value
            else STUNDENPLANLOGIN
        }:${
            if (viewModel.saveHandler.effectiveTeacherMode.value
            ) viewModel.saveHandler.valuePassword.value
            else STUNDENPLANPASSWORT
        }@stundenplan.bkb.nrw/${
            if (viewModel.saveHandler.effectiveTeacherMode.value
            ) "$VERZEICHNISSNAMELEHRER/"
            else "$VERZEICHNISSNAMESCHUELER/"
        }"

    }//returns base url for example "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/"

    @SuppressLint("AuthLeak")
    fun updateURL() {


        fun calenderWeekAsString(week: Int): String {
            return when (week) {
                in 0..9 -> "0${week}"
                in 10..99 -> "$week"
                else -> week.toString()
            }
        }

        fun numberOfElementAsString(element: Int): String {
            return when (element) {
                in 0..9 -> "0000${element}"
                in 10..99 -> "000${element}"
                in 100..999 -> "00${element}"
                else -> "00000"
            }
        }

        try {
            with(viewModel.saveHandler) {

                if (effectiveTeacherMode.value
                ) {

                    urlStundenplan.value = "${getBaseUrl()}${
                        calenderWeekAsString(
                            valueDate.value
                        )
                    }/${effectiveValueType.value}/${effectiveValueType.value}${numberOfElementAsString(valueElement.value)}.htm"

                }
                else if (!effectiveTeacherMode.value) {
                    urlStundenplan.value = "${getBaseUrl()}${
                        calenderWeekAsString(
                            valueDate.value
                        )
                    }/c/c${numberOfElementAsString(valueElement.value)}.htm"
                }
            }

        }
        catch (_: Exception) {
            println("URLMaker: Failed to update URL")
        }
    }




}