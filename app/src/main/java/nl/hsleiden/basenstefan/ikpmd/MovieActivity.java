package nl.hsleiden.basenstefan.ikpmd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.hsleiden.basenstefan.ikpmd.api.MovieDetailed;
import nl.hsleiden.basenstefan.ikpmd.api.MovieRepository;
import nl.hsleiden.basenstefan.ikpmd.movieSearch.SearchActivity;

public class MovieActivity extends BaseActivity {

    private TextView title;
    private TextView year;
    private TextView imdbId;
    private TextView imdbRating;
    private TextView plot;
    private ImageView poster;
    String id = "";

    MovieDetailed movie;

    boolean remove = false;
    private Button addOrRemoveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.onCreate(savedInstanceState, R.layout.activity_movie);

        title = findViewById(R.id.TitleTxt);
        year = findViewById(R.id.YearTxt);
        imdbId = findViewById(R.id.ImdbIdTxt);
        imdbRating = findViewById(R.id.ImdbRatingTxt);
        plot = findViewById(R.id.PlotText);
        poster = findViewById(R.id.PosterImg);
        addOrRemoveButton = findViewById(R.id.AddMovieButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("imdbId");
            MovieRepository.fetchMovie(id, this, this::onResult, this::onError);
            addOrDelete(id);
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
                addOrDelete(id);
            }
        }
        Button shareButton = findViewById(R.id.ShareButton);
        if (remove) {
            addOrRemoveButton.setOnClickListener(this::onRemove);
        }
        else {
            addOrRemoveButton.setOnClickListener(this::onAdd);
        }
        addOrRemoveButton.setOnClickListener(this::addOrRemove);
        shareButton.setOnClickListener(this::onShare);

    }

    private void addOrDelete(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(currentUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    List<String> movieIds;
                    if (data != null && data.size() > 0) {
                        movieIds = new ArrayList<>(data.keySet());
                        for (String movieId: Arrays.copyOf(movieIds.toArray(), movieIds.size(), String[].class)) {
                            if (movieId.equals(id)) {
                                remove = true;
                                addOrRemoveButton.setText(R.string.remove);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addOrRemove(View view) {
        if (remove) {
            onRemove(view);
        } else {
            onAdd(view);
        }
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void onAdd(View view) {
        FirebaseUser user = getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(user.getUid());
        if (!id.equals(""))
            databaseReference.child(id).setValue(id);
    }

    private void onRemove(View view) {
        FirebaseUser user = getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(user.getUid());
        if (!id.equals(""))
            databaseReference.child(id).removeValue();
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

    FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }
}
