package br.ufpe.cin.if710.rss.ui

import android.os.Bundle
import android.app.Activity
import android.content.SharedPreferences
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import br.ufpe.cin.if710.rss.R

class PrefsMenuActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferencias)
    }

    class RSSFeedPreferenceFragment : PreferenceFragment() {
        private var mListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
        private var mRSSFeedURL: Preference? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.feed_prefs)

            val prefs  = PreferenceManager.getDefaultSharedPreferences(activity)
            mRSSFeedURL = preferenceManager.findPreference(getString(R.string.rss_url_key))

            mRSSFeedURL?.summary = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(getString(R.string.rss_url_key), "Error")


            mListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                val newValue = sharedPreferences.getString(getString(R.string.rss_url_key), "Unknown url")
                mRSSFeedURL?.summary = newValue
            }

            prefs.registerOnSharedPreferenceChangeListener(mListener)

            mListener?.onSharedPreferenceChanged(prefs, getString(R.string.rss_url_key))
        }

    }

}
