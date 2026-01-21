import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.IptvPlaylistParser
import com.lagradost.cloudstream3.utils.Qualities

class HarekatJKProvider : MainAPI() {
    override var name = "HarekatJK"
    override var mainUrl = "https://raw.githubusercontent.com/miracxzx/harekatjk-cloudstream/main/"
    override val supportedTypes = setOf(TvType.Live)

    override suspend fun getMainPage(page: Int, request: RequestContext): HomePageResponse {
        val m3uUrl = "https://raw.githubusercontent.com/miracxzx/harekatjk-cloudstream/main/harekatjk.m3u"
        
        val playlist = IptvPlaylistParser().parseM3U(app.get(m3uUrl).text)
        
        val items = playlist.map { item ->
            LiveSearchResponse(
                item.name,
                item.url,
                this.name,
                TvType.Live,
                item.logo,
                lang = "tr"
            )
        }

        // Gruplara göre ayırarak ana sayfada göster
        val groups = playlist.groupBy { it.group }.map { (groupName, list) ->
            HomePageList(
                groupName ?: "Genel",
                list.map { item ->
                    LiveSearchResponse(
                        item.name,
                        item.url,
                        this.name,
                        TvType.Live,
                        item.logo,
                        lang = "tr"
                    )
                }
            )
        }

        return HomePageResponse(groups)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val m3uUrl = "https://raw.githubusercontent.com/miracxzx/harekatjk-cloudstream/main/harekatjk.m3u"
        val playlist = IptvPlaylistParser().parseM3U(app.get(m3uUrl).text)
        
        return playlist.filter { it.name.contains(query, ignoreCase = true) }.map { item ->
            LiveSearchResponse(
                item.name,
                item.url,
                this.name,
                TvType.Live,
                item.logo,
                lang = "tr"
            )
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val playlist = IptvPlaylistParser().parseM3U(app.get("https://raw.githubusercontent.com/miracxzx/harekatjk-cloudstream/main/harekatjk.m3u").text)
        val item = playlist.find { it.url == url } ?: throw ErrorLoadingException("Kanal bulunamadı")

        return LiveStreamLoadResponse(
            item.name,
            url,
            this.name,
            url,
            item.logo,
            dataUrl = url
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCORS: Boolean,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // M3U dosyasındaki User-Agent ve Referer bilgilerini ekleyelim
        // Not: Basitlik adına genel bir header seti kullanıyoruz, istenirse M3U'dan çekilebilir.
        
        callback.invoke(
            ExtractorLink(
                this.name,
                this.name,
                data,
                referer = "https://monotv529.com/",
                quality = Qualities.Unknown.value,
                isM3u8 = true,
                headers = mapOf("User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5)")
            )
        )
        return true
    }
}
