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
    fun getClassListTest()
    {

    }


}