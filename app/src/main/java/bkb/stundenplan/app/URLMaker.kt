package bkb.stundenplan.app

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class URLMaker(private var viewModel: ViewModelStundenplanData) {

    @SuppressLint("AuthLeak")

    var urlStundenplan: MutableState<String> =
        mutableStateOf("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/")

    fun getBaseUrl(
    ): String {
        return "https://${
            if (viewModel.saveHandler.teacherMode && viewModel.saveHandler.valueLoginName.trim()
                        .isNotEmpty()
            ) viewModel.saveHandler.valueLoginName
            else "schueler"
        }:${
            if (viewModel.saveHandler.teacherMode && viewModel.saveHandler.valuePassword.trim()
                        .isNotEmpty()
            ) viewModel.saveHandler.valuePassword
            else "stundenplan"
        }@stundenplan.bkb.nrw/${
            if (viewModel.saveHandler.teacherMode && viewModel.saveHandler.valueLoginName.trim()
                        .isNotEmpty() && viewModel.saveHandler.valuePassword.trim().isNotEmpty()
            ) "lehrer"
            else "schueler"
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
            viewModel.saveHandler.valueDate.let { valueDate ->
                viewModel.saveHandler.valueElement.let { valueElement ->
                    if (viewModel.saveHandler.valueLoginName.trim()
                                .isNotEmpty() && viewModel.saveHandler.valuePassword.trim()
                                .isNotEmpty() && viewModel.saveHandler.teacherMode
                    ) {
                        viewModel.saveHandler.valueType.let { valueType ->
                            urlStundenplan.value =
                                "https://${viewModel.saveHandler.valueLoginName}:${viewModel.saveHandler.valuePassword}@stundenplan.bkb.nrw/lehrer/${
                                    calenderWeekAsString(
                                        valueDate
                                    )
                                }/${valueType}/${valueType}${numberOfElementAsString(valueElement)}.htm}"
                        }

                    } else if (!viewModel.saveHandler.teacherMode) {
                        urlStundenplan.value =
                            "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/${
                                calenderWeekAsString(
                                    valueDate
                                )
                            }/c/c${numberOfElementAsString(valueElement)}.htm"
                    }
                }
            }


        } catch (_: Exception) {
            println("URLMaker: Failed to update URL")
        }
    }
}