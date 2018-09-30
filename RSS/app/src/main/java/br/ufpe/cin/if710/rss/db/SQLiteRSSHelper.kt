package br.ufpe.cin.if710.rss.db

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import br.ufpe.cin.if710.rss.model.ItemRSS
import org.jetbrains.anko.db.*


class SQLiteRSSHelper private constructor(
        private var c: Context) : ManagedSQLiteOpenHelper(c, DATABASE_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Cria tabela items
        db.createTable(DATABASE_TABLE, true,
                    ITEM_ROWID to INTEGER + PRIMARY_KEY + AUTOINCREMENT + UNIQUE,
                        ITEM_TITLE to TEXT + NOT_NULL,
                        ITEM_DATE to TEXT + NOT_NULL,
                        ITEM_DESC to TEXT + NOT_NULL,
                        ITEM_LINK to TEXT + NOT_NULL,
                        ITEM_UNREAD to INTEGER + NOT_NULL)
        Log.d("DB", "CREATED")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //estamos ignorando esta possibilidade no momento
        throw RuntimeException("nao se aplica")
    }

    fun insertItem(item: ItemRSS): Long {
        if (getItemRSS(item.link) == null) {
            return insertItem(item.title, item.pubDate, item.description, item.link) ?: -1
        }
        return -1
    }

    private fun insertItem(title: String, pubDate: String, description: String, link: String): Long? {
        return c.database.use {
            insert(DATABASE_TABLE,
                    ITEM_TITLE to title,
                    ITEM_DATE to pubDate,
                    ITEM_DESC to description,
                    ITEM_LINK to link,
                    ITEM_UNREAD to 1)
        }
    }

    fun getItemRSS(link: String): ItemRSS? {
        var res: ItemRSS? = null
        try {
            res = c.database.use {
                select(DATABASE_TABLE)
                        .whereSimple("link = ?", link)
                        .exec {
                            if (this.count == 1) parseSingle(RSSItemParser())
                            else null
                        }
            }
        } catch(e: SQLException) {
            res = null
        }
        return res
    }

    fun markAsUnread(link: String): Boolean {
        val unread = 1
        return setUnreadValue(link, unread)
    }

    private fun setUnreadValue(link: String, value: Int): Boolean {
        try {
            c.database.use {
                update(DATABASE_TABLE, ITEM_UNREAD to value)
                        .whereSimple("link = ?", link)
                        .exec()
            }
        } catch(e: SQLException) {
            return false
        }
        return true
    }

    fun markAsRead(link: String): Boolean {
        val read = 0
        return setUnreadValue(link, read)
    }

    companion object {
        //Nome do Banco de Dados
        private val DATABASE_NAME = "rss"
        //Nome da tabela do Banco a ser usada
        val DATABASE_TABLE = "items"
        //Versão atual do banco
        private val DB_VERSION = 1

        private var db: SQLiteRSSHelper? = null

        //Definindo Singleton
        @Synchronized
        fun getInstance(c: Context): SQLiteRSSHelper {
            if (db == null) {
                db = SQLiteRSSHelper(c.applicationContext)
            }
            return db!!
        }

        //Definindo constantes que representam os campos do banco de dados
        val ITEM_ROWID = RssProviderContract._ID
        val ITEM_TITLE = RssProviderContract.TITLE
        val ITEM_DATE = RssProviderContract.DATE
        val ITEM_DESC = RssProviderContract.DESCRIPTION
        val ITEM_LINK = RssProviderContract.LINK
        val ITEM_UNREAD = RssProviderContract.UNREAD

        //Definindo constante que representa um array com todos os campos
        val columns = arrayOf(ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD)

        //Definindo constante que representa o comando de criação da tabela no banco de dados
        private val CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
                ITEM_ROWID + " integer primary key autoincrement, " +
                ITEM_TITLE + " text not null, " +
                ITEM_DATE + " text not null, " +
                ITEM_DESC + " text not null, " +
                ITEM_LINK + " text not null, " +
                ITEM_UNREAD + " boolean not null);"
    }

    // Faz o parser de uma linha do banco
    class RSSItemParser: MapRowParser<ItemRSS> {
        override fun parseRow(columns: Map<String, Any?>): ItemRSS {
            var unread = false
            if (columns.getValue(ITEM_UNREAD).toString().toInt() == 1) unread = true
            return ItemRSS(
                    title=columns.getValue(ITEM_TITLE).toString(),
                    link=columns.getValue(ITEM_LINK).toString(),
                    pubDate=columns.getValue(ITEM_DATE).toString(),
                    description=columns.getValue(ITEM_DESC).toString(),
                    unread=unread
            )
        }
    }

}

// Usa extension function para q o banco esteja acessível de qualquer
// lugar onde o contexto da aplicação também esteja
val Context.database: SQLiteRSSHelper
    get() = SQLiteRSSHelper.getInstance(applicationContext)