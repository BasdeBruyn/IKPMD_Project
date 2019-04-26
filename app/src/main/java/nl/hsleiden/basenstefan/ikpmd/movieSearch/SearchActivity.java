package nl.hsleiden.basenstefan.ikpmd.movieSearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.hsleiden.basenstefan.ikpmd.ActivityState;
import nl.hsleiden.basenstefan.ikpmd.BaseActivity;
import nl.hsleiden.basenstefan.ikpmd.R;
import nl.hsleiden.basenstefan.ikpmd.api.Movie;
import nl.hsleiden.basenstefan.ikpmd.api.MovieRepository;
import nl.hsleiden.basenstefan.ikpmd.api.SearchResponse;

public class SearchActivity extends BaseActivity {
    private EditText searchInput;
    private RecyclerView resultsView;

    private String searchTitle;
    private List<Movie> moviesFound = new ArrayList<>();
    private int pageNr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState, R.layout.activity_search);

        ActivityState.setState(ActivityState.SEARCH);

        searchInput = findViewById(R.id.SearchInput);
        resultsView = findViewById(R.id.ResultsView);
        final Button searchButton = findViewById(R.id.SearchButton);
        searchButton.setOnClickListener(this::onSearch);

        resultsView.setLayoutManager(new LinearLayoutManager(this));

        searchInput.setOnKeyListener((view, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    onSearch(view);
                    return true;
                }
                return false;
        });
    }

    private void onSearch(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        searchTitle = searchInput.getText().toString();
        moviesFound.clear();
        pageNr = 1;
        searchMovie();
    }

    private void searchMovie() {
        MovieRepository.searchMovie(searchTitle, pageNr, this, this::onResult, this::onError);
    }

    private void onResult(SearchResponse searchResponse) {
        Log.d("Movies fetched", Arrays.toString(searchResponse.getSearch()));
        if (searchResponse.getSearch() == null)
            Toast.makeText(this, "No movies found!"
                    ,Toast.LENGTH_LONG).show();
        else {
            int resultCount = Integer.parseInt(searchResponse.getTotalResults());
            if (resultCount / (10 * pageNr) > 1) {
                moviesFound.addAll(Arrays.asList(searchResponse.getSearch()));
                pageNr ++;
                searchMovie();
            } else if (resultCount <= 10) {
                moviesFound.addAll(Arrays.asList(searchResponse.getSearch()));
            }
            resultsView.setAdapter(new SearchResultAdapter(Arrays.copyOf(moviesFound.toArray(), moviesFound.size(), Movie[].class)));
        }
    }

    private void onError(VolleyError volleyError) {
        Toast.makeText(this, "No internet!"
                ,Toast.LENGTH_LONG).show();
        Log.d("Error getting movies", volleyError.getMessage());
    }
}
