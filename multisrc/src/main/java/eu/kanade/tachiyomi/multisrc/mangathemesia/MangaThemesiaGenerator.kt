package eu.kanade.tachiyomi.multisrc.mangathemesia

import generator.ThemeSourceData.SingleLang
import generator.ThemeSourceGenerator

// Formerly WPMangaStream & WPMangaReader -> MangaThemesia
class MangaThemesiaGenerator : ThemeSourceGenerator {

    override val themePkg = "mangathemesia"

    override val themeClass = "MangaThemesia"

    override val baseVersionCode: Int = 25

    override val sources = listOf(
        SingleLang("Constellar Scans", "https://constellarscans.com", "en", isNsfw = true, overrideVersionCode = 15),
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            MangaThemesiaGenerator().createAll()
        }
    }
}
