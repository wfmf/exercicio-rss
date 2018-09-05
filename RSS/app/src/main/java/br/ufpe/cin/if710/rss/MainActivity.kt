package br.ufpe.cin.if710.rss

import android.app.Activity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
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
    }

    override fun onStart() {
        super.onStart()
        try {
            doAsync {
                val rawXML = getRssFeed(feedUrl)
                val parsedContent = ParserRSS.parse(rawXML)
                uiThread {
                    conteudoRSS.text = parsedContent.toString()
                    progressIndicator.visibility = GONE
                }
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getRssFeed(feed: String): String {
        return URL(feed).readText()
    }
}
