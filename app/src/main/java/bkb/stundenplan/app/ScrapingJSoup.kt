package bkb.stundenplan.app

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class ScrapingJSoup(
    private var teacherMode: Boolean,
    private var loginName: String,
    private var password: String,
    var stundenplanSiteUrl: String?
) {
    private var oldTeacherMode: Boolean? = null
    private var oldLoginName: String? = null
    private var oldPassword: String? = null
    private var oldStundenplanSiteUrl: String? = null



    data class TypeArrays(
        var classes: MutableMap<Int, String> = mutableMapOf(),
        var teachers: MutableMap<Int, String> = mutableMapOf(),
        var rooms: MutableMap<Int, String> = mutableMapOf(),
        var corridors: MutableMap<Int, String> = mutableMapOf(),
        var flc1: Float = 1F,
        var flte: Float = 1F
    )

    var typeArrays by mutableStateOf<TypeArrays?>(null)
    private val _datesPairMap = MutableStateFlow<Pair<String?, Map<Int, String>?>?>(null)
    val datesPairMap = _datesPairMap.asStateFlow()




    var typesPairMap by mutableStateOf<Pair<String?, Map<String, String>?>?>(null)


    private val getNavBarURL: (Boolean) -> String = { teacherMode ->
        "https://stundenplan.bkb.nrw/${if (teacherMode) VERZEICHNISSNAMELEHRER else VERZEICHNISSNAMESCHUELER}/frames/navbar.htm"
    }
    private var selectBoxes by mutableStateOf<Elements?>(null)
    private var navBarDoc by mutableStateOf<Document?>(null)


    var stundenplanSite by mutableStateOf<Document?>(null)
    fun updateStundenplanSite(url: String) {
        val login = "$loginName:$password"
        val base64login = encodeToBase64(login)


        try {

            stundenplanSite =
                Jsoup.connect(url).header("Authorization", "Basic $base64login").timeout(10000)
                    .userAgent("Mozilla/5.0").get()

        }
        catch (e: Exception) {
            stundenplanSite = null
            println("Could not fetch StundenplanSite from the Web")
        }
    }





    private fun encodeToBase64(input: String): String {
        val bytes = input.toByteArray()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getEncoder().encodeToString(bytes)
        }
        else {
            android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)

        }
    }


    private fun getNavBarDoc(
        teacherMode: Boolean = false,
        loginName: String = STUNDENPLANLOGIN,
        password: String = STUNDENPLANPASSWORT
    ): Document? {
        var navBarDoc: Document?

        //authentication https://webscraping.ai/faq/jsoup/how-do-i-manage-sessions-and-authentication-with-jsoup
        val login =
            if (teacherMode) "$loginName:$password" else "$STUNDENPLANLOGIN:$STUNDENPLANPASSWORT"
        val base64login = encodeToBase64(login)

        try {
            //withContext(Dispatchers.IO) {
            navBarDoc = Jsoup.connect(getNavBarURL(teacherMode))
                .header("Authorization", "Basic $base64login").timeout(10000)
                .userAgent("Mozilla/5.0").get()

            //}
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

        selectBoxes = getSelectBoxes(navBarDoc, teacherMode)
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
        stundenplanSiteUrl: String? = null,
    ) {
        if (teacherMode != oldTeacherMode || loginName != oldLoginName || password != oldLoginName) {




            CoroutineScope(Dispatchers.IO).launch {

                if (updateNavBarValues) {
                    navBarDoc = getNavBarDoc(teacherMode, loginName, password)
                    selectBoxes = getSelectBoxes(navBarDoc, teacherMode)

                    if (selectBoxes == null) updateSelectBoxes()
                    typeArrays = extractVariables(navBarDoc.toString())
                    _datesPairMap.value = getMap(selectBoxes?.get(0))
                    typesPairMap = getMap(selectBoxes?.get(1))

                }

            }

        }

        if (teacherMode != oldTeacherMode || loginName != oldLoginName || password != oldPassword || stundenplanSiteUrl != oldStundenplanSiteUrl) {
            this.stundenplanSiteUrl = stundenplanSiteUrl
            CoroutineScope(Dispatchers.IO).launch {
                stundenplanSiteUrl?.let { url ->
                    updateStundenplanSite(url = url)
                }
            }
        }

        if(oldTeacherMode != teacherMode) oldTeacherMode = teacherMode
        if(oldLoginName != loginName) oldLoginName = loginName
        if(oldPassword != password )oldPassword = password





    }

}