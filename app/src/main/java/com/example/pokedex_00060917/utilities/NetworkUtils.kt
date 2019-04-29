package com.example.pokedex_00060917.utilities

import android.net.Uri
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class NetworkUtils {
    val POKEMON_API_BASE_URL = "https://pokeapi.co/api/v2/"
    val POKEMON_API_ALL_POKEMONS_URL = "https://pokeapi.co/api/v2/pokemon?offset=0&limit=807"
    val POKEMON_INFO = "pokemon"
    val POKEMON_TYPE = "type"
    var builtUri = ""

    private val TAG = NetworkUtils::class.java.simpleName

    fun buildUrl(root: String?, pokeID: String): URL {
        if (root != null) {
            builtUri = Uri.parse(POKEMON_API_BASE_URL)
                .buildUpon()
                .appendPath(root)
                .appendPath(pokeID)
                .build().toString()
        } else {
            builtUri = Uri.parse(POKEMON_API_ALL_POKEMONS_URL)
                .buildUpon()
                .build().toString()
        }


        val url = try {
            URL(builtUri)
        } catch (e: MalformedURLException) {
            URL("")
        }


        Log.d("Pokemon", "Built URI $url")

        return url
    }

    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String {
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in` = urlConnection.inputStream

            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            return if (hasInput) {
                scanner.next()
            } else {
                ""
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}