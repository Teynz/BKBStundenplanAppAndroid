package bkb.stundenplan.app

import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.regexFilterRedundantNumbers
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit


/**Ein einzelnes Fach
 * multiplier liegt zwischen 2 und 20
 * Eine Normale Zelle ist 2 eine doppelte 4 usw
 *in html wird dieses attribut rowspan genannt
 */
data class Subject(
    var multiplier: Int, val content: Element
)

class Day(
    var date: LocalDate? = null, var subjects: MutableList<Subject> = mutableListOf()
) {
    fun mergeCellsRemoveRedundant(mergeCells: Boolean, removeRedundantNumbers: Boolean): Day {
        var subjects = subjects
        //RemoveRedundantNumbers

        if (removeRedundantNumbers) {
            val newWithoutRedundantSubjectsList = subjects.map { subject ->
                val modifiedContent = subject.content.clone()



                modifiedContent.select("font").forEach { font ->
                    font?.let{ font ->
                    if (font.text().contains(regexFilterRedundantNumbers)) {
                        font.remove()
                    }}
                }



                subject.copy(content = modifiedContent)
            }

            subjects = newWithoutRedundantSubjectsList.toMutableList()
        }

        //Merging Cells
        if (mergeCells)
        {
        val newMergedSubjectsList = mutableListOf<Subject>()
        var lastNewSubjectAsString: String? = null
        var indexCounter = 0
        while (indexCounter < subjects.size) {
            lastNewSubjectAsString?.let {
                if (lastNewSubjectAsString == subjects[indexCounter].content.text()) {
                    newMergedSubjectsList[newMergedSubjectsList.size - 1].multiplier += subjects[indexCounter].multiplier
                } else {
                    newMergedSubjectsList.add(subjects[indexCounter])
                }
                lastNewSubjectAsString =
                    newMergedSubjectsList[newMergedSubjectsList.size - 1].content.text()
            } ?: run {
                newMergedSubjectsList.add(subjects[0])
                lastNewSubjectAsString = subjects[0].content.text()
            }
            indexCounter++
        }
    subjects = newMergedSubjectsList
    }
        return Day(date, subjects)
    }
}


class Week(
    private var monday: Day = Day(),
    private var tuesday: Day = Day(),
    private var wednesday: Day = Day(),
    private var thursday: Day = Day(),
    private var friday: Day = Day(),
    var customCellColor: Boolean = false
) {


    fun mergeAndRemoveRedundantAll(mergeCells: Boolean, removeRedundantNumbers: Boolean):Week {
        return Week(
        monday.mergeCellsRemoveRedundant(mergeCells = mergeCells, removeRedundantNumbers = removeRedundantNumbers),
        tuesday.mergeCellsRemoveRedundant(mergeCells = mergeCells, removeRedundantNumbers = removeRedundantNumbers),
        wednesday.mergeCellsRemoveRedundant(mergeCells = mergeCells, removeRedundantNumbers = removeRedundantNumbers),
        thursday.mergeCellsRemoveRedundant(mergeCells = mergeCells, removeRedundantNumbers = removeRedundantNumbers),
        friday.mergeCellsRemoveRedundant(mergeCells = mergeCells, removeRedundantNumbers = removeRedundantNumbers),
            this.customCellColor)

    }


    fun asList(): List<Day?> = listOf(monday, tuesday, wednesday, thursday, friday)
    fun asMap(): Map<LocalDate?, Day?> = mapOf(
        monday.date to monday,
        tuesday.date to tuesday,
        wednesday.date to wednesday,
        thursday.date to thursday,
        friday.date to friday
    )

    fun setDay(index: Int, day: Day) {
        when (index) {
            1 -> monday = day
            2 -> tuesday = day
            3 -> wednesday = day
            4 -> thursday = day
            5 -> friday = day
        }
    }

    fun accessDay(index: Int, unit: (day: Day) -> Unit) {
        when (index) {
            1 -> unit(monday)
            2 -> unit(tuesday)
            3 -> unit(wednesday)
            4 -> unit(thursday)
            5 -> unit(friday)
        }
    }

    /**
     * Tage 1-5
     */
    fun getDay(index: Int): Day {
        return when (index) {
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
        this.asMap().forEach { if (it.key == today) return it.value }
        return null
    }

    fun getNextDayOrMonday(): Day? {
        val today = LocalDate.now()
        this.asMap().forEach { if (it.key == today.plusDays(1)) return it.value }
        this.asMap().forEach { if (it.key == today.plusDays(2)) return it.value }
        return null
    }

    fun setDates(element: Element) {
        var count = 1
        element.select("td > table > tbody > tr > td > font").forEach { font ->
            getDay(count).date = font.text().toDate()
            count++
        }
    }

}


/**
 * verwandelt Stundenplan Datum in aktuelles, Das Jahr ist entweder das der Letzten 8 Monate oder das der nächsten 4 Monate
 */
private fun String.toDate(): LocalDate? {
    try {
        val parts = this.split(" ") // teilt String in 2 Teile auf, 1. Wochentag 2. datum
        val dateString = parts[1].removeSuffix(".") //Entfernt den Punkt hinter dem Monat.
        val currentDate = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("d.M")
        val parsedDate = formatter.parse(dateString)
        val day = parsedDate.get(ChronoField.DAY_OF_MONTH)
        val month = parsedDate.get(ChronoField.MONTH_OF_YEAR)

        var resultDate = LocalDate.of(
            currentDate.year, month, day
        ) //Setzt das Datum auf das Momentane Jahr

        if (ChronoUnit.MONTHS.between(resultDate, currentDate) > 8) {
            resultDate = resultDate.plusYears(1)
        } else if (ChronoUnit.MONTHS.between(currentDate, resultDate) > 4) {
            resultDate = resultDate.minusYears(1)
        }
        return resultDate

    } catch (e: DateTimeException) {
        return null
    }


}

/**
 * Return the Week as `List<Day>?`
 */

fun Document.getWeek(): Week {
    val week = Week()
    week.customCellColor= this.selectFirst("table > tbody")?.select("> tr > td")?.attr("bgcolor")?.isEmpty()?: false


    var rowspanCounter = 2 //max 20 Eine Zelle nimmt 2, startet deswegen für die erste zeile bei 2
    val rowspanTracker = IntArray(5) { 0 }


    val tbody = this.selectFirst("table > tbody") ?: return week
    val rows = tbody.select("> tr")

    rows?.forEachIndexed {indexRow, currentRow ->

        if (currentRow.childrenSize() != 0) {
            if (indexRow == 0) {
                week.setDates(currentRow)
            }
            else {
                val cells = currentRow.select(">td")
                var dayCounter = 1//diese Variable steht für Montag bis freitag



                cells?.forEachIndexed {indexCell, currentCell ->

                    if (indexCell != 0) {



                        while (rowspanCounter <= rowspanTracker[dayCounter-1] && dayCounter <= 5) {
                            dayCounter++


                        }
                        if (dayCounter in 1..5){
                            val multiplier = currentCell.attributes()["rowspan"]?.toInt() ?: 0
                            rowspanTracker[dayCounter-1] += multiplier

                            week.accessDay(dayCounter)
                            {
                                it.subjects.add(
                                    Subject(
                                        multiplier, currentCell
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
