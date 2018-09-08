package br.ufpe.cin.if710.rss

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.net.URL

class MainActivity : Activity() {
    private var feedUrl = ""
    private var adapter = ItemRSSAdapter(listOf(), this)

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss // Não funciou, erro de parsing
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        feedUrl = getString(R.string.rssfeed)
        conteudoRSS.layoutManager = LinearLayoutManager(this)
        conteudoRSS.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        // Para evitar o erro "No adapter attached; skipping layout" criei primeiro um adapter com uma
        // lista vazia e depois que a requisição é resolvida mudo a lista associada ao adapter
        conteudoRSS.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        try {
            // Inicia tarefa assíncrona
            doAsync {
                val rawXML = getRssFeed(feedUrl)
                val parsedContent: List<ItemRSS> = ParserRSS.parse(rawXML)
                // Esconde o progess indicator e muda a lista
                // de itens do RSS
                uiThread {
                    adapter.items = parsedContent
                    // GONE faz uma view não ser visível e não tomar espaço
                    progressIndicator.visibility = GONE
                }
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }

    // Forma bem simples de fazer requisição com
    // extension functions de Kotlin
    private fun getRssFeed(feed: String): String {
        return URL(feed).readText()
    }
}
