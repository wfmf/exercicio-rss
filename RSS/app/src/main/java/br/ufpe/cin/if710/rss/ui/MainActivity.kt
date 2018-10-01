package br.ufpe.cin.if710.rss.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import br.ufpe.cin.if710.rss.util.ParserRSS
import br.ufpe.cin.if710.rss.R
import br.ufpe.cin.if710.rss.db.SQLiteRSSHelper
import br.ufpe.cin.if710.rss.db.database
import br.ufpe.cin.if710.rss.model.ItemRSS
import br.ufpe.cin.if710.rss.service.RssService
import br.ufpe.cin.if710.rss.service.RssService.Companion.RSS_DOWNLOAD
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

//OUTROS LINKS PARA TESTAR...
//http://rss.cnn.com/rss/edition.rss
//http://pox.globo.com/rss/g1/brasil/
//http://pox.globo.com/rss/g1/ciencia-e-saude/
//http://pox.globo.com/rss/g1/tecnologia/

class MainActivity : Activity() {
    private var feedUrl = ""
    private var adapter = ItemRSSAdapter(listOf(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        conteudoRSS.layoutManager = LinearLayoutManager(this)
        conteudoRSS.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        // Para evitar o erro "No adapter attached; skipping layout" criei primeiro um adapter com uma
        // lista vazia e depois que a requisição é resolvida mudo a lista associada ao adapter
        conteudoRSS.adapter = adapter
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        feedUrl = sharedPref.getString(getString(R.string.rss_url_key), "http://leopoldomt.com/if1001/g1brasil.xml")

        handleDownloadCompleted.onReceive(this, Intent())

        startService(Intent(this, RssService::class.java).apply {
            action = DOWNLOAD_SERVICE
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.settings_button -> startActivity(Intent(this@MainActivity, PrefsMenuActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        Log.d("DB", "ON_RESUME")
        super.onResume()
        registerReceiver(this.handleDownloadCompleted, IntentFilter(RSS_DOWNLOAD))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(this.handleDownloadCompleted)
    }

    private val handleDownloadCompleted = object: BroadcastReceiver() {
        override fun onReceive(ctx: Context?, i: Intent?) {
            doAsync {
                val feedItems = ctx?.database?.getItems()
                uiThread {
                    if (feedItems != null) {
                        adapter.items = feedItems
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
