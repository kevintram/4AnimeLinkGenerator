import java.lang.IllegalArgumentException

fun main(args: Array<String>) {

    println()
    var episode = 1

    print("Enter First Episode ID: ")
    var episodeID = readLine()!!.toInt()

    print("Enter Show ID: ")
    val showID = readLine()!!.toInt()

    print("Enter Number of Episodes: ")
    val episodeCount = readLine()!!.toInt()

    print("Sub or Dub?: ")
    val vocalTrack: String = readLine()!!.trim().toLowerCase()
    if (vocalTrack != "sub" && vocalTrack != "dub")
        throw IllegalArgumentException("Please enter sub or dub")

    println()

    println("----- LINKS -----")

    repeat(episodeCount) {
        println("https://storage.googleapis.com/auengine.appspot.com/$showID/$vocalTrack/${episode++}_${episodeID++}.mp4")
    }

    println()
}
