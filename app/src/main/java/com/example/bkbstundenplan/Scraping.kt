package com.example.bkbstundenplan


import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.CssSelectable
import it.skrape.selects.DocElement
import org.junit.Test


class Scraping {

    fun getSelectBoxes(): List<DocElement>? {


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
    fun getDates(selectionBoxes: List<DocElement>? = this.getSelectBoxes()): List<String>? {
        var classList: MutableList<String> = mutableListOf()

        selectionBoxes?.get(0)?.findAll("option")?.forEach {
            classList.add(it.text)
        }



        return classList
    }

    fun getClasses(selectionBoxes: List<DocElement>? = this.getSelectBoxes()): List<String>? {
        var classList: MutableList<String> = mutableListOf()

        selectionBoxes?.get(2)?.findAll("option")?.forEach {
             classList.add(it.text)
        }



        return classList
    }


    /*
        fun getDateList(HTMLListDocElement: List<DocElement>? = this.getSelectBoxes())
        {
            var dateList: List<DocElement> = HTMLListDocElement?.first()



        }*/


}

data class ScrapingResult(
    val Classes: MutableList<String> = mutableListOf(),
    var count: Int = 0
)