package bkb.stundenplan.app

import org.jsoup.Jsoup

class ScrapingEMail {
    var url = "https://www.berufskolleg-bottrop.de/unsere-schule/kollegium"

    data class LehrerEMail(
        val mail: String, val pictureLink: String
    )

    fun getLehrerEMail(): List<LehrerEMail>? {

        var list: List<LehrerEMail> = mutableListOf()

        try {

            var Kollegium = Jsoup.connect(url).timeout(10000).userAgent("Mozilla/5.0").get()
            var Lehrer = Kollegium.select("body > div.uk-modal.uk-open > div")
            Lehrer.forEach {
                var mail = it.select("#gallery-modal-info-1963109349 > div").text()
                var pictureLink = it.select("body > div.uk-modal.uk-open > div > div.uk-lightbox-content > img").attr("href")
                list += LehrerEMail(mail, pictureLink)
            }

        } catch (e: Exception) {

            println("Could not fetch Kollegium from the Web")
        }



        return list
    }


}