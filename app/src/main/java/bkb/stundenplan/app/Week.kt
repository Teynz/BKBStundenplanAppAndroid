package bkb.stundenplan.app

import androidx.compose.ui.graphics.Color
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.regexFilterRedundantNumbers
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.DateTimeException
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
    var multiplier: Int, val content: Element
)

class Day(
    var date: LocalDate? = null, var subjects: MutableList<Subject> = mutableListOf()
) {


    fun mergeCellsRemoveRedundant(mergeCells: Boolean, removeRedundantNumbers: Boolean): Day {
        var subjects = subjects
        //RemoveRedundantNumbers

        if (removeRedundantNumbers) {
            var newWithoutRedundantSubjectsList = subjects.map { subject ->
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
        var newMergedSubjectsList = mutableListOf<Subject>()
        var lastNewSubjectAsString: String? = null
        var indexCounter = 0
        while (indexCounter < subjects.size) {

            lastNewSubjectAsString?.let {
                if (lastNewSubjectAsString == subjects[indexCounter].content.toString()) {
                    newMergedSubjectsList[newMergedSubjectsList.size - 1].multiplier += subjects[indexCounter].multiplier
                } else {
                    newMergedSubjectsList.add(subjects[indexCounter])
                }

                lastNewSubjectAsString =
                    newMergedSubjectsList[newMergedSubjectsList.size - 1].content.toString()
            } ?: run {
                newMergedSubjectsList.add(subjects[0])
                lastNewSubjectAsString = subjects[0].content.toString()
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
        monday.mergeCellsRemoveRedundant(mergeCells = true, removeRedundantNumbers = true),
        tuesday.mergeCellsRemoveRedundant(mergeCells = true, removeRedundantNumbers = true),
        wednesday.mergeCellsRemoveRedundant(mergeCells = true, removeRedundantNumbers = true),
        thursday.mergeCellsRemoveRedundant(mergeCells = true, removeRedundantNumbers = true),
        friday.mergeCellsRemoveRedundant(mergeCells = true, removeRedundantNumbers = true),
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

    fun setDates(Element: Element) {
        var count = 1
        Element.select("td > table > tbody > tr > td > font").forEach { font ->
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
    val tbody = this.selectFirst("table > tbody") ?: return week
    val rows = tbody.select("> tr")


    val rowspanTracker = IntArray(5)
    var currentRowspanCounter = 2
    val defaultColor = tbody.select("> tr > td").firstOrNull()?.attr("bgcolor")
    week.customCellColor = defaultColor.isNullOrEmpty()

    rows.forEachIndexed { rowIndex, row ->
        when {
            rowIndex == 0 -> week.setDates(row)
            else -> {
                val cells = row.select("> td")
                var currentDay = 0
                var cellIndex = 0

                while (cellIndex < cells.size && currentDay < 5) {
                    if (cellIndex == 0) {
                        cellIndex++
                        continue
                    }

                    val cell = cells[cellIndex]
                    val rowspan = cell.attr("rowspan").toIntOrNull() ?: 1


                    while (currentDay < 5 && rowspanTracker[currentDay] > currentRowspanCounter) {
                        currentDay++
                    }

                    if (currentDay < 5) {
                        rowspanTracker[currentDay] = currentRowspanCounter + rowspan
                        week.accessDay(currentDay + 1) {
                            it.subjects.add(Subject(rowspan, cell))
                        }
                        currentDay++
                    }
                    cellIndex++
                }
                currentRowspanCounter += 2
            }
        }
    }
    return week
}