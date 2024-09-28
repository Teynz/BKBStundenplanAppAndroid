package com.example.bkbstundenplan


import kotlinx.coroutines.runBlocking
import org.junit.Test

class SkrapeItTest {
    @Test
    fun getSelectBoxesTest() {
        runBlocking {
            val scrapingObject = Scraping()



            for (index in scrapingObject.getSelectBoxes()) {

                println("\nContent:  \n")
                println(index)
            }
        }

    }

    @Test
    fun getTablesTest()
    {
        var tables: Scraping.Stundenplan? = null
        runBlocking{Scraping().getTables("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/39/c/c00005.htm")

        }
        if(tables == null)
        {
            println("tables is null")
        }
        else
        {
           println("Content:\n\n ${tables!!.stundenplanTable}")
            //println("Content:\n\n ${tables!!.lehrerTable}")
            //println("Content:\n\n ${tables!!.faecherTable}")
        }


    }




    @Test
    fun getClassListTest() {
        runBlocking {
            val scrapingObject = Scraping()
            println("\nContent:  \n")
            
            scrapingObject.getDatesMap(null).forEach()
            { entry ->
                println("${entry.key} | ${entry.value}")
            }
        }
    }

    @Test
    fun getDatesMapTest() {
        runBlocking {
            val scrapingObject = Scraping()
            println("\nContent:\n")

            scrapingObject.getDatesMap(null).forEach()
            { index ->
                println("${index.key} | ${index.value}")

            }


        }
    }

    @Test
    fun getClassesMapTest() {
        runBlocking {
            val scrapingObject = Scraping()
            println("\nContent:\n")

            scrapingObject.getClassesMap(null).forEach()
            { index ->
                println("${index.key} | ${index.value}")

            }
        }
    }


}