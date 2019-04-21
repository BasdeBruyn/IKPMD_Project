package nl.hsleiden.basenstefan.ikpmd;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.TextView;

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

        databaseHelper = DatabaseHelper.getHelper(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> switchToSearch());
        loadList();
    }

    private void loadList() {
        resultsView = findViewById(R.id.ResultsView);
        resultsView.setLayoutManager(new LinearLayoutManager(this));

        //TODO REMOVE THIS, FOR DEBUGGING ONLY
        TextView testData = findViewById(R.id.testDataPlaceholder);
        loadOffline(testData);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(currentUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ListValueListener(testData));
    }

    private void switchToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    private class ListValueListener implements ValueEventListener {

        private final TextView testData;

        public ListValueListener(TextView testData) {
            this.testData = testData;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                movies.clear();
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                List<String> movieIds;
                if (data != null && data.size() > 0) {
                    movieIds = new ArrayList<>(data.keySet());
                    for (String movieId: Arrays.copyOf(movieIds.toArray(), movieIds .size(), String[].class)) {
                        databaseHelper.clearDB();
                        MovieRepository.fetchMovie(movieId, resultsView.getContext(), this::onResult, this::onError);
                    }
                } else {
                    //TODO add some kind of warning
                    testData.setText("Geen data");
                }
            }
        }

        private void onError(VolleyError volleyError) {
            testData.setText(volleyError.toString());
        }

        private void onResult(MovieDetailed movieDetailed) {
            addMovieToList(movieDetailed);
            saveMovie(movieDetailed);
            testData.setText(movieDetailed.getTitle());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            loadOffline(testData);
            testData.setText(databaseError.getMessage());
        }
    }

    private void addMovieToList(MovieDetailed movieDetailed) {
        movies.add(movieDetailed);
        resultsView.setAdapter(new SearchResultAdapter(Arrays.copyOf(movies.toArray(), movies.size(), Movie[].class)));
    }

    private void loadOffline(TextView testData) {
        testData.setText(movies.size() + "");
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

