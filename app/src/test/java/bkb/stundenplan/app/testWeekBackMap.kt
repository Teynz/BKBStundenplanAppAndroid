package bkb.stundenplan.app

import bkb.stundenplan.app.ui.reverse
import bkb.stundenplan.app.ui.weeksAgo
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
    fun testWeekBackMap() {
        var webMap = mutableMapOf<Int, String>()
        webMap.put(46, "11.11.2024")
        webMap.put(47, "18.11.2024")

        var newMap = weeksAgo(webMap, 8)

        println("webMap: $webMap")
    }
}