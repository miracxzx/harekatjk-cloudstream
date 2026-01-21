package com.harekatjk

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class HarekatJKPlugin: Plugin() {
    override fun load(context: Context) {
        // Eklenti yüklendiğinde provider'ı sisteme kaydet
        registerMainAPI(HarekatJKProvider())
    }
}
