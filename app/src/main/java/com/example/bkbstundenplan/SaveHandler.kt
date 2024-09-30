@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.example.bkbstundenplan

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
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking





class SaveHandler(private val context: Context,private val scope: CoroutineScope,val viewModel: ViewModelStundenplanData) {
    var saveHandlerInitJob: CompletableJob = Job()
    companion object SaveNames
    {
        const val SETTINGS = "settings"
            const val DARKMODE = "darkmode"
            const val EXPERIMENTELLERSTUNDENPLAN = "ExperimentellerStundenplan"
        const val ALTESTUNDENPLENE = "AlteStundenplaene"

        const val VALUES ="values"
            const val VALUEDATES = "ValueDates"
            const val VALUECLASSES = "ValueClasses"
    }


    private val Context.dataStoreSettings: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)
    private val Context.dataStoreValues: DataStore<Preferences> by preferencesDataStore(name = VALUES)

    private suspend fun <T> getPreference(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, defaultValue: T): T {
        return dataStore.data.map { preferences -> preferences[key] ?: defaultValue }.first()
    }

    private fun <T> savePreference(dataStore: DataStore<Preferences>, key: Preferences.Key<T>, value: T) {
        scope.launch {
            dataStore.edit { preferences -> preferences[key] = value }
        }
    }


    var darkmode: Boolean by mutableStateOf(getDarkModeSave())
    fun getDarkModeSave(): Boolean {
        return runBlocking {
            getPreference(context.dataStoreSettings, booleanPreferencesKey(DARKMODE), true)
        }
    }
    fun saveDarkMode(value: Boolean) {
        darkmode = value
        savePreference(context.dataStoreSettings, booleanPreferencesKey(DARKMODE), value)
    }

    var experimentellerStundenplan by mutableStateOf(getExperimentellerStundenplanSave())
    private fun getExperimentellerStundenplanSave(): Boolean {
        return runBlocking {
            getPreference(context.dataStoreSettings, booleanPreferencesKey(EXPERIMENTELLERSTUNDENPLAN), false)
        }
    }
    fun saveExperimentellerStundenplan(value: Boolean) {
        experimentellerStundenplan = value
        savePreference(context.dataStoreSettings, booleanPreferencesKey(EXPERIMENTELLERSTUNDENPLAN), value)
    }

    var alteStundenplaene by mutableStateOf(getAlteStundenplaeneSave())
    private fun getAlteStundenplaeneSave(): Boolean {
        return runBlocking {
            getPreference(context.dataStoreSettings, booleanPreferencesKey(ALTESTUNDENPLENE), false)
        }
    }

    fun saveAlteStundenplaene(value: Boolean) {
        alteStundenplaene = value
        savePreference(context.dataStoreSettings, booleanPreferencesKey(ALTESTUNDENPLENE), value)
    }



    var valueDates by mutableIntStateOf(0)
    private fun getValueDatesSave(): Int {
        return runBlocking {
            getPreference(context.dataStoreValues, intPreferencesKey(VALUEDATES), 0)
        }
    }
    fun saveValueDates(value: Int) {
        valueDates = value
        scope.launch {
            savePreference(context.dataStoreValues, intPreferencesKey(VALUEDATES), value)
        }
    }

    var valueClasses by mutableIntStateOf(0)
    private fun getValueClassesSave(): Int {
        return runBlocking {
            getPreference(context.dataStoreValues, intPreferencesKey(VALUECLASSES), 0)
        }
    }
    fun saveValueClasses(value: Int) {
        valueClasses = value
        scope.launch {
            savePreference(context.dataStoreValues, intPreferencesKey(VALUECLASSES), value)
            }
        }


    init {
         scope.launch {
             // Launch each get each future variable with paralell processing
             val darkModeDeferred = async { getPreference(context.dataStoreSettings, booleanPreferencesKey(DARKMODE), true) }
             val experimentellerStundenplanDeferred = async { getPreference(context.dataStoreSettings, booleanPreferencesKey(EXPERIMENTELLERSTUNDENPLAN), false) }
             val valueClassesDeferred = async { getPreference(context.dataStoreValues, intPreferencesKey(VALUECLASSES), 0) }

             // Await results in parallel and assign to properties
             darkmode = darkModeDeferred.await()
             experimentellerStundenplan = experimentellerStundenplanDeferred.await()
             valueClasses = valueClassesDeferred.await()

             viewModel.updateURLStundenplan()
            saveHandlerInitJob.complete()
        }
    }

}