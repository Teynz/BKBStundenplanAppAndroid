package bkb.stundenplan.app

import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.CLASSES_FULL
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.CORRIDORS_FULL
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.FLC1
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.FLTE
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.ROOMS_FULL
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.STUNDENPLANLOGIN
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.STUNDENPLANPASSWORT
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.TEACHERS_FULL
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.VERZEICHNISSNAMELEHRER
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.VERZEICHNISSNAMESCHUELER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class DateMap : LinkedHashMap<Int, String>() {
    override fun get(key: Int): String {
        return super.get(key) ?: getMondayDateForWeek(key)
    }

    private fun getMondayDateForWeek(weekNumber: Int): String {
        val formatter = DateTimeFormatter.ofPattern("d.M.yyyy")
        return try {
            val date = LocalDate.now()
                .with(WeekFields.of(Locale.getDefault()).weekOfYear(), weekNumber.toLong())
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
            date.format(formatter)
        } catch (e: Exception) {
            "Kalenderwoche: $weekNumber"
        }
    }
}



class ScrapingJSoup(
    private var teacherMode: StateFlow<Boolean>,
    private var loginName: StateFlow<String>,
    private var password: StateFlow<String>,
    var stundenplanSiteUrl: MutableStateFlow<String>
) {
    data class TypeArrays(
        var classes: MutableMap<Int, String> = mutableMapOf(),
        var teachers: MutableMap<Int, String> = mutableMapOf(),
        var rooms: MutableMap<Int, String> = mutableMapOf(),
        var corridors: MutableMap<Int, String> = mutableMapOf(),
        var flc1: Float = 1F,
        var flte: Float = 1F
    )

    var typeArrays = MutableStateFlow<TypeArrays?>(null)
    private val _datesPairMap = MutableStateFlow<Pair<String?, DateMap?>?>(null)
    val datesPairMap = _datesPairMap.asStateFlow()









    var _typesPairMap = MutableStateFlow<Pair<String?, Map<String, String>?>?>(null)
    val typesPairMap = _typesPairMap.asStateFlow()


    private val getNavBarURL: (Boolean) -> String = { teacherMode ->
        "https://stundenplan.bkb.nrw/${if (teacherMode) VERZEICHNISSNAMELEHRER else VERZEICHNISSNAMESCHUELER}/frames/navbar.htm"
    }
    private var selectBoxes = MutableStateFlow<Elements?>(null)
    private var navBarDoc = MutableStateFlow<Document?>(null)


    var _stundenplanSite = MutableStateFlow<Document?>(null)
    val stundenplanSite = _stundenplanSite.asStateFlow()







    fun updateStundenplanSite(url: String) {
        val login = "${if(teacherMode.value)loginName.value else STUNDENPLANLOGIN}:${if(teacherMode.value)password.value else STUNDENPLANPASSWORT}"
        val base64login = encodeToBase64(login)


        try {

            _stundenplanSite.value =
                Jsoup.connect(url).header("Authorization", "Basic $base64login").timeout(10000)
                    .userAgent("Mozilla/5.0").get()

        }
        catch (e: Exception) {
            _stundenplanSite.value = null
            println("Could not fetch StundenplanSite from the Web")
        }
    }


    private fun encodeToBase64(input: String): String {
        val bytes = input.toByteArray()
        return java.util.Base64.getEncoder().encodeToString(bytes)

    }


    private fun getNavBarDoc(
        teacherMode: Boolean = false,
        loginName: String = STUNDENPLANLOGIN,
        password: String = STUNDENPLANPASSWORT
    ): Document? {
        var navBarDoc: Document?


        val login =
            if (teacherMode) "$loginName:$password" else "$STUNDENPLANLOGIN:$STUNDENPLANPASSWORT"
        val base64login = encodeToBase64(login)

        try {
            navBarDoc = Jsoup.connect(getNavBarURL(teacherMode))
                .header("Authorization", "Basic $base64login").timeout(10000)
                .userAgent("Mozilla/5.0").get()

        }
        catch (e: Exception) {
            navBarDoc = null
            println("Could not fetch navBar from the Web")
        }
        return if (navBarDoc == null && teacherMode) {
            getNavBarDoc()
        }
        else {
            navBarDoc
        }
    }


    private fun updateSelectBoxes() {

        selectBoxes.value = getSelectBoxes(navBarDoc.value, teacherMode.value)
    }

    private fun getSelectBoxes(
        navBarDoc: Document?, teacherMode: Boolean
    ): Elements? {
        val selectBoxes: Elements? = navBarDoc?.select(".selectbox")

        return if (selectBoxes == null && teacherMode) {
            getSelectBoxes(navBarDoc, false)
        }
        else {
            selectBoxes
        }
    }

    private fun String.intOrString() = toIntOrNull() ?: this

    @Suppress("UNCHECKED_CAST")
    private fun <K, V> getMap(selectBox: Element?): Pair<String?, Map<K, V>?>? {
        if (selectBox == null) return null
        var map: MutableMap<K, V>?
        var nameOfSelectBox: String?

        selectBox.let { sBox ->
            nameOfSelectBox = sBox.attributes()["name"]
            map = mutableMapOf()

            sBox.select("option").forEach { listEntry ->

                listEntry.attributes()["value"]?.let { value ->
                    map?.let { map ->
                        map[value.intOrString() as K] = listEntry.text() as V
                    }
                }
            }
        }



        return Pair(nameOfSelectBox, map?.toMap())
    }

    private fun extractVariables(htmlContent: String): TypeArrays {
        val result = TypeArrays()

        val arrayRegex = """var\s+(\w+)\s*=\s*\[(.*?)\];""".toRegex()
        val floatRegex = """var\s+(flc1|flte)\s*=\s*([\d.]+);""".toRegex()

        arrayRegex.findAll(htmlContent).forEach { matchResult ->
            val (varName, varContent) = matchResult.destructured
            val items = varContent.split(",").map { it.trim().removeSurrounding("\"") }
            val itemMap =
                items.mapIndexed { index, item -> (index + 1) to item }.toMap().toMutableMap()

            when (varName) {
                CLASSES_FULL -> result.classes = itemMap
                TEACHERS_FULL -> result.teachers = itemMap
                ROOMS_FULL -> result.rooms = itemMap
                CORRIDORS_FULL -> result.corridors = itemMap
            }
        }

        floatRegex.findAll(htmlContent).forEach { matchResult ->
            val (varName, value) = matchResult.destructured
            when (varName) {
                FLC1 -> result.flc1 = value.toFloat()
                FLTE -> result.flte = value.toFloat()
            }
        }

        return result
    }

    fun smartUpdate(
        updateNavBarValues: Boolean = true,
        updateStundenplanSite: Boolean = true,
    ) {


        if (updateNavBarValues) {
            CoroutineScope(Dispatchers.IO).launch {


                navBarDoc.value = getNavBarDoc(teacherMode.value, loginName.value, password.value)
                selectBoxes.value = getSelectBoxes(navBarDoc.value, teacherMode.value)

                if (selectBoxes.value == null) updateSelectBoxes()
                typeArrays.value = extractVariables(navBarDoc.value.toString())
                val map = getMap<Int, String>(selectBoxes.value?.get(0))

                _datesPairMap.value  = getMap<Int, String>(selectBoxes.value?.get(0))?.let { (string, map) ->
                    Pair(string, map?.let { DateMap().apply { putAll(it) } })
                }
                _typesPairMap.value = getMap(selectBoxes.value?.get(1))

            }

        }



        if (updateStundenplanSite) {
            val url = this.stundenplanSiteUrl.value
            CoroutineScope(Dispatchers.IO).launch {
                updateStundenplanSite(url = url)
            }
        }
    }

    //fun updateStundenplanCustomObject


}