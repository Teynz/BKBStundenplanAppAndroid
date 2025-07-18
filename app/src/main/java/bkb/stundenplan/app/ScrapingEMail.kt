package bkb.stundenplan.app

import org.jsoup.Jsoup

class ScrapingEMail {
    var url = "https://www.berufskolleg-bottrop.de/unsere-schule/kollegium"

    data class LehrerEMail(
        val mail: String, val pictureLink: String
    )

    public fun getLehrerEMail(): List<LehrerEMail>? {

        var list: List<LehrerEMail> = mutableListOf()

        try {

            var Kollegium = Jsoup.connect(url).timeout(10000).userAgent("Mozilla/5.0").get()
            var Lehrer = Kollegium.select("#module-gallery-239-particle > div > div > div")
            Lehrer.forEach {
                var mail = it.text()
                var pictureLink = "https://www.berufskolleg-bottrop.de" + it.select("img").attr("src")
                list += LehrerEMail(mail, pictureLink)
            }

        } catch (e: Exception) {

            println("Could not fetch Kollegium from the Web")
        }



        return list
    }


}