package bkb.stundenplan.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bkb.stundenplan.app.Day
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime
import bkb.stundenplan.app.Subject
import bkb.stundenplan.app.ViewModelStundenplanData
import bkb.stundenplan.app.Week
import org.jsoup.nodes.Element


object StundenplanCustom {

    @Composable
    fun StundenplanCompose(
        modifier: Modifier, viewModel: ViewModelStundenplanData
    ) {
        val textSizeCells = if (viewModel.isPortrait) 11.sp else 7.5.sp

        val week = viewModel.week.collectAsStateWithLifecycle().value

        BoxWithConstraints(modifier = modifier) {


            Ruler(
                Modifier, this.maxHeight / 10, true
            ) {

                week?.let {
                    val mergeCells by viewModel.saveHandler.mergeCells.collectAsStateWithLifecycle()
                    WeekRow(
                        modifier = Modifier,
                        it,
                        Color.Red,
                        textSizeCells,
                        this.maxHeight / 10,
                        mergeCells,
                    )
                }


            }
        }


    }


    @Composable
    fun Ruler(
        modifier: Modifier,
        cellHeight: Dp,
        onlyShowStart: Boolean,
        clockFontSize: TextUnit = 7.sp,
        content: @Composable () -> Unit

    ) {
        val map = listOf(
            "7:30" to "8:15",
            "8:15" to "9:00",
            "9:15" to "10:00",
            "10:00" to "10:45",
            "11:00" to "11:45",
            "11:45" to "12:30",
            "12:45" to "13:30",
            "13:30" to "14:15",
            "14:15" to "15:00",
            "15:15" to "16:00"
        )





        Row(modifier = modifier) {
            Column {
                map.forEach { entry ->
                    Column(
                        modifier = Modifier.height(cellHeight),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {

                        Text(
                            text = entry.first,
                            fontSize = clockFontSize,
                            textAlign = TextAlign.Start
                        )




                        if (!onlyShowStart) Text(
                            text = entry.second,
                            fontSize = clockFontSize,
                            textAlign = TextAlign.Start
                        )

                    }

                }
            }




            Box(
                modifier = Modifier
                    .height(cellHeight * 10)
                    .weight(1f)
            ) {
                Column {

                    for (i in 1..10) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(cellHeight)
                        )
                    }
                }

                Box(modifier = Modifier.padding(start = 7.dp)) {
                    VerticalDivider()
                    content()
                }


            }

        }
    }

    @Composable
    fun WeekRow(
        modifier: Modifier,
        week: Week,
        farbeVertretung: Color,
        standardTextSize: TextUnit,
        cellHeight: Dp,
        mergeCells: Boolean
    ) {


        Row(modifier = modifier) {
            week.asList().forEach { day ->
                day?.let {
                    DayColumn(
                        Modifier.weight(1f),
                        day,
                        farbeVertretung,
                        standardTextSize,
                        cellHeight,
                        customCellColor = week.customCellColor,
                        mergeCells = mergeCells
                    )
                } ?: run { Text(text = "Fehler") }
            }

        }
    }

}

@Composable
fun DayColumn(
    modifier: Modifier,
    day: Day,
    farbeVertretung: Color,
    standardTextSize: TextUnit,
    cellHeight: Dp,
    showDate: Boolean = false,
    customCellColor: Boolean,
    mergeCells: Boolean
) {

    Column(modifier = modifier) {
        if (showDate) {
            Text(
                text = day.date.toString(), modifier = Modifier.padding()
            )
        }

        var currentIndex = 0
        while (currentIndex < day.subjects.size) {
            val subject = day.subjects[currentIndex]





            Box {
                SubjectToComposeable(
                    modifier = Modifier
                        .height(cellHeight * subject.multiplier / 2)
                        .padding(vertical = 3.dp, horizontal = 1.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    subject = subject,
                    farbeVertretung = farbeVertretung,
                    standardTextSize = standardTextSize,
                    customCellColor = customCellColor
                )
            }


            currentIndex++
        }
    }
}

@Composable
fun SubjectToComposeable(
    modifier: Modifier,
    subject: Subject,
    farbeVertretung: Color,
    standardTextSize: TextUnit,
    customCellColor: Boolean
) {


    //start at td
    //continue at > table > tbody > tr
    val subjectIsEmpty = subject.content.select("> table > tbody > tr > td > font").isEmpty()
    if (!subjectIsEmpty) {

        val subjectBGColor = if (customCellColor) Color.Unspecified else try {
            Color(android.graphics.Color.parseColor(subject.content.attr("bgcolor")))

        } catch (e: Exception) {
            Color.White
        }

        Column(
            modifier = modifier.background(subjectBGColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            subject.content.select("> table > tbody > tr").forEach { row ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    row.select("> td").forEach { cell ->


                        val fontColor = cell.select("> font").attr("color")
                        val composeColor: Color = if (!customCellColor) {
                            try {
                                Color(android.graphics.Color.parseColor(fontColor))
                            } catch (e: Exception) {
                                Color.Black
                            }

                        } else if (fontColor != "#000000" && fontColor != "#ff0000") {
                            try {
                                Color(android.graphics.Color.parseColor(fontColor))
                            } catch (e: Exception) {
                                Color.Unspecified
                            }
                        } else if (fontColor == "#ff0000") {
                            farbeVertretung
                        } else Color.Unspecified


                        if (cell.text().isNotEmpty()) {

                            fun hasTagInChildren(element: Element, tagName: String): Boolean {
                                if (element.tagName() == tagName) return true
                                for (child in element.children()) {
                                    if (hasTagInChildren(child, tagName)) return true
                                }
                                return false
                            }

                            val isBold = hasTagInChildren(cell, "b")
                            val isItalic = hasTagInChildren(cell, "i")



                            val textSize = try {
                                standardTextSize //* fontSize.toInt() / 2
                            } catch (e: Exception) {
                                standardTextSize
                            }

                            Text(text = ParameterWhichMayChangeOverTime.extractGebaeudeUndNummer(
                                cell.text()
                            )?.let { "${it.first}\n${it.second}" } ?: cell.text(),
                                color = composeColor,
                                textAlign = TextAlign.Center,
                                lineHeight = textSize,
                                fontSize = textSize,
                                fontStyle = if(isItalic) FontStyle.Italic else FontStyle.Normal,
                                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)


                        }
                    }
                }

            }
        }
    } else {
        Spacer(modifier = modifier)
    }
}

