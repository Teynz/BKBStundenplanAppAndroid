@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package bkb.stundenplan.app

import android.app.UiModeManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SaveHandler(
    private val context: Context,
    private val scope: CoroutineScope,
    val viewModel: ViewModelStundenplanData
) {
    companion object SaveNames {
        const val SETTINGS = "settings"
        const val DARKMODE = "darkmode"
        const val ADAPTIVECOLOR = "adaptiveColor"
        const val EXPERIMENTELLERSTUNDENPLAN = "ExperimentellerStundenplan"
        const val ALTESTUNDENPLENE = "AlteStundenplaene"
        const val STUNDENPLANPADDING = "StundenplanPadding"

        const val TEACHERMODE = "TeacherMode"
        const val LOGINNAME = "LoginName"
        const val PASSWORD = "Password"


        const val VALUES = "values"
        const val VALUEDATE = "ValueDate"
        const val VALUETYPE = "ValueType"
        const val VALUEELEMENT = "ValueElement"
    }


    private val Context.dataStoreSettings: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)
    private val Context.dataStoreValues: DataStore<Preferences> by preferencesDataStore(name = VALUES)

    private suspend fun <T> getPreference(
        dataStore: DataStore<Preferences>, key: Preferences.Key<T>, defaultValue: T
    ): T {
        return dataStore.data.map { preferences -> preferences[key] ?: defaultValue }.first()
    }

    private fun <T> savePreference(
        dataStore: DataStore<Preferences>, key: Preferences.Key<T>, value: T
    ) {
        scope.launch {
            dataStore.edit { preferences -> preferences[key] = value }
        }
    }


    var darkmode: Boolean by mutableStateOf(getDarkModeSave())
    fun getDarkModeSave(): Boolean {
        return runBlocking {


            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            val mode = uiModeManager.nightMode
            if (mode == UiModeManager.MODE_NIGHT_NO) {
                getPreference(context.dataStoreSettings, booleanPreferencesKey(DARKMODE), false)
            }
            else {
                getPreference(context.dataStoreSettings, booleanPreferencesKey(DARKMODE), true)
            }/*todo add as system*/

        }
    }

    fun saveDarkMode(value: Boolean) {
        darkmode = value
        savePreference(context.dataStoreSettings, booleanPreferencesKey(DARKMODE), value)
    }


    var adaptiveColor: Boolean by mutableStateOf(getAdaptiveColorSave())
    fun getAdaptiveColorSave(): Boolean {
        return runBlocking {
            getPreference(context.dataStoreSettings, booleanPreferencesKey(ADAPTIVECOLOR), true)
        }
    }

    fun saveAdaptiveColor(value: Boolean) {
        adaptiveColor = value
        savePreference(context.dataStoreSettings, booleanPreferencesKey(ADAPTIVECOLOR), value)
    }

    var experimentellerStundenplan by mutableStateOf(getExperimentellerStundenplanSave())
    private fun getExperimentellerStundenplanSave(): Boolean {
        return runBlocking {
            getPreference(
                context.dataStoreSettings, booleanPreferencesKey(EXPERIMENTELLERSTUNDENPLAN), true
            )
        }
    }

    fun saveExperimentellerStundenplan(value: Boolean) {
        experimentellerStundenplan = value
        savePreference(
            context.dataStoreSettings, booleanPreferencesKey(EXPERIMENTELLERSTUNDENPLAN), value
        )
    }

    var alteStundenplaene by mutableStateOf(getAlteStundenplaeneSave())
    private fun getAlteStundenplaeneSave(): Boolean {
        return runBlocking {
            getPreference(context.dataStoreSettings, booleanPreferencesKey(ALTESTUNDENPLENE), true)
        }
    }

    fun saveAlteStundenplaene(value: Boolean) {
        alteStundenplaene = value
        savePreference(context.dataStoreSettings, booleanPreferencesKey(ALTESTUNDENPLENE), value)
    }


    var teacherMode by mutableStateOf(getTeacherModeSave())
        private set

    private fun getTeacherModeSave(): Boolean {
        return runBlocking {
            getPreference(context.dataStoreSettings, booleanPreferencesKey(TEACHERMODE), false)
        }
    }

    fun saveTeacherMode(value: Boolean) {
        teacherMode = value
        savePreference(context.dataStoreSettings, booleanPreferencesKey(TEACHERMODE), value)
        scope.launch {
            viewModel.viewModelInit()
        }
    }


    var valueLoginName by mutableStateOf(getValueLoginNameSave())
        private set

    private fun getValueLoginNameSave(): String {
        return runBlocking {
            getPreference(context.dataStoreValues, stringPreferencesKey(LOGINNAME), "")
        }
    }

    fun saveLoginName(value: String) {
        valueLoginName = value
        savePreference(context.dataStoreValues, stringPreferencesKey(LOGINNAME), value)
        scope.launch {
            viewModel.viewModelInit()
        }

    }

    var valuePassword by mutableStateOf(getValuePasswordSave())
        private set

    private fun getValuePasswordSave(): String {
        return runBlocking {
            getPreference(context.dataStoreValues, stringPreferencesKey(PASSWORD), "")
        }
    }

    fun savePassword(value: String) {
        valuePassword = value
        savePreference(context.dataStoreValues, stringPreferencesKey(PASSWORD), value)
        scope.launch {
            viewModel.viewModelInit()
        }

    }


    var valueDate by mutableIntStateOf(getValueDateSave())
    private fun getValueDateSave(): Int {
        return runBlocking {
            getPreference(context.dataStoreValues, intPreferencesKey(VALUEDATE), 0)
        }
    }

    fun saveValueDate(value: Int) {
        valueDate = value
        scope.launch {
            savePreference(context.dataStoreValues, intPreferencesKey(VALUEDATE), value)
        }
    }

    var valueType by mutableStateOf(getValueTypeSave())

    private fun getValueTypeSave(): String {
        var value:String
        runBlocking {
            value = getPreference(context.dataStoreValues, stringPreferencesKey(VALUETYPE), "c")
        }

        return if(teacherMode) value else ParameterWhichMayChangeOverTime.CLASSES_SHORT

    }

    fun saveValueType(value: String) {
        valueType = value
        scope.launch {
            savePreference(context.dataStoreValues, stringPreferencesKey(VALUETYPE), value)
        }
    }
    val effectiveValueType: String
        get() = if (teacherMode) valueType else ParameterWhichMayChangeOverTime.CLASSES_SHORT

    var valueElement by mutableIntStateOf(getValueElementSave())
    private fun getValueElementSave(): Int {
        return runBlocking {
            getPreference(context.dataStoreValues, intPreferencesKey(VALUEELEMENT), 0)
        }
    }

    fun saveValueElement(value: Int) {
        valueElement = value
        scope.launch {
            savePreference(context.dataStoreValues, intPreferencesKey(VALUEELEMENT), value)
        }
    }


    var saveHandlerInitJob: CompletableJob = Job()

    init {
        scope.launch {
            val teacherModeDeferred = async { getTeacherModeSave() }
            val valueLoginNameDeferred = async { getValueLoginNameSave() }
            val valuePasswordDeferred = async { getValuePasswordSave() }
            val valueDateDeferred = async { getValueDateSave() }
            val valueTypeDeferred = async { getValueTypeSave() }
            val valueElementDeferred = async { getValueElementSave() }

            val darkModeDeferred = async { getDarkModeSave() }
            val adaptiveColorDeferred = async { getAdaptiveColorSave() }
            val experimentellerStundenplanDeferred = async { getExperimentellerStundenplanSave() }

            val alteStundenplaeneDeferred = async { getAlteStundenplaeneSave() }



            teacherMode = teacherModeDeferred.await()
            valueLoginName = valueLoginNameDeferred.await()
            valuePassword = valuePasswordDeferred.await()
            valueDate = valueDateDeferred.await()
            valueType = valueTypeDeferred.await()
            valueElement = valueElementDeferred.await()


            alteStundenplaene = alteStundenplaeneDeferred.await()
            darkmode = darkModeDeferred.await()
            adaptiveColor = adaptiveColorDeferred.await()
            experimentellerStundenplan = experimentellerStundenplanDeferred.await()







            saveHandlerInitJob.complete()
        }
    }

}