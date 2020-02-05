package com.example.moviedbsearch.screens.search

import android.content.Intent
import android.os.Bundle
import com.androidbuts.multispinnerfilter.KeyPairBoolData
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
    private var searchGenres = listOf<Int>()
    private var personsSearchPage: Int? = null
    private var searchCrewMembersIds = listOf<Int>()
    private var searchActorsIds = listOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (getGenresList().isNullOrEmpty()) loadGenres()
        if (getPersonsList().isNullOrEmpty()) loadPopularPersons()
        initUi()
        initListeners()
    }

    private fun initUi() {
        initYearAdapter()
        initGenresSpinner()
        initCrewMembersSpinner()
        initActorsSpinner()
        checkFilledFields()
    }

    private fun initListeners() {
        editTitle.afterTextChanged { checkFilledFields() }
        search.setOnClickListener {
            if (connectedToInternet) {
                startSearchByParams()
            } else {
                showErrorMessage(R.string.error_no_internet_connection)
            }
        }
        clearAllFields.setOnClickListener {
            searchYear = null
            searchGenres = listOf()
            personsSearchPage = null
            searchCrewMembersIds = listOf()
            searchActorsIds = listOf()
            editTitle.text.clear()
            showAdultCheckbox.isChecked = false
            initUi()
        }
    }

    private fun startSearchByParams() {
        Intent(this, MoviesListActivity::class.java).apply {
            putExtra(ExtraNames.EXTRA_SEARCH_YEAR, searchYear)
            putIntegerArrayListExtra(ExtraNames.EXTRA_SEARCH_GENRES, searchGenres as? ArrayList)
            putExtra(ExtraNames.EXTRA_SEARCH_TITLE, editTitle.text.toString())
            putIntegerArrayListExtra(ExtraNames.EXTRA_SEARCH_ACTORS, searchActorsIds as? ArrayList)
            putIntegerArrayListExtra(ExtraNames.EXTRA_SEARCH_CREW_MEMBERS, searchCrewMembersIds as? ArrayList)
            putExtra(ExtraNames.EXTRA_SEARCH_SHOW_ADULT, showAdultCheckbox.isChecked)
            startActivity(this)
        }
    }

    private fun loadGenres() {
        scopeUiSilent.launch {
            val apiResponse = withContext(Dispatchers.IO) {
                moviesApiService.getMoviesGenres()
            }
            saveGenresList(apiResponse.genres)
            initGenresSpinner()
        }
    }

    private fun loadPopularPersons() {
        scopeUiSilent.launch {
            val firstApiResponse = withContext(Dispatchers.IO) {
                moviesApiService.getPopularPersons(null)
            }
            savePersonsList(firstApiResponse.results)
            val currentPage = firstApiResponse.page
            personsSearchPage = currentPage + 1
            val totalPages = firstApiResponse.total_pages
            if (totalPages > currentPage) {
                for (page in (currentPage + 1)..(if (totalPages <= 10) totalPages else 10)) {
                    val apiResponse = withContext(Dispatchers.IO) {
                        moviesApiService.getPopularPersons(personsSearchPage)
                    }
                    val persons = getPersonsList().toMutableList()
                    persons.addAll(apiResponse.results)
                    savePersonsList(persons)
                }
            }
            initCrewMembersSpinner()
            initActorsSpinner()
        }
    }

    private fun initYearAdapter() {
        val yearsData = mutableListOf<KeyPairBoolData>()
        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        for (i in thisYear downTo 1900) {
            val data = KeyPairBoolData()
            data.id = i.toLong()
            data.name = i.toString()
            data.isSelected = false
            yearsData.add(data)
        }
        yearSpinner.setItems(yearsData, -1) { items ->
            searchYear = items.filter { it.isSelected }.map { it.id.toInt() }.firstOrNull()
            checkFilledFields()
        }
    }

    private fun initGenresSpinner() {
        val genresData = mutableListOf<KeyPairBoolData>()
        val genres = getGenresList()
        for (i in genres.indices) {
            val data = KeyPairBoolData()
            data.id = genres[i].id.toLong()
            data.name = genres[i].name
            data.isSelected = false
            genresData.add(data)
        }
        genresSpinner.setEmptyTitle(getString(R.string.no_data_found))
        genresSpinner.setSearchHint(getString(R.string.find_genre))
        genresSpinner.setItems(genresData, -1) { items ->
            searchGenres = items.filter { it.isSelected }.map { it.id.toInt() }
            checkFilledFields()
        }
    }

    private fun preparePersonsSpinnerData(): MutableList<KeyPairBoolData> {
        val personsData = mutableListOf<KeyPairBoolData>()
        val persons = getPersonsList()
        for (i in persons.indices) {
            val data = KeyPairBoolData()
            data.id = persons[i].id.toLong()
            data.name = persons[i].name
            data.isSelected = false
            personsData.add(data)
        }
        return personsData
    }

    private fun initCrewMembersSpinner() {
        crewMembersMultiSpinner.setEmptyTitle(getString(R.string.no_data_found))
        crewMembersMultiSpinner.setSearchHint(getString(R.string.find_person))
        val personsData = preparePersonsSpinnerData()
        crewMembersMultiSpinner.setItems(personsData, -1) { items ->
            searchCrewMembersIds = items.filter { it.isSelected }.map { it.id.toInt() }
            checkFilledFields()
        }
    }

    private fun initActorsSpinner() {
        actorsMultiSpinner.setEmptyTitle(getString(R.string.no_data_found))
        actorsMultiSpinner.setSearchHint(getString(R.string.find_person))
        val personsData = preparePersonsSpinnerData()
        actorsMultiSpinner.setItems(personsData, -1) { items ->
            searchActorsIds = items.filter { it.isSelected }.map { it.id.toInt() }
            checkFilledFields()
        }
    }

    private fun checkFilledFields() {
        val isSearchFieldsFilled = searchYear != null || editTitle.text.isNotEmpty() ||
                searchGenres.isNotEmpty() || searchCrewMembersIds.isNotEmpty() ||
                searchActorsIds.isNotEmpty()
        search.isClickable = isSearchFieldsFilled
        search.isEnabled = isSearchFieldsFilled
        search.alpha = if (isSearchFieldsFilled) 1f else 0.5f
    }

    private fun getGenresList(): List<Genre> {
        return Hawk.get<List<Genre>>(PreferenceData.MOVIES_GENRES).orEmpty()
    }

    private fun saveGenresList(list: List<Genre>) {
        Hawk.put(PreferenceData.MOVIES_GENRES, list)
    }

    private fun getPersonsList(): List<PersonInfo> {
        return Hawk.get<List<PersonInfo>>(PreferenceData.POPULAR_PERSONS).orEmpty()
    }

    private fun savePersonsList(list: List<PersonInfo>) {
        Hawk.put(PreferenceData.POPULAR_PERSONS, list)
    }
}
