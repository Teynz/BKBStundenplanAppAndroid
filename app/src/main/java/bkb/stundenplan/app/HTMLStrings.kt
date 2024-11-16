package bkb.stundenplan.app

object HTMLStrings {


    private fun addDarkMode(
        darkBackground: String = "#000000",
        textcolor: String = "#ffffff",
        borderColor: String = "#ffffff"
    ): String {
        return """
b{
    color: #ffffff;
} 
body {
    background-color: ${darkBackground};
    color: ${textcolor};
}  
td{
    background-color: ${darkBackground};
}
"""
    }

    fun String.addDivHTML(): String {

        var result = this@addDivHTML.replaceFirst(
            "<table", "<div class=\"content-container\">\n" +
                    "<div class=\"table-container\" id=\"divActivites\" name=\"divActivites\" style=\"border-style: solid;border-width: 1px;border-color: #000000\"><table"
        )
            .replaceAfterLast(
                "</table>", "\n" +
                        "</div>\n" +
                        "</div>"
            )
        /*var result = "<div class=\"content-container\">\n" +
                "<div class=\"table-container\" id=\"divActivites\" name=\"divActivites\" style=\"border-style: solid;border-width: 1px;border-color: #000000\">" + this@addDivHTML + "</div>\n" +
                "</div>"*/

        return result
    }


    fun styleExperimentellerStundenplan(
        darkMode: Boolean = false,
        fontmultiplier: Float = 1.5F,
        border: Float = 2F,
        hPaddingL: Float, hPaddingR: Float
    ): String {
        return """<style>${if (darkMode) addDarkMode() else ""}            
.content-container {
    padding-left: ${hPaddingL * 100}%;
    padding-right: ${hPaddingR * 100}%;
    
    box-sizing: border-box;
}
table {
	border: ${border}em; //Border Size
    width: 100%%;
	margin: 0 auto;
   
}
.table-container {
    width: 100%;
    overflow-x: auto;
}

font{
	font-size: ${fontmultiplier}em;//also increases table size
}
</style>
"""
    }


}