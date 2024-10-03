@file:Suppress("NAME_SHADOWING")

package com.example.bkbstundenplan


import android.annotation.SuppressLint
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import it.skrape.selects.html5.table


class Scraping {

    @SuppressLint("AuthLeak")
    suspend fun getSelectBoxes(): List<DocElement> {
        val mainStundenplanURL =
            "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/frames/navbar.htm"
        var selectBoxes: List<DocElement> = listOf()
        skrape(BrowserFetcher) {
            request {
                url = mainStundenplanURL
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
        return selectBoxes
    }


    suspend fun getDatesMap(selectionBoxes: List<DocElement>?): Map<Int, String> {
        val selectionBoxes = selectionBoxes ?: this.getSelectBoxes()
        val datesList: MutableMap<Int, String> = mutableMapOf()

        selectionBoxes[0].findAll("option").forEach {
            it.attributes["value"]?.let { it1 -> datesList[it1.toInt()] = it.text }
        }

        return datesList
    }


    suspend fun getClassesMap(selectionBoxes: List<DocElement>?): Map<Int, String> {
        val selectionBoxes = selectionBoxes ?: this.getSelectBoxes()
        val classList: MutableMap<Int, String> = mutableMapOf()

        selectionBoxes[2].findAll("option").forEach {
            it.attributes["value"]?.let { it1 -> classList[it1.toInt()] = it.text }
        }

        return classList
    }


    suspend fun getStundenplanTable(stundenplanURL: String): DocElement? {
        var stundenplan: DocElement? = null

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