package br.ufpe.cin.if710.rss

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
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

    override fun onStart() {
        super.onStart()
        try {
            fetchRSS()
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            feedUrl = PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .getString(getString(R.string.rss_url_key), "")
            fetchRSS()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun fetchRSS() {
        // Inicia tarefa assíncrona
        doAsync {
            val rawXML = getRssFeed(feedUrl)
            val parsedContent: List<ItemRSS> = ParserRSS.parse(rawXML)
            // Esconde o progess indicator e muda a lista
            // de itens do RSS
            uiThread {
                adapter.items = parsedContent
                conteudoRSS.adapter = adapter
                // GONE faz uma view não ser visível e não tomar espaço
                progressIndicator.visibility = GONE
            }
        }
    }

    // Forma bem simples de fazer requisição com
    // extension functions de Kotlin
    private fun getRssFeed(feed: String): String {
        return URL(feed).readText()
    }
}
