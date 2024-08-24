package com.example.bkbstundenplan


import org.junit.Test

class SkrapeItTest
{
    @Test
    fun getSelectBoxesTest()
    {
        var ScrapingObject = Scraping()

        for (index in ScrapingObject.getSelectBoxes()!!)
        {

            println("\nContent:  \n")
            println(index)
        }

    }

    @Test
    fun getDatesListTest()
    {
        var ScrapingObject = Scraping()
        println("\nContent:  \n")
        for (index in ScrapingObject.getDates()!!)
        {


            println(index)
        }

    }



    @Test
    fun getClassListTest()
    {
        var ScrapingObject = Scraping()
        println("\nContent:  \n")
        for (index in ScrapingObject.getClasses()!!)
        {


            println(index)
        }

    }


}