package br.ufpe.cin.if710.rss

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.itemlista.view.*

// Adapter personalizado
class ItemRSSAdapter(private val items: List<ItemRSS>, private val c: Context): RecyclerView.Adapter<ItemRSSAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.itemlista, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder?.item_date.text = item.pubDate
        holder?.item_title.text = item.title
    }

    class ViewHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
        val item_title = item.item_titulo
        val item_date = item.item_data

        init {
            item.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            Toast.makeText(v.context, "You clicked on item $position", Toast.LENGTH_SHORT).show()
        }

    }
}