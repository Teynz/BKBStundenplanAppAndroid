package com.example.bkbstundenplan


import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement


class Scraping {

    suspend fun getSelectBoxes(): List<DocElement>? {


        //https://www.scrapingbee.com/blog/web-scraping-kotlin/ good guide
        val MainStundenplanURL =
            "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/frames/navbar.htm"
        var selectBoxes: List<DocElement> = listOf()
        skrape(BrowserFetcher) {
            request {
                url = MainStundenplanURL
                //timeout = 10000
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

    suspend fun getDates(selectionBoxes: List<DocElement>?): List<String>? {
        val selectionBoxes = selectionBoxes ?: this.getSelectBoxes()
        var classList: MutableList<String> = mutableListOf()

        selectionBoxes?.get(0)?.findAll("option")?.forEach {
            classList.add(it.text)
        }



        return classList
    }

    suspend fun getDatesMap(selectionBoxes: List<DocElement>?): Map<Int, String>? {
        val selectionBoxes = selectionBoxes ?: this.getSelectBoxes()
        var classList: MutableMap<Int, String> = mutableMapOf()

        selectionBoxes?.get(0)?.findAll("option")?.forEach {
            it.attributes["value"]?.let { it1 -> classList.put(it1.toInt(), it.text) }
        }



        return classList
    }

    suspend fun getClasses(selectionBoxes: List<DocElement>?): List<String>? {
        val selectionBoxes = selectionBoxes ?: this.getSelectBoxes()
        var classList: MutableList<String> = mutableListOf()

        selectionBoxes?.get(2)?.findAll("option")?.forEach {
            classList.add(it.text)
        }



        return classList
    }

    suspend fun getClassesMap(selectionBoxes: List<DocElement>?): Map<Int, String>? {
        val selectionBoxes = selectionBoxes ?: this.getSelectBoxes()
        var classList: MutableMap<Int, String> = mutableMapOf()

        selectionBoxes?.get(2)?.findAll("option")?.forEach {
            it.attributes["value"]?.let { it1 -> classList.put(it1.toInt(), it.text) }
        }
        return classList


    }
}

data class ScrapingResult(
    val Classes: MutableList<String> = mutableListOf(),
    var count: Int = 0
)