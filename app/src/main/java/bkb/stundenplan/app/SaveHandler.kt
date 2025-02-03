
//Für diese Version des Savehandlers, habe ich eine KI nach Optimierungen gefragt, dass Ergebniss schien mir sinnvoll zu sein.
package bkb.stundenplan.app

import android.app.UiModeManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SaveHandler(
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: ViewModelStundenplanData
) {
    companion object {
        private const val SETTINGS = "settings"
        private const val VALUES = "values"

        private object Keys {
            // Settings
            const val DARKMODE = "darkmode"
            const val ADAPTIVECOLOR = "adaptiveColor"
            const val EXPERIMENTELLERSTUNDENPLAN = "ExperimentellerStundenplan"
            const val STUNDENPLANZOOM = "StundenplanZoom"
            const val FANCYSTUNDENPLAN = "FancyStundenplan"
            const val VERBINDEFÄCHER = "VerbindeFacher"
            const val ALTESTUNDENPLENE = "AlteStundenplaene"
            const val TEACHERMODE = "TeacherMode"

            // Values
            const val LOGINNAME = "LoginName"
            const val PASSWORD = "Password"
            const val VALUEDATE = "ValueDate"
            const val VALUETYPE = "ValueType"
            const val VALUEELEMENT = "ValueElement"
        }

        private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(SETTINGS)
        private val Context.valuesStore: DataStore<Preferences> by preferencesDataStore(VALUES)
    }

    // Region: State Initialization
    var darkmode by mutableStateOf(runBlocking { loadInitialDarkMode() })
    var adaptiveColor by mutableStateOf(true)
    var alteStundenplaene by mutableStateOf(true)

    private val _experimentellerStundenplan = MutableStateFlow(true)
    val experimentellerStundenplan = _experimentellerStundenplan.asStateFlow()

    private val _stundenplanZoom = MutableStateFlow(true)
    val stundenplanZoom = _stundenplanZoom.asStateFlow()

    private val _fancyStundenplan = MutableStateFlow(true)
    val fancyStundenplan = _fancyStundenplan.asStateFlow()

    private val _mergeCells = MutableStateFlow(true)
    val mergeCells = _mergeCells.asStateFlow()

    private val _teacherMode = MutableStateFlow(false)
    val teacherMode = _teacherMode.asStateFlow()

    private val _valueLoginName = MutableStateFlow("")
    val valueLoginName = _valueLoginName.asStateFlow()

    private val _valuePassword = MutableStateFlow("")
    val valuePassword = _valuePassword.asStateFlow()

    private val _valueDate = MutableStateFlow(0)
    val valueDate = _valueDate.asStateFlow()

    private val _valueType = MutableStateFlow("c")
    val valueType = _valueType.asStateFlow()

    private val _valueElement = MutableStateFlow(0)
    val valueElement = _valueElement.asStateFlow()

    // Region: Combined Flows
    val effectiveTeacherMode = combine(
        teacherMode,
        valuePassword,
        valueLoginName
    ) { tm, pwd, login -> tm && pwd.isNotBlank() && login.isNotBlank() }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), false)
    val effectiveFancyStundenplan= combine(fancyStundenplan, experimentellerStundenplan, valueType) {fancyStundenplan, experimentellerStundenplan, valueType->
        experimentellerStundenplan && fancyStundenplan && (valueType == "c"||valueType == "t"||valueType == "r")
    }.stateIn(scope, SharingStarted.Eagerly, false)
    val effectiveStundenplanZoom= combine(stundenplanZoom, experimentellerStundenplan, valueType) {stundenplanZoom, experimentellerStundenplan, valueType->
        experimentellerStundenplan && stundenplanZoom && (valueType == "c"||valueType == "t"||valueType == "r")
    }.stateIn(scope, SharingStarted.Eagerly, false)



    val effectiveValueType = combine(teacherMode, valueType) { tm, vt ->
        if (tm) vt else ParameterWhichMayChangeOverTime.CLASSES_SHORT
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), ParameterWhichMayChangeOverTime.CLASSES_SHORT)


    var saveHandlerInitJob: CompletableJob = Job()
    init {
        scope.launch {

                val settings = listOf(
                    async { darkmode = loadInitialDarkMode() },
                    async { adaptiveColor = loadPreference(Keys.ADAPTIVECOLOR, true) },
                    async { _experimentellerStundenplan.value = loadPreference(Keys.EXPERIMENTELLERSTUNDENPLAN, true) },
                    async { _stundenplanZoom.value = loadPreference(Keys.STUNDENPLANZOOM, true) },
                    async { _fancyStundenplan.value = loadPreference(Keys.FANCYSTUNDENPLAN, true) },
                    async { _mergeCells.value = loadPreference(Keys.VERBINDEFÄCHER, true) },
                    async { alteStundenplaene = loadPreference(Keys.ALTESTUNDENPLENE, true) },
                    async { _teacherMode.value = loadPreference(Keys.TEACHERMODE, false) }
                )

                val values = listOf(
                    async { _valueLoginName.value = loadPreference(Keys.LOGINNAME, "") },
                    async { _valuePassword.value = loadPreference(Keys.PASSWORD, "") },
                    async { _valueDate.value = loadPreference(Keys.VALUEDATE, 0) },
                    async { _valueType.value = loadPreference(Keys.VALUETYPE, "c") },
                    async { _valueElement.value = loadPreference(Keys.VALUEELEMENT, 0) }
                )

                settings.awaitAll()
                values.awaitAll()

            saveHandlerInitJob.complete()
        }
    }

    // Region: Save Functions
    fun saveDarkMode(value: Boolean) = updateStateAndSave(Keys.DARKMODE, value, {darkmode = it})
    fun saveAdaptiveColor(value: Boolean) = updateStateAndSave(Keys.ADAPTIVECOLOR, value, {adaptiveColor = it})
    fun saveExperimentellerStundenplan(value: Boolean) = updateFlowAndSave(Keys.EXPERIMENTELLERSTUNDENPLAN, value, _experimentellerStundenplan)
    fun saveStundenplanZoom(value: Boolean) = updateFlowAndSave(Keys.STUNDENPLANZOOM, value, _stundenplanZoom)
    fun saveFancyStundenplan(value: Boolean) = updateFlowAndSave(Keys.FANCYSTUNDENPLAN, value, _fancyStundenplan)
    fun saveMergeCells(value: Boolean) = updateFlowAndSave(Keys.VERBINDEFÄCHER, value, _mergeCells)
    fun saveAlteStundenplaene(value: Boolean) = updateStateAndSave(Keys.ALTESTUNDENPLENE, value, {alteStundenplaene = it})
    fun saveTeacherMode(value: Boolean) = updateFlowAndSave(Keys.TEACHERMODE, value, _teacherMode)
    fun saveLoginName(value: String) = updateFlowAndSave(Keys.LOGINNAME, value, _valueLoginName)
    fun savePassword(value: String) = updateFlowAndSave(Keys.PASSWORD, value, _valuePassword)

    fun saveValueDate(value: Int) {
        val validated = when {
            value <= 0 -> 53
            value >= 54 -> 1
            else -> value
        }
        scope.launch {
            _valueDate.value = validated
            savePreference(Keys.VALUEDATE, validated)
        }
    }

    fun saveValueType(value: String) = updateFlowAndSave(Keys.VALUETYPE, value, _valueType)
    fun saveValueElement(value: Int) = updateFlowAndSave(Keys.VALUEELEMENT, value, _valueElement)

    // Region: Private Helpers
    private suspend fun loadInitialDarkMode(): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_NO) {
            loadPreference(Keys.DARKMODE, false)
        } else {
            loadPreference(Keys.DARKMODE, true)
        }
    }

    private inline fun <reified T> loadPreference(key: String, defaultValue: T): T {
        val store = when (T::class) {
            Boolean::class -> context.settingsStore
            String::class, Int::class -> context.valuesStore
            else -> throw IllegalArgumentException("Unsupported type")
        }

        return runBlocking {
            store.data.map { it[preferenceKey(key)] ?: defaultValue }.first()
        }
    }

    private fun <T> updateStateAndSave(key: String, value: T, stateSetter: (T) -> Unit) {
        stateSetter(value)
        scope.launch { savePreference(key, value) }
    }

    private fun <T> updateFlowAndSave(key: String, value: T, flow: MutableStateFlow<T>) {
        flow.value = value
        scope.launch { savePreference(key, value) }
    }

    private suspend fun <T> savePreference(key: String, value: T) {
        val store = when (value) {
            is Boolean -> context.settingsStore
            is String, is Int -> context.valuesStore
            else -> throw IllegalArgumentException("Unsupported type")
        }

        store.edit { preferences ->
            when (value) {
                is Boolean -> preferences[booleanPreferencesKey(key)] = value
                is String -> preferences[stringPreferencesKey(key)] = value
                is Int -> preferences[intPreferencesKey(key)] = value
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> preferenceKey(key: String): Preferences.Key<T> = when (T::class) {
        Boolean::class -> booleanPreferencesKey(key) as Preferences.Key<T>
        String::class -> stringPreferencesKey(key) as Preferences.Key<T>
        Int::class -> intPreferencesKey(key) as Preferences.Key<T>
        else -> throw IllegalArgumentException("Unsupported type")
    }
}
