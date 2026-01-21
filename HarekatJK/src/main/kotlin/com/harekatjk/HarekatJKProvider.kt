package com.harekatjk

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.newExtractorLink

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
                // Parse EXTINF line
                val nameMatch = Regex("tvg-name=\"([^\"]+)\"").find(line)
                val logoMatch = Regex("tvg-logo=\"([^\"]+)\"").find(line)
                val groupMatch = Regex("group-title=\"([^\"]+)\"").find(line)
                val displayName = line.substringAfterLast(",").trim()
                
                val name = nameMatch?.groupValues?.get(1) ?: displayName
                val logo = logoMatch?.groupValues?.get(1)
                val group = groupMatch?.groupValues?.get(1)
                
                // Next line should be the URL
                if (i + 1 < lines.size) {
                    val urlLine = lines[i + 1].trim()
                    if (urlLine.isNotEmpty() && !urlLine.startsWith("#")) {
                        entries.add(M3UEntry(name, urlLine, logo, group))
                        i++
                    }
                }
            }
            i++
        }
        return entries
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val response = app.get(m3uUrl).text
        val playlist = parseM3U(response)
        
        val groups = playlist.groupBy { it.group ?: "Genel" }.map { (groupName, list) ->
            HomePageList(
                groupName,
                list.map { item ->
                    newLiveSearchResponse(item.name, item.url) {
                        this.posterUrl = item.logo
                        this.lang = "tr"
                    }
                }
            )
        }
        
        return newHomePageResponse(groups)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val response = app.get(m3uUrl).text
        val playlist = parseM3U(response)
        
        return playlist.filter { it.name.contains(query, ignoreCase = true) }.map { item ->
            newLiveSearchResponse(item.name, item.url) {
                this.posterUrl = item.logo
                this.lang = "tr"
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val response = app.get(m3uUrl).text
        val playlist = parseM3U(response)
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
        callback.invoke(
            newExtractorLink(
                source = this.name,
                name = this.name,
                url = data,
                referer = "https://monotv529.com/"
            ) {
                this.quality = Qualities.Unknown.value
                this.isM3u8 = true
                this.headers = mapOf("User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5)")
            }
        )
        return true
    }
}
