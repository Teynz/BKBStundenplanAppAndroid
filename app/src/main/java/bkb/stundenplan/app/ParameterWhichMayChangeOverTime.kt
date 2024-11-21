package bkb.stundenplan.app

class ParameterWhichMayChangeOverTime {/*
            * An Herr Grantz und die anderen IT lehrer:
            * Hier werden konstanten festgelegt, welche dafür verwendet werden, die Arrays aus
            * der navbar.htm zu finden
            *sollten diese sich nach der Abwesenheit der Schule ändern, hoffe ich, dass sie diese einfach abändern können
            * */


    companion object {
        fun selectType(type: String, typeArrays: Scraping.TypeArrays?): MutableMap<Int, String>? {
            return typeArrays?.let {
                return when (type) {
                    "c" -> typeArrays.classes
                    "t" -> typeArrays.teachers
                    "r" -> typeArrays.rooms
                    "g" -> typeArrays.corridors
                    "w" -> mutableMapOf(typeArrays.flc1.toInt() to "-Alle-")
                    "v" -> mutableMapOf(typeArrays.flc1.toInt() to "-Alle-")
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
        const val CLASSES_Short = "c"
        const val TEACHERS_FULL = "teachers"
        const val TEACHERS_Short = "t"
        const val ROOMS_FULL = "rooms"
        const val CORRIDORS_FULL = "corridors"
        const val FLC1 = "flc1"
        const val FLTE = "flte"

    }

}