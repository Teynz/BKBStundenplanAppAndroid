package com.example.bkbstundenplan

import com.example.bkbstundenplan.StundenplanData.ScrapingResult
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape

class Scraping
{
    fun getClasses(): MutableList<String>?
    {


        //https://www.scrapingbee.com/blog/web-scraping-kotlin/ good guide
        val MainStundenplanURL = "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/frames/navbar.htm"
        val ClassesList: MutableList<String> = mutableListOf()
        skrape(HttpFetcher) {
            request {
                url = MainStundenplanURL
                //timeout = 10000
            }

            extractIt<ScrapingResult> { results ->
                htmlDocument {


                    ".selectbox" {
                        findAll {
                            forEach {
                                ClassesList.add(it.text)


                            }

                        }
                    }


                }
            }
        }
        return ClassesList
    }





    fun outputHTMLSourceCode() {

        skrape(HttpFetcher) {

// make an HTTP GET request to the specified URL

            request {

                url = "https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/frames/navbar.htm"

            }

            response {

// get the HTML source code and print it

                htmlDocument {

                    print(html)

                }

            }

        }

    }

}