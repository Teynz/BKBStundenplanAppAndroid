package bkb.stundenplan.app

class ParameterWhichMayChangeOverTime {/*
            * An Herr Grantz und die anderen IT lehrer:
            * Hier werden konstanten festgelegt, welche dafür verwendet werden, die Arrays aus
            * der navbar.htm zu finden
            *sollten diese sich nach der Abwesenheit der Schule ändern, hoffe ich, dass sie diese einfach abändern können
            * */


    companion object {
        val regexFilterRedundantNumbers = "[0-9]+\\)".toRegex()//filter welcher z.b. "3)" oder "10)" erkennen soll

        //Regex Objekt für die Räume, nur so können diese später getrennt werden, damit die Stundenplan Ansicht schöner ist.
        //Berücksichtigt sollen z.B. werden: "x 004", "THR3","E 103", "D 020", "CK 09", "C 212", "BK 12", "B 311" "A 409"
        fun extractGebaeudeUndNummer(input: String): Pair<String, String>? {
            val pattern = Regex("""^([A-Za-z]+)\s*(\d+)$""")
            val matchResult = pattern.matchEntire(input)
            return matchResult?.let {
                val prefix = it.groupValues[1]
                val number = it.groupValues[2]
                prefix to number
            }
        }


        fun selectType(type: String, typeArrays: ScrapingJSoup.TypeArrays?): MutableMap<Int, String>? {
            return typeArrays?.let {
                return when (type) {
                    "c" -> typeArrays.classes
                    "t" -> typeArrays.teachers
                    "r" -> typeArrays.rooms
                    "g" -> typeArrays.corridors
                    "w" -> mutableMapOf(VERKLAVALUE to "-Alle-")
                    "v" -> mutableMapOf(VERLEHVALUE to "-Alle-")
                    else -> typeArrays.classes

                }
            }

        }

        /*
        * An Herr Grantz und die anderen IT lehrer:
        * Hier werden konstanten festgelegt, welche dafür verwendet werden, die Arrays aus
        * der navbar.htm zu finden
        *sollten diese sich nach der Abwesenheit der Schule ändern, hoffe ich, dass sie diese einfach abändern können
        * */
        const val CLASSES_FULL = "classes"
        const val CLASSES_SHORT = "c"
        const val TEACHERS_FULL = "teachers"
        const val ROOMS_FULL = "rooms"
        const val CORRIDORS_FULL = "corridors"
        const val FLC1 = "flc1"
        const val FLTE = "flte"
        const val VERKLAVALUE = 0
        const val VERLEHVALUE = 0

        const val STUNDENPLANLOGIN ="schueler"
        const val STUNDENPLANPASSWORT ="stundenplan"

        const val VERZEICHNISSNAMESCHUELER = "schueler"
        const val VERZEICHNISSNAMELEHRER = "lehrer"

    }

}