@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package bkb.stundenplan.app

import android.app.UiModeManager
import android.content.Context
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SaveHandler(
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: ViewModelStundenplanData
) {
    companion object SaveNames {
        const val SETTINGS = "settings"
        const val DARKMODE = "darkmode"
        const val ADAPTIVECOLOR = "adaptiveColor"
        const val EXPERIMENTELLERSTUNDENPLAN = "ExperimentellerStundenplan"
        const val STUNDENPLANZOOM = "StundenplanZoom"
        const val FANCYSTUNDENPLAN = "FancyStundenplan"
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
            }

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

    private val _experimentellerStundenplan = MutableStateFlow(getExperimentellerStundenplanSave())
    val experimentellerStundenplan = _experimentellerStundenplan.asStateFlow()

    private fun getExperimentellerStundenplanSave(): Boolean {
        return runBlocking {
            getPreference(
                context.dataStoreSettings, booleanPreferencesKey(EXPERIMENTELLERSTUNDENPLAN), true
            )
        }
    }

    fun saveExperimentellerStundenplan(value: Boolean) {
        _experimentellerStundenplan.value = value
        savePreference(
            context.dataStoreSettings, booleanPreferencesKey(EXPERIMENTELLERSTUNDENPLAN), value
        )
    }

    private val _stundenplanZoom = MutableStateFlow(getStundenplanZoomSave())
    val stundenplanZoom = _stundenplanZoom.asStateFlow()


    private fun getStundenplanZoomSave(): Boolean {
        return runBlocking {
            getPreference(
                context.dataStoreSettings, booleanPreferencesKey(STUNDENPLANZOOM), false
            )
        }
    }

    fun saveStundenplanZoom(value: Boolean) {
        _stundenplanZoom.value = value
        savePreference(
            context.dataStoreSettings, booleanPreferencesKey(STUNDENPLANZOOM), value
        )
    }




    private val _fancyStundenplan = MutableStateFlow(getFancyStundenplanSave())
    val fancyStundenplan = _fancyStundenplan.asStateFlow()


    private fun getFancyStundenplanSave(): Boolean {
        return runBlocking {
            getPreference(
                context.dataStoreSettings, booleanPreferencesKey(EXPERIMENTELLERSTUNDENPLAN), false
            )
        }
    }

    fun saveFancyStundenplan(value: Boolean) {
        _fancyStundenplan.value = value
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

    private val _valueLoginName = MutableStateFlow(getValueLoginNameSave())
    val valueLoginName: StateFlow<String> = _valueLoginName.asStateFlow()

    private fun getValueLoginNameSave(): String {
        return runBlocking {
            getPreference(context.dataStoreValues, stringPreferencesKey(LOGINNAME), "")
        }
    }

    fun saveLoginName(value: String) {
        _valueLoginName.value = value
        savePreference(context.dataStoreValues, stringPreferencesKey(LOGINNAME), value)
    }


    private val _valuePassword = MutableStateFlow(getValuePasswordSave())
    val valuePassword: StateFlow<String> = _valuePassword.asStateFlow()

    private fun getValuePasswordSave(): String {
        return runBlocking {
            getPreference(context.dataStoreValues, stringPreferencesKey(PASSWORD), "")
        }
    }

    fun savePassword(value: String) {
        _valuePassword.value = value
        savePreference(context.dataStoreValues, stringPreferencesKey(PASSWORD), value)
    }


    private val _teacherMode = MutableStateFlow(getTeacherModeSave())
    val teacherMode: StateFlow<Boolean> = _teacherMode.asStateFlow()


    private fun getTeacherModeSave(): Boolean {
        return runBlocking {
            getPreference(context.dataStoreSettings, booleanPreferencesKey(TEACHERMODE), false)
        }
    }

    fun saveTeacherMode(value: Boolean) {
        _teacherMode.value = value
        savePreference(context.dataStoreSettings, booleanPreferencesKey(TEACHERMODE), value)
    }
    val effectiveTeacherMode: StateFlow<Boolean> =combine(_teacherMode, _valuePassword, _valueLoginName) { teacherMode, password, loginName ->
        teacherMode && password.trim().isNotEmpty() && loginName.trim().isNotEmpty()
    }.stateIn(scope, SharingStarted.Eagerly, false)



    private val _valueDate = MutableStateFlow(getValueDateSave())
    val valueDate: StateFlow<Int> = _valueDate.asStateFlow()
    private fun getValueDateSave(): Int {
        return runBlocking {
            getPreference(context.dataStoreValues, intPreferencesKey(VALUEDATE), 0)
        }
    }

    fun saveValueDate(value: Int) {
        var newValue = value
        if(value <= 0)
        {
        newValue = 53
        }
        else if(value >= 54)
        {
            newValue = 1
        }
        _valueDate.value = newValue
        scope.launch { savePreference(context.dataStoreValues, intPreferencesKey(VALUEDATE), newValue) }
    }

    private val _valueType = MutableStateFlow(getValueTypeSave())
    val valueType: StateFlow<String> = _valueType.asStateFlow()

    private fun getValueTypeSave(): String {
        var value:String
        runBlocking {
            value = getPreference(context.dataStoreValues, stringPreferencesKey(VALUETYPE), "c")
        }

        return if(teacherMode.value) value else ParameterWhichMayChangeOverTime.CLASSES_SHORT

    }
    fun saveValueType(value: String) {
        _valueType.value = value
        scope.launch { savePreference(context.dataStoreValues, stringPreferencesKey(VALUETYPE), value) }
    }


    val effectiveValueType: StateFlow<String> = combine(_teacherMode, _valueType) { teacherMode, valueType ->
        if (teacherMode) valueType else ParameterWhichMayChangeOverTime.CLASSES_SHORT
    }.stateIn(scope, SharingStarted.Eagerly, ParameterWhichMayChangeOverTime.CLASSES_SHORT)

    val effectiveFancyStundenplan= combine(fancyStundenplan, experimentellerStundenplan, valueType) {fancyStundenplan, experimentellerStundenplan, valueType->
        experimentellerStundenplan && fancyStundenplan && (valueType == "c"||valueType == "t"||valueType == "r")
    }.stateIn(scope, SharingStarted.Eagerly, false)

    val effectiveStundenplanZoom= combine(stundenplanZoom, experimentellerStundenplan, valueType) {stundenplanZoom, experimentellerStundenplan, valueType->
        experimentellerStundenplan && stundenplanZoom && (valueType == "c"||valueType == "t"||valueType == "r")
    }.stateIn(scope, SharingStarted.Eagerly, false)

    private val _valueElement = MutableStateFlow(getValueElementSave())
    val valueElement: StateFlow<Int> = _valueElement.asStateFlow()
    private fun getValueElementSave(): Int {
        return runBlocking {
            getPreference(context.dataStoreValues, intPreferencesKey(VALUEELEMENT), 0)
        }
    }

    fun saveValueElement(value: Int) {
        _valueElement.value = value
        scope.launch { savePreference(context.dataStoreValues, intPreferencesKey(VALUEELEMENT), value) }
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
            val stundenplanZoomDeferred = async { getStundenplanZoomSave()  }
            val fancyStundenplanDeferred = async { getFancyStundenplanSave() }



            val alteStundenplaeneDeferred = async { getAlteStundenplaeneSave() }

            _teacherMode.value = teacherModeDeferred.await()
            _valueLoginName.value = valueLoginNameDeferred.await()
            _valuePassword.value = valuePasswordDeferred.await()
            _valueDate.value = valueDateDeferred.await()
            _valueType.value = valueTypeDeferred.await()
            _valueElement.value = valueElementDeferred.await()


            alteStundenplaene = alteStundenplaeneDeferred.await()
            darkmode = darkModeDeferred.await()
            adaptiveColor = adaptiveColorDeferred.await()
            _experimentellerStundenplan.value = experimentellerStundenplanDeferred.await()
            _stundenplanZoom.value = stundenplanZoomDeferred.await()
            _fancyStundenplan.value = fancyStundenplanDeferred.await()








            saveHandlerInitJob.complete()
        }

    }

}