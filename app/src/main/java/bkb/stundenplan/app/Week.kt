package bkb.stundenplan.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.LocalDate

data class Text(
    val text: String?, val bold: Boolean, val color: Color
)

/**Ein einzelnes Fach
 * multiplier liegt zwischen 2 und 20
 * Eine Normale Zelle ist 2 eine doppelte 4 usw
 *in html wird dieses attribut rowspan genannt
 */
data class Subject(
    val multiplier: Int, val content: Element
)

data class Day(
    var date: LocalDate? = null, var subjects: MutableList<Subject> = mutableListOf()
)

class Week(
    private var monday: Day = Day(), private var tuesday: Day = Day(), private var wednesday: Day = Day(), private var thursday: Day = Day(), private var friday: Day = Day()
) {

    fun asList(): List<Day?> = listOf(monday, tuesday, wednesday, thursday, friday)
    fun asMap(): Map<LocalDate?,Day?> = mapOf(monday.date to monday,tuesday.date to tuesday,wednesday.date to wednesday,thursday.date to thursday,friday.date to friday)

    fun setDay(index: Int, day: Day) {
        when (index)
        {
            1 -> monday = day
            2 -> tuesday = day
            3 -> wednesday = day
            4 -> thursday = day
            5 -> friday = day
        }
    }
    fun accessDay(index: Int, unit: (day:Day) -> Unit)
    {
        when (index)
        {
            1 -> unit(monday)
            2 -> unit(tuesday)
            3 -> unit(wednesday)
            4 -> unit(thursday)
            5 -> unit(friday)
        }
    }
    fun getDay(index: Int):Day {
        return when (index)
        {
            1 -> monday
            2 -> tuesday
            3 -> wednesday
            4 -> thursday
            5 -> friday
            else -> throw IndexOutOfBoundsException("Index muss zwischen 1 und 5 sein")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getToday(): Day? {
        val today = LocalDate.now()
         this.asMap().forEach{if(it.key == today) return it.value}
    return null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNextDayOrMonday(): Day? {
        val today = LocalDate.now()
        this.asMap().forEach{if(it.key == today.plusDays(1)) return it.value}
        this.asMap().forEach{if(it.key == today.plusDays(2)) return it.value}
        return null
    }

}

/**
 * Return the Week as `List<Day>?`
 */
fun Document.getWeek(): Week {
    data class WeekStringsClass(
        var mo: String = "error",
        var tu: String = "error",
        var we: String = "error",
        var th: String = "error",
        var fr: String = "error"
    )

    fun getDates(dates: Element): WeekStringsClass {
        val weekStrings = WeekStringsClass()

        var count = 0
        dates.select("td > table > tbody > tr > td > font").forEach {
            when (count) {
                0 -> weekStrings.mo = it.text()
                1 -> weekStrings.tu = it.text()
                2 -> weekStrings.we = it.text()
                3 -> weekStrings.th = it.text()
                4 -> weekStrings.fr = it.text()
            }
            count++
        }
        return weekStrings
    }


    var rowspanCounter = 0 //max 20 one cell takes 2
    var cDayOne = 0
    var cDayTwo = 0
    var cDayThree = 0
    var cDayFour = 0
    var cDayFive = 0

    val week: Week = Week()
    var weekStrings = WeekStringsClass()

    val rows = this.select("table")?.get(0)?.selectFirst("table > tbody")?.select("> tr")

    rows?.forEach { counterRow ->

        if (counterRow.childrenSize() != 0) {
            if (counterRow == rows.first()) {
                weekStrings = getDates(counterRow)
            }
            else {
                val cells = counterRow.select(">td")
                var dayCounter = 1//diese Variable steht für Montag bis freitag
                cells?.forEach { counterCell ->

                    if (counterCell != cells.first()) {

                        var oldRowspan = when (dayCounter) {
                            1 -> cDayOne
                            2 -> cDayTwo
                            3 -> cDayThree
                            4 -> cDayFour
                            5 -> cDayFive
                            else -> 20
                        }

                        while (rowspanCounter <= oldRowspan) {
                            oldRowspan = when (dayCounter) {
                                1 -> cDayOne
                                2 -> cDayTwo
                                3 -> cDayThree
                                4 -> cDayFour
                                5 -> cDayFive
                                else -> 20
                            }
                            dayCounter++
                        }

                        val multiplier = counterCell.attributes()["rowspan"]?.toInt() ?: 0
                         when (dayCounter) {
                            1 -> cDayOne += multiplier
                            2 -> cDayTwo += multiplier
                            3 -> cDayThree += multiplier
                            4 -> cDayFour += multiplier
                            5 -> cDayFive += multiplier
                        }

                            week.accessDay(dayCounter)
                            {
                                it.subjects.add(
                                    Subject(
                                        multiplier, counterCell
                                    )
                                )
                            }

                        dayCounter++
                    }
                }
                rowspanCounter += 2
            }
        }
    }
    return week
}