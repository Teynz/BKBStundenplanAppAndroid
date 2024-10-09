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

    fun styleExperimentellerStundenplan(
        darkMode: Boolean = false, fontmultiplier: Float = 1.5F, border: Float = 2F
    ): String {
        return """<style>${if (darkMode) addDarkMode() else ""}  
table {

      margin-left: auto;//To Center Content
      margin-right: auto;

    width: 100%;//To Fill Width
    border: ${border}em; //Border Size
}

font{
font-size: ${fontmultiplier}em;//also increases table size
}

</style>
"""
    }


}