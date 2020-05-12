import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


fun main(args: Array<String>) {

    print("Enter Link of First Episode To Download: ")
    var firstLink = readLine()!!

    var isValidUrl = false
    while (!isValidUrl) {
        if (firstLink.contains("https://storage.googleapis.com/auengine.appspot.com") && urlIsMp4(firstLink)) {
            isValidUrl = true
        } else {
            print("ERROR: Please enter a valid url from the auengine data base: ")
            firstLink = readLine()!!
        }
    }

    if (firstLink.contains('?')) {//if link contains google access id
        firstLink = firstLink.substringBefore('?')
    }
    //split link
    val linkSplit = firstLink.split('/')
    //get juicy info
    val showID = linkSplit[4].toInt()
    val vocalTrack = linkSplit[5]
    var episode = linkSplit[6][0].toInt() - 48
    var episodeID = linkSplit[6].substringAfter('_').substringBefore('.').toInt()

    print("Enter Number of Episodes To Download: ")
    val episodeCount = readLine()!!.toInt()

    println()

    println("----- LINKS -----")

    var link: String
    for (i in 1..episodeCount) {
        link = "https://storage.googleapis.com/auengine.appspot.com/$showID/$vocalTrack/${episode++}_${episodeID++}.mp4"
        if (urlIsMp4(link)) {
            println(link)
        } else {
            println("Link #$i is not valid. Putting a stop to printing links.")
            break
        }

    }

    println()
}

/**
 * checks if a url links to a valid .mp4 file
 * inputStream will throw a file io exception if url does not link to a valid .mp4
 */
fun urlIsMp4(url: String): Boolean {
    return try {
        val inputStream = URL(url).openConnection().getInputStream()
        true
    }  catch (e: Exception) {
        false
    }
}
