package bkb.stundenplan.app

import androidx.compose.ui.graphics.Color

data class Text(
    val text: String?, val bold: Boolean, val color: Color
)

data class subject(
    val multiplier: Int, val subject: Text?, val room: Text?, val teacher: Text?
)

class StundenPlanClass {


}