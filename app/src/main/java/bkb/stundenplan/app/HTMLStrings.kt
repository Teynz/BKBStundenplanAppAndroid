package bkb.stundenplan.app

object HTMLStrings {
    data class Styling(
        var darkBackground: String = "#000000",
        var typeValue: String,
        var darkMode: Boolean = false,
        var textcolor: String = "#ffffff",
        var fontmultiplier: Float = 1.5F,
        var border: Float = 1F,
        var borderColor: String = if (darkMode) "#ffffff" else "#000000"

    )


    private fun addDarkMode(
        styling: Styling
    ): String {
        return """
b{
    color: #ffffff;
} 
body {
    background-color: ${styling.darkBackground};
    color: ${styling.textcolor};
}  
td{
    background-color: ${styling.darkBackground};
}
"""
    }


    fun styleExperimentellerStundenplan(
        styling: Styling
    ): String {
        return """<style>
            ${if (styling.darkMode&&(styling.typeValue == ParameterWhichMayChangeOverTime.CLASSES_SHORT)) addDarkMode(styling) else ""}  
            ${
            if (styling.typeValue == ParameterWhichMayChangeOverTime.CLASSES_SHORT) layoutSchueler(
                styling
            )
            else ""
        }
</style>
"""
    }

    private fun layoutSchueler(styling: Styling): String {
        return """td{
height: 1;
width:1;
}
table {
border: ${styling.border}em; //Border Size
 border-style: solid;
    width: 100%;
    height: 100%;
    table-layout: auto;
    border-collapse: collapse;
  }
  
  th, td {
    text-align: center;
    white-space: normal;
    word-wrap: break-word;
    padding: 0;
  }
  
  /* Adjust the width of the first column */
  table th:first-child,
  table td:first-child {
    width: 1%;
  }
  
  /* Allow text wrapping in the first column */
  table th:first-child,
  table td:first-child {
    white-space: normal;
    word-wrap: break-word;
  }
  
  /* Ensure all content fits without scrolling */
  body, html {
    margin: 0;
    padding: 0;
    height: 100%;
    overflow: hidden;
  }
  :root {
  --base-font-size: calc(0.25vw + 0.7vh);
}
font[size="1"] { font-size: calc(var(--base-font-size) * 1.2); }
font[size="2"] { font-size: calc(var(--base-font-size) * 1.6); }
font[size="3"] { font-size: calc(var(--base-font-size) * 2.4); }
font[size="4"] { font-size: calc(var(--base-font-size) * 2.4); }
font[size="5"] { font-size: calc(var(--base-font-size) * 2.4); }
font[size="6"] { font-size: calc(var(--base-font-size) * 2.4); }
font[size="7"] { font-size: calc(var(--base-font-size) * 2.4); }

font[size] {
  text-align: center;
}"""

    }
}