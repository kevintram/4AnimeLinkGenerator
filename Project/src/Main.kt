import java.io.*
import java.net.URL
import java.util.*


fun main(args: Array<String>) {
    val firstLink = promptUntilValid("Enter the link from 4anime of the first episode you want to download: ",
            "Please enter a valid link of an episode from 4Anime!\n" ) { link ->
        link.contains("4anime.to") && link.contains("episode")
    }

    val episodeCount = promptUntilValid("Enter the number of episodes you want to retrieve. Enter \"all\" to download all episodes: ",
            "Please enter a number! ") { answer ->
        answer.toIntOrNull() != null || answer.toLowerCase() == "all"
    }.toIntOrNull() ?: Int.MAX_VALUE

    println("\n----- LINKS -----")

    val sourceLinks = mutableListOf<String>()
    val showTitle = firstLink.substringAfterLast("/").substringBefore("-episode")
    var episode = firstLink.substringAfter("episode-").substringBefore("?").toInt()
    for (i in 1..episodeCount) {
        val episodeStr = if (episode >= 10) episode.toString() else "0$episode"
        episode++
        val episodeLink = "https://4anime.to/${showTitle}-episode-${episodeStr}"
        if (linkIsValid(episodeLink)) {
            val source = getVideoSource(episodeLink)
            println(source)
            sourceLinks.add(source)
        } else {
            break
        }
    }

    val toDownload = askYesOrNo(question = "Do you want to download these episodes?")

    if (toDownload) {
        val path = getPathFromUser()
        println()
        for (i in 0 until sourceLinks.size) {
            println("Downloading Video #${i+1}...")
            downloadVideo(sourceLinks[i],path)
            println("Finished Downloading Video #${i+1}!")
            println()
        }
    }
}

/**
 * Continually prompts user for an input until isValid returns true
 * returns the user input
 */
fun promptUntilValid(prompt: String, errorMessage: String, isValid: (String) -> Boolean): String {
    print("\n$prompt")
    var input = readLine()!!
    while (true) {
        if (isValid(input)) {
            return input
        } else {
            print("ERROR: $errorMessage")
            input = readLine()!!
        }
    }
}

/**
 * continually prompts for a yes or no answer until the user types in "yes" or "no" (case doesn't matter)
 * returns false if the answer is no
 * returns true if the answer is yes
 */
fun askYesOrNo(question: String): Boolean {
    val answer = promptUntilValid("$question Enter yes or no: ","Please enter yes or no! ") { answer ->
        answer.toLowerCase().trim()
        answer == "yes" || answer == "no"
    }
    return answer == "yes"
}

fun getPathFromUser(): String {
    return promptUntilValid("Enter the path of the folder you want to save to: ","Please enter a valid path! ") { path ->
        val file = File("$path/test.txt")
        try {
            file.createNewFile()
            file.delete()
            true
        } catch (e: Exception) {
            false
        }
    }
}

fun linkIsValid(link: String): Boolean {
    try {
        val inputStream = URL(link).openConnection().getInputStream()
        return true
    }  catch (e: Exception) {}
    return false
}

/**
 * Gets the source of the video of the page
 */
fun getVideoSource(link: String): String {
    var html = ""
    try {
        val connection = URL(link).openConnection()
        val scanner = Scanner(connection.getInputStream())
        scanner.useDelimiter("\\Z")
        html = scanner.next()
        scanner.close()
    } catch (e: Exception) {
        println("An error occurred")
        e.printStackTrace()
    }
    var source = ""
    for (line in html.split("\n")) {
        if (line.contains("<source")) {
            source = line.substringAfter("\"").substringBefore("\"")
        }
    }
    if (!source.contains("google")) { //if source is not a direct link to google cloud storage
        val baseLink = source.substringBefore("?").substringAfter("https://")
        // first try linear theater app spot
        val linearTheaterLink = "https://storage.googleapis.com/linear-theater-254209.appspot.com/${baseLink}"
        if (linkIsValid(linearTheaterLink)) {
            return linearTheaterLink
        }
        // next try justawesome app spot
        val justAwesomeLink = "https://storage.googleapis.com/justawesome-183319.appspot.com/${baseLink}"
        if (linkIsValid(justAwesomeLink)) {
            return linearTheaterLink
        }
        // if neither doesn't work then uh oh
        throw Exception("Uh oh can't find the appspot for this show. Please open an issue on github.")
    }
    return source
}

/**
 * downloads the mp4 file from the url to the given path
 */
fun downloadVideo(url: String, path: String) {
    val name = url.substringAfterLast('/').substringBefore('.')
    val pathName = "$path/$name.mp4"
    try {
        //get input stream from url
        val bufferedInputStream = BufferedInputStream(URL(url).openConnection().getInputStream())
        //create file
        val file = File(pathName)
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
        return
    } catch (e: IOException) {
        println("An error occurred")
        e.printStackTrace()
        return
    } catch (e: Exception) {
        println("An error occurred")
        e.printStackTrace()
        return
    }
}