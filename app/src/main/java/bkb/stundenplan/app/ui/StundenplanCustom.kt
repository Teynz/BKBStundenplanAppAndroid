package bkb.stundenplan.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import bkb.stundenplan.app.Day
import bkb.stundenplan.app.Subject
import bkb.stundenplan.app.ViewModelStundenplanData
import bkb.stundenplan.app.getWeek
import bkb.stundenplan.app.ui.StundenplanCustom.SubjectToComposeable
import org.jsoup.Jsoup

object StundenplanCustom {

    @Composable
    fun StundenplanCompose(modifier: Modifier, viewModel: ViewModelStundenplanData) {
        var week = viewModel.scraping.stundenplanSite?.getWeek()
        Row()
        {

        }



    }
    @Composable
    fun RowHours()
    {
        val map = mapOf("7:30" to "8:15", "8:15" to "9:00",
            "9:15" to "10:00","10:00" to "10:45",
            "11:00" to "11:45", "11:45" to "12:30",
            "12:45" to "13:30", "13:30" to "14:15",
            "14:15" to "15:00", "15:15" to "16:00")

    }


    @Composable
    fun DayColumn(
        modifier: Modifier, day: Day, farbeVertretung: Color, standardTextSize: TextUnit
    ) {
        Column {
            Text(text = day.date.toString(), modifier = Modifier.padding())

            day.subjects.forEach { subject ->
                for (count in 1..(subject.multiplier / 2)) {

                    SubjectToComposeable(Modifier, subject, farbeVertretung, standardTextSize)
                }
            }
        }
    }

    @Composable
    fun SubjectToComposeable(
        modifier: Modifier, subject: Subject, farbeVertretung: Color, standardTextSize: TextUnit
    ) {
        //start at td
        //continue at > table > tbody > tr

        Column {
            subject.content.select("> table > tbody > tr").forEach { row ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.select("> td").forEach { cell ->
                        val fontSize = cell.select("> font").attr("size")
                        val fontColor = cell.select("> font").attr("color")
                        val composeColor: Color =
                            if (fontColor != "#000000" && fontColor != "#ff0000") {
                                try {
                                    Color(android.graphics.Color.parseColor(fontColor))
                                }
                                catch (e: Exception) {
                                    Color.Unspecified
                                }
                            }
                            else if (fontColor == "#ff0000") {
                                farbeVertretung
                            }
                            else Color.Unspecified

                        Text(
                            text = cell.text(), color = composeColor,
                            fontSize = standardTextSize * fontSize.toInt()/2
                        )
                    }
                }

            }
        }
    }




    /*@Composable
    @Preview
    fun DayColumnPreview() {
    val doc = Jsoup.parse(exampleStundenplanString().html)

    val week = doc.getWeek()

        DayColumn(Modifier, week.getDay(1), Color.Red, 16.sp)
    }*/
}

@Composable
@Preview
fun SubjectToComposeablePreview() {
    val html =
        "<td colspan=\"12\" rowspan=\"2\" align=\"center\" nowrap=\"1\"><table><tbody><tr><td width=\"25%\" nowrap=\"1\"><font size=\"2\" face=\"Arial\" color=\"#000000\">\n" + "<b>Phys</b>\n" + "</font> </td>\n" + "<td width=\"25%\" nowrap=\"1\"><font size=\"1\" face=\"Arial\">\n" + "11)\n" + "</font> </td>\n" + "<td width=\"25%\" nowrap=\"1\"><font size=\"2\" face=\"Arial\">\n" + "A 212\n" + "</font> </td>\n" + "<td width=\"25%\" nowrap=\"1\"><font size=\"2\" face=\"Arial\">\n" + "Loev\n" + "</font> </td>\n" + "</tr><tr><td width=\"25%\" nowrap=\"1\"><font size=\"2\" face=\"Arial\">\n" + "<b>Phys</b>\n" + "</font> </td>\n" + "<td width=\"25%\" nowrap=\"1\"><font size=\"2\" face=\"Arial\">\n" + "A 212\n" + "</font> </td>\n" + "<td colspan=\"2\" width=\"25%\" nowrap=\"1\"><font size=\"2\" face=\"Arial\">\n" + "Gohr\n" + "</font> </td>\n" + "</tr></tbody></table></td>"
    val element = Jsoup.parse("<table><tr>$html</tr></table>").select("td").first()
    SubjectToComposeable(Modifier, Subject(2, element), Color.Red, 16.sp)

}