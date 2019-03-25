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

import java.util.Arrays;

import nl.hsleiden.basenstefan.ikpmd.R;
import nl.hsleiden.basenstefan.ikpmd.api.MovieRepository;
import nl.hsleiden.basenstefan.ikpmd.api.SearchResponse;

public class SearchActivity extends AppCompatActivity {
    private EditText searchInput;
    private RecyclerView resultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
        final String title = searchInput.getText().toString();
        MovieRepository.searchMovie(title, this, this::onResult, this::onError);
    }

    private void onResult(SearchResponse searchResponse) {
        Log.d("Movies fetched", Arrays.toString(searchResponse.getSearch()));
        if (searchResponse.getSearch() == null)
            Toast.makeText(this, "No movies found!"
                    ,Toast.LENGTH_LONG).show();
        else
            resultsView.setAdapter(new SearchResultAdapter(searchResponse.getSearch()));
    }

    private void onError(VolleyError volleyError) {
        Log.d("Error getting movies", volleyError.getMessage());
    }
}
