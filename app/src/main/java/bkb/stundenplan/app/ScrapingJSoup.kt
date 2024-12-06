package bkb.stundenplan.app

import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import it.skrape.selects.DocElement
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class ScrapingJSoup(
    private var teacherMode: Boolean = false,
    private var loginName: String = STUNDENPLANLOGIN,
    private var password: String = STUNDENPLANPASSWORT
) {

    data class TypeArrays (
        var classes: MutableMap<Int, String> = mutableMapOf(),
        var teachers: MutableMap<Int, String> = mutableMapOf(),
        var rooms: MutableMap<Int, String> = mutableMapOf(),
        var corridors: MutableMap<Int, String> = mutableMapOf(),
        var flc1: Float = 1F,
        var flte: Float = 1F
    )
    var typeArrays:TypeArrays? = null

    val getNavBarURL: (Boolean) -> String = { teacherMode ->
        "https://stundenplan.bkb.nrw/${if (teacherMode) VERZEICHNISSNAMELEHRER else VERZEICHNISSNAMESCHUELER}/frames/navbar.htm"
    }


    private var navBarURL: String = getNavBarURL(teacherMode)
    private var navBarDoc: MutableState<Document?> = mutableStateOf(null)
    var StundenplanSite: MutableState<Document?> = mutableStateOf(null)


    fun myInit(
        teacherMode: Boolean = false,
        loginName: String = STUNDENPLANLOGIN,
        password: String = STUNDENPLANPASSWORT
    ) {
        this.teacherMode = teacherMode
        this.loginName = loginName
        this.password = password
        getNavBarURL(this.teacherMode)
        navBarDoc.value = getNavBarDoc(teacherMode, loginName, password)
        val selectBoxes = getSelectBoxes(navBarDoc.value, teacherMode)

    }

    init {
        myInit(teacherMode, loginName, password)
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
        var siteDoc: Document?

        //authentication https://webscraping.ai/faq/jsoup/how-do-i-manage-sessions-and-authentication-with-jsoup
        val login = "$loginName:$password"
        val base64login = encodeToBase64(login)

        try {
            //withContext(Dispatchers.IO) {
            siteDoc = Jsoup.connect(navBarURL).header("Authorization", "Basic $base64login")
                .timeout(10000).userAgent("Mozilla/5.0").get()

            //}
        }
        catch (e: Exception) {
            siteDoc = null
            println("Could not fetch navBar from the Web")
        }
        return if (siteDoc == null && teacherMode) {
            getNavBarDoc()
        }
        else {
            siteDoc
        }
    }


    fun getSelectBoxes(
        siteDoc: Document?, teacherMode: Boolean
    ): Elements? {
        val selectBoxes: Elements? = siteDoc?.select(".selectbox")

        return if (selectBoxes == null && teacherMode) {
            getSelectBoxes(siteDoc, false)
        }
        else {
            selectBoxes
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <K, V> getMap(selectBox: Element?): Pair<String?, Map<K, V>?>? {
        if (selectBox == null) return null
        var map: MutableMap<K, V>?
        var nameOfSelectBox: String?

        selectBox.let { sBox ->
            nameOfSelectBox = sBox.attributes()["name"]
            map = mutableMapOf()

            sBox.select("option").forEach { listEntry ->

                listEntry.attributes()["value"]?.let { value ->
                    map?.let { map ->
                        map[value as K] = listEntry.text() as V
                    }
                }
            }
        }



        return Pair(nameOfSelectBox, map?.toMap())
    }

    fun extractVariables(htmlContent: String): TypeArrays {
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


    fun updateStundenplanSite()
    {
        val login = "$loginName:$password"
        val base64login = encodeToBase64(login)


        try {
            //withContext(Dispatchers.IO) {
            StundenplanSite.value = Jsoup.connect().header("Authorization", "Basic $base64login")
                .timeout(10000).userAgent("Mozilla/5.0").get()

            //}
        }
        catch (e: Exception) {
            StundenplanSite.value = null
            println("Could not fetch navBar from the Web")
        }
    }




    fun getStundenplanTable(siteDoc: Document?): Element? {
        return siteDoc?.select("table")?.get(0)
    }

    }