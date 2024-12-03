package bkb.stundenplan.app

import android.os.Build
import androidx.annotation.RequiresApi
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.Base64

class ScrapingJSoup {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getSelectBoxes(
        teacherMode: Boolean = false,
        loginName: String = "schueler",
        password: String = "stundenplan"
    ): Elements? {

        val navBarStundenplanURL =
            "https://stundenplan.bkb.nrw/${if (teacherMode) "lehrer" else "schueler"}/frames/navbar.htm"
        var selectBoxes: Elements? = null

        //authentication https://webscraping.ai/faq/jsoup/how-do-i-manage-sessions-and-authentication-with-jsoup
        val login = "$loginName:$password"
        val base64login = Base64.getEncoder().encodeToString(login.toByteArray())

        try {
            var siteDoc =
                Jsoup.connect(navBarStundenplanURL).header("Authorization", "Basic " + base64login)
                    .timeout(10000).userAgent("Mozilla/5.0").get()
            selectBoxes = siteDoc.select(".selectbox")
        }
        catch (e: Exception) {
            selectBoxes = null
            println("Could not fetch select Boxes from the Web")
        }
        return if (selectBoxes == null && teacherMode) {
            getSelectBoxes()
        }
        else {
            selectBoxes
        }
    }


    fun <K,V>getMap(selectBoxes: Element?): Map<K, V>?
    {
        var map: MutableMap<K, V>? = null
        var nameOfSelectBox: String?

        selectBoxes.let { sBox ->
            if (sBox != null) {
                nameOfSelectBox = sBox.attributes()["name"]

                sBox.select("option").forEach { listEntry ->

                    listEntry.attributes()["value"]?.let { value ->
                        map?.let { it[value as K] = listEntry.text() as V }
                    }
                }

            }


        }



    return map
    }

}