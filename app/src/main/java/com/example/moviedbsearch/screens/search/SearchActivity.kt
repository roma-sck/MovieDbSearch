package com.example.moviedbsearch.screens.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.moviedbsearch.R
import com.example.moviedbsearch.extensions.afterTextChanged
import com.example.moviedbsearch.screens.list.MoviesListActivity
import com.example.moviedbsearch.utils.ExtraNames
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {
    private var searchYear: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initUi()
        initListeners()
    }

    private fun initUi() {
        initYearAdapter()
        initGenresSpinner()
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
    }

    private fun checkFilledFields() {
        val isSearchFieldsFilled = searchYear != null || editTitle.text.isNotEmpty() ||
                editActorName.text.isNotEmpty() || ediDirectorName.text.isNotEmpty()
        search.isClickable = isSearchFieldsFilled
        search.isEnabled = isSearchFieldsFilled
        search.alpha = if (isSearchFieldsFilled) 1f else 0.5f
    }
}
