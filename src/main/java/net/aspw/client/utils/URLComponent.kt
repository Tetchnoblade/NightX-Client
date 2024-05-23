package net.aspw.client.utils

object URLComponent {
    var gifLoaded = false
    var interval = 1_000_000_000L / 150
    const val WEBSITE = "https://aspw-w.github.io/NightX"
    const val STATUS = "$WEBSITE/database/data.txt"
    const val STAFFS = "$WEBSITE/database/staffs.txt"
    const val CHANGELOGS = "$WEBSITE/database/changelogs.txt"
    const val BUGS = "$WEBSITE/database/bugs.txt"
    const val DONORS = "$WEBSITE/donors/"
    const val CONFIGLIST = "$WEBSITE/configs/str/list.txt"
    const val CONFIGS = "$WEBSITE/configs/"
}