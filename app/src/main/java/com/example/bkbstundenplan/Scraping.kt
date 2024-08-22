package com.example.bkbstundenplan

import com.example.bkbstundenplan.StundenplanData.ScrapingResult
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.CssSelectable
import it.skrape.selects.DocElement
import org.junit.Test


class Scraping
{

    fun getSelectBoxes(): List<DocElement>?
    {


        //https://www.scrapingbee.com/blog/web-scraping-kotlin/ good guide
        val MainStundenplanURL = "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/frames/navbar.htm"
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
/*
    fun getDateList(HTMLListDocElement: List<DocElement>? = this.getSelectBoxes())
    {
        var dateList: List<DocElement> = HTMLListDocElement?.first()



    }*/



}