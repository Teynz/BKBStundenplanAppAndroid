package com.example.bkbstundenplan

import org.junit.Assert.assertEquals
import org.junit.Test

class SkrapeItTest
{
    @Test
    fun MainStundenplanWebsiteScrapeOutput()
    {
        var StundenplanDataObjectect = Scraping()

        var ArrayClasses = StundenplanDataObjectect.getClasses()

        for (i in ArrayClasses!!)
        {
            println(i)

        }

    }


    @Test
    fun OutputHTMLSourceCode()
    {
        var StundenplanDataObjectect = Scraping()
        StundenplanDataObjectect.outputHTMLSourceCode()
    }

}