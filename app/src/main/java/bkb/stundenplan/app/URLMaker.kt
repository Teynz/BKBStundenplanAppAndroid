package bkb.stundenplan.app

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.STUNDENPLANLOGIN
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.STUNDENPLANPASSWORT
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.VERZEICHNISSNAMELEHRER
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.VERZEICHNISSNAMESCHUELER

class URLMaker(private var viewModel: ViewModelStundenplanData) {

    @SuppressLint("AuthLeak")

    var urlStundenplan: MutableState<String> =
        mutableStateOf("https://$STUNDENPLANLOGIN:$STUNDENPLANPASSWORT@stundenplan.bkb.nrw/$VERZEICHNISSNAMESCHUELER/")

    private fun getBaseUrl(
    ): String {
        return "https://${if (!viewModel.saveHandler.experimentellerStundenplan)"${
            if (viewModel.saveHandler.effectiveTeacherMode
            ) viewModel.saveHandler.valueLoginName
            else STUNDENPLANLOGIN
        }:${
            if (viewModel.saveHandler.effectiveTeacherMode
            ) viewModel.saveHandler.valuePassword
            else STUNDENPLANPASSWORT
        }@" else ""}stundenplan.bkb.nrw/${
            if (viewModel.saveHandler.effectiveTeacherMode
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

                if (effectiveTeacherMode
                ) {

                    urlStundenplan.value = "${getBaseUrl()}${
                        calenderWeekAsString(
                            valueDate
                        )
                    }/${effectiveValueType}/${effectiveValueType}${numberOfElementAsString(valueElement)}.htm"

                }
                else if (!effectiveTeacherMode) {
                    urlStundenplan.value = "${getBaseUrl()}${
                        calenderWeekAsString(
                            valueDate
                        )
                    }/c/c${numberOfElementAsString(valueElement)}.htm"
                }
            }

        }
        catch (_: Exception) {
            println("URLMaker: Failed to update URL")
        }
    }
}