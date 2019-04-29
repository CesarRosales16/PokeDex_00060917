package com.example.pokedex_00060917.activities

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.pokedex_00060917.R
import com.example.pokedex_00060917.models.Pokemon
import com.example.pokedex_00060917.utilities.NetworkUtils
import kotlinx.android.synthetic.main.viewer_element_pokemon.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class PokemonViewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewer_element_pokemon)

        val uri: String = this.intent.extras.getString("CLAVIER")
        setSupportActionBar(toolbarviewer)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        collapsingtoolbarviewer.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        collapsingtoolbarviewer.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)

        FetchPokemonTask().execute(uri)
    }

    fun init(pokemon: Pokemon) {
        Glide.with(this)
            .load(pokemon.sprite)
            .centerCrop()
            .error(R.drawable.ic_pokemon_go)
            .into(app_bar_image_viewer)
        collapsingtoolbarviewer.title = pokemon.name
        tv_number.text = pokemon.id.toString()
        tv_weight.text = pokemon.weight
        tv_height.text = pokemon.height
        tv_first_type.text = pokemon.fsttype
        tv_second_type.text = pokemon.sndtype
        tv_pokemon_xp.text = pokemon.xp
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed();true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class FetchPokemonTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg query: String): String {

            if (query.isNullOrEmpty()) return ""

            val url = query[0]
            val pokeAPI = Uri.parse(url).buildUpon().build()
            val finalurl = try {
                URL(pokeAPI.toString())
            } catch (e: MalformedURLException) {
                URL("")
            }

            return try {
                NetworkUtils().getResponseFromHttpUrl(finalurl)
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }

        }

        override fun onPostExecute(pokemonInfo: String) {
            val pokemon = if (!pokemonInfo.isEmpty()) {
                val root = JSONObject(pokemonInfo)
                val sprites = root.getString("sprites")
                val types = root.getJSONArray("types")
                val fsttype = JSONObject(types[0].toString()).getString("type")
                val sndtype = try {
                    JSONObject(types[1].toString()).getString("type")
                } catch (e: JSONException) {
                    ""
                }

                Pokemon(
                    root.getInt("id"),
                    root.getString("name").capitalize(),
                    JSONObject(fsttype).getString("name").capitalize(),
                    if (sndtype.isEmpty()) " " else JSONObject(sndtype).getString("name").capitalize(),
                    root.getString("weight"), root.getString("height"), root.getString("base_experience"),root.getString("location_area_encounters"),
                    JSONObject(sprites).getString("front_default")
                )

            } else {
                Pokemon(
                    0,
                    R.string.text_na.toString(),
                    R.string.text_na.toString(),
                    R.string.text_na.toString(),
                    R.string.text_na.toString(),
                    R.string.text_na.toString(),
                    R.string.text_na.toString(),
                    R.string.text_na.toString(),
                    R.string.text_na.toString()
                )
            }
            init(pokemon)
        }
    }
}