package com.example.moviedbsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviedbsearch.api.ApiConstants
import com.example.moviedbsearch.models.MovieInfo
import kotlinx.android.synthetic.main.movie_list_item.view.*

class MoviesAdapter(
    private val movies: List<MovieInfo>,
    private val onItemClick: (MovieInfo) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.VH>() {

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(container.context).inflate(
                R.layout.movie_list_item, container, false
            )
        )
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(movies[position])
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: MovieInfo) {
            itemView.movieTitle.text = item.title
            itemView.movieYear.text = item.release_date
            val posterUrl = ApiConstants.THE_MOVIES_DB_IMAGE_BASE_URL_WITH_SIZE + item.poster_path
            Glide.with(itemView.context)
                .load(posterUrl)
                .into(itemView.movieLogo)

            itemView.setOnClickListener { onItemClick.invoke(item) }
        }
    }
}