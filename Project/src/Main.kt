import java.io.*
import java.net.URL


fun main(args: Array<String>) {

    val firstLink = promptUntilValid("Enter Link of First Episode: ","Please enter a valid url from the 4Anime database! " +
            "\nIt can be found by clicking the download button of an episode.\n") { link ->
        link.contains("storage.googleapis.com/") && urlIsMp4(link)
    }

    //split link
    val linkSplit = firstLink.split('/')
    //get juicy info
    val appSpotVersion = linkSplit[3]
    val databaseVersion = linkSplit[4].substringAfter(' ')
    val showName = linkSplit[5]
    var episode = linkSplit[6].substringBeforeLast('-').substringAfterLast('-').toInt()
    val quality = linkSplit[6].substringBeforeLast('.').substringAfterLast('-')

    val episodeCount = promptUntilValid("Enter the number of episodes you want to retrieve, enter all to download all episodes: ", "Please enter a number! ") { answer ->
        answer.toIntOrNull() != null || answer.toLowerCase() == "all"
    }.toIntOrNull() ?: Int.MAX_VALUE

    println("\n----- LINKS -----")

    val links = ArrayList<String>()
    for (i in 1..episodeCount) {
        val episodeStr = if (episode >= 10) episode.toString() else "0$episode"
        episode++
        val link = "https://storage.googleapis.com/${appSpotVersion}/${databaseVersion}/${showName}/${showName}-Episode-${episodeStr}-${quality}.mp4"
        if (urlIsMp4(link)) {
            links.add(link)
            println(links.last())
        } else {
            break
        }
    }

    val toDownload = askYesOrNo(question = "Do you want to download these episodes?")

    if (toDownload) {
        val path = getPath()
        println()
        for (i in 0 until links.size) {
            println("Downloading Video #${i+1}...")
            downloadVideo(links[i],path)
            println("Finished Downloading Video #${i+1}!")
            println()
        }
    }

    val toSaveAsTxt = askYesOrNo(question = "Do you want to save these links as a .txt file?")

    if (toSaveAsTxt) {
        val path = getPath()
        println("\nWhat do you want the file name to be? ")
        val name = readLine()!!
        saveAsTxt(name,path,links)
    }

    println()
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

fun getPath(): String {
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

/**
 * checks if a url links to a valid .mp4 file
 * val inputStream will throw a file io exception if url does not link to a valid .mp4
 */
fun urlIsMp4(url: String): Boolean {
    // try 10 times before declaring dead link because sometimes data base will deny a request
    repeat(10) {
        try {
            val inputStream = URL(url).openConnection().getInputStream()
            return true
        }  catch (e: Exception) {}
    }
    return false
}
/**
 * downloads the mp4 file from the url to the given path
 */
fun downloadVideo(url: String, path: String) {
    val name = url.substringAfterLast('/').substringBefore('.')
    val pathName = "$path/$name.mp4"
    // keep on trying to download file because sometimes, 4anime database will deny request
    while (true) {
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

        } catch (e: Exception) {
            println("An error occurred")
            e.printStackTrace()
            return
        }
    }
}

/**
 * saves the links in a .txt file at the given path with the given name
 */
fun saveAsTxt(name: String, path: String, links: ArrayList<String>) {
    try {
        val file = FileWriter("$path/$name")

        for (link in links) {
            if (link != links.last()) file.write("$link\n") else file.write(link)
        }

        file.close()

        print("Links saved successfully!")
    } catch (e: Exception) {
        println("An error occurred")
        e.printStackTrace()
    }
}