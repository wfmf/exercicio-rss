package br.ufpe.cin.if710.rss.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.ufpe.cin.if710.rss.R
import br.ufpe.cin.if710.rss.db.database
import br.ufpe.cin.if710.rss.model.ItemRSS
import kotlinx.android.synthetic.main.itemlista.view.*

// Adapter personalizado
class ItemRSSAdapter(var items: List<ItemRSS> = listOf(), private val c: Context): RecyclerView.Adapter<ItemRSSAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.itemlista, parent, false)
        return ViewHolder(view, c)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder?.item_date.text = item.pubDate
        holder?.item_title.text = item.title
        holder?.item_link = Uri.parse(item.link)
    }

    class ViewHolder(item: View, context: Context): RecyclerView.ViewHolder(item), View.OnClickListener {
        val item_title = item.item_titulo
        val item_date = item.item_data
        val c = context
        var item_link: Uri? = null

        init {
            item.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("LINK", item_link?.toString())
            if (c.database.markAsRead(item_link?.path.toString())) {
                val i = Intent()
                i.action = ACTION_VIEW
                i.data = item_link
                i.`package` = "com.android.chrome"
                i.addCategory(CATEGORY_DEFAULT)
                i.addCategory(CATEGORY_BROWSABLE)
                startActivity(this.c, i, null)
            }
        }

    }
}