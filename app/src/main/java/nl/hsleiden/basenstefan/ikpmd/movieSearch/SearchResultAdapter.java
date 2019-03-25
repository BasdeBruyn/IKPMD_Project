package nl.hsleiden.basenstefan.ikpmd.movieSearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.hsleiden.basenstefan.ikpmd.MovieActivity;
import nl.hsleiden.basenstefan.ikpmd.R;
import nl.hsleiden.basenstefan.ikpmd.api.Movie;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private Movie[] movies;

    public SearchResultAdapter(Movie[] movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_search_result_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        try {
            Movie movie = movies[index];
            viewHolder.movie.setText(movie.getTitle() + " (" + movie.getYear() + ")");
            viewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), MovieActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("imdbId", movies[index].getImdbID());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            });
        } catch (IndexOutOfBoundsException exception){
            Log.d("SearchResultAdapter",
                    "IndexOutOfBoundsException: " + index);
        }
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView movie;
        private View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            movie = itemView.findViewById(R.id.Movie);
        }
    }
}
