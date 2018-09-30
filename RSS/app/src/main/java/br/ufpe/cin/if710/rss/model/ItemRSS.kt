package br.ufpe.cin.if710.rss.model

class ItemRSS(val title: String, val link: String, val pubDate: String, val description: String, var unread: Boolean=true) {
    override fun toString(): String {
        return title
    }


}
