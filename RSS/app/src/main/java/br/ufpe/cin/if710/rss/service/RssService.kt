package br.ufpe.cin.if710.rss.service

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import br.ufpe.cin.if710.rss.R
import br.ufpe.cin.if710.rss.db.SQLiteRSSHelper
import br.ufpe.cin.if710.rss.model.ItemRSS
import br.ufpe.cin.if710.rss.util.ParserRSS
import java.net.URL

class RssService : IntentService("RssService") {
    companion object {
        const val RSS_DOWNLOAD = "br.ufpe.cin.if710.rss.action.FETCH_AND_SAVE_DATA"
    }

    override fun onHandleIntent(i: Intent?) {
        when(i?.action) {
            RSS_DOWNLOAD -> handleFetchAndSaveData()
        }
    }

    private fun handleFetchAndSaveData() {
        Log.d("DB", "HANDLE_FETCH")
        val url = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getString(getString(R.string.rss_url_key), "http://leopoldomt.com/if1001/g1brasil.xml")
        val feed = fetchRSS(url)
        feed.forEach {
            SQLiteRSSHelper.getInstance(applicationContext).insertItem(it)
        }
        sendBroadcast(Intent(RSS_DOWNLOAD))
    }

    private fun fetchRSS(feedUrl: String): List<ItemRSS>  {
        // Inicia tarefa assíncrona
        val rawXML = getRssFeed(feedUrl)
        return ParserRSS.parse(rawXML)
    }

    // Forma bem simples de fazer requisição com
    // extension functions de Kotlin
    private fun getRssFeed(feed: String): String {
        return URL(feed).readText()
    }
}