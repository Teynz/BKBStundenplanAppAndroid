package com.example.bkbstundenplan

import org.junit.Test

class StundenplanDataTest {

    @Test
   fun ListObjectTest()
    {
        for (index in StundenplanData.ScrapingLists.datesList!!)
        {

            println("\nContent:  \n")
            println(index)
        }

        for (index in StundenplanData.ScrapingLists.classList!!)
        {

            println("\nContent:  \n")
            println(index)
        }


   }

}