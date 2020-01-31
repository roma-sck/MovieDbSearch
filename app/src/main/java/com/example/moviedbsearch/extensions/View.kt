package com.example.moviedbsearch.extensions

import android.view.View

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()
