package bkb.stundenplan.app

import androidx.compose.ui.graphics.Color
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

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

    fun getToday(): Day? {
        val today = LocalDate.now()
         this.asMap().forEach{if(it.key == today) return it.value}
    return null
    }
    fun getNextDayOrMonday(): Day? {
        val today = LocalDate.now()
        this.asMap().forEach{if(it.key == today.plusDays(1)) return it.value}
        this.asMap().forEach{if(it.key == today.plusDays(2)) return it.value}
        return null
    }
     fun setDates(Element: Element)
    {
        var count = 1
        Element.select("td > table > tbody > tr > td > font").forEach {font ->
            getDay(count).date = font.text().toDate()
            count++
        }
    }

}


/**
 * verwandelt Stundenplan Datum in aktuelles, Das Jahr ist entweder das der Letzten 8 Monate oder das der nächsten 4 Monate
 */
private fun String.toDate(): LocalDate {
    val parts = this.split(" ") // teilt String in 2 Teile auf, 1. Wochentag 2. datum
    val dateString = parts[1].removeSuffix(".") //Entfernt den Punkt hinter dem Monat.
    val currentDate = LocalDate.now()

    val formatter = DateTimeFormatter.ofPattern("d.M")
    val parsedDate = formatter.parse(dateString)
    val day = parsedDate.get(ChronoField.DAY_OF_MONTH)
    val month = parsedDate.get(ChronoField.MONTH_OF_YEAR)

    var resultDate = LocalDate.of(currentDate.year, month, day) //Setzt das Datum auf das Momentane Jahr

    if (ChronoUnit.MONTHS.between(resultDate, currentDate) > 8) {
        resultDate = resultDate.plusYears(1)
    } else if (ChronoUnit.MONTHS.between(currentDate, resultDate) > 4) {
        resultDate = resultDate.minusYears(1)
    }
    return resultDate
}


/**
 * Return the Week as `List<Day>?`
 */
fun Document.getWeek(): Week {
    val week: Week = Week()
    var rowspanCounter = 2 //max 20 Eine Zelle nimmt 2, startet deswegen für die erste zeile bei 2
    var cDayOne = 0
    var cDayTwo = 0
    var cDayThree = 0
    var cDayFour = 0
    var cDayFive = 0


    val rows = this.select("table")?.get(0)?.selectFirst("table > tbody")?.select("> tr")

    rows?.forEach { counterRow ->

        if (counterRow.childrenSize() != 0) {
            if (counterRow == rows.first()) {
                week.setDates(counterRow)
            }
            else {
                val cells = counterRow.select(">td")
                var dayCounter = 1//diese Variable steht für Montag bis freitag
                cells?.forEach { counterCell ->

                    if (counterCell != cells.first()) {

                        var rowspanOfColumn = when (dayCounter) {
                            1 -> cDayOne
                            2 -> cDayTwo
                            3 -> cDayThree
                            4 -> cDayFour
                            5 -> cDayFive
                            else -> 20
                        }

                        while (rowspanCounter <= rowspanOfColumn && dayCounter <= 5) {
                            dayCounter++
                            rowspanOfColumn = when (dayCounter) {
                                1 -> cDayOne
                                2 -> cDayTwo
                                3 -> cDayThree
                                4 -> cDayFour
                                5 -> cDayFive
                                else -> 20
                            }

                        }
                        if (0 <dayCounter && dayCounter <= 5){
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