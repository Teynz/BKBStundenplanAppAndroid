@file:Suppress("NAME_SHADOWING")

package bkb.stundenplan.app


import android.annotation.SuppressLint
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.CLASSES_FULL
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.CORRIDORS_FULL
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.FLC1
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.FLTE
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.ROOMS_FULL
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.TEACHERS_FULL
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import it.skrape.selects.html5.table
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Scraping {


    @SuppressLint("AuthLeak")
    suspend fun getSelectBoxes(
        teacherMode: Boolean = false,
        loginName: String = "schueler",
        password: String = "stundenplan"
    ): List<DocElement>? {
        val navBarStundenplanURL =
            "https://$loginName:$password@stundenplan.bkb.nrw/${if (teacherMode) "lehrer" else "schueler"}/frames/navbar.htm"
        var selectBoxes: List<DocElement>? = listOf()
        try {
            skrape(BrowserFetcher) {
                request {
                    url = navBarStundenplanURL
                    timeout = 10000
                }

                extractIt<ScrapingResult> { results ->
                    htmlDocument {
                        selectBoxes = ".selectbox" {
                            findAll { this }
                        }
                    }
                }
            }

        }
        catch (e: Exception) {
            selectBoxes = null
            println("Could not fetch select Boxes from the Web")
        }
        return if (selectBoxes == null && teacherMode) {
            getSelectBoxes()
        }
        else {
            selectBoxes
        }
    }


    /*todo add generics*/


    fun getDatesPairMap(selectionBoxes: List<DocElement>?): Pair<String?, Map<Int, String>?>? {
        if (selectionBoxes == null) return null
        val selectionBoxes = selectionBoxes
        var datesMap: MutableMap<Int, String>?
        var nameOfSelectBox: String?

        selectionBoxes.let { sBoxes ->
            nameOfSelectBox = sBoxes[0].attributes["name"]

            datesMap = mutableMapOf()
            sBoxes[0].findAll("option").forEach { listEntry ->
                listEntry.attributes["value"]?.let { dateValue ->
                    datesMap?.let { it[dateValue.toInt()] = listEntry.text }
                }
            }
        }





        return Pair(nameOfSelectBox, datesMap?.toMap())
    }

    fun getTypesPairMap(selectionBoxes: List<DocElement>?): Pair<String?, Map<String, String>?>? {

        if (selectionBoxes == null) return null
        val selectionBoxes = selectionBoxes
        var typesMap: MutableMap<String, String>?
        var nameOfSelectBox: String?

        selectionBoxes.let { sBoxes ->
            nameOfSelectBox = sBoxes[1].attributes["name"]
            typesMap = mutableMapOf()

            sBoxes[1].findAll("option").forEach { listEntry ->
                listEntry.attributes["value"]?.let { typeValue ->
                    typesMap?.let { it[typeValue] = listEntry.text }
                }
            }
        }



        return Pair(nameOfSelectBox, typesMap?.toMap())

    }


    fun getElementsPairMap(selectionBoxes: List<DocElement>?): Pair<String?, Map<Int, String>?>? {
        if (selectionBoxes == null) return null
        val selectionBoxes = selectionBoxes
        var elementMap: MutableMap<Int, String>?
        var nameOfSelectBox: String?

        selectionBoxes.let { sBoxes ->
            nameOfSelectBox = sBoxes[2].attributes["name"]
            elementMap = mutableMapOf()
            sBoxes[2].findAll("option").forEach { listEntry ->
                listEntry.attributes["value"]?.let { elementValue ->
                    elementMap?.let { it[elementValue.toInt()] = listEntry.text }
                }
            }
        }
        return Pair(nameOfSelectBox, elementMap?.toMap())
    }

    suspend fun navbarHTML(navBarStundenplanURL: String): Doc? {

        return withContext(Dispatchers.IO) {
            try {
                skrape(BrowserFetcher) {
                    request {
                        url = navBarStundenplanURL
                        timeout = 10000
                    }
                    response {
                        htmlDocument {
                            this
                        }
                    }
                }
            }
            catch (e: Exception) {
                println("could not load navbar")
                null
            }
        }
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


    data class TypeArrays(
        var classes: MutableMap<Int, String> = mutableMapOf(),
        var teachers: MutableMap<Int, String> = mutableMapOf(),
        var rooms: MutableMap<Int, String> = mutableMapOf(),
        var corridors: MutableMap<Int, String> = mutableMapOf(),
        var flc1: Float = 1F,
        var flte: Float = 1F
    )


    suspend fun getStundenplanTable(stundenplanURL: String): DocElement? {


        var stundenplan: DocElement? = null

        try {
            skrape(BrowserFetcher) {
                request {
                    url = stundenplanURL
                }
                extractIt<Stundenplan> { results ->
                    htmlDocument {
                        stundenplan = table {
                            findFirst { this }
                        }
                    }
                }
            }
        }
        catch (_: Exception) {
        }


        return stundenplan
    }

    data class Stundenplan(
        var stundenplanTable: DocElement? = null,
        var lehrerTable: DocElement? = null,
        var faecherTable: DocElement? = null
    )

}

data class ScrapingResult(
    val classes: MutableList<String> = mutableListOf(), var count: Int = 0
)