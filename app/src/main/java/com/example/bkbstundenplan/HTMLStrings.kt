package com.example.bkbstundenplan

object HTMLStrings {


    private fun addDarkMode():String
    {
        return """
            b{
color: #ffffff;
} 
        body {
background-color: #000000;
color: #ffffff;
}  
td{
    background-color: #000000;
}
        """
    }

    fun styleExperimentellerStundenplan(darkMode:Boolean = false, fontmultiplier:Float = 1.5F, border:Float = 2F):String{
        return """<style>${if(darkMode) addDarkMode() else ""}  
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
"""}


}