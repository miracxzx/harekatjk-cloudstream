package com.harekatjk

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import java.net.URL

class HarekatJKProvider : MainAPI() {
    override var name = "HarekatJK"
    override var mainUrl = "https://raw.githubusercontent.com/miracxzx/harekatjk-cloudstream/main"
    override val supportedTypes = setOf(TvType.Live)
    override var lang = "tr"
    override val hasMainPage = true

    private val m3uUrl = "$mainUrl/harekatjk.m3u"

    // M3U entry data class
    data class M3UEntry(
        val name: String,
        val url: String,
        val logo: String?,
        val group: String?
    )

    // Simple M3U parser
    private fun parseM3U(content: String): List<M3UEntry> {
        val entries = mutableListOf<M3UEntry>()
        val lines = content.lines()
        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF:")) {
                val nameMatch = Regex("tvg-name=\"([^\"]+)\"").find(line)
                val logoMatch = Regex("tvg-logo=\"([^\"]+)\"").find(line)
                val groupMatch = Regex("group-title=\"([^\"]+)\"").find(line)
                val displayName = line.substringAfterLast(",").trim()
                
                val entryName = nameMatch?.groupValues?.get(1) ?: displayName
                val logo = logoMatch?.groupValues?.get(1)
                val group = groupMatch?.groupValues?.get(1)
                
                if (i + 1 < lines.size) {
                    val urlLine = lines[i + 1].trim()
                    if (urlLine.isNotEmpty() && !urlLine.startsWith("#")) {
                        entries.add(M3UEntry(entryName, urlLine, logo, group))
                        i++
                    }
                }
            }
            i++
        }
        return entries
    }

    private suspend fun fetchM3UContent(): String {
        return try {
            URL(m3uUrl).readText()
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val content = fetchM3UContent()
        val playlist = parseM3U(content)
        
        val groups = playlist.groupBy { it.group ?: "Genel" }.map { (groupName, list) ->
            HomePageList(
                groupName,
                list.map { item ->
                    newLiveSearchResponse(item.name, item.url) {
                        this.posterUrl = item.logo
                    }
                }
            )
        }
        
        return newHomePageResponse(groups)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val content = fetchM3UContent()
        val playlist = parseM3U(content)
        
        return playlist.filter { it.name.contains(query, ignoreCase = true) }.map { item ->
            newLiveSearchResponse(item.name, item.url) {
                this.posterUrl = item.logo
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val content = fetchM3UContent()
        val playlist = parseM3U(content)
        val item = playlist.find { it.url == url } ?: throw ErrorLoadingException("Kanal bulunamadı")

        return newLiveStreamLoadResponse(item.name, url, url) {
            this.posterUrl = item.logo
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        @Suppress("DEPRECATION")
        callback.invoke(
            ExtractorLink(
                source = this.name,
                name = this.name,
                url = data,
                referer = "https://monotv529.com/",
                quality = Qualities.Unknown.value,
                isM3u8 = true,
                headers = mapOf("User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5)")
            )
        )
        return true
    }
}
