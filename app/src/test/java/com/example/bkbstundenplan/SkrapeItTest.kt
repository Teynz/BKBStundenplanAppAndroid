package com.example.bkbstundenplan


import kotlinx.coroutines.runBlocking
import org.junit.Test

class SkrapeItTest {
    @Test
    fun getSelectBoxesTest() {
        runBlocking {
            var ScrapingObject = Scraping()



            for (index in ScrapingObject.getSelectBoxes()!!) {

                println("\nContent:  \n")
                println(index)
            }
        }

    }

    @Test
    fun getDatesListTest() {
        runBlocking {
            var ScrapingObject = Scraping()
            println("\nContent:  \n")
            for (index in ScrapingObject.getDates(null)!!) {


                println(index)
            }
        }
    }


    @Test
    fun getClassListTest() {
        runBlocking {
            var ScrapingObject = Scraping()
            println("\nContent:  \n")

            ScrapingObject.getDatesMap(null)!!.forEach()
            {entry ->
                println("${entry.key} | ${entry.value}")
            }
        }
    }

    @Test
   fun getDatesMapTest()
    {
       runBlocking{
           var ScrapingObject = Scraping()
           println("\nContent:\n")

          ScrapingObject.getDatesMap(null)!!.forEach()
          {
              index -> println("${index.key} | ${index.value}")

          }


       }
   }

    @Test
    fun getClassesMapTest()
    {
        runBlocking{
            var ScrapingObject = Scraping()
            println("\nContent:\n")

            ScrapingObject.getClassesMap(null)!!.forEach()
            {
                    index -> println("${index.key} | ${index.value}")

            }
        }
    }


}