package bkb.stundenplan.app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class ViewModelStundenplanData(context: Context) : ViewModel() {
    data class CombinedState(
        val teacherMode: Boolean,
        val valueDate: Int,
        val valueType: String,
        val valueElement: Int,
        val loginName: String,
        val password: String
    )

    private var previousState: CombinedState? = null

    @SuppressLint("StaticFieldLeak")
    var urlMaker = URLMaker(this)
    var saveHandler = SaveHandler(context, viewModelScope, this)
    val scraping by lazy {
        ScrapingJSoup(
            saveHandler.effectiveTeacherMode,
            saveHandler.valueLoginName,
            saveHandler.valuePassword,
            urlMaker.urlStundenplan
        )
    }
    var isPortrait by mutableStateOf(true)

    val week = scraping._stundenplanSite.map()
    {

        it?.getWeek()?.mergeAndRemoveRedundantAll(saveHandler.mergeCells.value,true)


    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    @Suppress("MemberVisibilityCanBePrivate")

    fun viewModelInit() {

        urlMaker.updateURL()
        scraping.smartUpdate(true, saveHandler.experimentellerStundenplan.value)

    }

    private fun tablesStateBackground()//Execute when either teacherMode, valueDate, valueType, valueElement, LoginName or Password changes
    {


        viewModelScope.launch {
            previousState = CombinedState(
                teacherMode = saveHandler.teacherMode.value,
                valueDate = saveHandler.valueDate.value,
                valueType = saveHandler.effectiveValueType.value,
                valueElement = saveHandler.valueElement.value,
                loginName = saveHandler.valueLoginName.value,
                password = saveHandler.valuePassword.value
            )

            combine(
                saveHandler.effectiveTeacherMode,
                saveHandler.valueDate,
                saveHandler.effectiveValueType,
                saveHandler.valueElement,
                saveHandler.valueLoginName,
                saveHandler.valuePassword
            ) {
                CombinedState(
                    teacherMode = saveHandler.effectiveTeacherMode.value,
                    valueDate = saveHandler.valueDate.value,
                    valueType = saveHandler.effectiveValueType.value,
                    valueElement = saveHandler.valueElement.value,
                    loginName = saveHandler.valueLoginName.value,
                    password = saveHandler.valuePassword.value
                )
            }.collect { newState ->
                urlMaker.updateURL()
                scraping.smartUpdate(
                    newState.teacherMode != previousState?.teacherMode || newState.loginName != previousState?.loginName || newState.password != previousState?.password,
                    saveHandler.experimentellerStundenplan.value
                )

                previousState = newState
            }

        }
    }


    init {
        viewModelInit()

        viewModelScope.launch {
            scraping.datesPairMap.first { pair ->
                if (pair != null) {
                    selectCurrentDate()
                    true // Coroutine wird beendet
                }
                else {
                    false
                }
            }
        }
        tablesStateBackground()

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

        scraping.datesPairMap.value?.second?.forEach {
            if (it.value == firstMondayofWeek()) {
                saveHandler.saveValueDate(it.key)
            }
            urlMaker.updateURL()

        }
    }
}