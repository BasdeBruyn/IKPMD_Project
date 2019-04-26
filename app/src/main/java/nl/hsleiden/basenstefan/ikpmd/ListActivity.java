package nl.hsleiden.basenstefan.ikpmd;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.hsleiden.basenstefan.ikpmd.api.Movie;
import nl.hsleiden.basenstefan.ikpmd.api.MovieDetailed;
import nl.hsleiden.basenstefan.ikpmd.api.MovieRepository;
import nl.hsleiden.basenstefan.ikpmd.movieSearch.SearchActivity;
import nl.hsleiden.basenstefan.ikpmd.movieSearch.SearchResultAdapter;

public class ListActivity extends BaseActivity {

    private RecyclerView resultsView;
    DatabaseHelper databaseHelper;
    List<Movie> movies = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState, R.layout.activity_list);

        ActivityState.setState(ActivityState.LIST);

        databaseHelper = DatabaseHelper.getHelper(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> switchToSearch());
        loadList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadList();
    }

    private void loadList() {
        movies.clear();
        resultsView = findViewById(R.id.ResultsView);
        resultsView.setLayoutManager(new LinearLayoutManager(this));

        loadOffline();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(currentUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ListValueListener(this));
    }

    private void switchToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private class ListValueListener implements ValueEventListener {

        private final ListActivity listActivity;

        public ListValueListener(ListActivity listActivity) {
            this.listActivity = listActivity;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            clear();
            if (dataSnapshot.getValue() != null) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                List<String> movieIds;
                if (data != null && data.size() > 0) {
                    movieIds = new ArrayList<>(data.keySet());
                    for (String movieId: Arrays.copyOf(movieIds.toArray(), movieIds .size(), String[].class)) {
                        MovieRepository.fetchMovie(movieId, resultsView.getContext(), this::onResult, this::onError);
                    }
                }
            }
        }

        private void onError(VolleyError volleyError) {
            Snackbar.make(listActivity.drawer, "No Connection", Snackbar.LENGTH_INDEFINITE).show();
        }

        private void onResult(MovieDetailed movieDetailed) {
            addMovieToList(movieDetailed);
            saveMovie(movieDetailed);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            loadOffline();
        }
    }

    private void clear() {
        movies.clear();
        databaseHelper.clearDB();
        Movie[] movies = new Movie[0];
        resultsView.setAdapter(new SearchResultAdapter(movies));
    }

    private void addMovieToList(MovieDetailed movieDetailed) {
        movies.add(movieDetailed);
        resultsView.setAdapter(new SearchResultAdapter(Arrays.copyOf(movies.toArray(), movies.size(), Movie[].class)));
    }

    private void loadOffline() {
        Cursor cursor = databaseHelper.query(DatabaseInfo.MovieTable.MOVIETABLE, new String[]{"*"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            MovieDetailed movieDetailed = new MovieDetailed(
                    cursor.getString(cursor.getColumnIndex(DatabaseInfo.MovieColumn.TITLE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseInfo.MovieColumn.YEAR)),
                    cursor.getString(cursor.getColumnIndex(DatabaseInfo.MovieColumn.IMDBID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseInfo.MovieColumn.POSTER)),
                    cursor.getString(cursor.getColumnIndex(DatabaseInfo.MovieColumn.IMDBRATING)),
                    cursor.getString(cursor.getColumnIndex(DatabaseInfo.MovieColumn.PLOT)));
            addMovieToList(movieDetailed);
        }
    }

    private void saveMovie(MovieDetailed movieDetailed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseInfo.MovieColumn.TITLE, movieDetailed.getTitle());
        contentValues.put(DatabaseInfo.MovieColumn.YEAR, movieDetailed.getYear());
        contentValues.put(DatabaseInfo.MovieColumn.IMDBID, movieDetailed.getImdbID());
        contentValues.put(DatabaseInfo.MovieColumn.POSTER, movieDetailed.getPoster());
        contentValues.put(DatabaseInfo.MovieColumn.IMDBRATING, movieDetailed.getImdbRating());
        contentValues.put(DatabaseInfo.MovieColumn.PLOT, movieDetailed.getPlot());
        databaseHelper.insert(DatabaseInfo.MovieTable.MOVIETABLE, null, contentValues);
    }

}

