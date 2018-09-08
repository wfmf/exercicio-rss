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
    private val feedUrl = "http://pox.globo.com/rss/g1/tecnologia/"

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        conteudoRSS.layoutManager = LinearLayoutManager(this)
        conteudoRSS.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    override fun onStart() {
        super.onStart()
        try {
            // Inicia tarefa assíncrona
            doAsync {
                val rawXML = getRssFeed(feedUrl)
                val parsedContent: List<ItemRSS> = ParserRSS.parse(rawXML)
                // Destroi o progess indicator e inicializa o adapter
                // com a lista de itens do RSS
                uiThread {
                    conteudoRSS.adapter = ItemRSSAdapter(parsedContent, applicationContext)
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
