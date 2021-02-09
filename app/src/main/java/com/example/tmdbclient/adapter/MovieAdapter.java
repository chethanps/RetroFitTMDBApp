package com.example.tmdbclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tmdbclient.R;
import com.example.tmdbclient.model.Movie;
import com.example.tmdbclient.views.MovieActivity;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Movie> movies;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.movieRating.setText(movie.getVoteAverage().toString());
        holder.movieTitle.setText(movie.getOriginalTitle());
        String imagePath = "https://image.tmdb.org/t/p/w500/" + movie.getPosterPath();
        Glide.with(context)  //2
                .load(imagePath) //3
                .placeholder(R.drawable.loading_icon_167801_435)
                .into(holder.movieImage);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView movieImage;
        private TextView movieTitle;
        private TextView movieRating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            movieImage = itemView.findViewById(R.id.iv_movie);
            movieTitle = itemView.findViewById(R.id.tv_title);
            movieRating = itemView.findViewById(R.id.tv_rating);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, MovieActivity.class);
                    intent.putExtra("movieDetails", movies.get(position));
                    context.startActivity(intent);
                }
            });

        }
    }
}
