package bkb.stundenplan.app

import android.os.Build
import androidx.annotation.RequiresApi
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.Base64

class ScrapingJSoup {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getSelectBoxes(
        teacherMode: Boolean = false,
        loginName: String = "schueler",
        password: String = "stundenplan"
    ): Elements?
    {

        val navBarStundenplanURL =
            "https://stundenplan.bkb.nrw/${if (teacherMode) "lehrer" else "schueler"}/frames/navbar.htm"


        //authentication https://webscraping.ai/faq/jsoup/how-do-i-manage-sessions-and-authentication-with-jsoup
        val login = "$loginName:$password"
        val base64login = Base64.getEncoder().encodeToString(login.toByteArray())

         var siteDoc = Jsoup.connect(navBarStundenplanURL)
             .header("Authorization", "Basic " + base64login)
             .timeout(10000)
             .userAgent("Mozilla/5.0")
             .get()
        return  siteDoc.select(".selectbox")


    }
}