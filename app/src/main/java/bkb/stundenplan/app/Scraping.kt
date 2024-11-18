@file:Suppress("NAME_SHADOWING")

package bkb.stundenplan.app


import android.annotation.SuppressLint
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import it.skrape.selects.html5.table


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

        } catch (e: Exception) {
            selectBoxes = null
            println("Could not fetch select Boxes from the Web")
        }
        return selectBoxes
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
        } catch (_: Exception) {
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