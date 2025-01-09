package bkb.stundenplan.app

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
    val multiplier: Int, val content:Element
)

data class Day(
    val date: LocalDate?, val subjects: List<Subject>?
)
class Week(val monday: Day, val tuesday: Day, val wednesday: Day, val thursday: Day, val friday: Day)
{
    fun getToday(): Day
    {return Day(null,null)}


    fun getTomorrow(): Day
    {return Day(null,null)}

    fun getWeek(): List<Day>?
    {return null}
}

/**
 * Return the Week as `List<Day>?`
 */
 fun Document.getWeek(): List<Day>?
 {
     data class weekStringsClass(var mo:String = "",var tu:String = "",var we:String = "",var th:String = "",var f:String = "")
     fun getDates(dates:Element):weekStringsClass
     {
         var mo = "error";var tu = "error";var we = "error";var th = "error";var f = "error"
         var count = 0
         dates.select("td > table > tbody > tr > td > font").forEach {
            when(count)
            {
                0->mo = it.text()
                1->tu = it.text()
                2->we = it.text()
                3->th = it.text()
                4->f = it.text()
            }
            count++
        }
    return weekStringsClass(mo,tu,we,th,f)
     }


     var actualRowCounter = 0 //max 20 one cell takes 2
     var cDayOne = 0
     var cDayTwo = 0
     var cDayThree = 0
     var cDayFour = 0
     var cDayFive = 0

     var listWeek: MutableList<Day>? = null
     var weekStrings = weekStringsClass()

     val rows = this.select("table")?.get(0)?.selectFirst("table > tbody")?.select("> tr")

         rows?.forEach {counterRow->

        if(counterRow.childrenSize() != 0)
        {
            if(counterRow == rows.first())
            {
                weekStrings = getDates(counterRow)
            }
            else
            {
                val cells = counterRow.select(">td")
                var dayCounter = 1//diese Variable steht für Montag bis freitag
                cells?.forEach {counterCell->

                    if(counterCell != cells.first())
                    {
                        val multiplier = counterCell.attributes()["rowspan"]?.toInt() ?: 0
                        when(dayCounter) {
                            1 -> cDayOne += multiplier
                            2 -> cDayTwo += multiplier
                            3 -> cDayThree += multiplier
                            4 -> cDayFour += multiplier
                            5 -> cDayFive += multiplier
                        }

                        if()
                        {

                        }


                        dayCounter++
                    }
                }


                actualRowCounter++
            }


        }

    }
     return null
 }