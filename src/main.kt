fun main() {
    var episode = 1
    
    print("Enter First Episode ID: ")
    var episodeID = readLine()!!.toInt()

    print("Enter Show ID: ")
    val showID = readLine()!!.toInt()

    print("Enter Number of Episodes: ")
    val episodeCount = readLine()!!.toInt()

    repeat(episodeCount) {
        println("https://storage.googleapis.com/auengine.appspot.com/$showID/sub/${episode++}_${episodeID++}.mp4")
    }
}