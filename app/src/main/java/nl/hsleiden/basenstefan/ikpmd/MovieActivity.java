package nl.hsleiden.basenstefan.ikpmd;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import nl.hsleiden.basenstefan.ikpmd.api.MovieDetailed;
import nl.hsleiden.basenstefan.ikpmd.api.MovieRepository;
import nl.hsleiden.basenstefan.ikpmd.api.SearchResponse;
import nl.hsleiden.basenstefan.ikpmd.movieSearch.SearchActivity;
import nl.hsleiden.basenstefan.ikpmd.movieSearch.SearchResultAdapter;

public class MovieActivity extends AppCompatActivity {

    private TextView title;
    private TextView year;
    private TextView imdbId;
    private TextView imdbRating;
    private TextView plot;
    private ImageView poster;

    MovieDetailed movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        title = findViewById(R.id.TitleTxt);
        year = findViewById(R.id.YearTxt);
        imdbId = findViewById(R.id.ImdbIdTxt);
        imdbRating = findViewById(R.id.ImdbRatingTxt);
        plot = findViewById(R.id.PlotText);
        poster = findViewById(R.id.PosterImg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final String id = bundle.getString("imdbId");
            MovieRepository.fetchMovie(id, this, this::onResult, this::onError);
        }

        Uri data = getIntent().getData();
        if (data != null) {
            final List<String> params = data.getQueryParameters("imdbId");
            if (params.size() == 0) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                Toast.makeText(this, "No imdbId given!"
                        , Toast.LENGTH_LONG).show();
            }
            else {
                final String id = params.get(0);
                MovieRepository.fetchMovie(id, this, this::onResult, this::onError);
            }
        }

        Button shareButton = findViewById(R.id.ShareButton);
        shareButton.setOnClickListener(this::onShare);

    }

    private void onShare(View view) {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle("Share " + movie.getTitle())
                .setText("https://open.movie.watchlist/?imdbId=" + movie.getImdbID())
                .startChooser();
    }

    private void onResult(MovieDetailed fetchedMovie) {
        if (fetchedMovie.getTitle() != null){
            movie = fetchedMovie;

            title.setText(fetchedMovie.getTitle());
            year.setText(fetchedMovie.getYear());
            imdbId.setText(fetchedMovie.getImdbID());
            imdbRating.setText(fetchedMovie.getImdbRating());
            plot.setText(fetchedMovie.getPlot());
            Picasso.get().load(fetchedMovie.getPoster()).into(poster);
        } else {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
            Toast.makeText(this, "Couldn't find movie!"
                    , Toast.LENGTH_LONG).show();
        }

    }

    private void onError(VolleyError volleyError) {
        Log.d("Error fetching movie", volleyError.getMessage());
        startActivity(new Intent(this, SearchActivity.class));
        finish();
        Toast.makeText(this, "Couldn't find movie!"
                , Toast.LENGTH_LONG).show();
    }
}
