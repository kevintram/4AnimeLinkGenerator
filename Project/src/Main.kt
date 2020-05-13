import java.io.*
import java.net.URL


fun main(args: Array<String>) {

    print("Enter Link of First Episode: ")
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
    //split link
    val linkSplit = firstLink.split('/')
    //get juicy info
    val showID = linkSplit[4].toInt()
    val vocalTrack = linkSplit[5]
    var episode = linkSplit[6][0].toInt() - 48
    var episodeID = linkSplit[6].substringAfter('_').substringBefore('.').toInt()

    print("Enter Number of Episodes You Want To Retrieve: ")
    val episodeCount = readLine()!!.toInt()

    println()

    println("----- LINKS -----")

    val links = ArrayList<String>()
    for (i in 1..episodeCount) {
        val link = "https://storage.googleapis.com/auengine.appspot.com/$showID/$vocalTrack/${episode++}_${episodeID++}.mp4"
        if (urlIsMp4(link)) {
            links.add(link)
            println(links.last())
        } else {
            println("Link #$i is not valid. Putting a stop to printing links.")
            break
        }
    }

    val toDownload = askYesOrNo(question = "Do you want to download these episodes?")

    if (toDownload) {
        print("Enter the path of the folder you want to download to: ")
        val path = readLine()!!
        for (i in 0 until links.size) {
            println("\nDownloading Video #${i+1}...")
            downloadVideo(links[i],path)
            println("Finished Downloading Video #${i+1}!")
            println()
        }
    }

    println()
}

/**
 * continually prompts for a yes or no answer until the user types in "yes" or "no" (case doesn't matter)
 * returns false if the answer is no
 * returns true if the answer is yes
 */
fun askYesOrNo(question: String): Boolean {
    print("\n$question Enter yes or no: ")
    var answer = readLine()!!.toLowerCase().trim()
    while(true) {
        if (answer == "yes" || answer == "no" ) {
            return answer == "yes"
        } else {
            print("\nPlease answer yes or no: ")
            answer = readLine()!!.toLowerCase().trim()
        }
    }
}
/**
 * checks if a url links to a valid .mp4 file
 * val inputStream will throw a file io exception if url does not link to a valid .mp4
 */
fun urlIsMp4(url: String): Boolean {
    return try {
        val inputStream = URL(url).openConnection().getInputStream()
        true
    }  catch (e: Exception) {
        false
    }
}

/**
 * downloads the mp4 file to the given path
 */
fun downloadVideo(url: String, path: String) {
    try {
        //get input stream from url
        val bufferedInputStream = BufferedInputStream(URL(url).openConnection().getInputStream())
        //create file
        val name = url.substringAfterLast('/').substringBefore('.')
        val file = File("$path/$name.mp4")
        //write to file
        val fileOutputStream = FileOutputStream(file.path)
        var count: Int
        val buffer = ByteArray(4 * 1024)
        count = bufferedInputStream.read(buffer)

        while(count != -1) {
            fileOutputStream.write(buffer,0,count)
            count = bufferedInputStream.read(buffer)
        }
        //save file
        file.createNewFile()
    }  catch (e: Exception) {
        println("An error occurred")
        e.printStackTrace()
    }
}
