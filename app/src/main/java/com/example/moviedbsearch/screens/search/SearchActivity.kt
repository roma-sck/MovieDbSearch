package com.example.moviedbsearch.screens.search

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.moviedbsearch.R
import com.example.moviedbsearch.screens.list.MoviesListActivity
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initUi()

        search.setOnClickListener {
            startActivity(Intent(this, MoviesListActivity::class.java))
        }
    }

    private fun initUi() {
        initYearAdapter()
        initGenresSpinner()

    }

    private fun initYearAdapter() {
        val years = ArrayList<String>()
        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        for (i in thisYear downTo 1900) {
            years.add(i.toString())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = adapter
    }

    private fun initGenresSpinner() {
    }
}
