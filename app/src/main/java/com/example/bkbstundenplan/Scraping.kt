package com.example.bkbstundenplan


import android.annotation.SuppressLint
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import java.lang.Thread.sleep


class Scraping {

    @SuppressLint("AuthLeak")
    suspend fun getSelectBoxes(): List<DocElement> {


        //https://www.scrapingbee.com/blog/web-scraping-kotlin/ good guide
        val mainStundenplanURL =
            "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/frames/navbar.htm"
        var selectBoxes: List<DocElement> = listOf()
        skrape(BrowserFetcher) {
            request {
                url = mainStundenplanURL
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

    suspend fun getDatesMap(selectionBoxes: List<DocElement>?): Map<Int, String> {
        val selectionBoxes = selectionBoxes ?: this.getSelectBoxes()
        val datesList: MutableMap<Int, String> = mutableMapOf()

        for (i in 0..2) {
            if (datesList.isNotEmpty()) break
            selectionBoxes[0].findAll("option").forEach {
                it.attributes["value"]?.let { it1 -> datesList[it1.toInt()] = it.text }
            }
        }


        return datesList
    }


    suspend fun getClassesMap(selectionBoxes: List<DocElement>?): Map<Int, String> {
        val selectionBoxes = selectionBoxes ?: this.getSelectBoxes()
        val classList: MutableMap<Int, String> = mutableMapOf()

        for (i in 0..2) {
            if (classList.isNotEmpty()) break
            selectionBoxes[2].findAll("option").forEach {
                it.attributes["value"]?.let { it1 -> classList[it1.toInt()] = it.text }
            }
        }

        return classList


    }
}

data class ScrapingResult(
    val classes: MutableList<String> = mutableListOf(),
    var count: Int = 0
)