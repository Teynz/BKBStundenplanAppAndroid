package bkb.stundenplan.app
import bkb.stundenplan.app.Scraping
import bkb.stundenplan.app.ui.reverse
import bkb.stundenplan.app.ui.weeksAgo
import kotlinx.coroutines.runBlocking
import org.junit.Test

class testWeekBackMap {

    @Test
    fun testReverseMap() {
        var webMap = mutableMapOf<Int, String>()
        webMap.put(46, "11.11.2024")
        webMap.put(47, "18.11.2024")
        webMap.put(48, "25.11.2024")

        var reversedMap = webMap.reverse()
        println("webMap: $webMap")
    }

    @Test
    fun TestExtractedVariables()
    {
        var webHtml = runBlocking{Scraping().navbarHTML("")}

        var html = """
        var classes = ["1G22A","1G23A","1G23B","1G24A","1I22A","1I23A","1I24A","1W22A","1W23A","1W24A"];
        var teachers = ["Agushi","Bangel","Bauer","Beckmann","Beine","Bergmann","El Bezzairi","Bley"];
        var rooms = ["?","A 003","A 004","A 008","A 013","A 014","A 101","A 104","A 105","A 109"];
        var corridors = ["A-0","A-1","A-2","A-3","B-Erdg","BoB","Europa1","Europa2","Flur-B","Flur-C"];
    """.trimIndent()

        val extractedVariables = Scraping().extractVariables(webHtml.toString())

        println("classes: ${extractedVariables.classes}")

    }



    @Test
    fun testWeekBackMap() {
        var webMap = mutableMapOf<Int, String>()
        webMap.put(46, "11.11.2024")
        webMap.put(47, "18.11.2024")

        var newMap = weeksAgo(webMap, 8)

        println("webMap: $webMap")
    }
}