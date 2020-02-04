package com.example.moviedbsearch.screens.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.moviedbsearch.R
import com.example.moviedbsearch.extensions.afterTextChanged
import com.example.moviedbsearch.models.Genre
import com.example.moviedbsearch.models.PersonInfo
import com.example.moviedbsearch.screens.base.BaseActivity
import com.example.moviedbsearch.screens.list.MoviesListActivity
import com.example.moviedbsearch.utils.ExtraNames
import com.example.moviedbsearch.utils.PreferenceData
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : BaseActivity() {
    private var searchYear: Int? = null
    private var searchGenres = mutableListOf<Int>()
    private var personsSearchPage: Int? = null
    private var searchPersons = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (Hawk.get<List<Genre>>(PreferenceData.MOVIES_GENRES).isNullOrEmpty()) {
            loadGenres()
        }
        if (Hawk.get<List<PersonInfo>>(PreferenceData.POPULAR_PERSONS).isNullOrEmpty()) {
            loadPopularPersons()
        }
        initUi()
        initListeners()
    }

    private fun initUi() {
        initYearAdapter()
        initGenresSpinner()
        initPersonsSpinner()
        checkFilledFields()
    }

    private fun initListeners() {
        editTitle.afterTextChanged { checkFilledFields() }
        editActorName.afterTextChanged { checkFilledFields() }
        ediDirectorName.afterTextChanged { checkFilledFields() }
        search.setOnClickListener {
            Intent(this, MoviesListActivity::class.java).apply {
                putExtra(ExtraNames.EXTRA_SEARCH_YEAR, searchYear)
                putExtra(ExtraNames.EXTRA_SEARCH_TITLE, editTitle.text.toString())
                putExtra(ExtraNames.EXTRA_SEARCH_ACTOR, editActorName.text.toString())
                putExtra(ExtraNames.EXTRA_SEARCH_DIRECTOR, ediDirectorName.text.toString())
                putExtra(ExtraNames.EXTRA_SEARCH_SHOW_ADULT, showAdultCheckbox.isChecked)
                putStringArrayListExtra(ExtraNames.EXTRA_SEARCH_GENRES, ArrayList<String>())
                startActivity(this)
            }
        }
    }

    private fun loadGenres() {
        scopeUiSilent.launch {
            val apiResponse = withContext(Dispatchers.IO) {
                moviesApiService.getMoviesGenres()
            }
            Hawk.put(PreferenceData.MOVIES_GENRES, apiResponse.genres)
            initGenresSpinner()
        }
    }

    private fun loadPopularPersons() {
        scopeUiSilent.launch {
            val firstApiResponse = withContext(Dispatchers.IO) {
                moviesApiService.getPopularPersons(null)
            }
            Hawk.put(PreferenceData.POPULAR_PERSONS, firstApiResponse.results)
            val currentPage = firstApiResponse.page
            personsSearchPage = currentPage + 1
            val totalPages = firstApiResponse.total_pages
            if (totalPages > currentPage) {
                for (page in (currentPage + 1)..(if (totalPages <= 10) totalPages else 10)) {
                    val apiResponse = withContext(Dispatchers.IO) {
                        moviesApiService.getPopularPersons(personsSearchPage)
                    }
                    val persons = Hawk.get<List<PersonInfo>>(PreferenceData.POPULAR_PERSONS).orEmpty().toMutableList()
                    persons.addAll(apiResponse.results)
                    Hawk.put(PreferenceData.POPULAR_PERSONS, persons)
                }
            }
            initPersonsSpinner()
        }
    }

    private fun initYearAdapter() {
        val years = ArrayList<Int>()
        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        for (i in thisYear downTo 1900) {
            years.add(i)
        }
        val adapter = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = adapter
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

            override fun onItemSelected(p: AdapterView<*>, view: View, position: Int, id: Long) {
                searchYear = if (position != -1) years[position] else null
                checkFilledFields()
            }
        }
    }

    private fun initGenresSpinner() {
        val genres = Hawk.get<List<Genre>>(PreferenceData.MOVIES_GENRES).orEmpty()
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genres.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genresSpinner.adapter = adapter
        genresSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

            override fun onItemSelected(p: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != -1) {
                    searchGenres.add(genres[position].id)
                } else {
                    searchGenres.clear()
                }
                checkFilledFields()
            }
        }
    }

    private fun initPersonsSpinner() {
        val persons = Hawk.get<List<PersonInfo>>(PreferenceData.POPULAR_PERSONS).orEmpty()
        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, persons.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        personsSpinner.adapter = adapter
        personsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

            override fun onItemSelected(p: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != -1) {
                    searchPersons.add(persons[position].id)
                } else {
                    searchPersons.clear()
                }
                checkFilledFields()
            }
        }
    }

    private fun checkFilledFields() {
        val isSearchFieldsFilled = searchYear != null || editTitle.text.isNotEmpty() ||
                editActorName.text.isNotEmpty() || ediDirectorName.text.isNotEmpty() ||
                searchGenres.isNotEmpty()
        search.isClickable = isSearchFieldsFilled
        search.isEnabled = isSearchFieldsFilled
        search.alpha = if (isSearchFieldsFilled) 1f else 0.5f
    }
}
