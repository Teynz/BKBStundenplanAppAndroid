package bkb.stundenplan.app

import bkb.stundenplan.app.HTMLStrings.addDivHTML
import kotlinx.coroutines.runBlocking
import org.junit.Test

class HTMLStringsTest {

    @Test
    fun testAddDivHTML() {
        val html = runBlocking {
            Scraping().getStundenplanTable("https://schueler:stundenplan@stundenplan.bkb.nrw/schueler/46/c/c00005.htm")
                .toString().addDivHTML()
        }

        println("Content:\n\n" + html)

    }


}