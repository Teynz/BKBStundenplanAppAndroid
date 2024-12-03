package bkb.stundenplan.app

import android.os.Build
import androidx.annotation.RequiresApi
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.STUNDENPLANLOGIN
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.STUNDENPLANPASSWORT
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.VERZEICHNISSNAMELEHRER
import bkb.stundenplan.app.ParameterWhichMayChangeOverTime.Companion.VERZEICHNISSNAMESCHUELER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.Base64

class ScrapingJSoup {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getSelectBoxes(
        teacherMode: Boolean = false,
        loginName: String = STUNDENPLANLOGIN,
        password: String = STUNDENPLANPASSWORT
    ): Elements? {


        val navBarStundenplanURL =
            "https://stundenplan.bkb.nrw/${if (teacherMode) VERZEICHNISSNAMELEHRER else VERZEICHNISSNAMESCHUELER}/frames/navbar.htm"
        var selectBoxes: Elements?

        //authentication https://webscraping.ai/faq/jsoup/how-do-i-manage-sessions-and-authentication-with-jsoup
        val login = "$loginName:$password"
        val base64login = Base64.getEncoder().encodeToString(login.toByteArray())

        try {
            //withContext(Dispatchers.IO) {
                val siteDoc =
                    Jsoup.connect(navBarStundenplanURL)
                        .header("Authorization", "Basic $base64login")
                        .timeout(10000).userAgent("Mozilla/5.0").get()
                selectBoxes = siteDoc.select(".selectbox")
            //}
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


    @Suppress("UNCHECKED_CAST")
    fun <K, V> getMap(selectBox: Element?): Pair<String?, Map<K, V>?>? {
        if (selectBox == null) return null
        var map: MutableMap<K, V>?
        var nameOfSelectBox: String?

        selectBox.let { sBox ->
            nameOfSelectBox = sBox.attributes()["name"]
            map = mutableMapOf()

            sBox.select("option").forEach { listEntry ->

                listEntry.attributes()["value"]?.let { value ->
                    map?.let { map ->
                        map[value as K] = listEntry.text() as V
                    }
                }
            }


        }



        return Pair(nameOfSelectBox, map?.toMap())
    }

}