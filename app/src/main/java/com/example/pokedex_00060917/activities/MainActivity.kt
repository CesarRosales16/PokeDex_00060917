package com.example.pokedex_00060917.activities

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.pokedex_00060917.adapters.PokemonAdapter
import com.example.pokedex_00060917.R
import com.example.pokedex_00060917.models.Pokemon
import com.example.pokedex_00060917.utilities.NetworkUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var viewAdapter: PokemonAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    var pokemonList: ArrayList<Pokemon> = ArrayList()
    var pokemonType: ArrayList<Pokemon> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FetchPokemonTask().execute("")
        searchPokemon()
        clearSearchPokemon()
    }
    fun initRecycler(pokemon: ArrayList<Pokemon>){
        viewManager = LinearLayoutManager(this)
        viewAdapter = PokemonAdapter(
            pokemon,
            { pokemonItem: Pokemon -> pokemonItemClicked(pokemonItem) })

        rv_pokemon_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun searchPokemon(){
        searchbarbutton.setOnClickListener {
            if (!searchbar.text.isEmpty()){
                QueryPokemonTask().execute("${searchbar.text}")
            }
        }
    }

    private fun clearSearchPokemon(){
        searchbarclearbutton.setOnClickListener {
            searchbar.setText("")
            FetchPokemonTask().execute("")
        }
    }

    private fun pokemonItemClicked(item: Pokemon){
        startActivity(Intent(this, PokemonViewer::class.java).putExtra("CLAVIER", item.url))
    }

    private inner class FetchPokemonTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg query: String): String {

            if (query.isNullOrEmpty()) return ""

            val ID = query[0]
            val pokeAPI = NetworkUtils().buildUrl(null,ID)

            return try {
                NetworkUtils().getResponseFromHttpUrl(pokeAPI)
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }

        }

        override fun onPostExecute(pokemonInfo: String) {
            val pokemon = if (!pokemonInfo.isEmpty()) {
                val root = JSONObject(pokemonInfo)
                val results = root.getJSONArray("results")

                for (i in 0 until results.length()) {
                    val result = JSONObject(results[i].toString())
                    val url = result.getString("url")
                    val urlParts = url.subSequence(34,url.length-1).toString()
                    val imgUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + urlParts.toInt() + ".png"
                    //Log.d("Pokemon",urlParts)
                    var pokemon = Pokemon(urlParts.toInt(),
                        result.getString("name").capitalize(),
                        R.string.text_na.toString(),
                        R.string.text_na.toString(),
                        R.string.text_na.toString(),
                        R.string.text_na.toString(),
                        R.string.text_na.toString(),
                        url,
                        imgUrl)
                    pokemonList.add(pokemon)
                }
            } else {
                for (i in 0 until 20) {
                    Pokemon(i, R.string.text_na.toString(), R.string.text_na.toString(), R.string.text_na.toString(),
                        R.string.text_na.toString(), R.string.text_na.toString(), R.string.text_na.toString(),R.string.text_na.toString(), R.string.text_na.toString())
                }
            }
            initRecycler(pokemonList)
        }
    }

    private inner class QueryPokemonTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg query: String): String {

            if (query.isNullOrEmpty()) return ""

            val ID = query[0]
            val pokeAPI = NetworkUtils().buildUrl("type",ID)

            return try {
                NetworkUtils().getResponseFromHttpUrl(pokeAPI)
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }

        }

        override fun onPostExecute(pokemonInfo: String) {
            val pokemon = if (!pokemonInfo.isEmpty()) {
                pokemonType.clear()
                val root = JSONObject(pokemonInfo)
                val results = root.getJSONArray("pokemon")
                for (i in 0 until results.length()) {
                    val resulty = JSONObject(results[i].toString())
                    val result = JSONObject(resulty.getString("pokemon"))
                    val url = result.getString("url")
                    val urlParts = url.subSequence(34,url.length-1).toString()
                    val imgUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + urlParts.toInt() + ".png"

                    var pokemon = Pokemon(urlParts.toInt(),
                        result.getString("name").capitalize(),
                        R.string.text_na.toString(),
                        R.string.text_na.toString(),
                        R.string.text_na.toString(),
                        R.string.text_na.toString(),
                        R.string.text_na.toString(),
                        url, imgUrl)
                    pokemonType.add(pokemon)
                }
            } else {
                for (i in 0 until 20) {
                    Pokemon(i, R.string.text_na.toString(), R.string.text_na.toString(), R.string.text_na.toString(),
                        R.string.text_na.toString(), R.string.text_na.toString(),R.string.text_na.toString(), R.string.text_na.toString(), R.string.text_na.toString())
                }
            }
            initRecycler(pokemonType)
        }
    }
}
